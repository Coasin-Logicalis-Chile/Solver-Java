package com.logicalis.apisolver.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.logicalis.apisolver.model.*;
import com.logicalis.apisolver.model.enums.App;
import com.logicalis.apisolver.model.enums.EndPointSN;
import com.logicalis.apisolver.model.servicenow.SnAttachment;
import com.logicalis.apisolver.view.JournalRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.hibernate.engine.jdbc.StreamUtils;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.UUID;

/**
 * SOLUCIÓN PARA CONCURRENCIA - Rest.java Thread-Safe
 * 
 * PROBLEMA ORIGINAL:
 * - Una sola instancia de RestTemplate compartida entre todos los hilos
 * - Múltiples hilos modifican la misma lista de interceptores (ArrayList no thread-safe)
 * - Causa ConcurrentModificationException cuando un hilo itera mientras otro modifica
 * 
 * SOLUCIÓN IMPLEMENTADA:
 * - Crear nuevas instancias de RestTemplate por cada operación
 * - Evitar modificar la instancia compartida
 * - Usar ThreadLocal como alternativa para mejor performance
 * 
 * @author Ivan Hills - Análisis y Solución de Concurrencia
 */
@Slf4j
public class RestConcurrencyFix {
    
    @Autowired
    @Qualifier("solverRestTemplate")
    private RestTemplate baseRestTemplate; // Plantilla base, NO se modifica directamente

    @Autowired
    Environment environment;

    @PostConstruct
    public void init(){
        App.setEnvironment(environment);
        Util.setEnvironment(environment);
    }
    
    /**
     * SOLUCIÓN PRINCIPAL: Crear RestTemplate thread-safe para ServiceNow
     * 
     * PROBLEMA ANTERIOR:
     * this.restTemplate.getInterceptors().add(...) -> Modifica instancia compartida
     * 
     * SOLUCIÓN:
     * Crear nueva instancia de RestTemplate por cada operación
     * 
     * @return RestTemplate configurado y thread-safe para ServiceNow
     */
    public RestTemplate restTemplateServiceNow() {
        // Crear nueva instancia para este hilo/request (thread-safe)
        RestTemplate threadSafeRestTemplate = new RestTemplate();
        
        // Copiar configuración de la plantilla base
        threadSafeRestTemplate.setRequestFactory(baseRestTemplate.getRequestFactory());
        threadSafeRestTemplate.setMessageConverters(baseRestTemplate.getMessageConverters());
        threadSafeRestTemplate.setErrorHandler(baseRestTemplate.getErrorHandler());
        
        // Agregar interceptor a la NUEVA instancia (thread-safe)
        threadSafeRestTemplate.getInterceptors().add(
            new BasicAuthenticationInterceptor(App.SNUser(), App.SNPassword())
        );
        
        return threadSafeRestTemplate;
    }
    
    /**
     * ALTERNATIVA: RestTemplate por hilo usando ThreadLocal (mejor performance)
     * Cada hilo tiene su propia instancia de RestTemplate
     */
    private final ThreadLocal<RestTemplate> threadLocalRestTemplate = ThreadLocal.withInitial(() -> {
        RestTemplate template = new RestTemplate();
        // Copiar configuración de la plantilla base
        template.setRequestFactory(baseRestTemplate.getRequestFactory());
        template.setMessageConverters(baseRestTemplate.getMessageConverters());
        template.setErrorHandler(baseRestTemplate.getErrorHandler());
        // Configurar interceptor de autenticación
        template.getInterceptors().add(
            new BasicAuthenticationInterceptor(App.SNUser(), App.SNPassword())
        );
        return template;
    });
    
    /**
     * Obtener RestTemplate thread-local (una instancia por hilo)
     * @return RestTemplate específico del hilo actual
     */
    public RestTemplate getThreadLocalRestTemplate() {
        return threadLocalRestTemplate.get();
    }

    /**
     * MÉTODO CORREGIDO: Respuesta por endpoint GET
     * 
     * CAMBIO PRINCIPAL:
     * - Antes: this.restTemplate.getInterceptors().add(...) -> PROBLEMA
     * - Ahora: RestTemplate safeRestTemplate = restTemplateServiceNow() -> SOLUCIÓN
     */
    public String responseByEndPoint(final String endPoint) {
        LogSolver.insertInitService("SERVICENOW", endPoint, "GET");
        
        // ✅ CORRECCIÓN: Usar RestTemplate thread-safe
        RestTemplate safeRestTemplate = restTemplateServiceNow();
        
        ResponseEntity<String> jsonResponse = safeRestTemplate.getForEntity(endPoint, String.class);
        String response = String.valueOf(jsonResponse.getBody());
        LogSolver.insertResponse("SERVICENOW", endPoint, "GET", "", response, jsonResponse.getStatusCode(), "");
        LogSolver.insertEndService("SERVICENOW", endPoint, "GET");
        return response;
    }

    /**
     * MÉTODO CORREGIDO: Respuesta por endpoint GET con JSON
     */
    public String responseByEndPoint(String endPoint, JSONObject json) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        try {
            endPoint = !Objects.isNull(endPoint)? endPoint: "";
            URI uri = new URI(endPoint);
            HttpEntity<JSONObject> httpEntity = new HttpEntity<>(json, headers);
            LogSolver.insertInitService("SERVICENOW", endPoint, "GET");
            
            // ✅ CORRECCIÓN: Usar RestTemplate thread-safe
            RestTemplate safeRestTemplate = restTemplateServiceNow();
            
            ResponseEntity<String> jsonResponse = safeRestTemplate.exchange(uri, HttpMethod.GET, httpEntity, String.class);
            LogSolver.insertResponse("SERVICENOW", endPoint, "GET", httpEntity.getBody().toString(), jsonResponse.getBody(), jsonResponse.getStatusCode(), headers.toString());
            LogSolver.insertEndService("SERVICENOW", endPoint, "GET");
            return String.valueOf(jsonResponse);
        } catch (URISyntaxException e) {
            log.error("Error en URI", e);
        }
        return "";
    }

    /**
     * MÉTODO CORREGIDO: Subir archivo de forma asíncrona
     */
    @Async
    public void uploadFileByEndPoint(final String element, MultipartFile file, String type, String fileName) {
        String urlEndPoint = EndPointSN.UploadAttachment();

        try {
            HttpHeaders headers = new HttpHeaders();
            String url = urlEndPoint;
            HttpMethod requestMethod = HttpMethod.POST;
            
            // ✅ CORRECCIÓN: Usar RestTemplate thread-safe
            RestTemplate safeRestTemplate = restTemplateServiceNow();
            
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            MultiValueMap<String, String> fileMap = new LinkedMultiValueMap<>();
            byte[] bytes = file.getName().getBytes(StandardCharsets.UTF_8);
            ContentDisposition contentDisposition = ContentDisposition
                    .builder("form-data")
                    .name("file")
                    .filename(fileName)
                    .build();

            fileMap.add(HttpHeaders.CONTENT_DISPOSITION, "form-data; name=\"".concat(file.getName()).concat("\"; filename=\"").concat(fileName).concat("\""));
            FileInputStream fileInputStream = (FileInputStream) file.getInputStream();
            Field field = fileInputStream.getClass().getDeclaredField("path");
            field.setAccessible(true);
            String path = (String) field.get(fileInputStream);
            Path filePath = Paths.get(path);

            HttpEntity<byte[]> fileEntity = new HttpEntity<>(Files.readAllBytes(filePath), fileMap);
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

            body.add("table_name", type);
            body.add("table_sys_id", element);
            body.add("file", fileEntity);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            LogSolver.insertInitService("SERVICENOW", url, requestMethod.toString());
            ResponseEntity<String> response = safeRestTemplate.exchange(url, requestMethod, requestEntity, String.class);
            LogSolver.insertResponse("SERVICENOW", url, requestMethod.toString(), requestEntity.getBody().toString(), response.getBody(), response.getStatusCode(), headers.toString());
            LogSolver.insertEndService("SERVICENOW", url, requestMethod.toString());
            log.info("Estado de subida de archivo: " + response.getStatusCode());

        } catch (IOException | NoSuchFieldException | IllegalAccessException e) {
            log.error("Error en uploadFileByEndPoint", e);
        }
    }

    /**
     * MÉTODO CORREGIDO: Enviar archivo a ServiceNow
     */
    public Attachment sendFileToServiceNow(final String element, MultipartFile file, String type, String fileName) {
        Attachment attachment = new Attachment();
        String urlEndPoint = EndPointSN.UploadAttachment();

        try {
            HttpHeaders headers = new HttpHeaders();
            String url = urlEndPoint;
            HttpMethod requestMethod = HttpMethod.POST;
            
            // ✅ CORRECCIÓN: Usar RestTemplate thread-safe
            RestTemplate safeRestTemplate = restTemplateServiceNow();
            
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            MultiValueMap<String, String> fileMap = new LinkedMultiValueMap<>();
            ContentDisposition contentDisposition = ContentDisposition
                    .builder("form-data")
                    .name("file")
                    .filename(fileName)
                    .build();

            fileMap.add(HttpHeaders.CONTENT_DISPOSITION, "form-data; name=\"".concat(file.getName()).concat("\"; filename=\"").concat(fileName).concat("\""));
            FileInputStream fileInputStream = (FileInputStream) file.getInputStream();
            Field field = fileInputStream.getClass().getDeclaredField("path");
            field.setAccessible(true);
            String path = (String) field.get(fileInputStream);
            Path filePath = Paths.get(path);

            HttpEntity<byte[]> fileEntity = new HttpEntity<>(Files.readAllBytes(filePath), fileMap);
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

            body.add("table_name", type);
            body.add("table_sys_id", element);
            body.add("file", fileEntity);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            LogSolver.insertInitService("SERVICENOW", url, requestMethod.toString());
            ResponseEntity<String> responseEntity = safeRestTemplate.exchange(url, requestMethod, requestEntity, String.class);
            LogSolver.insertResponse("SERVICENOW", url, requestMethod.toString(), requestEntity.getBody().toString(), responseEntity.getBody(), responseEntity.getStatusCode(), headers.toString());
            LogSolver.insertEndService("SERVICENOW", url, requestMethod.toString());
            
            // Procesar respuesta
            Gson gson = new Gson();
            JsonObject response = gson.fromJson(responseEntity.getBody(), JsonObject.class);
            JsonObject result = (JsonObject) response.get("result");
            attachment.setIntegrationId(result.get("sys_id").getAsString());
            attachment.setSysCreatedOn(result.get("sys_created_on").getAsString());
            attachment.setSysUpdatedOn(result.get("sys_updated_on").getAsString());
            attachment.setSysCreatedBy(result.get("sys_created_by").getAsString());
            attachment.setSysUpdatedBy(result.get("sys_updated_by").getAsString());
            attachment.setDownloadLinkSN(result.get("download_link").getAsString());
            attachment.setContentType(result.get("content_type").getAsString());
            JsonObject domainJson = (JsonObject) result.get("sys_domain");
            Domain domain = new Domain();
            domain.setIntegrationId(domainJson.get("value").getAsString());
            attachment.setDomain(domain);
        } catch (IOException | NoSuchFieldException | IllegalAccessException e) {
            log.error("Error en sendFileToServiceNow", e);
            return null;
        }
        return attachment;
    }

    /**
     * MÉTODO CORREGIDO: Agregar Journal a ServiceNow
     */
    public Journal addJournal(String endPoint, Journal journal) {
        JournalRequest journalRequest = new JournalRequest();
        String createBy = "";

        if (journal.getCreateBy() != null){
            createBy = journal.getCreateBy().getName();
        }
        journalRequest.setElement(journal.getOrigin());
        journalRequest.setElement_id(journal.getElement());
        journalRequest.setName(journal.getName());

        journalRequest.setValue(!createBy.equals("") ? createBy.concat(": ").concat(journal.getValue()) : journal.getValue());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {
            endPoint = !Objects.isNull(endPoint)? endPoint: "";
            URI uri = new URI(endPoint);

            HttpEntity<JournalRequest> httpEntity = new HttpEntity<>(journalRequest, headers);

            // ✅ CORRECCIÓN: Usar RestTemplate thread-safe
            RestTemplate safeRestTemplate = restTemplateServiceNow();
            LogSolver.insertInitService("SERVICENOW", endPoint, "POST");

            ResponseEntity<String> responseEntity = safeRestTemplate.postForEntity(uri, httpEntity, String.class);

            log.info("httpEntity: "+httpEntity);
            log.info("httpEntity Body: "+httpEntity.getBody());

            LogSolver.insertResponse("SERVICENOW", endPoint, "POST", httpEntity.getBody().toString(), responseEntity.getBody(), responseEntity.getStatusCode(), httpEntity.getHeaders().toString());
            LogSolver.insertEndService("SERVICENOW", endPoint, "POST");

            log.info("========================== Información de Agregar Journal ==========================");
            log.info("EndPoint: "+endPoint);
            log.info("responseEntity: "+responseEntity);
            log.info("responseEntity Body: "+responseEntity.getBody());

            if (responseEntity.getBody() != null){
                journal.setCreatedOn(Util.parseJson(responseEntity.getBody(), "result", "sys_created_on"));
                journal.setIntegrationId(Util.parseJson(responseEntity.getBody(), "result", "sys_id"));
            }else{
                log.info("responseEntity.getBody() es Null");

                if (httpEntity.getBody().getSys_created_on() != null){
                    journal.setCreatedOn(httpEntity.getBody().getSys_created_on());
                }else{
                    LocalDateTime fechaActual = LocalDateTime.now();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    String fechaFormateada = fechaActual.format(formatter);
                    journal.setCreatedOn(fechaFormateada);
                }

                if (httpEntity.getBody().getElement_id() != null){
                    UUID uuid = UUID.randomUUID();
                    String formattedUUID = uuid.toString().replace("-", "");
                    log.info("UUID: "+ formattedUUID);
                    journal.setIntegrationId(formattedUUID);
                }
            }

        } catch (URISyntaxException e) {
            log.error("Error en addJournal", e);
        }
        return journal;
    }

    /**
     * MÉTODO CORREGIDO: Actualizar Incident (Asíncrono)
     */
    @Async
    public Incident putIncident(String endPoint, Incident incident) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        try {
            endPoint = !Objects.isNull(endPoint)? endPoint: "";
            URI uri = new URI(endPoint);
            JSONObject json = new JSONObject();
            json.put("assignmentGroup", Util.isNullIntegration(incident.getAssignmentGroup()));
            json.put("assignedTo", Util.isNullIntegration(incident.getAssignedTo()));
            json.put("integrationId", incident.getIntegrationId());
            json.put("description", incident.getDescription());
            json.put("shortDescription", incident.getShortDescription());
            json.put("closeNotes", incident.getCloseNotes());
            json.put("closeCode", incident.getCloseCode());
            json.put("resolvedAt", incident.getResolvedAt() != null && !incident.getResolvedAt().equals("") ? incident.getResolvedAt().replace("T", " ") : "");
            json.put("resolvedBy", Util.isNullIntegration(incident.getResolvedBy()));
            json.put("state", incident.getState());
            json.put("incidentState", incident.getIncidentState());
            json.put("impact", incident.getImpact());
            json.put("urgency", incident.getUrgency());
            json.put("priority", incident.getPriority());
            json.put("knowledge", incident.getKnowledge());
            json.put("reasonPending", incident.getReasonPending());
            
            HttpEntity<JSONObject> httpEntity = new HttpEntity<>(json, headers);
            
            // ✅ CORRECCIÓN: Usar RestTemplate thread-safe
            RestTemplate safeRestTemplate = restTemplateServiceNow();
            LogSolver.insertInitService("SERVICENOW", endPoint, "PUT");
            ResponseEntity<String> jsonResponse = safeRestTemplate.exchange(uri, HttpMethod.PUT, httpEntity, String.class);
            LogSolver.insertResponse("SERVICENOW", endPoint, "PUT", httpEntity.getBody().toString(), jsonResponse.getBody(), jsonResponse.getStatusCode(), headers.toString());
            LogSolver.insertEndService("SERVICENOW", endPoint, "PUT");
            
            // Procesar respuesta
            String search = "\"result\":";
            if (jsonResponse.toString().contains(search)) {
                String responseSplit = jsonResponse.toString().replaceAll(" ", "").split(search)[1];
                responseSplit = "{\"result\":".concat(responseSplit.split("}}")[0]).concat("}}");
                incident.setState(Util.parseJson(responseSplit, "result", "state"));
            }
        } catch (URISyntaxException e) {
            log.error("Error en putIncident", e);
        }
        return incident;
    }

    // NOTA: Todos los demás métodos (putSysUser, putScRequestItem, putScTask) 
    // deben seguir el mismo patrón de corrección:
    // RestTemplate safeRestTemplate = restTemplateServiceNow();
    // en lugar de modificar this.restTemplate directamente
}

/*
 * RESUMEN DE LA SOLUCIÓN:
 * 
 * PROBLEMA IDENTIFICADO:
 * - ConcurrentModificationException en BasicAuthenticationInterceptor
 * - Causado por múltiples hilos modificando la misma lista de interceptores
 * - RestTemplate compartido entre todos los requests concurrentes
 * 
 * SOLUCIÓN IMPLEMENTADA:
 * 1. Crear nuevas instancias de RestTemplate por operación
 * 2. ThreadLocal como alternativa para mejor performance
 * 3. Evitar modificar la instancia compartida
 * 
 * BENEFICIOS:
 * - Elimina ConcurrentModificationException
 * - Mejora la estabilidad bajo alta concurrencia
 * - Mantiene la funcionalidad existente
 * - Thread-safe por diseño
 * 
 * Análisis y solución por: Ivan Hills
 * Fecha: Agosto 2025
 */

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
import java.time.ZoneId;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;
import java.time.format.DateTimeFormatter;

@Slf4j
public class Rest {
    @Autowired
    @Qualifier("solverRestTemplate")
    private RestTemplate restTemplate;

    @Autowired
    Environment environment;

    @PostConstruct
    public void init(){
        App.setEnvironment(environment);
        Util.setEnvironment(environment);
    }
    public RestTemplate restTemplateServiceNow() {
        this.restTemplate.getInterceptors().add(new BasicAuthenticationInterceptor(App.SNUser(), App.SNPassword()));
        return restTemplate;
    }

    public String responseByEndPoint(final String endPoint) {
        LogSolver.insertInitService("SERVICENOW", endPoint, "GET");
        this.restTemplate.getInterceptors().add(new BasicAuthenticationInterceptor(App.SNUser(), App.SNPassword()));
        ResponseEntity<String> jsonResponse = restTemplate.getForEntity(endPoint, String.class);
        String response = String.valueOf(jsonResponse.getBody());
        LogSolver.insertResponse("SERVICENOW", endPoint, "GET", "", response, jsonResponse.getStatusCode(), "");
        LogSolver.insertEndService("SERVICENOW", endPoint, "GET");
        return response;
    }

    public String responseByEndPoint(String endPoint, JSONObject json) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        try {
            endPoint = !Objects.isNull(endPoint)? endPoint: "";
            URI uri = new URI(endPoint);
            HttpEntity<JSONObject> httpEntity = new HttpEntity<>(json, headers);
            LogSolver.insertInitService("SERVICENOW", endPoint, "GET");
            restTemplate.getInterceptors().add(new BasicAuthenticationInterceptor(App.SNUser(), App.SNPassword()));
            ResponseEntity<String> jsonResponse = restTemplate.exchange(uri, HttpMethod.GET, httpEntity, String.class);
            LogSolver.insertResponse("SERVICENOW", endPoint, "GET", httpEntity.getBody().toString(), jsonResponse.getBody(), jsonResponse.getStatusCode(), headers.toString());
            LogSolver.insertEndService("SERVICENOW", endPoint, "GET");
            return String.valueOf(jsonResponse);
        } catch (URISyntaxException e) {
            log.error("Context", e);
        }
        return "";
    }

    @Async
    public void uploadFileByEndPoint(final String element, MultipartFile file, String type, String fileName) {
        String urlEndPoint = EndPointSN.UploadAttachment();

        try {
            HttpHeaders headers = new HttpHeaders();
            String url = urlEndPoint;
            HttpMethod requestMethod = HttpMethod.POST;
            restTemplate.getInterceptors().add(new BasicAuthenticationInterceptor(App.SNUser(), App.SNPassword()));
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
            ResponseEntity<String> response = restTemplate.exchange(url, requestMethod, requestEntity, String.class);
            LogSolver.insertResponse("SERVICENOW", url, requestMethod.toString(), requestEntity.getBody().toString(), response.getBody(), response.getStatusCode(), headers.toString());
            LogSolver.insertEndService("SERVICENOW", url, requestMethod.toString());
            log.info("file upload status code: " + response.getStatusCode());

        } catch (IOException | NoSuchFieldException | IllegalAccessException e) {
            log.error("Context", e);
        }
    }

    public Attachment sendFileToServiceNow(final String element, MultipartFile file, String type, String fileName) {
        Attachment attachment = new Attachment();
        String urlEndPoint = EndPointSN.UploadAttachment();

        try {
            HttpHeaders headers = new HttpHeaders();
            String url = urlEndPoint;
            HttpMethod requestMethod = HttpMethod.POST;
            restTemplate.getInterceptors().add(new BasicAuthenticationInterceptor(App.SNUser(), App.SNPassword()));
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            MultiValueMap<String, String> fileMap = new LinkedMultiValueMap<>();
            //  byte[] bytes = file.getName().getBytes(StandardCharsets.UTF_8);
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
            ResponseEntity<String> responseEntity = restTemplate.exchange(url, requestMethod, requestEntity, String.class);
            LogSolver.insertResponse("SERVICENOW", url, requestMethod.toString(), requestEntity.getBody().toString(), responseEntity.getBody(), responseEntity.getStatusCode(), headers.toString());
            LogSolver.insertEndService("SERVICENOW", url, requestMethod.toString());
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
            log.error("Context", e);
            return null;
        }
        return attachment;
    }

    public File responseFileByEndPointSOAsync(SnAttachment attachment, RestTemplate restTemplate, String attachmentDir) {
        LogSolver.insertInitService("SERVICENOW",  attachment.getDownload_link(), HttpMethod.GET.toString());
        ResponseEntity<File> responseEntity = restTemplate.execute(attachment.getDownload_link(), HttpMethod.GET, null, clientHttpResponse -> {
            File fileRest = new File(attachmentDir.concat(attachment.getSys_id().concat(".".concat(FilenameUtils.getExtension(attachment.getFile_name())))));
            StreamUtils.copy(clientHttpResponse.getBody(), new FileOutputStream(fileRest));
            log.info(fileRest.getAbsolutePath());
            return ResponseEntity.ok().body(fileRest);
        });
        LogSolver.insertResponse("SERVICENOW", attachment.getDownload_link(), "GET", "", responseEntity.getBody().toString(), responseEntity.getStatusCode(),"");
        LogSolver.insertEndService("SERVICENOW", attachment.getDownload_link(), HttpMethod.GET.toString());
        return responseEntity.getBody();
    }

    @Async
    public File responseFileByEndPointSOAsync(Attachment attachment, RestTemplate restTemplate, String filePath) {
        String finalFilePath = generatePathDirectory(attachment, filePath);
        LogSolver.insertInitService("SERVICENOW",  attachment.getDownloadLinkSN(), HttpMethod.GET.toString());
        ResponseEntity<File> responseEntity = restTemplate.execute(attachment.getDownloadLinkSN(), HttpMethod.GET, null, clientHttpResponse -> {
            File fileRest = new File(finalFilePath);
            StreamUtils.copy(clientHttpResponse.getBody(), new FileOutputStream(fileRest));
            String tag = "[Attachment] ";
            log.info(tag.concat(fileRest.getAbsolutePath()));
            return ResponseEntity.ok().body(fileRest);
        });
        LogSolver.insertResponse("SERVICENOW", attachment.getDownloadLinkSN(), "GET", "", responseEntity.getBody().toString(), responseEntity.getStatusCode(), "");
        LogSolver.insertEndService("SERVICENOW", attachment.getDownloadLinkSN(), HttpMethod.GET.toString());
        return responseEntity.getBody();
    }

    public String generatePathDirectory(Attachment attachment, String filePath) {
        try {
            filePath = getPathDirectory(attachment, filePath);
            Path path = Paths.get(filePath);
            File directory = new File(filePath);
            if (!directory.exists()) {
                Files.createDirectories(path);
            }
            return getFilePathDirectory(attachment, filePath);

        } catch (IOException e) {
            log.error("Context", e);
        }
        return filePath;
    }

    public String getPathDirectory(Attachment attachment, String filePath) {
        String[] date = attachment.getSysCreatedOn().split(" ")[0].split("-");
        return filePath
                .concat(attachment.getDomain().getIntegrationId())
                .concat(File.separator)
                .concat(date[0])
                .concat(File.separator)
                .concat(date[1])
                .concat(File.separator)
                .concat(date[2])
                .concat(File.separator)
                .concat(attachment.getElement())
                .concat(File.separator);
    }

    public String getFilePathDirectory(Attachment attachment, String filePath) {
        return filePath.concat(attachment.getIntegrationId()).concat(attachment.getExtension());
    }

    public File responseFileByEndPointSO(Attachment attachment, RestTemplate restTemplate, String filePath) {
        try {
            filePath = getPathDirectory(attachment, filePath);
            Path path = Paths.get(getPathDirectory(attachment, filePath));
            Files.createDirectories(path);
            log.info(path.toString());
            filePath = getFilePathDirectory(attachment, filePath);
        } catch (IOException e) {
            log.error("Context", e);
        }
        String finalFilePath = filePath;
        LogSolver.insertInitService("SERVICENOW", attachment.getDownloadLinkSN(), HttpMethod.GET.toString());
        ResponseEntity<File> responseEntity = restTemplate.execute(attachment.getDownloadLinkSN(), HttpMethod.GET, null, clientHttpResponse -> {
            log.info(finalFilePath);
            File fileRest = new File(finalFilePath);
            log.info(finalFilePath);
            StreamUtils.copy(clientHttpResponse.getBody(), new FileOutputStream(fileRest));
            log.info(fileRest.getAbsolutePath());
            return ResponseEntity.ok().body(fileRest);
        });
        LogSolver.insertResponse("SERVICENOW", attachment.getDownloadLinkSN(), "GET", "", responseEntity.getBody().toString(), responseEntity.getStatusCode(), "");
        LogSolver.insertEndService("SERVICENOW", attachment.getDownloadLinkSN(), HttpMethod.GET.toString());
        return responseEntity.getBody();
    }

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
        System.out.println("headers: "+headers);

        try {
            endPoint = !Objects.isNull(endPoint)? endPoint: "";
            URI uri = new URI(endPoint);

            HttpEntity<JournalRequest> httpEntity = new HttpEntity<>(journalRequest, headers);

            restTemplate.getInterceptors().add(new BasicAuthenticationInterceptor(App.SNUser(), App.SNPassword()));
            LogSolver.insertInitService("SERVICENOW", endPoint, "POST");

            ResponseEntity<String> responseEntity = restTemplate.postForEntity(uri, httpEntity, String.class);

            log.info("httpEntity: "+httpEntity);
            log.info("httpEntity Body: "+httpEntity.getBody());

            LogSolver.insertResponse("SERVICENOW", endPoint, "POST", httpEntity.getBody().toString(), responseEntity.getBody(), responseEntity.getStatusCode(), httpEntity.getHeaders().toString());
            LogSolver.insertEndService("SERVICENOW", endPoint, "POST");

            log.info("========================== Add Journal Info ==========================");
            log.info("EndPoint: "+endPoint);
            log.info("responseEntity: "+responseEntity);
            log.info("responseEntity Body: "+responseEntity.getBody());
            log.info("responseEntity Header: "+responseEntity.getHeaders());
            log.info("responseEntity Header get Class: "+responseEntity.getClass());
            log.info("responseEntity Header get Status code Value: "+responseEntity.getStatusCodeValue());
            log.info("responseEntity Header get Status code: "+responseEntity.getStatusCode());

            if (responseEntity.getBody() != null){
                journal.setCreatedOn(Util.parseJson(responseEntity.getBody(), "result", "sys_created_on"));
                journal.setIntegrationId(Util.parseJson(responseEntity.getBody(), "result", "sys_id"));
            }else{


                log.info("responseEntity.getBody() is Null ");


                if (httpEntity.getBody().getSys_created_on() != null){
                    journal.setCreatedOn(httpEntity.getBody().getSys_created_on());
                }else{
                    LocalDateTime fechaActual = LocalDateTime.now();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    String fechaFormateada = fechaActual.format(formatter);
                    //Date date = Date.from(fechaActual.atZone(ZoneId.systemDefault()).toInstant());
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
            log.error("Context", e);
        }
        return journal;
    }

    @Async
    public Incident putIncident(String endPoint, Incident incident) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        try {
            endPoint = !Objects.isNull(endPoint)? endPoint: "";
            URI uri = new URI(endPoint);
            String strUri = !Objects.isNull(uri) ? uri.toString() : "";
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
            restTemplate.getInterceptors().add(new BasicAuthenticationInterceptor(App.SNUser(), App.SNPassword()));
            LogSolver.insertInitService("SERVICENOW", endPoint, "PUT");
            ResponseEntity<String> jsonResponse = restTemplate.exchange(uri, HttpMethod.PUT, httpEntity, String.class);
            LogSolver.insertResponse("SERVICENOW", endPoint, "PUT", httpEntity.getBody().toString(), jsonResponse.getBody(), jsonResponse.getStatusCode(), headers.toString());
            LogSolver.insertEndService("SERVICENOW", endPoint, "PUT");
            String search = "\"result\":";
            if (jsonResponse.toString().contains(search)) {
                String responseSplit = jsonResponse.toString().replaceAll(" ", "").split(search)[1];
                responseSplit = "{\"result\":".concat(responseSplit.split("}}")[0]).concat("}}");
                incident.setState(Util.parseJson(responseSplit, "result", "state"));
            }
        } catch (URISyntaxException e) {
            log.error("Context", e);
        }
        return incident;
    }

    public SysUser putSysUser(String endPoint, SysUser sysUser) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        try {
            endPoint = !Objects.isNull(endPoint)? endPoint: "";
            URI uri = new URI(endPoint);
            JSONObject json = new JSONObject();
            json.put("code", sysUser.getCode());
            json.put("integrationId", sysUser.getIntegrationId());
            HttpEntity<JSONObject> httpEntity = new HttpEntity<>(json, headers);

            restTemplate.getInterceptors().add(new BasicAuthenticationInterceptor(App.SNUser(), App.SNPassword()));

            LogSolver.insertInitService("SERVICENOW", endPoint, "PUT");
            ResponseEntity<String> jsonResponse = restTemplate.exchange(uri, HttpMethod.PUT, httpEntity, String.class);

            System.out.println("=========================");
            System.out.println("Posible Null: "+httpEntity.getBody().toString());
            System.out.println("=========================");

            LogSolver.insertResponse("SERVICENOW", endPoint, "PUT", httpEntity.getBody().toString(), jsonResponse.getBody(), jsonResponse.getStatusCode(), headers.toString());
            LogSolver.insertEndService("SERVICENOW", endPoint, "PUT");
            String search = "\"result\":";
            if (jsonResponse.toString().contains(search)) {
                String responseSplit = jsonResponse.toString();
            }
        } catch (URISyntaxException e) {
            log.error("Context", e);
        }
        return sysUser;
    }

    public ScRequestItem putScRequestItem(String endPoint, ScRequestItem scRequestItem) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        try {
            endPoint = !Objects.isNull(endPoint)? endPoint: "";
            URI uri = new URI(endPoint);
            String strUri = !Objects.isNull(uri) ? uri.toString() : "";
            JSONObject json = new JSONObject();
            json.put("assignmentGroup", scRequestItem.getAssignmentGroup().getIntegrationId());
            json.put("assignedTo", scRequestItem.getAssignedTo().getIntegrationId());
            json.put("integrationId", scRequestItem.getIntegrationId());
            json.put("description", scRequestItem.getDescription());
            json.put("short_description", scRequestItem.getShortDescription());
            json.put("state", scRequestItem.getState());
            HttpEntity<JSONObject> httpEntity = new HttpEntity<>(json, headers);
            LogSolver.insertInitService("SERVICENOW", endPoint, "PUT");
            restTemplate.getInterceptors().add(new BasicAuthenticationInterceptor(App.SNUser(), App.SNPassword()));
            ResponseEntity<String> jsonResponse = restTemplate.exchange(uri, HttpMethod.PUT, httpEntity, String.class);
            LogSolver.insertResponse("SERVICENOW", endPoint, "PUT", httpEntity.getBody().toString(), jsonResponse.getBody(), jsonResponse.getStatusCode(), headers.toString());
            LogSolver.insertEndService("SERVICENOW", endPoint, "PUT");
        } catch (URISyntaxException e) {
            log.error("Context", e);
        }
        return scRequestItem;
    }

    public ScTask putScTask(String endPoint, ScTask scTask) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        try {
            endPoint = !Objects.isNull(endPoint)? endPoint: "";
            URI uri = new URI(endPoint);
            String strUri = !Objects.isNull(uri) ? uri.toString() : "";
            JSONObject json = new JSONObject();
            json.put("solverId",scTask.getId());
            json.put("assignmentGroup", Util.isNullIntegration(scTask.getAssignmentGroup()));
            json.put("assignedTo", Util.isNullIntegration(scTask.getAssignedTo()));
            json.put("scalingAssignmentGroup", Util.isNullIntegration(scTask.getScalingAssignmentGroup()));
            json.put("scalingAssignedTo", Util.isNullIntegration(scTask.getScalingAssignedTo()));
            json.put("scaling", scTask.getScaling());
            json.put("integrationId", scTask.getIntegrationId());
            json.put("description", scTask.getDescription());
            json.put("shortDescription", scTask.getShortDescription());
            json.put("state", scTask.getState());
            json.put("priority",scTask.getPriority());
            json.put("closeNotes", scTask.getCloseNotes());
            json.put("closedAt", scTask.getClosedAt() != null && !scTask.getClosedAt().equals("") ? scTask.getClosedAt().replace("T", " ") : "");
            json.put("closedBy", Util.isNullIntegration(scTask.getClosedBy()));
            json.put("reasonPending", scTask.getReasonPending());
            HttpEntity<JSONObject> httpEntity = new HttpEntity<>(json, headers);
            restTemplate.getInterceptors().add(new BasicAuthenticationInterceptor(App.SNUser(), App.SNPassword()));
            LogSolver.insertInitService("SERVICENOW", endPoint, "PUT");
            ResponseEntity<String> jsonResponse = restTemplate.exchange(uri, HttpMethod.PUT, httpEntity, String.class);
            LogSolver.insertResponse("SERVICENOW", endPoint, "PUT", httpEntity.getBody().toString(), jsonResponse.getBody().toString(), jsonResponse.getStatusCode(), headers.toString() );
            LogSolver.insertEndService("SERVICENOW", endPoint, "PUT");
            String tag = "[ScTask]";
            String tagAction = "(Update in ServiceNow)";
            Util.printData(tag, tagAction, Util.getFieldDisplay(scTask), Util.getFieldDisplay(scTask.getAssignedTo()));
        } catch (URISyntaxException e) {
            log.error("Context", e);
        }
        return scTask;
    }
}

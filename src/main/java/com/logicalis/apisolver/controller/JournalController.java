
package com.logicalis.apisolver.controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.logicalis.apisolver.model.*;
import com.logicalis.apisolver.model.enums.*;
import com.logicalis.apisolver.model.utilities.AttachmentInfo;
import com.logicalis.apisolver.services.*;
import com.logicalis.apisolver.util.Rest;
import com.logicalis.apisolver.util.Util;
import com.logicalis.apisolver.view.JournalRequest;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@CrossOrigin(origins = {"${app.api.settings.cross-origin.urls}", "*"})
@RestController
@RequestMapping("/api/v1")
public class JournalController {

    private final Logger logger = LoggerFactory.getLogger(JournalController.class);

    @Autowired
    private IJournalService journalService;
    @Autowired
    private IIncidentService incidentService;
    @Autowired
    private IScTaskService scTaskService;
    @Autowired
    private IAttachmentService attachmentService;
    @Autowired
    private IScRequestItemService scRequestItemService;
    @Autowired
    private IScRequestService scRequestService;
    @Autowired
    private ISysUserService sysUserService;
    @Autowired
    private IAPIExecutionStatusService statusService;
    Util util = new Util();
    App app = new App();
    EndPointSN endPointSN = new EndPointSN();
    @Autowired
    private Environment environment;
    @Async
    @PutMapping("/journalSN") 
    public ResponseEntity<?> update(@RequestBody JournalRequest journalRequest) {
        Journal currentJournal = new Journal();
        Journal journalUpdated = null;
        Map<String, Object> response = new HashMap<>();
        logger.info("Solicitud PUT recibida para actualizar Journal: {}", journalRequest.getSys_id());
        Rest rest = new Rest();
        if (currentJournal == null) {
            logger.warn("Journal no encontrada para sys_id: {}", journalRequest.getSys_id());
            response.put("mensaje", Errors.dataAccessExceptionUpdate.get(journalRequest.getSys_id()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }

        try {

            /*System.out.println("journalRequest.getElement: ".concat(journalRequest.getElement()));
            System.out.println("journalRequest.getElement_id: ".concat(journalRequest.getElement_id()));
            System.out.println("journalRequest.getValue: ".concat(journalRequest.getValue()));
            System.out.println("journalRequest.getSys_created_on: ".concat(journalRequest.getSys_created_on()));*/

            logger.debug("Actualizando Journal. sys_id Journal: {}", journalRequest.getSys_id());

            Journal journal = new Journal();
            String tagAction = app.CreateConsole();
            String tag = "[Journal] ";

            logger.info("Estableciendo nuevos atributos al Objeto Journal. sys_id Journal: {}", journalRequest.getSys_id());
            journal.setOrigin(journalRequest.getElement());
            journal.setElement(journalRequest.getElement_id());
            journal.setCreatedOn(journalRequest.getSys_created_on());
            journal.setIntegrationId(journalRequest.getSys_id());

            logger.info("Determinando relacion entre objeto Incident o ScRequestItem. sys_id Task SLA: {}", journalRequest.getSys_id());
            String element = "";
            if (journalRequest.getName().equals(SnTable.Incident.get())) {
                Incident incident = incidentService.findByIntegrationId(journal.getElement());
                if (incident != null) {
                    logger.info("Relacion establecida con Incident: {}", incident.getNumber());
                    journal.setIncident(incident);
                    element = util.getFieldDisplay(incident);
                }
            } else if (journalRequest.getName().equals(SnTable.ScRequestItem.get())) {
                ScRequestItem scRequestItem = scRequestItemService.findByIntegrationId(journal.getElement());
                if (scRequestItem != null) {
                    logger.info("Relacion establecida con scRequestItem: {}", scRequestItem.getNumber());
                    journal.setScRequestItem(scRequestItem);
                    element = util.getFieldDisplay(scRequestItem);
                }
            }

        
            logger.info("Procesando objeto journalRequest: {} para Verificar y actualizar informacion", journalRequest.getSys_id());
            if (util.hasData(journalRequest.getSys_created_by())) {
                SysUser openedBy = sysUserService.findByUserName(journalRequest.getSys_created_by());
                if (openedBy != null)
                    journal.setCreateBy(openedBy);
            }
            Journal exists = journalService.findByIntegrationId(journal.getIntegrationId());
            if (exists != null) {
                journal.setId(exists.getId());
                tagAction = app.UpdateConsole();
            }
            journal.setValue(util.reeplaceImg(journalRequest.getValue()));
           /* System.out.println("IMG VALUE READ");
            System.out.println(journalRequest.getValue());
            System.out.println("IMG VALUE PROCESS");
            System.out.println(journal.getValue());*/

            logger.info("Guardando Journal en la Base de Datos: {}", journalRequest.getSys_id());
            journalService.save(journal);

            logger.info("Journal Actualizado correctamente: sys_id Journal: {}", journalRequest.getSys_id());

            //util.printData(tag, tagAction, util.getFieldDisplay(journal), element);

        } catch (DataAccessException e) {
            logger.error("Error al actualizar la Task SLA: {}. Error: {}", journalRequest.getSys_id(), e.getMessage());
            response.put("mensaje", Errors.dataAccessExceptionUpdate.get());
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        logger.info("Journal Actualizado correctamente: sys_id Journal: {}", journalRequest.getSys_id());
        response.put("mensaje", Messages.UpdateOK.get());
        response.put("journal", journalUpdated);

        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }

    @GetMapping("/journals")
    public List<Journal> index() {
        return journalService.findAll();
    }

    @GetMapping("/journalsInfoByIncident/{id}")
    public List<Journal> findInfoByIncident(@PathVariable Long id) {
        List<Journal> journals = journalService.findByIncident(incidentService.findById(id));
        List<AttachmentInfo> attachments = attachmentService.findAttachmentsByIncident(id);
        journals.forEach(journal -> {
            Pattern p = Pattern.compile(".*\\<[^>]+>.*");
            Matcher m = p.matcher(journal.getValue());
            if (m.find() && !journal.getReviewed()) {
                attachments.forEach(attachment -> {
                    journal.setParse(true);
                    journal.setReviewed(true);
                    String master = journal.getValue();
                    String target = endPointSN.SrcAttachment().concat(attachment.getIntegrationId());
                    String replacement = environment.getProperty("setting.url.api.img").concat(":").concat(environment.getProperty("server.port")).concat(environment.getProperty("setting.api.version")).concat("/").concat("snAttachment/").concat(attachment.getId().toString());
                    int startIndex = master.indexOf(target);
                    int stopIndex = startIndex + target.length();
                    StringBuilder builder = new StringBuilder(master);
                    if (startIndex != -1) {
                        builder.replace(startIndex, stopIndex, replacement);

                        System.out.println("IMG VALUE READ");
                        System.out.println(builder.toString());
                        System.out.println("IMG VALUE PROCESS");
                        System.out.println(util.reeplaceImg(builder.toString()));

                        journal.setValue(util.reeplaceImg(builder.toString()));
                        journal.setValue(builder.toString());
                    }
                    //String newValue = journal.getValue().
                    //       replaceAll(endPointSN.SrcAttachment().concat(attachment.getIntegrationId()),
                    //               environment.getProperty("setting.url.api").concat(":").concat(environment.getProperty("server.port")).concat(environment.getProperty("setting.api.version")).concat("/").concat(attachment.getIntegrationId()));

                });
                journalService.save(journal);
            }
        });


        return journals;
    }

    @GetMapping("/journalsInfoIncidentOrScRequestItem")
    public List<Journal> findInfoByIncidentOrScRequestItem(@NotNull @RequestParam(value = "incident", required = true, defaultValue = "") String incident,
                                                           @NotNull @RequestParam(value = "scRequestItem", required = true, defaultValue = "") String scRequestItem) {

        List<JournalInfo> journalsInfo = journalService.findInfoByIncidentOrScRequestItem(incident, scRequestItem);
        List<Journal> journals = new ArrayList<>();
        List<AttachmentInfo> attachments = attachmentService.findAttachmentsByIncidentOrScRequestItem(incident, scRequestItem);

        journalsInfo.forEach(journalInfo -> {
            Journal journal = journalService.findById(journalInfo.getId());
            Pattern p = Pattern.compile(".*\\<[^>]+>.*");
            Matcher m = p.matcher(journal.getValue());

            if (m.find() && !journal.getReviewed()) {
                attachments.forEach(attachment -> {
                    journal.setParse(true);
                    journal.setReviewed(true);
                    String master = journal.getValue();
                    String target = endPointSN.SrcAttachment().concat(attachment.getIntegrationId());
                    String replacement = environment.getProperty("setting.url.api.img").concat(":").concat(environment.getProperty("server.port")).concat(environment.getProperty("setting.api.version")).concat("/").concat("snAttachment/").concat(attachment.getId().toString());
                    int startIndex = master.indexOf(target);
                    int stopIndex = startIndex + target.length();
                    StringBuilder builder = new StringBuilder(master);
                    if (startIndex != -1) {
                        builder.replace(startIndex, stopIndex, replacement);
                        //System.out.println(util.reeplaceImg(builder.toString()));
                        journal.setValue(util.reeplaceImg(builder.toString()));
                        //(journal.setValue(builder.toString());
                    }
                });
                journalService.save(journal);
            }
            journals.add(journal);
        });

        return journals;
    }

    @GetMapping("/journalsByScRequestItem/{id}")
    public List<Journal> findJournalsByScRequestItem(@PathVariable Long id) {
        List<Journal> journals = journalService.findJournalsByScRequestItem(scRequestItemService.findById(id));
        List<AttachmentInfo> attachments = attachmentService.findAttachmentsByScRequestItem(id);

        journals.forEach(journal -> {
            Pattern p = Pattern.compile(".*\\<[^>]+>.*");
            Matcher m = p.matcher(journal.getValue());
            if (m.find() && !journal.getReviewed()) {
                attachments.forEach(attachment -> {
                    journal.setParse(true);
                    journal.setReviewed(true);
                    String master = journal.getValue();
                    String target = endPointSN.SrcAttachment().concat(attachment.getIntegrationId());
                    String replacement = environment.getProperty("setting.url.api.img").concat(":").concat(environment.getProperty("server.port")).concat(environment.getProperty("setting.api.version")).concat("/").concat("snAttachment/").concat(attachment.getId().toString());
                    int startIndex = master.indexOf(target);
                    int stopIndex = startIndex + target.length();
                    StringBuilder builder = new StringBuilder(master);
                    if (startIndex != -1) {
                        builder.replace(startIndex, stopIndex, replacement);
                        journal.setValue(builder.toString());
                    }
                });
                journalService.save(journal);
            }
        });


        return journals;
    }

    @GetMapping("/journalsByScTask/{id}")
    public List<Journal> findJournalsByScTask(@PathVariable Long id) {
        List<Journal> journals = journalService.findJournalsByScRequestItem(scRequestItemService.findById(id));
        List<AttachmentInfo> attachments = attachmentService.findAttachmentsByScRequestItem(id);

        journals.forEach(journal -> {
            Pattern p = Pattern.compile(".*\\<[^>]+>.*");
            Matcher m = p.matcher(journal.getValue());
            if (m.find() && !journal.getReviewed()) {
                attachments.forEach(attachment -> {
                    journal.setParse(true);
                    journal.setReviewed(true);
                    String master = journal.getValue();
                    String target = endPointSN.SrcAttachment().concat(attachment.getIntegrationId());
                    String replacement = environment.getProperty("setting.url.api.img").concat(":").concat(environment.getProperty("server.port")).concat(environment.getProperty("setting.api.version")).concat("/").concat("snAttachment/").concat(attachment.getId().toString());
                    int startIndex = master.indexOf(target);
                    int stopIndex = startIndex + target.length();
                    StringBuilder builder = new StringBuilder(master);
                    if (startIndex != -1) {
                        builder.replace(startIndex, stopIndex, replacement);
                        journal.setValue(builder.toString());
                    }
                });
                journalService.save(journal);
            }
        });


        return journals;
    }

    @GetMapping("/journalsByIncident/{id}")
    public List<Journal> index(@PathVariable Long id) {

        return journalService.findByIncident(incidentService.findById(id));
    }

    @GetMapping("/journal/{id}")
    public ResponseEntity<?> show(@PathVariable Long id) {

        Journal journal = null;
        Map<String, Object> response = new HashMap<>();

        try {
            journal = journalService.findById(id);
        } catch (DataAccessException e) {
            response.put("mensaje", Errors.dataAccessExceptionQuery.get());
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (journal == null) {
            response.put("mensaje", Messages.notExist.get(id.toString()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<Journal>(journal, HttpStatus.OK);
    }


    @PostMapping("/journal")
    public ResponseEntity<?> create(@RequestBody String json) {

        logger.info("Se recibio una solicitud POST para crear una entrada de Journal.");

        Map<String, Object> response = new HashMap<>();
        Journal addJournal = new Journal();
        try {

            Rest rest = new Rest();
            Journal journal = new Journal();
            journal.setValue(util.parseJson(json, "journal", "value"));
            journal.setOrigin(util.parseJson(json, "journal", "origin"));
            journal.setName(util.parseJson(json, "journal", "name"));
            journal.setElement(util.parseJson(json, "journal", "element"));
            if (journal.getName().equals(SnTable.Incident.get())) {
                Incident incident = incidentService.findByIntegrationId(journal.getElement());
                if (incident != null)
                    logger.info("Numero de Incidente de Journal: {}", incident.getNumber());
                    journal.setIncident(incident);
            } else if (journal.getName().equals(SnTable.ScRequestItem.get())) {
                ScRequestItem scRequestItem = scRequestItemService.findByIntegrationId(journal.getElement());
                if (scRequestItem != null)
                    logger.info("Numero de RequestItem de Journal: {}", scRequestItem.getNumber());
                    journal.setScRequestItem(scRequestItem);
            }
            journal.setCreateBy(sysUserService.findById(util.parseIdJson(json, "journal", "createBy")));
            addJournal = rest.addJournal(endPointSN.PostJournal(), journal);
            logger.info("Guardando Journal en la Base de Datos: {}", addJournal.getIntegrationId());
            journalService.save(addJournal);
            logger.info("Journal creado correctamente.", addJournal.getIntegrationId());
        } catch (DataAccessException e) {
            logger.error("Error al crear Journal: {}", e.getMessage());
            response.put("mensaje", Errors.dataAccessExceptionInsert.get());
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", Messages.createOK.get());
        response.put("journal", addJournal);
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }

    @Secured("ROLE_ADMIN")
    @PutMapping("/journal/{id}")
    public ResponseEntity<?> update(@RequestBody Journal journal, @PathVariable Long id) {

        Journal currentJournal = journalService.findById(id);
        Journal journalUpdated = null;

        Map<String, Object> response = new HashMap<>();

        if (currentJournal == null) {
            response.put("mensaje", Errors.dataAccessExceptionUpdate.get(id.toString()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }

        try {
            currentJournal.setElement(journal.getElement());
            journalUpdated = journalService.save(currentJournal);

        } catch (DataAccessException e) {
            response.put("mensaje", Errors.dataAccessExceptionUpdate.get());
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", Messages.UpdateOK.get());
        response.put("journal", journalUpdated);

        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/journal/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {

        Map<String, Object> response = new HashMap<>();
        try {
            journalService.delete(id);
        } catch (DataAccessException e) {
            response.put("mensaje", Errors.dataAccessExceptionDelete.get());
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", Messages.DeleteOK.get());
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
    }

    @GetMapping("/journal/{integration_id}")
    public ResponseEntity<?> show(@PathVariable String integration_id) {
        Journal journal = null;
        Map<String, Object> response = new HashMap<>();
        try {
            journal = journalService.findByIntegrationId(integration_id);
        } catch (DataAccessException e) {
            response.put("mensaje", Errors.dataAccessExceptionQuery.get());
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (journal == null) {
            response.put("mensaje", "El registro ".concat(integration_id.concat(" no existe en la base de datos!")));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Journal>(journal, HttpStatus.OK);
    }

    @GetMapping("/journalsBySolver")
    public List<JournalRequest> show() {
        System.out.println(app.Start());
        APIResponse apiResponse = null;
        List<JournalRequest> journalRequests = new ArrayList<>();
        String[] sparmOffSets = util.offSets1500000();
        long startTime = 0;
        long endTime = 0;
        String tag = "[Journal] ";
        try {
            Rest rest = new Rest();
            startTime = System.currentTimeMillis();
            final int[] count = {1};
            for (String sparmOffSet : sparmOffSets) {

                String result = rest.responseByEndPoint(endPointSN.Journal().concat(sparmOffSet));
                System.out.println(tag.concat("(".concat(endPointSN.Journal().concat(sparmOffSet)).concat(")")));
                ObjectMapper mapper = new ObjectMapper();
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

                JSONParser parser = new JSONParser();
                JSONObject resultJson = new JSONObject();
                JSONArray ListSnJournalJson = new JSONArray();
                resultJson = (JSONObject) parser.parse(result);
                if (resultJson.get("result") != null)
                    ListSnJournalJson = (JSONArray) parser.parse(resultJson.get("result").toString());

                ListSnJournalJson.stream().forEach(snJournalJson -> {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    JournalRequest journalRequest = new JournalRequest();
                    boolean flag = false;
                    try {
                        journalRequest = objectMapper.readValue(snJournalJson.toString(), JournalRequest.class);
                        Journal journal = new Journal();

                        //System.out.println(journalRequest);
                        String element = "";
                        String company = "";
                        if (journalRequest.getName().equals(SnTable.Incident.get())) {
                            Incident incident = incidentService.findByIntegrationId(journalRequest.getElement_id());
                            if (incident != null) {
                                journal.setIncident(incident);
                                flag = incident.getCompany().getSolver();
                                company = util.getFieldDisplay(incident.getCompany());
                                element = util.getFieldDisplay(incident);
                            }
                        } else if (journalRequest.getName().equals(SnTable.ScRequestItem.get())) {
                            ScRequestItem scRequestItem = scRequestItemService.findByIntegrationId(journalRequest.getElement_id());
                            if (scRequestItem != null) {
                                journal.setScRequestItem(scRequestItem);
                                flag = scRequestItem.getCompany().getSolver();
                                company = util.getFieldDisplay(scRequestItem.getCompany());
                                element = util.getFieldDisplay(scRequestItem);
                            }
                        } else if (journalRequest.getName().equals(SnTable.ScRequest.get())) {
                            ScRequest scRequest = scRequestService.findByIntegrationId(journalRequest.getElement_id());
                            if (scRequest != null) {
                                journal.setScRequest(scRequest);
                                flag = scRequest.getCompany().getSolver();
                                company = util.getFieldDisplay(scRequest.getCompany());
                                element = util.getFieldDisplay(scRequest);
                            }
                        }

                        if (flag) {
                            journalRequests.add(journalRequest);
                            journal.setOrigin(journalRequest.getElement());
                            journal.setElement(journalRequest.getElement_id());
                            journal.setValue(util.reeplaceImg(journalRequest.getValue().trim()));
                            journal.setCreatedOn(journalRequest.getSys_created_on());
                            journal.setIntegrationId(journalRequest.getSys_id());
                            journal.setActive(true);
                            if (util.hasData(journalRequest.getSys_created_by())) {
                                SysUser openedBy = sysUserService.findByUserName(journalRequest.getSys_created_by());
                                if (openedBy != null)
                                    journal.setCreateBy(openedBy);
                            }

                            String tagAction = app.CreateConsole();
                            Journal exists = journalService.findByIntegrationId(journal.getIntegrationId());
                            if (exists != null) {
                                journal.setId(exists.getId());
                                tagAction = app.UpdateConsole();
                            }
                            util.printData(tag, count[0], tagAction.concat(util.getFieldDisplay(journal)), element, company);


                            journalService.save(journal);


                            count[0] = count[0] + 1;
                        }
                    } catch (Exception e) {
                        System.out.println(tag.concat("Exception (I) : ").concat(String.valueOf(e)));
                    }
                });

                apiResponse = mapper.readValue(result, APIResponse.class);
                APIExecutionStatus status = new APIExecutionStatus();
                status.setUri(endPointSN.Location());
                status.setUserAPI(app.SNUser());
                status.setPasswordAPI(app.SNPassword());
                status.setError(apiResponse.getError());
                status.setMessage(apiResponse.getMessage());
                endTime = (System.currentTimeMillis() - startTime);
                status.setExecutionTime(endTime);
                statusService.save(status);
            }

        } catch (Exception e) {
            System.out.println(tag.concat("Exception (II) : ").concat(String.valueOf(e)));
        }
        System.out.println(app.End());
        return journalRequests;
    }


    @GetMapping("/journalsBySolverByQuery")
    public List<JournalRequest> show(String query, boolean flagQuery) {
        System.out.println(app.Start());
        APIResponse apiResponse = null;
        List<JournalRequest> journalRequests = new ArrayList<>();
        String[] sparmOffSets = util.offSets1500000();
        long startTime = 0;
        long endTime = 0;
        String tag = "[Journal] ";
        try {
            Rest rest = new Rest();
            startTime = System.currentTimeMillis();
            final int[] count = {1};
            for (String sparmOffSet : sparmOffSets) {

                String result = rest.responseByEndPoint(endPointSN.JournalByQuery().replace("QUERY", query).concat(sparmOffSet));
                System.out.println(tag.concat("(".concat(endPointSN.JournalByQuery().replace("QUERY", query).concat(sparmOffSet))));
                ObjectMapper mapper = new ObjectMapper();
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

                JSONParser parser = new JSONParser();
                JSONObject resultJson = new JSONObject();
                JSONArray ListSnJournalJson = new JSONArray();
                resultJson = (JSONObject) parser.parse(result);
                if (resultJson.get("result") != null)
                    ListSnJournalJson = (JSONArray) parser.parse(resultJson.get("result").toString());

                ListSnJournalJson.stream().forEach(snJournalJson -> {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    JournalRequest journalRequest = new JournalRequest();
                    boolean flag = false;
                    try {
                        journalRequest = objectMapper.readValue(snJournalJson.toString(), JournalRequest.class);
                        Journal journal = new Journal();

                        //System.out.println(journalRequest);
                        String element = "";
                        String company = "";
                        if (journalRequest.getName().equals(SnTable.Incident.get())) {
                            Incident incident = incidentService.findByIntegrationId(journalRequest.getElement_id());
                            if (incident != null) {
                                journal.setIncident(incident);
                                flag = incident.getCompany().getSolver();
                                company = util.getFieldDisplay(incident.getCompany());
                                element = util.getFieldDisplay(incident);
                            }
                        } else if (journalRequest.getName().equals(SnTable.ScRequestItem.get())) {
                            ScRequestItem scRequestItem = scRequestItemService.findByIntegrationId(journalRequest.getElement_id());
                            if (scRequestItem != null) {
                                journal.setScRequestItem(scRequestItem);
                                flag = scRequestItem.getCompany().getSolver();
                                company = util.getFieldDisplay(scRequestItem.getCompany());
                                element = util.getFieldDisplay(scRequestItem);
                            }
                        } else if (journalRequest.getName().equals(SnTable.ScRequest.get())) {
                            ScRequest scRequest = scRequestService.findByIntegrationId(journalRequest.getElement_id());
                            if (scRequest != null) {
                                journal.setScRequest(scRequest);
                                flag = scRequest.getCompany().getSolver();
                                company = util.getFieldDisplay(scRequest.getCompany());
                                element = util.getFieldDisplay(scRequest);
                            }
                        }

                        if (flag) {
                            journalRequests.add(journalRequest);
                            journal.setOrigin(journalRequest.getElement());
                            journal.setElement(journalRequest.getElement_id());
                            journal.setValue(util.reeplaceImg(journalRequest.getValue().trim()));
                            journal.setCreatedOn(journalRequest.getSys_created_on());
                            journal.setIntegrationId(journalRequest.getSys_id());
                            journal.setActive(true);
                            if (util.hasData(journalRequest.getSys_created_by())) {
                                SysUser openedBy = sysUserService.findByUserName(journalRequest.getSys_created_by());
                                if (openedBy != null)
                                    journal.setCreateBy(openedBy);
                            }

                            String tagAction = app.CreateConsole();
                            Journal exists = journalService.findByIntegrationId(journal.getIntegrationId());
                            if (exists != null) {
                                journal.setId(exists.getId());
                                tagAction = app.UpdateConsole();
                            }
                            util.printData(tag, count[0], tagAction.concat(util.getFieldDisplay(journal)), element, company);


                            journalService.save(journal);


                            count[0] = count[0] + 1;
                        }
                    } catch (Exception e) {
                        System.out.println(tag.concat("Exception (I) : ").concat(String.valueOf(e)));
                    }
                });

                apiResponse = mapper.readValue(result, APIResponse.class);
                APIExecutionStatus status = new APIExecutionStatus();
                status.setUri(endPointSN.Location());
                status.setUserAPI(app.SNUser());
                status.setPasswordAPI(app.SNPassword());
                status.setError(apiResponse.getError());
                status.setMessage(apiResponse.getMessage());
                endTime = (System.currentTimeMillis() - startTime);
                status.setExecutionTime(endTime);
                statusService.save(status);
            }

        } catch (Exception e) {
            System.out.println(tag.concat("Exception (II) : ").concat(String.valueOf(e)));
        }
        System.out.println(app.End());
        return journalRequests;
    }


    @GetMapping("/journalsBySolverAndQueryCreate")
    public List<JournalRequest> show(String query, boolean flagQuery, boolean flagA) {
        System.out.println(app.Start());
        APIResponse apiResponse = null;
        List<JournalRequest> journalRequests = new ArrayList<>();
        String[] sparmOffSets = util.offSets1500000();
        long startTime = 0;
        long endTime = 0;
        String tag = "[Journal] ";
        try {
            Rest rest = new Rest();
            startTime = System.currentTimeMillis();
            final int[] count = {1};
            for (String sparmOffSet : sparmOffSets) {

                String result = rest.responseByEndPoint(endPointSN.JournalByQuery().replace("QUERY", query).concat(sparmOffSet));
                System.out.println(tag.concat("(".concat(endPointSN.JournalByQuery().replace("QUERY", query).concat(sparmOffSet))));
                ObjectMapper mapper = new ObjectMapper();
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

                JSONParser parser = new JSONParser();
                JSONObject resultJson = new JSONObject();
                JSONArray ListSnJournalJson = new JSONArray();
                resultJson = (JSONObject) parser.parse(result);
                if (resultJson.get("result") != null)
                    ListSnJournalJson = (JSONArray) parser.parse(resultJson.get("result").toString());

                ListSnJournalJson.stream().forEach(snJournalJson -> {


                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    JournalRequest journalRequest = new JournalRequest();
                    boolean flag = false;
                    try {
                        Journal journal = new Journal();
                        String tagAction = app.CreateConsole();
                        journalRequest = objectMapper.readValue(snJournalJson.toString(), JournalRequest.class);
                        Journal exists = journalService.findByIntegrationId(journalRequest.getSys_id());
                        if (exists != null) {
                            journal.setId(exists.getId());
                            tagAction = app.UpdateConsole();
                        } else {




                            String element = "";
                            String company = "";
                            if (journalRequest.getName().equals(SnTable.Incident.get())) {
                                Incident incident = incidentService.findByIntegrationId(journalRequest.getElement_id());
                                if (incident != null) {
                                    journal.setIncident(incident);
                                    flag = incident.getCompany().getSolver();
                                    company = util.getFieldDisplay(incident.getCompany());
                                    element = util.getFieldDisplay(incident);
                                }
                            } else if (journalRequest.getName().equals(SnTable.ScRequestItem.get())) {
                                ScRequestItem scRequestItem = scRequestItemService.findByIntegrationId(journalRequest.getElement_id());
                                if (scRequestItem != null) {
                                    journal.setScRequestItem(scRequestItem);
                                    flag = scRequestItem.getCompany().getSolver();
                                    company = util.getFieldDisplay(scRequestItem.getCompany());
                                    element = util.getFieldDisplay(scRequestItem);
                                }
                            } else if (journalRequest.getName().equals(SnTable.ScRequest.get())) {
                                ScRequest scRequest = scRequestService.findByIntegrationId(journalRequest.getElement_id());
                                if (scRequest != null) {
                                    journal.setScRequest(scRequest);
                                    flag = scRequest.getCompany().getSolver();
                                    company = util.getFieldDisplay(scRequest.getCompany());
                                    element = util.getFieldDisplay(scRequest);
                                }
                            }

                            if (flag) {
                                journalRequests.add(journalRequest);
                                journal.setOrigin(journalRequest.getElement());
                                journal.setElement(journalRequest.getElement_id());
                                journal.setValue(util.reeplaceImg(journalRequest.getValue().trim()));
                                journal.setCreatedOn(journalRequest.getSys_created_on());
                                journal.setIntegrationId(journalRequest.getSys_id());
                                journal.setActive(true);
                                if (util.hasData(journalRequest.getSys_created_by())) {
                                    SysUser openedBy = sysUserService.findByUserName(journalRequest.getSys_created_by());
                                    if (openedBy != null)
                                        journal.setCreateBy(openedBy);
                                }


                                util.printData(tag, count[0], tagAction.concat(util.getFieldDisplay(journal)), element, company);


                                journalService.save(journal);


                                count[0] = count[0] + 1;
                            }
                        }
                    } catch (Exception e) {
                      // System.out.println(tag.concat("Exception (I) : ").concat(String.valueOf(e)));
                    }
                });

                apiResponse = mapper.readValue(result, APIResponse.class);
                APIExecutionStatus status = new APIExecutionStatus();
                status.setUri(endPointSN.Location());
                status.setUserAPI(app.SNUser());
                status.setPasswordAPI(app.SNPassword());
                status.setError(apiResponse.getError());
                status.setMessage(apiResponse.getMessage());
                endTime = (System.currentTimeMillis() - startTime);
                status.setExecutionTime(endTime);
                statusService.save(status);
            }

        } catch (Exception e) {
           // System.out.println(tag.concat("Exception (II) : ").concat(String.valueOf(e)));
        }
        System.out.println(app.End());
        return journalRequests;
    }

    @GetMapping("/journalsSNByElement")
    public List<JournalRequest> findJournalsSNByElement(String elementId) {
        System.out.println(app.Start());
        APIResponse apiResponse = null;
        List<JournalRequest> journalRequests = new ArrayList<>();
        long startTime = 0;
        long endTime = 0;
        String tag = "[Journal] ";
        try {
            Rest rest = new Rest();
            startTime = System.currentTimeMillis();
            final int[] count = {1};


            String result = rest.responseByEndPoint(endPointSN.JournalByElement().concat(elementId));
            System.out.println(tag.concat("(".concat(endPointSN.JournalByElement().concat(elementId)).concat(")")));
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            List<Attachment> attachments = attachmentService.findByElement(elementId);
            JSONParser parser = new JSONParser();
            JSONObject resultJson = new JSONObject();
            JSONArray ListSnJournalJson = new JSONArray();
            resultJson = (JSONObject) parser.parse(result);
            if (resultJson.get("result") != null)
                ListSnJournalJson = (JSONArray) parser.parse(resultJson.get("result").toString());

            ListSnJournalJson.stream().forEach(snJournalJson -> {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                JournalRequest journalRequest = new JournalRequest();
                boolean flag = false;
                try {
                    journalRequest = objectMapper.readValue(snJournalJson.toString(), JournalRequest.class);
                    Journal journal = new Journal();
                    String element = "";
                    String company = "";
                    if (journalRequest.getName().equals(SnTable.Incident.get())) {
                        Incident incident = incidentService.findByIntegrationId(journalRequest.getElement_id());
                        if (incident != null) {
                            journal.setIncident(incident);
                            flag = incident.getCompany().getSolver();
                            company = util.getFieldDisplay(incident.getCompany());
                            element = util.getFieldDisplay(incident);
                        }
                    } else if (journalRequest.getName().equals(SnTable.ScRequestItem.get())) {
                        ScRequestItem scRequestItem = scRequestItemService.findByIntegrationId(journalRequest.getElement_id());
                        if (scRequestItem != null) {
                            journal.setScRequestItem(scRequestItem);
                            flag = scRequestItem.getCompany().getSolver();
                            company = util.getFieldDisplay(scRequestItem.getCompany());
                            element = util.getFieldDisplay(scRequestItem);
                        }
                    } else if (journalRequest.getName().equals(SnTable.ScRequest.get())) {
                        ScRequest scRequest = scRequestService.findByIntegrationId(journalRequest.getElement_id());
                        if (scRequest != null) {
                            journal.setScRequest(scRequest);
                            flag = scRequest.getCompany().getSolver();
                            company = util.getFieldDisplay(scRequest.getCompany());
                            element = util.getFieldDisplay(scRequest);
                        }
                    }

                    if (flag) {
                        journalRequests.add(journalRequest);
                        journal.setOrigin(journalRequest.getElement());
                        journal.setElement(journalRequest.getElement_id());
                        journal.setValue(util.reeplaceImg(journalRequest.getValue().trim()));
                        Pattern p = Pattern.compile(".*\\<[^>]+>.*");
                        Matcher m = p.matcher(journal.getValue());
                        if (m.find() && !journal.getReviewed()) {
                            attachments.forEach(attachment -> {
                                journal.setParse(true);
                                journal.setReviewed(true);
                                String master = journal.getValue();
                                String target = endPointSN.SrcAttachment().concat(attachment.getIntegrationId());
                                String replacement = environment.getProperty("setting.url.api.img").concat(":").concat(environment.getProperty("server.port")).concat(environment.getProperty("setting.api.version")).concat("/").concat("snAttachment/").concat(attachment.getId().toString());
                                int startIndex = master.indexOf(target);
                                int stopIndex = startIndex + target.length();
                                StringBuilder builder = new StringBuilder(master);
                                if (startIndex != -1) {
                                    builder.replace(startIndex, stopIndex, replacement);
                                    journal.setValue(builder.toString());
                                }
                            });
                        }
                        journal.setCreatedOn(journalRequest.getSys_created_on());
                        journal.setIntegrationId(journalRequest.getSys_id());
                        journal.setActive(true);
                        if (util.hasData(journalRequest.getSys_created_by())) {
                            SysUser openedBy = sysUserService.findByUserName(journalRequest.getSys_created_by());
                            if (openedBy != null)
                                journal.setCreateBy(openedBy);
                        }

                        String tagAction = app.CreateConsole();
                        Journal exists = journalService.findByIntegrationId(journal.getIntegrationId());
                        if (exists != null) {
                            journal.setId(exists.getId());
                            tagAction = app.UpdateConsole();
                        }
                        util.printData(tag, count[0], tagAction.concat(util.getFieldDisplay(journal)), element, company);


                        journalService.save(journal);


                        count[0] = count[0] + 1;
                    }
                } catch (Exception e) {
                    System.out.println(tag.concat("Exception (I) : ").concat(String.valueOf(e)));
                }
            });

            apiResponse = mapper.readValue(result, APIResponse.class);
            APIExecutionStatus status = new APIExecutionStatus();
            status.setUri(endPointSN.Location());
            status.setUserAPI(app.SNUser());
            status.setPasswordAPI(app.SNPassword());
            status.setError(apiResponse.getError());
            status.setMessage(apiResponse.getMessage());
            endTime = (System.currentTimeMillis() - startTime);
            status.setExecutionTime(endTime);
            statusService.save(status);


        } catch (Exception e) {
            System.out.println(tag.concat("Exception (II) : ").concat(String.valueOf(e)));
        }
        System.out.println(app.End());
        return journalRequests;
    }


    @GetMapping("/journal/{internalCheckpoint}")
    public ResponseEntity<?> findByInternalCheckpoint(@PathVariable String internalCheckpoint) {
        Journal journal = null;
        Map<String, Object> response = new HashMap<>();
        try {
            journal = journalService.findByInternalCheckpoint(internalCheckpoint);
        } catch (DataAccessException e) {
            response.put("mensaje", Errors.dataAccessExceptionQuery.get());
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (journal == null) {
            response.put("mensaje", "El registro ".concat(internalCheckpoint.concat(" no existe en la base de datos!")));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Journal>(journal, HttpStatus.OK);
    }
}


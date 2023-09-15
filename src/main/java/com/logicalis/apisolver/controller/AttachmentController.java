
package com.logicalis.apisolver.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.logicalis.apisolver.model.*;
import com.logicalis.apisolver.model.enums.*;
import com.logicalis.apisolver.model.utilities.AttachmentInfo;
import com.logicalis.apisolver.services.*;
import com.logicalis.apisolver.util.Rest;
import com.logicalis.apisolver.util.Util;
import com.logicalis.apisolver.view.AttachmentRequest;
import com.logicalis.apisolver.view.AttachmentSolver;
import org.apache.commons.io.FilenameUtils;
import org.hibernate.engine.jdbc.StreamUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.zip.Deflater;

@CrossOrigin(origins = {"${app.api.settings.cross-origin.urls}", "*"})
@RestController
@RequestMapping("/api/v1")
public class AttachmentController {

    @Autowired
    private IAttachmentService attachmentService;
    @Autowired
    private IIncidentService incidentService;
    @Autowired
    private IDomainService domainService;
    @Autowired
    private Environment environment;
    @Autowired
    private IScRequestItemService scRequestItemService;
    @Autowired
    private IScRequestService scRequestService;
    @Autowired
    private IScTaskService scTaskService;
    @Autowired
    private IAPIExecutionStatusService statusService;
    @Autowired
    private Rest rest;

    Util util = new Util();
    App app = new App();
    EndPointSN endPointSN = new EndPointSN();

    @GetMapping("/attachments")
    public List<Attachment> index() {
        return attachmentService.findAll();
    }

    @GetMapping("/attachmentsByIncident/{id}")
    public List<AttachmentInfo> findAttachmentsByIncident(@PathVariable Long id) {
        return attachmentService.findAttachmentsByIncident(id);
    }

    @GetMapping("/attachmentsByScRequestItem/{id}")
    public List<AttachmentInfo> findAttachmentsByScRequestItem(@PathVariable Long id) {
        return attachmentService.findAttachmentsByScRequestItem(id);
    }

    @GetMapping("/attachmentsByIncidentOrScRequestItem")
    public List<AttachmentInfo> findAttachmentsByIncidentOrScRequestItem(@NotNull @RequestParam(value = "incident", required = true, defaultValue = "") String incident,
                                                                         @NotNull @RequestParam(value = "scRequestItem", required = true, defaultValue = "") String scRequestItem) {
        return attachmentService.findAttachmentsByIncidentOrScRequestItem(incident, scRequestItem);
    }

    @GetMapping("/attachmentsByScTask/{id}")
    public List<AttachmentInfo> findAttachmentsByScTask(@PathVariable Long id) {
        return attachmentService.findAttachmentsByScTask(id);
    }

    @GetMapping("/attachment/{id}")
    public ResponseEntity<?> show(@PathVariable Long id) {
        Attachment attachment = null;
        Map<String, Object> response = new HashMap<>();
        try {
            attachment = attachmentService.findById(id);
        } catch (DataAccessException e) {
            response.put("mensaje", Errors.dataAccessExceptionQuery.get());
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (attachment == null) {
            response.put("mensaje", Messages.notExist.get(id.toString()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<Attachment>(attachment, HttpStatus.OK);
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/attachment")
    public ResponseEntity<?> create(@RequestBody Attachment attachment) {
        Attachment newAttachment = null;

        Map<String, Object> response = new HashMap<>();
        try {
            newAttachment = attachmentService.save(attachment);
        } catch (DataAccessException e) {
            response.put("mensaje", Errors.dataAccessExceptionInsert.get());
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", Messages.createOK.get());
        response.put("attachment", newAttachment);

        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }


    @PutMapping("/attachmentSN")
    public ResponseEntity<?> update(@RequestBody AttachmentRequest attachmentRequest) {
        Attachment currentAttachment = new Attachment();
        Attachment attachmentUpdated = null;
        Map<String, Object> response = new HashMap<>();

        String tagAction = app.CreateConsole();
        String tag = "[Attachment] ";

        if (currentAttachment == null) {
            response.put("mensaje", Errors.dataAccessExceptionUpdate.get(attachmentRequest.getSys_id()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }

        try {
            Attachment attachment = new Attachment();


            attachment.setIntegrationId(attachmentRequest.getSys_id());
            attachment.setDownloadLinkSN(endPointSN.ApiAttachment().concat("/").concat(attachmentRequest.getSys_id()).concat("/file"));
            attachment.setExtension(".".concat(FilenameUtils.getExtension(attachmentRequest.getFile_name())));
            attachment.setFileName(util.isNull(attachmentRequest.getFile_name()));
            attachment.setElement(util.isNull(attachmentRequest.getTable_sys_id()));
            attachment.setOrigin(util.isNull(attachmentRequest.getTable_name()));
            attachment.setContentType(util.isNull(attachmentRequest.getContent_type()));
            attachment.setSysCreatedBy(util.isNull(attachmentRequest.getSys_created_by()));
            attachment.setSysCreatedOn(util.isNull(attachmentRequest.getSys_created_on()));
            attachment.setSysUpdatedBy(util.isNull(attachmentRequest.getSys_updated_by()));
            attachment.setSysUpdatedOn(util.isNull(attachmentRequest.getSys_updated_on()));


            if (attachmentRequest.getTable_name().equals(SnTable.Incident.get())) {
                Incident incident = incidentService.findByIntegrationId(attachment.getElement());
                if (incident != null) {
                    attachment.setIncident(incident);
                    attachment.setDomain(incident.getCompany().getDomain());
                }
            } else if (attachmentRequest.getTable_name().equals(SnTable.ScRequestItem.get())) {
                ScRequestItem scRequestItem = scRequestItemService.findByIntegrationId(attachment.getElement());
                if (scRequestItem != null) {
                    attachment.setScRequestItem(scRequestItem);
                    attachment.setDomain(scRequestItem.getCompany().getDomain());
                    attachmentRequest.setTable_name(SnTable.ScRequestItem.get());
                }
            } else if (attachmentRequest.getTable_name().equals(SnTable.ScTask.get())) {
                ScTask scTask = scTaskService.findByIntegrationId(attachment.getElement());
                if (scTask != null) {
                    attachment.setScTask(scTask);
                    attachment.setDomain(scTask.getCompany().getDomain());
                }
            }
            if (attachment.getDomain() == null) {
                Domain domain = domainService.findByIntegrationId(attachmentRequest.getDomain());
                attachment.setDomain(domain);

            }

            RestTemplate restTemplate = rest.restTemplateServiceNow();
            File downloadFile = rest.responseFileByEndPointSOAsync(attachment, restTemplate, environment.getProperty("setting.attachments.dir"));
            String filePath = rest.getPathDirectory(attachment, environment.getProperty("setting.attachments.dir"));
            filePath = rest.getFilePathDirectory(attachment, filePath);
            attachment.setDownloadLink(filePath);
            Attachment exists = attachmentService.findByIntegrationId(attachment.getIntegrationId());
            if (exists != null) {
                attachment.setId(exists.getId());
                attachment.setDomain(exists.getDomain());
                tagAction = app.UpdateConsole();
            }

            attachmentUpdated = attachmentService.save(attachment);
            util.printData(tag, tagAction, util.getFieldDisplay(attachment), attachmentRequest.getTable_sys_id());

        } catch (DataAccessException e) {
            System.out.println("error " + e.getMessage());
            response.put("mensaje", Errors.dataAccessExceptionUpdate.get());
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", Messages.UpdateOK.get());
        response.put("attachment", attachmentUpdated);

        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }

    ScTask getScTask(AttachmentRequest attachment) {
        ScTask currentScTask = new ScTask();
        ScRequest currentScRequest;
        ScRequest scRequest = scRequestService.findByIntegrationId(attachment.getRequest_sys_id());
        if (scRequest == null) {
            scRequest = new ScRequest();
            scRequest.setIntegrationId(attachment.getRequest_sys_id());
            scRequest.setNumber(attachment.getRequest_number());
            currentScRequest = scRequestService.save(scRequest);
        } else {
            currentScRequest = scRequest;
        }
        currentScTask.setScRequest(currentScRequest);
        ScRequestItem currentScRequestItem;
        ScRequestItem scRequestItem = scRequestItemService.findByIntegrationId(attachment.getRequest_item_sys_id());
        if (scRequestItem == null) {
            scRequestItem = new ScRequestItem();
            scRequestItem.setIntegrationId(attachment.getRequest_item_sys_id());
            scRequestItem.setNumber(attachment.getRequest_item_number());
            scRequestItem.setScRequest(currentScRequest);
            currentScRequestItem = scRequestItemService.save(scRequestItem);
        } else {
            currentScRequestItem = scRequestItem;
        }
        currentScTask.setScRequestItem(currentScRequestItem);
        currentScTask.setIntegrationId(attachment.getTask_sys_id());
        currentScTask.setNumber(attachment.getTask_number());

        return scTaskService.save(currentScTask);

    }

    ScRequestItem getScRequestItem(AttachmentRequest attachment) {
        ScRequestItem currentScRequestItem = new ScRequestItem();
        ScRequest currentScRequest;
        ScRequest scRequest = scRequestService.findByIntegrationId(attachment.getRequest_sys_id());
        if (scRequest == null) {
            scRequest = new ScRequest();
            scRequest.setIntegrationId(attachment.getRequest_sys_id());
            scRequest.setNumber(attachment.getRequest_number());
            currentScRequest = scRequestService.save(scRequest);
        } else {
            currentScRequest = scRequest;
        }
        currentScRequestItem.setScRequest(currentScRequest);
        currentScRequestItem.setIntegrationId(attachment.getTable_sys_id());
        currentScRequestItem.setNumber(attachment.getRequest_item_number());
        return scRequestItemService.save(currentScRequestItem);
    }

    @Secured("ROLE_ADMIN")
    @PutMapping("/attachment/{id}")
    public ResponseEntity<?> update(@RequestBody Attachment attachment, @PathVariable Long id) {

        Attachment currentAttachment = attachmentService.findById(id);
        Attachment attachmentUpdated = null;

        Map<String, Object> response = new HashMap<>();

        if (currentAttachment == null) {
            response.put("mensaje", Errors.dataAccessExceptionUpdate.get(id.toString()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }

        try {
            currentAttachment.setFileName(attachment.getFileName());
            attachmentUpdated = attachmentService.save(currentAttachment);

        } catch (DataAccessException e) {
            response.put("mensaje", Errors.dataAccessExceptionUpdate.get());
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", Messages.UpdateOK.get());
        response.put("attachment", attachmentUpdated);

        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/attachment/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {

        Map<String, Object> response = new HashMap<>();
        try {
            attachmentService.delete(id);
        } catch (DataAccessException e) {
            response.put("mensaje", Errors.dataAccessExceptionDelete.get());
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", Messages.DeleteOK.get());
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
    }

    @GetMapping("/attachment/{integration_id}")
    public ResponseEntity<?> show(@PathVariable String integration_id) {
        Attachment attachment = null;
        Map<String, Object> response = new HashMap<>();
        try {
            attachment = attachmentService.findByIntegrationId(integration_id);
        } catch (DataAccessException e) {
            response.put("mensaje", Errors.dataAccessExceptionQuery.get());
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (attachment == null) {
            response.put("mensaje", "El registro ".concat(integration_id.concat(" no existe en la base de datos!")));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Attachment>(attachment, HttpStatus.OK);
    }


    @GetMapping("/findBySolver")
    public List<Attachment> show() {

        System.out.println(app.Start());
        final APIResponse[] apiResponse = {null};
        List<AttachmentSolver> snAttachments = new ArrayList<>();
        List<Attachment> attachments = new ArrayList<>();
        Util util = new Util();
        final long[] startTime = {0};
        final long[] endTime = {0};
        String tag = "[Attachment] ";
        final long MAXREGISTERS = incidentService.count();
        /*
        List<Incident> listInsident = new ArrayList<Incident>();
        final Integer PAGESIZE = 3000;
        for(int j = 0; j < MAXREGISTERS;j+=PAGESIZE) {
            //Pageable paging = PageRequest.of(i, pageSize, Sort.Direction.valueOf("getIntegrationId"));
            Pageable paging = PageRequest.of(j, PAGESIZE);
            Page<Incident> pagedResult = incidentService.findAll(paging);
            if (pagedResult.hasContent()) {
                listInsident = pagedResult.getContent();
            } else {
                listInsident = null;
            }
            listInsident = incidentService.findAll();
            listInsident.forEach(i -> {
        */
            incidentService.findAll().forEach(i -> {
                try {
                    RestTemplate restTemplate = rest.restTemplateServiceNow();
                    startTime[0] = System.currentTimeMillis();
                    String result = rest.responseByEndPoint(endPointSN.Attachment().concat(i.getIntegrationId()));
                    endTime[0] = (System.currentTimeMillis() - startTime[0]);
                    ObjectMapper mapper = new ObjectMapper();
                    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    JSONParser parser = new JSONParser();
                    JSONObject resultJson = (JSONObject) parser.parse(result);
                    JSONArray ListSnAttachmentJson = new JSONArray();
                    if (resultJson.get("result") != null)
                        ListSnAttachmentJson = (JSONArray) parser.parse(resultJson.get("result").toString());
                    final int[] count = {1};
                    ListSnAttachmentJson.forEach(snAttachmentJson -> {
                        String tagAction = app.CreateConsole();
                        ObjectMapper objectMapper = new ObjectMapper();
                        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                        try {
                            AttachmentSolver attachmentSolver = objectMapper.readValue(snAttachmentJson.toString(), AttachmentSolver.class);
                            Attachment attachment = new Attachment();
                            attachment.setActive(attachmentSolver.getActive());
                            attachment.setIntegrationId(attachmentSolver.getSys_id());
                            attachment.setDownloadLinkSN(attachmentSolver.getDownload_link());
                            attachment.setExtension(".".concat(attachmentSolver.getFile_name().substring(Math.max(0, attachmentSolver.getFile_name().length() - 5)).split("\\s*[.]+")[1]));
                            attachment.setFileName(attachmentSolver.getFile_name());
                            attachment.setElement(attachmentSolver.getTable_sys_id());
                            attachment.setOrigin(attachmentSolver.getTable_name());
                            attachment.setContentType(attachmentSolver.getContent_type());
                            attachment.setSysCreatedBy(attachmentSolver.getSys_created_by());
                            attachment.setSysCreatedOn(attachmentSolver.getSys_created_on());
                            attachment.setSysUpdatedBy(attachmentSolver.getSys_updated_by());
                            attachment.setSysUpdatedOn(attachmentSolver.getSys_updated_on());

                            Domain domain = getDomainByIntegrationId((JSONObject) snAttachmentJson, SnTable.Domain.get(), app.Value());
                            if (domain != null)
                                attachment.setDomain(domain);

                            switch (attachment.getOrigin()) {
                                case "incident":
                                    Incident incident = incidentService.findByIntegrationId(attachment.getElement());
                                    if (incident != null)
                                        attachment.setIncident(incident);
                                    break;
                                case "sc_req_item":
                                    ScRequestItem scRequestItem = scRequestItemService.findByIntegrationId(attachment.getElement());
                                    if (scRequestItem != null)
                                        attachment.setScRequestItem(scRequestItem);
                                    break;
                                case "sc_task":
                                    ScTask scTask = scTaskService.findByIntegrationId(attachment.getElement());
                                    if (scTask != null)
                                        attachment.setScTask(scTask);
                                    break;
                            }


                            Attachment exists = attachmentService.findByIntegrationId(attachment.getIntegrationId());
                            if (exists != null) {
                                attachment.setId(exists.getId());
                                tagAction = app.UpdateConsole();
                            }
                            attachmentService.save(attachment);
                            snAttachments.add(attachmentSolver);
                            util.printData(tag, count[0], tagAction.concat(util.getFieldDisplay(attachment)), util.getFieldDisplay(domain));


                            count[0] = count[0] + 1;
                        } catch (JsonProcessingException e) {
                            System.out.println(tag.concat("JsonProcessingException (I) : ").concat(String.valueOf(e)));
                        }
                    });

                    HttpHeaders headers = new HttpHeaders();
                    headers.add("Content-Disposition", "attachments");

                    //return ResponseEntity.ok().headers(headers).body(attachments);
                    apiResponse[0] = mapper.readValue(result, APIResponse.class);
                    APIExecutionStatus status = new APIExecutionStatus();
                    status.setUri(endPointSN.Incident());
                    status.setUserAPI(app.SNUser());
                    status.setPasswordAPI(app.SNPassword());
                    status.setError(apiResponse[0].getError());
                    status.setMessage(apiResponse[0].getMessage());
                    status.setExecutionTime(endTime[0]);
                    statusService.save(status);
                } catch (Exception e) {
                    System.out.println(tag.concat("Exception (I) : ").concat(String.valueOf(e)));
                }
            });
        //}
        return attachments;
    }

    @GetMapping("/findBySolverByQuery")
    public List<Attachment> show(String query, boolean flagQuery) {

        System.out.println(app.Start());
        final APIResponse[] apiResponse = {null};
        List<AttachmentSolver> snAttachments = new ArrayList<>();
        List<Attachment> attachments = new ArrayList<>();
        Util util = new Util();
        final long[] startTime = {0};
        final long[] endTime = {0};
        String tag = "[Attachment] ";
        String[] sparmOffSets = util.offSets1500000();

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        incidentService.findAll().forEach(i -> {
            try {
                RestTemplate restTemplate = rest.restTemplateServiceNow();
                startTime[0] = System.currentTimeMillis();
                String result = "";

                for (String sparmOffSet : sparmOffSets) {
                    result = rest.responseByEndPoint(endPointSN.AttachmentByQuery().replace("QUERY", query).concat(sparmOffSet));
                    endTime[0] = (System.currentTimeMillis() - startTime[0]);

                    JSONParser parser = new JSONParser();
                    JSONObject resultJson = (JSONObject) parser.parse(result);
                    JSONArray ListSnAttachmentJson = new JSONArray();
                    if (resultJson.get("result") != null)
                        ListSnAttachmentJson = (JSONArray) parser.parse(resultJson.get("result").toString());
                    final int[] count = {1};
                    ListSnAttachmentJson.forEach(snAttachmentJson -> {
                        String tagAction = app.CreateConsole();
                        ObjectMapper objectMapper = new ObjectMapper();
                        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                        try {


                            Gson gson = new Gson();
                            AttachmentSolver attachmentSolver = gson.fromJson(snAttachmentJson.toString(), AttachmentSolver.class);


                            Attachment attachment = new Attachment();
                            attachment.setActive(attachmentSolver.getActive());
                            attachment.setIntegrationId(attachmentSolver.getSys_id());
                            attachment.setDownloadLinkSN(attachmentSolver.getDownload_link());
                            attachment.setExtension(".".concat(attachmentSolver.getFile_name().substring(Math.max(0, attachmentSolver.getFile_name().length() - 5)).split("\\s*[.]+")[1]));
                            attachment.setFileName(attachmentSolver.getFile_name());
                            attachment.setElement(attachmentSolver.getTable_sys_id());
                            attachment.setOrigin(attachmentSolver.getTable_name());
                            attachment.setContentType(attachmentSolver.getContent_type());
                            attachment.setSysCreatedBy(attachmentSolver.getSys_created_by());
                            attachment.setSysCreatedOn(attachmentSolver.getSys_created_on());
                            attachment.setSysUpdatedBy(attachmentSolver.getSys_updated_by());
                            attachment.setSysUpdatedOn(attachmentSolver.getSys_updated_on());

                            Domain domain = getDomainByIntegrationId((JSONObject) snAttachmentJson, SnTable.Domain.get(), app.Value());
                            if (domain != null)
                                attachment.setDomain(domain);

                            switch (attachment.getOrigin()) {
                                case "incident":
                                    Incident incident = incidentService.findByIntegrationId(attachment.getElement());
                                    if (incident != null)
                                        attachment.setIncident(incident);
                                    break;
                                case "sc_req_item":
                                    ScRequestItem scRequestItem = scRequestItemService.findByIntegrationId(attachment.getElement());
                                    if (scRequestItem != null)
                                        attachment.setScRequestItem(scRequestItem);
                                    break;
                                case "sc_task":
                                    ScTask scTask = scTaskService.findByIntegrationId(attachment.getElement());
                                    if (scTask != null)
                                        attachment.setScTask(scTask);
                                    break;
                            }


                            Attachment exists = attachmentService.findByIntegrationId(attachment.getIntegrationId());
                            if (exists != null) {
                                attachment.setId(exists.getId());
                                tagAction = app.UpdateConsole();
                            }
                            attachmentService.save(attachment);
                            snAttachments.add(attachmentSolver);
                            util.printData(tag, count[0], tagAction.concat(util.getFieldDisplay(attachment)), util.getFieldDisplay(domain));


                            count[0] = count[0] + 1;


                        } catch (Exception e) {
                            System.out.println(tag.concat("JsonProcessingException (I) : ").concat(String.valueOf(e)));
                        }
                    });
                }
                HttpHeaders headers = new HttpHeaders();
                headers.add("Content-Disposition", "attachments");

                //return ResponseEntity.ok().headers(headers).body(attachments);
                apiResponse[0] = mapper.readValue(result, APIResponse.class);
                APIExecutionStatus status = new APIExecutionStatus();
                status.setUri(endPointSN.Incident());
                status.setUserAPI(app.SNUser());
                status.setPasswordAPI(app.SNPassword());
                status.setError(apiResponse[0].getError());
                status.setMessage(apiResponse[0].getMessage());
                status.setExecutionTime(endTime[0]);
                statusService.save(status);
            } catch (Exception e) {
                System.out.println(tag.concat("Exception (I) : ").concat(String.valueOf(e)));
            }
        });
        return attachments;
    }

    @GetMapping("/findBySolverByQueryCreate")
    public List<Attachment> show(String query, boolean flagQuery, boolean flagCreate) {

        System.out.println(app.Start());
        final APIResponse[] apiResponse = {null};
        List<AttachmentSolver> snAttachments = new ArrayList<>();
        List<Attachment> attachments = new ArrayList<>();
        Util util = new Util();
        final long[] startTime = {0};
        final long[] endTime = {0};
        String tag = "[Attachment] ";
        String[] sparmOffSets = util.offSets1500000();

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        incidentService.findAll().forEach(i -> {
            try {
                RestTemplate restTemplate = rest.restTemplateServiceNow();
                startTime[0] = System.currentTimeMillis();
                String result = "";

                for (String sparmOffSet : sparmOffSets) {
                    result = rest.responseByEndPoint(endPointSN.AttachmentByQuery().replace("QUERY", query).concat(sparmOffSet));
                    endTime[0] = (System.currentTimeMillis() - startTime[0]);

                    JSONParser parser = new JSONParser();
                    JSONObject resultJson = (JSONObject) parser.parse(result);
                    JSONArray ListSnAttachmentJson = new JSONArray();
                    if (resultJson.get("result") != null)
                        ListSnAttachmentJson = (JSONArray) parser.parse(resultJson.get("result").toString());
                    final int[] count = {1};
                    ListSnAttachmentJson.forEach(snAttachmentJson -> {
                        String tagAction = app.CreateConsole();
                        ObjectMapper objectMapper = new ObjectMapper();
                        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                        try {


                            Gson gson = new Gson();
                            AttachmentSolver attachmentSolver = gson.fromJson(snAttachmentJson.toString(), AttachmentSolver.class);


                            Attachment attachment = new Attachment();

                            Attachment exists = attachmentService.findByIntegrationId(attachment.getIntegrationId());
                            if (exists != null) {
                                attachment.setId(exists.getId());
                                tagAction = app.UpdateConsole();
                            } else {
                                attachment.setActive(attachmentSolver.getActive());
                                attachment.setIntegrationId(attachmentSolver.getSys_id());
                                attachment.setDownloadLinkSN(attachmentSolver.getDownload_link());
                                attachment.setExtension(".".concat(attachmentSolver.getFile_name().substring(Math.max(0, attachmentSolver.getFile_name().length() - 5)).split("\\s*[.]+")[1]));
                                attachment.setFileName(attachmentSolver.getFile_name());
                                attachment.setElement(attachmentSolver.getTable_sys_id());
                                attachment.setOrigin(attachmentSolver.getTable_name());
                                attachment.setContentType(attachmentSolver.getContent_type());
                                attachment.setSysCreatedBy(attachmentSolver.getSys_created_by());
                                attachment.setSysCreatedOn(attachmentSolver.getSys_created_on());
                                attachment.setSysUpdatedBy(attachmentSolver.getSys_updated_by());
                                attachment.setSysUpdatedOn(attachmentSolver.getSys_updated_on());

                                Domain domain = getDomainByIntegrationId((JSONObject) snAttachmentJson, SnTable.Domain.get(), app.Value());
                                if (domain != null)
                                    attachment.setDomain(domain);

                                switch (attachment.getOrigin()) {
                                    case "incident":
                                        Incident incident = incidentService.findByIntegrationId(attachment.getElement());
                                        if (incident != null)
                                            attachment.setIncident(incident);
                                        break;
                                    case "sc_req_item":
                                        ScRequestItem scRequestItem = scRequestItemService.findByIntegrationId(attachment.getElement());
                                        if (scRequestItem != null)
                                            attachment.setScRequestItem(scRequestItem);
                                        break;
                                    case "sc_task":
                                        ScTask scTask = scTaskService.findByIntegrationId(attachment.getElement());
                                        if (scTask != null)
                                            attachment.setScTask(scTask);
                                        break;
                                }


                                attachmentService.save(attachment);
                                snAttachments.add(attachmentSolver);
                                util.printData(tag, count[0], tagAction.concat(util.getFieldDisplay(attachment)), util.getFieldDisplay(domain));


                                count[0] = count[0] + 1;
                            }

                        } catch (Exception e) {
                            //    System.out.println(tag.concat("JsonProcessingException (I) : ").concat(String.valueOf(e)));
                        }
                    });
                }
                HttpHeaders headers = new HttpHeaders();
                headers.add("Content-Disposition", "attachments");

                //return ResponseEntity.ok().headers(headers).body(attachments);
                apiResponse[0] = mapper.readValue(result, APIResponse.class);
                APIExecutionStatus status = new APIExecutionStatus();
                status.setUri(endPointSN.Incident());
                status.setUserAPI(app.SNUser());
                status.setPasswordAPI(app.SNPassword());
                status.setError(apiResponse[0].getError());
                status.setMessage(apiResponse[0].getMessage());
                status.setExecutionTime(endTime[0]);
                statusService.save(status);
            } catch (Exception e) {
                //  System.out.println(tag.concat("Exception (I) : ").concat(String.valueOf(e)));
            }
        });
        return attachments;
    }

    @GetMapping("/findByElement/{integrationId}")
    public List<Attachment> findByElement(@PathVariable String integrationId) {

        System.out.println(app.Start());
        APIResponse apiResponse = null;
        List<AttachmentSolver> snAttachments = new ArrayList<>();
        List<Attachment> attachments = new ArrayList<>();
        Util util = new Util();
        long startTime = 0;
        long endTime = 0;
        String tag = "[Attachment] ";
        try {
            RestTemplate restTemplate = rest.restTemplateServiceNow();
            startTime = System.currentTimeMillis();
            String result = rest.responseByEndPoint(endPointSN.Attachment().concat(integrationId));
            endTime = (System.currentTimeMillis() - startTime);
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JSONParser parser = new JSONParser();
            JSONObject resultJson = (JSONObject) parser.parse(result);
            JSONArray ListSnAttachmentJson = new JSONArray();
            if (resultJson.get("result") != null)
                ListSnAttachmentJson = (JSONArray) parser.parse(resultJson.get("result").toString());
            final int[] count = {1};
            ListSnAttachmentJson.forEach(snAttachmentJson -> {
                String tagAction = app.CreateConsole();
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                try {
                    AttachmentSolver attachmentSolver = objectMapper.readValue(snAttachmentJson.toString(), AttachmentSolver.class);
                    Attachment attachment = new Attachment();
                    attachment.setActive(attachmentSolver.getActive());
                    attachment.setIntegrationId(attachmentSolver.getSys_id());
                    attachment.setDownloadLinkSN(attachmentSolver.getDownload_link());
                    attachment.setExtension(".".concat(attachmentSolver.getFile_name().substring(Math.max(0, attachmentSolver.getFile_name().length() - 5)).split("\\s*[.]+")[1]));
                    attachment.setFileName(attachmentSolver.getFile_name());
                    attachment.setElement(attachmentSolver.getTable_sys_id());
                    attachment.setOrigin(attachmentSolver.getTable_name());
                    attachment.setContentType(attachmentSolver.getContent_type());
                    attachment.setSysCreatedBy(attachmentSolver.getSys_created_by());
                    attachment.setSysCreatedOn(attachmentSolver.getSys_created_on());
                    attachment.setSysUpdatedBy(attachmentSolver.getSys_updated_by());
                    attachment.setSysUpdatedOn(attachmentSolver.getSys_updated_on());

                    Domain domain = getDomainByIntegrationId((JSONObject) snAttachmentJson, SnTable.Domain.get(), app.Value());

                    attachment.setDomain(getDomainByIntegrationId((JSONObject) snAttachmentJson, SnTable.Domain.get(), app.Value()));

                    switch (attachment.getOrigin()) {
                        case "incident":
                            Incident incident = incidentService.findByIntegrationId(attachment.getElement());
                            if (incident != null)
                                attachment.setIncident(incident);
                            break;
                        case "sc_req_item":
                            ScRequestItem scRequestItem = scRequestItemService.findByIntegrationId(attachment.getElement());
                            if (scRequestItem != null)
                                attachment.setScRequestItem(scRequestItem);
                            break;
                        case "sc_task":
                            ScTask scTask = scTaskService.findByIntegrationId(attachment.getElement());
                            if (scTask != null)
                                attachment.setScTask(scTask);
                            break;
                    }


                    Attachment exists = attachmentService.findByIntegrationId(attachment.getIntegrationId());
                    if (exists != null) {
                        attachment.setId(exists.getId());
                        tagAction = app.UpdateConsole();
                    }
                    attachmentService.save(attachment);
                    snAttachments.add(attachmentSolver);
                    util.printData(tag, count[0], tagAction.concat(util.getFieldDisplay(attachment)), util.getFieldDisplay(domain));


                    count[0] = count[0] + 1;
                } catch (JsonProcessingException e) {
                    System.out.println(tag.concat("JsonProcessingException (I) : ").concat(String.valueOf(e)));
                }
            });

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachments");

            //return ResponseEntity.ok().headers(headers).body(attachments);
            apiResponse = mapper.readValue(result, APIResponse.class);
            APIExecutionStatus status = new APIExecutionStatus();
            status.setUri(endPointSN.Incident());
            status.setUserAPI(app.SNUser());
            status.setPasswordAPI(app.SNPassword());
            status.setError(apiResponse.getError());
            status.setMessage(apiResponse.getMessage());
            status.setExecutionTime(endTime);
            statusService.save(status);
        } catch (Exception e) {
            System.out.println(tag.concat("Exception (I) : ").concat(String.valueOf(e)));
        }
        return attachments;
    }


    @GetMapping("/findSolverByElement/{integrationId}")
    public List<Attachment> findSolverByElement(@PathVariable String integrationId) {


        List<Attachment> attachments = new ArrayList<>();
        Map<String, Object> response = new HashMap<>();

/*
        try {
            attachments = attachmentService.findAttachmentsByScRequestItem(id);
        } catch (DataAccessException e) {
            response.put("mensaje", Errors.dataAccessExceptionQuery.get());
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (attachment == null) {
            response.put("mensaje", Messages.notExist.get(id.toString()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<Attachment>(attachment, HttpStatus.OK);


        System.out.println(app.Start());
        APIResponse apiResponse = null;
        List<AttachmentSolver> snAttachments = new ArrayList<>();
        List<Attachment> attachments = new ArrayList<>();
        Util util = new Util();
        long startTime = 0;
        long endTime = 0;
        String tag = "[Attachment] ";
        try {
            RestTemplate restTemplate = rest.restTemplate();
            startTime = System.currentTimeMillis();
            String result = rest.responseByEndPoint(endPointSN.Attachment().concat(integrationId));
            endTime = (System.currentTimeMillis() - startTime);
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JSONParser parser = new JSONParser();
            JSONObject resultJson = (JSONObject) parser.parse(result);
            JSONArray ListSnAttachmentJson = new JSONArray();
            if (resultJson.get("result") != null)
                ListSnAttachmentJson = (JSONArray) parser.parse(resultJson.get("result").toString());
            final int[] count = {1};
            ListSnAttachmentJson.forEach(snAttachmentJson -> {
                String tagAction = app.CreateConsole();
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                try {
                    AttachmentSolver attachmentSolver = objectMapper.readValue(snAttachmentJson.toString(), AttachmentSolver.class);
                    Attachment attachment = new Attachment();
                    attachment.setActive(attachmentSolver.getActive());
                    attachment.setIntegrationId(attachmentSolver.getSys_id());
                    attachment.setDownloadLinkSN(attachmentSolver.getDownload_link());
                    attachment.setExtension(".".concat(attachmentSolver.getFile_name().substring(Math.max(0, attachmentSolver.getFile_name().length() - 5)).split("\\s*[.]+")[1]));
                    attachment.setFileName(attachmentSolver.getFile_name());
                    attachment.setElement(attachmentSolver.getTable_sys_id());
                    attachment.setOrigin(attachmentSolver.getTable_name());
                    attachment.setContentType(attachmentSolver.getContent_type());
                    attachment.setSysCreatedBy(attachmentSolver.getSys_created_by());
                    attachment.setSysCreatedOn(attachmentSolver.getSys_created_on());
                    attachment.setSysUpdatedBy(attachmentSolver.getSys_updated_by());
                    attachment.setSysUpdatedOn(attachmentSolver.getSys_updated_on());

                    Domain domain = getDomainByIntegrationId((JSONObject) snAttachmentJson, SnTable.Domain.get(), app.Value());

                    attachment.setDomain(getDomainByIntegrationId((JSONObject) snAttachmentJson, SnTable.Domain.get(), app.Value()));

                    switch (attachment.getOrigin()) {
                        case "incident":
                            Incident incident = incidentService.findByIntegrationId(attachment.getElement());
                            if (incident != null)
                                attachment.setIncident(incident);
                            break;
                        case "sc_req_item":
                            ScRequestItem scRequestItem = scRequestItemService.findByIntegrationId(attachment.getElement());
                            if (scRequestItem != null)
                                attachment.setScRequestItem(scRequestItem);
                            break;
                        case "sc_task":
                            ScTask scTask = scTaskService.findByIntegrationId(attachment.getElement());
                            if (scTask != null)
                                attachment.setScTask(scTask);
                            break;
                    }


                    Attachment exists = attachmentService.findByIntegrationId(attachment.getIntegrationId());
                    if (exists != null) {
                        attachment.setId(exists.getId());
                        tagAction = app.UpdateConsole();
                    }
                    attachmentService.save(attachment);
                    snAttachments.add(attachmentSolver);
                    util.printData(tag, count[0], tagAction.concat(util.getFieldDisplay(attachment)), util.getFieldDisplay(domain));


                    count[0] = count[0] + 1;
                } catch (JsonProcessingException e) {
                    System.out.println(tag.concat("JsonProcessingException (I) : ").concat(String.valueOf(e)));
                }
            });

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachments");

            //return ResponseEntity.ok().headers(headers).body(attachments);
            apiResponse = mapper.readValue(result, APIResponse.class);
            APIExecutionStatus status = new APIExecutionStatus();
            status.setUri(endPointSN.Incident());
            status.setUserAPI(app.SNUser());
            status.setPasswordAPI(app.SNPassword());
            status.setError(apiResponse.getError());
            status.setMessage(apiResponse.getMessage());
            status.setExecutionTime(endTime);
            statusService.save(status);
        } catch (Exception e) {
            System.out.println(tag.concat("Exception (I) : ").concat(String.valueOf(e)));
        }*/
        return attachments;
    }

    @PostMapping("/uploadFile/{element}")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file, @PathVariable String element) {
        Map<String, Object> response = new HashMap<>();
        try {
            String type = element.split(",")[1];
            element = element.split(",")[0];
            String tag = "[Attachment] ";
            String tagAction = app.CreateConsole();
            if (!file.isEmpty() && element != null) {

                Attachment current = new Attachment();
                Attachment attachment;
                String filename = file.getOriginalFilename();

                current.setExtension(".".concat(FilenameUtils.getExtension(filename)));
                current.setFileName(filename);
                current.setElement(element);
                current.setOrigin(type);

                attachmentService.save(current);
                util.printData(tag, tagAction.concat("BD"), util.getFieldDisplay(current), current.getElement());

                attachment = rest.sendFileToServiceNow(element, file, type, file.getOriginalFilename());
                if (attachment != null)
                    util.printData(tag, tagAction.concat("(ServiceNow)"), util.getFieldDisplay(current), current.getElement());

                attachment.setId(current.getId());
                attachment.setExtension(current.getExtension());
                attachment.setFileName(current.getFileName());
                attachment.setElement(current.getElement());
                attachment.setOrigin(current.getOrigin());
                Domain domain = domainService.findByIntegrationId(attachment.getDomain().getIntegrationId());
                attachment.setDomain(domain);
                String pathDirectory = rest.generatePathDirectory(attachment, environment.getProperty("setting.attachments.dir").replaceAll("//", File.separator));

                InputStream initialStream = file.getInputStream();
                byte[] buffer = new byte[initialStream.available()];
                initialStream.read(buffer);
                File targetFile = new File(pathDirectory);

                try (OutputStream outStream = new FileOutputStream(targetFile)) {
                    outStream.write(buffer);
                    outStream.close();
                    util.printData(tag, tagAction.concat("(SO)"), util.getFieldDisplay(current), current.getElement());
                    attachment.setDownloadLink(pathDirectory);
                }

                tagAction = app.UpdateConsole();
                attachmentService.save(attachment);
                util.printData(tag, tagAction.concat("(BD)"), util.getFieldDisplay(attachment), attachment.getElement());
            }

        } catch (DataAccessException | IOException e) {
            response.put("mensaje", "Error upload attachment");
            response.put("error", e.getMessage().concat(": ").concat(e.getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", "Upload attachment");
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
    }

    public static byte[] compressBytes(byte[] data) {
        Deflater deflater = new Deflater();
        deflater.setInput(data);
        deflater.finish();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] buffer = new byte[1024];
        while (!deflater.finished()) {
            int count = deflater.deflate(buffer);
            outputStream.write(buffer, 0, count);
        }
        try {
            outputStream.close();
        } catch (IOException e) {
        }
        System.out.println("Compressed Image Byte Size - " + outputStream.toByteArray().length);

        return outputStream.toByteArray();
    }

    public Domain getDomainByIntegrationId(JSONObject jsonObject, String levelOne, String levelTwo) {
        String integrationId = util.getIdByJson(jsonObject, levelOne, levelTwo);
        if (util.hasData(integrationId)) {
            return domainService.findByIntegrationId(integrationId);
        } else
            return null;
    }
}


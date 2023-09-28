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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.io.*;
import java.util.*;
import java.util.zip.Deflater;

@CrossOrigin(origins = {"${app.api.settings.cross-origin.urls}", "*"})
@RestController
@RequestMapping("/api/v1")
@Slf4j
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
        String tagAction = App.CreateConsole();
        String tag = "[Attachment] ";
        if (currentAttachment == null) {
            response.put("mensaje", Errors.dataAccessExceptionUpdate.get(attachmentRequest.getSys_id()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }
        try {
            Attachment attachment = new Attachment();
            attachment.setIntegrationId(attachmentRequest.getSys_id());
            attachment.setDownloadLinkSN(EndPointSN.ApiAttachment().concat("/").concat(attachmentRequest.getSys_id()).concat("/file"));
            attachment.setExtension(".".concat(FilenameUtils.getExtension(attachmentRequest.getFile_name())));
            attachment.setFileName(Util.isNull(attachmentRequest.getFile_name()));
            attachment.setElement(Util.isNull(attachmentRequest.getTable_sys_id()));
            attachment.setOrigin(Util.isNull(attachmentRequest.getTable_name()));
            attachment.setContentType(Util.isNull(attachmentRequest.getContent_type()));
            attachment.setSysCreatedBy(Util.isNull(attachmentRequest.getSys_created_by()));
            attachment.setSysCreatedOn(Util.isNull(attachmentRequest.getSys_created_on()));
            attachment.setSysUpdatedBy(Util.isNull(attachmentRequest.getSys_updated_by()));
            attachment.setSysUpdatedOn(Util.isNull(attachmentRequest.getSys_updated_on()));
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
                tagAction = App.UpdateConsole();
            }

            attachmentUpdated = attachmentService.save(attachment);
            Util.printData(tag, tagAction, Util.getFieldDisplay(attachment), attachmentRequest.getTable_sys_id());

        } catch (DataAccessException e) {
            log.error("error " + e.getMessage());
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
        log.info(App.Start());
        final APIResponse[] apiResponse = {null};
        List<AttachmentSolver> snAttachments = new ArrayList<>();
        List<Attachment> attachments = new ArrayList<>();
        final long[] startTime = {0};
        final long[] endTime = {0};
        String tag = "[Attachment] ";
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachments");
        //final long MAXREGISTERS = incidentService.count();
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
                ObjectMapper mapper = new ObjectMapper();
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                JSONParser parser = new JSONParser();
                final JSONArray[] ListSnAttachmentJson = {new JSONArray()};
                final int[] count = {0};
                final Attachment[] attachment = {new Attachment()};
                final Attachment[] exists = {new Attachment()};
                final String[] result = new String[1];
                final AttachmentSolver[] attachmentSolver = new AttachmentSolver[1];
                final String[] tagAction = new String[1];
                final Domain[] domain = new Domain[1];
                final JSONObject[] resultJson = new JSONObject[1];
                final ScRequestItem[] scRequestItem = new ScRequestItem[1];
                final ScTask[] scTask = new ScTask[1];
                APIExecutionStatus status = new APIExecutionStatus();
                final Incident[] incident = new Incident[1];
                incidentService.findAll().forEach(i -> {
                try {
                    startTime[0] = System.currentTimeMillis();
                    result[0] = rest.responseByEndPoint(EndPointSN.Attachment().concat(i.getIntegrationId()));
                    endTime[0] = (System.currentTimeMillis() - startTime[0]);
                    resultJson[0] = (JSONObject) parser.parse(result[0]);
                    if (resultJson[0].get("result") != null)
                        ListSnAttachmentJson[0] = (JSONArray) parser.parse(resultJson[0].get("result").toString());
                    count[0] = 1;
                    ListSnAttachmentJson[0].forEach(snAttachmentJson -> {
                        tagAction[0] = App.CreateConsole();
                        try {
                            //AttachmentSolver attachmentSolver = objectMapper.readValue(snAttachmentJson.toString(), AttachmentSolver.class);
                            attachmentSolver[0] = mapper.readValue(snAttachmentJson.toString(), AttachmentSolver.class);
                            attachment[0] = new Attachment();
                            attachment[0].setActive(attachmentSolver[0].getActive());
                            attachment[0].setIntegrationId(attachmentSolver[0].getSys_id());
                            attachment[0].setDownloadLinkSN(attachmentSolver[0].getDownload_link());
                            attachment[0].setExtension(".".concat(attachmentSolver[0].getFile_name().substring(Math.max(0, attachmentSolver[0].getFile_name().length() - 5)).split("\\s*[.]+")[1]));
                            attachment[0].setFileName(attachmentSolver[0].getFile_name());
                            attachment[0].setElement(attachmentSolver[0].getTable_sys_id());
                            attachment[0].setOrigin(attachmentSolver[0].getTable_name());
                            attachment[0].setContentType(attachmentSolver[0].getContent_type());
                            attachment[0].setSysCreatedBy(attachmentSolver[0].getSys_created_by());
                            attachment[0].setSysCreatedOn(attachmentSolver[0].getSys_created_on());
                            attachment[0].setSysUpdatedBy(attachmentSolver[0].getSys_updated_by());
                            attachment[0].setSysUpdatedOn(attachmentSolver[0].getSys_updated_on());
                            domain[0] = getDomainByIntegrationId((JSONObject) snAttachmentJson, SnTable.Domain.get(), App.Value());
                            if (domain[0] != null)
                                attachment[0].setDomain(domain[0]);
                            switch (attachment[0].getOrigin()) {
                                case "incident":
                                    incident[0] = incidentService.findByIntegrationId(attachment[0].getElement());
                                    if (incident[0] != null)
                                        attachment[0].setIncident(incident[0]);
                                    break;
                                case "sc_req_item":
                                    scRequestItem[0] = scRequestItemService.findByIntegrationId(attachment[0].getElement());
                                    if (scRequestItem[0] != null)
                                        attachment[0].setScRequestItem(scRequestItem[0]);
                                    break;
                                case "sc_task":
                                    scTask[0] = scTaskService.findByIntegrationId(attachment[0].getElement());
                                    if (scTask[0] != null)
                                        attachment[0].setScTask(scTask[0]);
                                    break;
                            }
                            exists[0] = attachmentService.findByIntegrationId(attachment[0].getIntegrationId());
                            if (exists[0] != null) {
                                attachment[0].setId(exists[0].getId());
                                tagAction[0] = App.UpdateConsole();
                            }
                            attachmentService.save(attachment[0]);
                            snAttachments.add(attachmentSolver[0]);
                            Util.printData(tag, count[0], tagAction[0].concat(Util.getFieldDisplay(attachment[0])), Util.getFieldDisplay(domain[0]));
                            count[0] = count[0] + 1;
                        } catch (JsonProcessingException e) {
                            log.error(tag.concat("JsonProcessingException (I) : ").concat(String.valueOf(e)));
                        }
                    });
                    apiResponse[0] = mapper.readValue(result[0], APIResponse.class);
                    status.setUri(EndPointSN.Incident());
                    status.setUserAPI(App.SNUser());
                    status.setPasswordAPI(App.SNPassword());
                    status.setError(apiResponse[0].getError());
                    status.setMessage(apiResponse[0].getMessage());
                    status.setExecutionTime(endTime[0]);
                    statusService.save(status);
                } catch (Exception e) {
                    log.error(tag.concat("Exception (I) : ").concat(String.valueOf(e)));
                }
            });
        //}
        return attachments;
    }

    @GetMapping("/findBySolverByQuery")
    public List<Attachment> show(String query, boolean flagQuery) {
        log.info(App.Start());
        final APIResponse[] apiResponse = {null};
        List<AttachmentSolver> snAttachments = new ArrayList<>();
        List<Attachment> attachments = new ArrayList<>();
        final long[] startTime = {0};
        final long[] endTime = {0};
        String tag = "[Attachment] ";
        String[] sparmOffSets = Util.offSets1500000();
        Gson gson = new Gson();
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        APIExecutionStatus status = new APIExecutionStatus();
        JSONParser parser = new JSONParser();
        final JSONObject[] resultJson = new JSONObject[1];
        final String[] tagAction = new String[1];
        final Incident[] incident = new Incident[1];
        final Domain[] domain = new Domain[1];
        final ScRequestItem[] scRequestItem = new ScRequestItem[1];
        final int[] count = {1};
        final String[] result = {""};
        final ScTask[] scTask = new ScTask[1];
        final Attachment[] exists = {new Attachment()};
        final AttachmentSolver[] attachmentSolver = new AttachmentSolver[1];
        final Attachment[] attachment = {new Attachment()};
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        final JSONArray[] ListSnAttachmentJson = {new JSONArray()};
        incidentService.findAll().forEach(i -> {
            try {
                startTime[0] = System.currentTimeMillis();
                for (String sparmOffSet : sparmOffSets) {
                    result[0] = rest.responseByEndPoint(EndPointSN.AttachmentByQuery().replace("QUERY", query).concat(sparmOffSet));
                    endTime[0] = (System.currentTimeMillis() - startTime[0]);
                    resultJson[0] = (JSONObject) parser.parse(result[0]);
                    ListSnAttachmentJson[0].clear();
                    if (resultJson[0].get("result") != null)
                        ListSnAttachmentJson[0] = (JSONArray) parser.parse(resultJson[0].get("result").toString());
                    ListSnAttachmentJson[0].forEach(snAttachmentJson -> {
                        tagAction[0] = App.CreateConsole();
                        try {
                            attachmentSolver[0] = gson.fromJson(snAttachmentJson.toString(), AttachmentSolver.class);
                            attachment[0] = new Attachment();
                            attachment[0].setActive(attachmentSolver[0].getActive());
                            attachment[0].setIntegrationId(attachmentSolver[0].getSys_id());
                            attachment[0].setDownloadLinkSN(attachmentSolver[0].getDownload_link());
                            attachment[0].setExtension(".".concat(attachmentSolver[0].getFile_name().substring(Math.max(0, attachmentSolver[0].getFile_name().length() - 5)).split("\\s*[.]+")[1]));
                            attachment[0].setFileName(attachmentSolver[0].getFile_name());
                            attachment[0].setElement(attachmentSolver[0].getTable_sys_id());
                            attachment[0].setOrigin(attachmentSolver[0].getTable_name());
                            attachment[0].setContentType(attachmentSolver[0].getContent_type());
                            attachment[0].setSysCreatedBy(attachmentSolver[0].getSys_created_by());
                            attachment[0].setSysCreatedOn(attachmentSolver[0].getSys_created_on());
                            attachment[0].setSysUpdatedBy(attachmentSolver[0].getSys_updated_by());
                            attachment[0].setSysUpdatedOn(attachmentSolver[0].getSys_updated_on());
                            domain[0] = getDomainByIntegrationId((JSONObject) snAttachmentJson, SnTable.Domain.get(), App.Value());
                            if (domain[0] != null)
                                attachment[0].setDomain(domain[0]);
                            switch (attachment[0].getOrigin()) {
                                case "incident":
                                    incident[0] = incidentService.findByIntegrationId(attachment[0].getElement());
                                    if (incident[0] != null)
                                        attachment[0].setIncident(incident[0]);
                                    break;
                                case "sc_req_item":
                                    scRequestItem[0] = scRequestItemService.findByIntegrationId(attachment[0].getElement());
                                    if (scRequestItem[0] != null)
                                        attachment[0].setScRequestItem(scRequestItem[0]);
                                    break;
                                case "sc_task":
                                    scTask[0] = scTaskService.findByIntegrationId(attachment[0].getElement());
                                    if (scTask[0] != null)
                                        attachment[0].setScTask(scTask[0]);
                                    break;
                            }
                            exists[0] = attachmentService.findByIntegrationId(attachment[0].getIntegrationId());
                            if (exists[0] != null) {
                                attachment[0].setId(exists[0].getId());
                                tagAction[0] = App.UpdateConsole();
                            }
                            attachmentService.save(attachment[0]);
                            snAttachments.add(attachmentSolver[0]);
                            Util.printData(tag, count[0], tagAction[0].concat(Util.getFieldDisplay(attachment[0])), Util.getFieldDisplay(domain[0]));
                            count[0] = count[0] + 1;
                        } catch (Exception e) {
                            log.error(tag.concat("JsonProcessingException (I) : ").concat(String.valueOf(e)));
                        }
                    });
                }
                apiResponse[0] = mapper.readValue(result[0], APIResponse.class);
                status.setUri(EndPointSN.Incident());
                status.setUserAPI(App.SNUser());
                status.setPasswordAPI(App.SNPassword());
                status.setError(apiResponse[0].getError());
                status.setMessage(apiResponse[0].getMessage());
                status.setExecutionTime(endTime[0]);
                statusService.save(status);
            } catch (Exception e) {
                log.error(tag.concat("Exception (I) : ").concat(String.valueOf(e)));
            }
        });
        return attachments;
    }

    @GetMapping("/findBySolverByQueryCreate")
    public List<Attachment> show(String query, boolean flagQuery, boolean flagCreate) {
        log.info(App.Start());
        final APIResponse[] apiResponse = {null};
        List<AttachmentSolver> snAttachments = new ArrayList<>();
        List<Attachment> attachments = new ArrayList<>();
        final long[] startTime = {0};
        final long[] endTime = {0};
        String tag = "[Attachment] ";
        String[] sparmOffSets = Util.offSets1500000();
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        final String[] result = new String[1];
        JSONParser parser = new JSONParser();
        final JSONArray[] ListSnAttachmentJson = {new JSONArray()};
        final int[] count = {1};
        final String[] tagAction = new String[1];
        final JSONObject[] resultJson = new JSONObject[1];
        Gson gson = new Gson();
        final AttachmentSolver[] attachmentSolver = new AttachmentSolver[1];
        final Attachment[] attachment = {new Attachment()};
        final Attachment[] exists = new Attachment[1];
        final Domain[] domain = new Domain[1];
        final Incident[] incident = new Incident[1];
        final ScRequestItem[] scRequestItem = new ScRequestItem[1];
        final ScTask[] scTask = new ScTask[1];
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachments");
        APIExecutionStatus status = new APIExecutionStatus();
        incidentService.findAll().forEach(i -> {
            try {
                //RestTemplate restTemplate = rest.restTemplateServiceNow();
                startTime[0] = System.currentTimeMillis();
                for (String sparmOffSet : sparmOffSets) {
                    result[0] = rest.responseByEndPoint(EndPointSN.AttachmentByQuery().replace("QUERY", query).concat(sparmOffSet));
                    endTime[0] = (System.currentTimeMillis() - startTime[0]);
                    resultJson[0] = (JSONObject) parser.parse(result[0]);
                    ListSnAttachmentJson[0].clear();
                    if (resultJson[0].get("result") != null)
                        ListSnAttachmentJson[0] = (JSONArray) parser.parse(resultJson[0].get("result").toString());
                    ListSnAttachmentJson[0].forEach(snAttachmentJson -> {
                        tagAction[0] = App.CreateConsole();
                        try {
                            attachmentSolver[0] = gson.fromJson(snAttachmentJson.toString(), AttachmentSolver.class);
                            attachment[0] = new Attachment();
                            exists[0] = attachmentService.findByIntegrationId(attachment[0].getIntegrationId());
                            if (exists[0] != null) {
                                attachment[0].setId(exists[0].getId());
                                tagAction[0] = App.UpdateConsole();
                            } else {
                                attachment[0].setActive(attachmentSolver[0].getActive());
                                attachment[0].setIntegrationId(attachmentSolver[0].getSys_id());
                                attachment[0].setDownloadLinkSN(attachmentSolver[0].getDownload_link());
                                attachment[0].setExtension(".".concat(attachmentSolver[0].getFile_name().substring(Math.max(0, attachmentSolver[0].getFile_name().length() - 5)).split("\\s*[.]+")[1]));
                                attachment[0].setFileName(attachmentSolver[0].getFile_name());
                                attachment[0].setElement(attachmentSolver[0].getTable_sys_id());
                                attachment[0].setOrigin(attachmentSolver[0].getTable_name());
                                attachment[0].setContentType(attachmentSolver[0].getContent_type());
                                attachment[0].setSysCreatedBy(attachmentSolver[0].getSys_created_by());
                                attachment[0].setSysCreatedOn(attachmentSolver[0].getSys_created_on());
                                attachment[0].setSysUpdatedBy(attachmentSolver[0].getSys_updated_by());
                                attachment[0].setSysUpdatedOn(attachmentSolver[0].getSys_updated_on());
                                domain[0] = getDomainByIntegrationId((JSONObject) snAttachmentJson, SnTable.Domain.get(), App.Value());
                                if (domain[0] != null)
                                    attachment[0].setDomain(domain[0]);
                                switch (attachment[0].getOrigin()) {
                                    case "incident":
                                        incident[0] = incidentService.findByIntegrationId(attachment[0].getElement());
                                        if (incident[0] != null)
                                            attachment[0].setIncident(incident[0]);
                                        break;
                                    case "sc_req_item":
                                        scRequestItem[0] = scRequestItemService.findByIntegrationId(attachment[0].getElement());
                                        if (scRequestItem[0] != null)
                                            attachment[0].setScRequestItem(scRequestItem[0]);
                                        break;
                                    case "sc_task":
                                        scTask[0] = scTaskService.findByIntegrationId(attachment[0].getElement());
                                        if (scTask[0] != null)
                                            attachment[0].setScTask(scTask[0]);
                                        break;
                                }
                                attachmentService.save(attachment[0]);
                                snAttachments.add(attachmentSolver[0]);
                                Util.printData(tag, count[0], tagAction[0].concat(Util.getFieldDisplay(attachment[0])), Util.getFieldDisplay(domain[0]));
                                count[0] = count[0] + 1;
                            }
                        } catch (Exception e) {
                            //    log.error(tag.concat("JsonProcessingException (I) : ").concat(String.valueOf(e)));
                        }
                    });
                }
                //return ResponseEntity.ok().headers(headers).body(attachments);
                apiResponse[0] = mapper.readValue(result[0], APIResponse.class);
                status.setUri(EndPointSN.Incident());
                status.setUserAPI(App.SNUser());
                status.setPasswordAPI(App.SNPassword());
                status.setError(apiResponse[0].getError());
                status.setMessage(apiResponse[0].getMessage());
                status.setExecutionTime(endTime[0]);
                statusService.save(status);
            } catch (Exception e) {
                //  log.error(tag.concat("Exception (I) : ").concat(String.valueOf(e)));
            }
        });
        return attachments;
    }

    @GetMapping("/findByElement/{integrationId}")
    public List<Attachment> findByElement(@PathVariable String integrationId) {
        log.info(App.Start());
        APIResponse apiResponse = null;
        List<AttachmentSolver> snAttachments = new ArrayList<>();
        List<Attachment> attachments = new ArrayList<>();
        long startTime = 0;
        long endTime = 0;
        String tag = "[Attachment] ";
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        JSONParser parser = new JSONParser();
        JSONObject resultJson;
        JSONArray ListSnAttachmentJson = new JSONArray();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        final Attachment[] attachment = {new Attachment()};
        final Domain[] domain = new Domain[1];
        final Incident[] incident = new Incident[1];
        final ScRequestItem[] scRequestItem = new ScRequestItem[1];
        final ScTask[] scTask = new ScTask[1];
        final Attachment[] exists = new Attachment[1];
        HttpHeaders headers = new HttpHeaders();
        final AttachmentSolver[] attachmentSolver = new AttachmentSolver[1];
        final String[] tagAction = new String[1];
        headers.add("Content-Disposition", "attachments");
        APIExecutionStatus status = new APIExecutionStatus();
        String result;
        try {
            startTime = System.currentTimeMillis();
            result = rest.responseByEndPoint(EndPointSN.Attachment().concat(integrationId));
            endTime = (System.currentTimeMillis() - startTime);
            resultJson = (JSONObject) parser.parse(result);
            if (resultJson.get("result") != null)
                ListSnAttachmentJson = (JSONArray) parser.parse(resultJson.get("result").toString());
            final int[] count = {1};
            ListSnAttachmentJson.forEach(snAttachmentJson -> {
                 tagAction[0] = App.CreateConsole();
                try {
                    attachmentSolver[0] = objectMapper.readValue(snAttachmentJson.toString(), AttachmentSolver.class);
                    attachment[0] = new Attachment();
                    attachment[0].setActive(attachmentSolver[0].getActive());
                    attachment[0].setIntegrationId(attachmentSolver[0].getSys_id());
                    attachment[0].setDownloadLinkSN(attachmentSolver[0].getDownload_link());
                    attachment[0].setExtension(".".concat(attachmentSolver[0].getFile_name().substring(Math.max(0, attachmentSolver[0].getFile_name().length() - 5)).split("\\s*[.]+")[1]));
                    attachment[0].setFileName(attachmentSolver[0].getFile_name());
                    attachment[0].setElement(attachmentSolver[0].getTable_sys_id());
                    attachment[0].setOrigin(attachmentSolver[0].getTable_name());
                    attachment[0].setContentType(attachmentSolver[0].getContent_type());
                    attachment[0].setSysCreatedBy(attachmentSolver[0].getSys_created_by());
                    attachment[0].setSysCreatedOn(attachmentSolver[0].getSys_created_on());
                    attachment[0].setSysUpdatedBy(attachmentSolver[0].getSys_updated_by());
                    attachment[0].setSysUpdatedOn(attachmentSolver[0].getSys_updated_on());
                    domain[0] = getDomainByIntegrationId((JSONObject) snAttachmentJson, SnTable.Domain.get(), App.Value());
                    attachment[0].setDomain(getDomainByIntegrationId((JSONObject) snAttachmentJson, SnTable.Domain.get(), App.Value()));
                    switch (attachment[0].getOrigin()) {
                        case "incident":
                            incident[0] = incidentService.findByIntegrationId(attachment[0].getElement());
                            if (incident[0] != null)
                                attachment[0].setIncident(incident[0]);
                            break;
                        case "sc_req_item":
                            scRequestItem[0] = scRequestItemService.findByIntegrationId(attachment[0].getElement());
                            if (scRequestItem[0] != null)
                                attachment[0].setScRequestItem(scRequestItem[0]);
                            break;
                        case "sc_task":
                            scTask[0] = scTaskService.findByIntegrationId(attachment[0].getElement());
                            if (scTask[0] != null)
                                attachment[0].setScTask(scTask[0]);
                            break;
                    }
                    exists[0] = attachmentService.findByIntegrationId(attachment[0].getIntegrationId());
                    if (exists[0] != null) {
                        attachment[0].setId(exists[0].getId());
                        tagAction[0] = App.UpdateConsole();
                    }
                    attachmentService.save(attachment[0]);
                    snAttachments.add(attachmentSolver[0]);
                    Util.printData(tag, count[0], tagAction[0].concat(Util.getFieldDisplay(attachment[0])), Util.getFieldDisplay(domain[0]));
                    count[0] = count[0] + 1;
                } catch (JsonProcessingException e) {
                    log.error(tag.concat("JsonProcessingException (I) : ").concat(String.valueOf(e)));
                }
            });
            apiResponse = mapper.readValue(result, APIResponse.class);
            status.setUri(EndPointSN.Incident());
            status.setUserAPI(App.SNUser());
            status.setPasswordAPI(App.SNPassword());
            status.setError(apiResponse.getError());
            status.setMessage(apiResponse.getMessage());
            status.setExecutionTime(endTime);
            statusService.save(status);
        } catch (Exception e) {
            log.error(tag.concat("Exception (I) : ").concat(String.valueOf(e)));
        }
        return attachments;
    }

    @GetMapping("/findSolverByElement/{integrationId}")
    public List<Attachment> findSolverByElement(@PathVariable String integrationId) {
        List<Attachment> attachments = new ArrayList<>();
        Map<String, Object> response = new HashMap<>();
        return attachments;
    }

    @PostMapping("/uploadFile/{element}")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file, @PathVariable String element) {
        Map<String, Object> response = new HashMap<>();
        try {
            String type = element.split(",")[1];
            element = element.split(",")[0];
            String tag = "[Attachment] ";
            String tagAction = App.CreateConsole();
            if (!file.isEmpty() && element != null) {
                Attachment current = new Attachment();
                Attachment attachment;
                String filename = file.getOriginalFilename();
                current.setExtension(".".concat(FilenameUtils.getExtension(filename)));
                current.setFileName(filename);
                current.setElement(element);
                current.setOrigin(type);
                attachmentService.save(current);
                Util.printData(tag, tagAction.concat("BD"), Util.getFieldDisplay(current), current.getElement());
                attachment = rest.sendFileToServiceNow(element, file, type, file.getOriginalFilename());
                if (attachment != null)
                    Util.printData(tag, tagAction.concat("(ServiceNow)"), Util.getFieldDisplay(current), current.getElement());
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
                    Util.printData(tag, tagAction.concat("(SO)"), Util.getFieldDisplay(current), current.getElement());
                    attachment.setDownloadLink(pathDirectory);
                }
                tagAction = App.UpdateConsole();
                attachmentService.save(attachment);
                Util.printData(tag, tagAction.concat("(BD)"), Util.getFieldDisplay(attachment), attachment.getElement());
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
        log.info("Compressed Image Byte Size - " + outputStream.toByteArray().length);
        return outputStream.toByteArray();
    }

    public Domain getDomainByIntegrationId(JSONObject jsonObject, String levelOne, String levelTwo) {
        String integrationId = Util.getIdByJson(jsonObject, levelOne, levelTwo);
        if (Util.hasData(integrationId)) {
            return domainService.findByIntegrationId(integrationId);
        } else
            return null;
    }
}


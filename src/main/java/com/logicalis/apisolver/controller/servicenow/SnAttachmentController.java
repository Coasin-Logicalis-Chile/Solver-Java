package com.logicalis.apisolver.controller.servicenow;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.logicalis.apisolver.model.*;
import com.logicalis.apisolver.model.enums.App;
import com.logicalis.apisolver.model.enums.EndPointSN;
import com.logicalis.apisolver.model.enums.SnTable;
import com.logicalis.apisolver.model.servicenow.SnAttachment;
import com.logicalis.apisolver.services.*;
import com.logicalis.apisolver.util.Rest;
import com.logicalis.apisolver.util.Util;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = {"${app.api.settings.cross-origin.urls}", "*"})
@RestController
@RequestMapping("/api/v1")
@Slf4j
public class SnAttachmentController {
    @Autowired
    private IAttachmentService attachmentService;
    @Autowired
    private IDomainService domainService;
    @Autowired
    private IIncidentService incidentService;
    @Autowired
    private IScRequestItemService scRequestItemService;
    @Autowired
    private IScTaskService scTaskService;
    @Autowired
    private IAPIExecutionStatusService statusService;
    @Autowired
    private Environment environment;
    @Autowired
    private Rest rest;

    @GetMapping("/snAttachment/{id}")
    public Object show(@PathVariable Long id) {
        log.info(App.Start());
        APIResponse apiResponse = null;
        long startTime = 0;
        long endTime = 0;
        String tag = "[Attachment] ";
        final InputStreamResource[] attachmentResource = {null};
        try {
            startTime = System.currentTimeMillis();
            Attachment attachment = attachmentService.findById(id);
            ObjectMapper mapper = new ObjectMapper();
            try {
                File downloadFile;
                if (!Util.isNullBool(attachment.getDownloadLink())) {
                    downloadFile = new File(attachment.getDownloadLink());
                    log.info("LINUX");
                    log.info(downloadFile.getAbsolutePath());
                } else {
                    RestTemplate restTemplate = rest.restTemplateServiceNow();
                    downloadFile = rest.responseFileByEndPointSO(attachment, restTemplate, environment.getProperty("setting.attachments.dir").replaceAll("//", File.separator));
                    attachment.setDownloadLink(downloadFile.getAbsolutePath());
                    attachment = attachmentService.save(attachment);

                    log.info("SERVICENOW");
                    log.info(downloadFile.getAbsolutePath());
                }

                byte[] fileByte = Files.readAllBytes(downloadFile.toPath());
                ByteArrayInputStream inputStream = new ByteArrayInputStream(fileByte);
                attachmentResource[0] = new InputStreamResource(inputStream);

            } catch (JsonProcessingException e) {
                log.error(tag.concat("/snAttachment/{id} JsonProcessingException (I) : ").concat(String.valueOf(e)));
            } catch (IOException e) {
                log.error(tag.concat("/snAttachment/{id} IOException (I) : ").concat(String.valueOf(e)));
            }
            endTime = (System.currentTimeMillis() - startTime);
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachmentResource");

            apiResponse = mapper.readValue(attachment.getFileName(), APIResponse.class);
            APIExecutionStatus status = new APIExecutionStatus();
            status.setUri(EndPointSN.Attachment().concat(attachment.getIntegrationId()));
            status.setUserAPI(App.SNUser());
            status.setPasswordAPI(App.SNPassword());
            status.setError(apiResponse.getError());
            status.setMessage(apiResponse.getMessage());
            status.setExecutionTime(endTime);
            statusService.save(status);
        } catch (Exception e) {
            log.error(tag.concat("Exception (II) : ").concat(String.valueOf(e)));
        }
        return attachmentResource[0];
    }

    @GetMapping("/findAttachmentByIntegration/{integration_id}")
    public Object findAttachmentByIntegration(@PathVariable String integration_id) {
        log.info(App.Start());
        APIResponse apiResponse = null;
        long startTime = 0;
        long endTime = 0;
        String tag = "[Attachment] ";
        final InputStreamResource[] attachmentResource = {null};
        try {
            startTime = System.currentTimeMillis();
            Attachment attachment = attachmentService.findByIntegrationId(integration_id);
            try {
                File downloadFile;
                if (!Util.isNullBool(attachment.getDownloadLink())) {
                    downloadFile = new File(attachment.getDownloadLink());
                    log.info("LINUX");
                    log.info(downloadFile.getAbsolutePath());
                } else {
                    RestTemplate restTemplate = rest.restTemplateServiceNow();
                    downloadFile = rest.responseFileByEndPointSO(attachment, restTemplate, environment.getProperty("setting.attachments.dir").replaceAll("//", File.separator));
                    attachment.setDownloadLink(downloadFile.getAbsolutePath());
                    attachment = attachmentService.save(attachment);
                    log.info("SERVICENOW");
                    log.info(downloadFile.getAbsolutePath());
                }
                byte[] fileByte = Files.readAllBytes(downloadFile.toPath());
                ByteArrayInputStream inputStream = new ByteArrayInputStream(fileByte);
                attachmentResource[0] = new InputStreamResource(inputStream);
            } catch (JsonProcessingException e) {
                log.error(tag.concat("/snAttachment/{id} JsonProcessingException (I) : ").concat(String.valueOf(e)));
            } catch (IOException e) {
                log.error(tag.concat("/snAttachment/{id} IOException (I) : ").concat(String.valueOf(e)));
            }
            endTime = (System.currentTimeMillis() - startTime);
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachmentResource");

            ObjectMapper mapper = new ObjectMapper();
            apiResponse = mapper.readValue("End process", APIResponse.class);
            APIExecutionStatus status = new APIExecutionStatus();
            status.setUri(EndPointSN.Attachment().concat(attachment.getIntegrationId()));
            status.setUserAPI(App.SNUser());
            status.setPasswordAPI(App.SNPassword());
            status.setError(apiResponse.getError());
            status.setMessage(apiResponse.getMessage());
            status.setExecutionTime(endTime);
            statusService.save(status);
        } catch (Exception e) {
            log.error(tag.concat("Exception (II) : ").concat(String.valueOf(e)));
        }
        return attachmentResource[0];
    }

    @GetMapping("/attachments/{integrationId}")
    public List<Attachment> show(@PathVariable String integrationId) {
        log.info(App.Start());
        APIResponse apiResponse = null;
        List<SnAttachment> snAttachments = new ArrayList<>();
        List<Attachment> attachments = new ArrayList<>();
        long startTime = 0;
        long endTime = 0;
        String tag = "[Attachment] ";
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachments");
        try {
            RestTemplate restTemplate = rest.restTemplateServiceNow();
            startTime = System.currentTimeMillis();
            String result = rest.responseByEndPoint(EndPointSN.Attachment().concat(integrationId));
            endTime = (System.currentTimeMillis() - startTime);
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JSONParser parser = new JSONParser();
            JSONObject resultJson = (JSONObject) parser.parse(result);
            JSONArray ListSnAttachmentJson = new JSONArray();
            final Domain[] domain = new Domain[1];
            final Incident[] incident = new Incident[1];
            final ScRequestItem[] scRequestItem = new ScRequestItem[1];
            final ScTask[] scTask = new ScTask[1];
            final Attachment[] exists = new Attachment[1];
            APIExecutionStatus status = new APIExecutionStatus();
            if (resultJson.get("result") != null)
                ListSnAttachmentJson = (JSONArray) parser.parse(resultJson.get("result").toString());
            final int[] count = {1};
            final String[] tagAction = {App.CreateConsole()};
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            final SnAttachment[] snAttachment = new SnAttachment[1];
            final Attachment[] attachment = {new Attachment()};
            ListSnAttachmentJson.forEach(snAttachmentJson -> {
                tagAction[0] = App.CreateConsole();
                try {
                    snAttachment[0] = objectMapper.readValue(snAttachmentJson.toString(), SnAttachment.class);
                    attachment[0] = new Attachment();
                    attachment[0].setActive(snAttachment[0].getActive());
                    attachment[0].setIntegrationId(snAttachment[0].getSys_id());
                    attachment[0].setDownloadLinkSN(snAttachment[0].getDownload_link());
                    attachment[0].setExtension(".".concat(snAttachment[0].getFile_name().substring(Math.max(0, snAttachment[0].getFile_name().length() - 5)).split("\\s*[.]+")[1]));
                    attachment[0].setFileName(snAttachment[0].getFile_name());
                    attachment[0].setElement(snAttachment[0].getTable_sys_id());
                    attachment[0].setOrigin(snAttachment[0].getTable_name());
                    attachment[0].setContentType(snAttachment[0].getContent_type());
                    attachment[0].setSysCreatedBy(snAttachment[0].getSys_created_by());
                    attachment[0].setSysCreatedOn(snAttachment[0].getSys_created_on());
                    attachment[0].setSysUpdatedBy(snAttachment[0].getSys_updated_by());
                    attachment[0].setSysUpdatedOn(snAttachment[0].getSys_updated_on());
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
                    snAttachments.add(snAttachment[0]);
                    exists[0] = attachmentService.findByIntegrationId(attachment[0].getIntegrationId());
                    if (exists[0] != null) {
                        attachment[0].setId(exists[0].getId());
                        tagAction[0] = App.UpdateConsole();
                    }
                    attachmentService.save(attachment[0]);
                    Util.printData(tag, count[0], tagAction[0].concat(Util.getFieldDisplay(attachment[0])), Util.getFieldDisplay(domain[0]));
                    count[0] = count[0] + 1;
                } catch (JsonProcessingException e) {
                    log.error(tag.concat("JsonProcessingException (I) : ").concat(String.valueOf(e)));
                } catch (IOException e) {
                    log.error(tag.concat("IOException (I) : ").concat(String.valueOf(e)));
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

    public Domain getDomainByIntegrationId(JSONObject jsonObject, String levelOne, String levelTwo) {
        String integrationId = Util.getIdByJson(jsonObject, levelOne, levelTwo);
        if (Util.hasData(integrationId)) {
            return domainService.findByIntegrationId(integrationId);
        } else
            return null;
    }
}


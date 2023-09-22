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
        System.out.println(App.Start());
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
                    System.out.println("LINUX");
                    System.out.println(downloadFile.getAbsolutePath());

                } else {
                    RestTemplate restTemplate = rest.restTemplateServiceNow();
                    downloadFile = rest.responseFileByEndPointSO(attachment, restTemplate, environment.getProperty("setting.attachments.dir").replaceAll("//", File.separator));
                    attachment.setDownloadLink(downloadFile.getAbsolutePath());
                    attachment = attachmentService.save(attachment);

                    System.out.println("SERVICENOW");
                    System.out.println(downloadFile.getAbsolutePath());
                }

                byte[] fileByte = Files.readAllBytes(downloadFile.toPath());
                ByteArrayInputStream inputStream = new ByteArrayInputStream(fileByte);
                attachmentResource[0] = new InputStreamResource(inputStream);

            } catch (JsonProcessingException e) {
                System.out.println(tag.concat("/snAttachment/{id} JsonProcessingException (I) : ").concat(String.valueOf(e)));
            } catch (IOException e) {
                System.out.println(tag.concat("/snAttachment/{id} IOException (I) : ").concat(String.valueOf(e)));
                e.printStackTrace();
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
            System.out.println(tag.concat("Exception (II) : ").concat(String.valueOf(e)));
        }
        return attachmentResource[0];
    }

    @GetMapping("/findAttachmentByIntegration/{integration_id}")
    public Object findAttachmentByIntegration(@PathVariable String integration_id) {
        System.out.println(App.Start());
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
                    System.out.println("LINUX");
                    System.out.println(downloadFile.getAbsolutePath());

                } else {
                    RestTemplate restTemplate = rest.restTemplateServiceNow();
                    downloadFile = rest.responseFileByEndPointSO(attachment, restTemplate, environment.getProperty("setting.attachments.dir").replaceAll("//", File.separator));
                    attachment.setDownloadLink(downloadFile.getAbsolutePath());
                    attachment = attachmentService.save(attachment);

                    System.out.println("SERVICENOW");
                    System.out.println(downloadFile.getAbsolutePath());
                }

                byte[] fileByte = Files.readAllBytes(downloadFile.toPath());
                ByteArrayInputStream inputStream = new ByteArrayInputStream(fileByte);
                attachmentResource[0] = new InputStreamResource(inputStream);

            } catch (JsonProcessingException e) {
                System.out.println(tag.concat("/snAttachment/{id} JsonProcessingException (I) : ").concat(String.valueOf(e)));
            } catch (IOException e) {
                System.out.println(tag.concat("/snAttachment/{id} IOException (I) : ").concat(String.valueOf(e)));
                e.printStackTrace();
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
            System.out.println(tag.concat("Exception (II) : ").concat(String.valueOf(e)));
        }
        return attachmentResource[0];
    }

    @GetMapping("/attachments/{integrationId}")
    public List<Attachment> show(@PathVariable String integrationId) {

        System.out.println(App.Start());
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
            if (resultJson.get("result") != null)
                ListSnAttachmentJson = (JSONArray) parser.parse(resultJson.get("result").toString());
            final int[] count = {1};
            ListSnAttachmentJson.forEach(snAttachmentJson -> {
                String tagAction = App.CreateConsole();
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                try {
                    SnAttachment snAttachment = objectMapper.readValue(snAttachmentJson.toString(), SnAttachment.class);
                    Attachment attachment = new Attachment();
                    attachment.setActive(snAttachment.getActive());
                    attachment.setIntegrationId(snAttachment.getSys_id());
                    attachment.setDownloadLinkSN(snAttachment.getDownload_link());
                    attachment.setExtension(".".concat(snAttachment.getFile_name().substring(Math.max(0, snAttachment.getFile_name().length() - 5)).split("\\s*[.]+")[1]));
                    attachment.setFileName(snAttachment.getFile_name());
                    attachment.setElement(snAttachment.getTable_sys_id());
                    attachment.setOrigin(snAttachment.getTable_name());
                    attachment.setContentType(snAttachment.getContent_type());
                    attachment.setSysCreatedBy(snAttachment.getSys_created_by());
                    attachment.setSysCreatedOn(snAttachment.getSys_created_on());
                    attachment.setSysUpdatedBy(snAttachment.getSys_updated_by());
                    attachment.setSysUpdatedOn(snAttachment.getSys_updated_on());

                    Domain domain = getDomainByIntegrationId((JSONObject) snAttachmentJson, SnTable.Domain.get(), App.Value());
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


                    snAttachments.add(snAttachment);
                    Attachment exists = attachmentService.findByIntegrationId(attachment.getIntegrationId());
                    if (exists != null) {
                        attachment.setId(exists.getId());
                        tagAction = App.UpdateConsole();
                    }
                    attachmentService.save(attachment);
                    Util.printData(tag, count[0], tagAction.concat(Util.getFieldDisplay(attachment)), Util.getFieldDisplay(domain));


                    count[0] = count[0] + 1;
                } catch (JsonProcessingException e) {
                    System.out.println(tag.concat("JsonProcessingException (I) : ").concat(String.valueOf(e)));
                } catch (IOException e) {
                    System.out.println(tag.concat("IOException (I) : ").concat(String.valueOf(e)));
                    e.printStackTrace();
                }
            });

            apiResponse = mapper.readValue(result, APIResponse.class);
            APIExecutionStatus status = new APIExecutionStatus();
            status.setUri(EndPointSN.Incident());
            status.setUserAPI(App.SNUser());
            status.setPasswordAPI(App.SNPassword());
            status.setError(apiResponse.getError());
            status.setMessage(apiResponse.getMessage());
            status.setExecutionTime(endTime);
            statusService.save(status);
        } catch (Exception e) {
            System.out.println(tag.concat("Exception (I) : ").concat(String.valueOf(e)));
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


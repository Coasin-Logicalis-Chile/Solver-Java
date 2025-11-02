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

            // ERROR DE MANERA LOCAL  DESDE AQUI =====================
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
}


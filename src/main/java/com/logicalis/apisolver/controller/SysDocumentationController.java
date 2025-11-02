package com.logicalis.apisolver.controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.logicalis.apisolver.model.APIExecutionStatus;
import com.logicalis.apisolver.model.APIResponse;
import com.logicalis.apisolver.model.SysDocumentation;
import com.logicalis.apisolver.model.enums.App;
import com.logicalis.apisolver.model.enums.EndPointSN;
import com.logicalis.apisolver.model.enums.Errors;
import com.logicalis.apisolver.model.enums.Messages;
import com.logicalis.apisolver.services.IAPIExecutionStatusService;
import com.logicalis.apisolver.services.ISysDocumentationService;
import com.logicalis.apisolver.util.Rest;
import com.logicalis.apisolver.util.Util;
import com.logicalis.apisolver.view.SysDocumentationRequest;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = {"${app.api.settings.cross-origin.urls}", "*"})
@RestController
@RequestMapping("/api/v1")
@Slf4j
public class SysDocumentationController {
    @Autowired
    private ISysDocumentationService sysDocumentationService;
    @Autowired
    private IAPIExecutionStatusService statusService;
    @Autowired
    private Rest rest;

    @GetMapping("/sysDocumentation/{id}")
    public ResponseEntity<?> show(@PathVariable Long id) {
        SysDocumentation sysDocumentation = null;
        Map<String, Object> response = new HashMap<>();
        try {
            sysDocumentation = sysDocumentationService.findById(id);
        } catch (DataAccessException e) {
            response.put("mensaje", Errors.dataAccessExceptionQuery.get());
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (sysDocumentation == null) {
            response.put("mensaje", Messages.notExist.get(id.toString()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<SysDocumentation>(sysDocumentation, HttpStatus.OK);
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/sysDocumentation")
    public ResponseEntity<?> create(@RequestBody SysDocumentation sysDocumentation) {
        SysDocumentation newSysDocumentation = null;
        Map<String, Object> response = new HashMap<>();
        try {
            newSysDocumentation = sysDocumentationService.save(sysDocumentation);
        } catch (DataAccessException e) {
            response.put("mensaje", Errors.dataAccessExceptionInsert.get());
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", Messages.createOK.get());
        response.put("sysDocumentation", newSysDocumentation);
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }

    @Secured("ROLE_ADMIN")
    @PutMapping("/sysDocumentation/{id}")
    public ResponseEntity<?> update(@RequestBody SysDocumentation sysDocumentation, @PathVariable Long id) {
        SysDocumentation currentSysDocumentation = sysDocumentationService.findById(id);
        SysDocumentation sysDocumentationUpdated = null;
        Map<String, Object> response = new HashMap<>();
        if (currentSysDocumentation == null) {
            response.put("mensaje", Errors.dataAccessExceptionUpdate.get(id.toString()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }
        try {
            currentSysDocumentation.setElement(sysDocumentation.getElement());
            sysDocumentationUpdated = sysDocumentationService.save(currentSysDocumentation);

        } catch (DataAccessException e) {
            response.put("mensaje", Errors.dataAccessExceptionUpdate.get());
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", Messages.UpdateOK.get());
        response.put("sysDocumentation", sysDocumentationUpdated);
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }

    @GetMapping("/byIntegrationId/{integrationId}")
    public ResponseEntity<?> findByIntegrationId(@PathVariable String integrationId) {
        SysDocumentation sysDocumentation = null;
        Map<String, Object> response = new HashMap<>();
        try {
            sysDocumentation = sysDocumentationService.findByIntegrationId(integrationId);
        } catch (DataAccessException e) {
            response.put("mensaje", Errors.dataAccessExceptionQuery.get());
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (sysDocumentation == null) {
            response.put("mensaje", Messages.notExist.get(integrationId));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<SysDocumentation>(sysDocumentation, HttpStatus.OK);
    }

    @GetMapping("/byUniqueIdentifier/{uniqueIdentifier}")
    public ResponseEntity<?> findByUniqueIdentifier(@PathVariable String uniqueIdentifier) {
        SysDocumentation sysDocumentation = null;
        Map<String, Object> response = new HashMap<>();
        try {
            sysDocumentation = sysDocumentationService.findByIntegrationId(uniqueIdentifier);
        } catch (DataAccessException e) {
            response.put("mensaje", Errors.dataAccessExceptionQuery.get());
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (sysDocumentation == null) {
            response.put("mensaje", Messages.notExist.get(uniqueIdentifier));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<SysDocumentation>(sysDocumentation, HttpStatus.OK);
    }

    @GetMapping("/sysDocumentationsBySolver")
    public List<SysDocumentationRequest> show() {
        log.info(App.Start());
        APIResponse apiResponse = null;
        List<SysDocumentationRequest> sysDocumentationRequests = new ArrayList<>();
        String[] sparmOffSets = Util.offSets99000();
        long startTime = 0;
        long endTime = 0;
        String tag = "[SysDocumentation] ";
        String result;
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        JSONParser parser = new JSONParser();
        JSONObject resultJson = new JSONObject();
        JSONArray ListSnSysDocumentationJson = new JSONArray();
        final SysDocumentationRequest[] sysDocumentationRequest = {new SysDocumentationRequest()};
        final SysDocumentation[] sysDocumentation = {new SysDocumentation()};
        final String[] tagAction = new String[1];
        final SysDocumentation[] exists = new SysDocumentation[1];
        final APIExecutionStatus[] status = {new APIExecutionStatus()};
        try {
            startTime = System.currentTimeMillis();
            final int[] count = {1};
            for (String sparmOffSet : sparmOffSets) {
                result = rest.responseByEndPoint(EndPointSN.SysDocumentation().concat(sparmOffSet));
                log.info(tag.concat("(".concat(EndPointSN.SysDocumentation().concat(sparmOffSet)).concat(")")));
                resultJson = (JSONObject) parser.parse(result);
                ListSnSysDocumentationJson.clear();
                if (resultJson.get("result") != null)
                    ListSnSysDocumentationJson = (JSONArray) parser.parse(resultJson.get("result").toString());
                ListSnSysDocumentationJson.stream().forEach(snSysDocumentationJson -> {
                    try {
                        sysDocumentationRequest[0] = mapper.readValue(snSysDocumentationJson.toString(), SysDocumentationRequest.class);
                        sysDocumentationRequests.add(sysDocumentationRequest[0]);
                        sysDocumentation[0] = new SysDocumentation();
                        sysDocumentation[0].setElement(sysDocumentationRequest[0].getElement());
                        sysDocumentation[0].setEs(sysDocumentationRequest[0].getLabel());
                        sysDocumentation[0].setOrigin(sysDocumentationRequest[0].getName());
                        sysDocumentation[0].setSysCreatedBy(sysDocumentationRequest[0].getSys_created_by());
                        sysDocumentation[0].setSysCreatedOn(sysDocumentationRequest[0].getSys_created_on());
                        sysDocumentation[0].setIntegrationId(sysDocumentationRequest[0].getSys_id());
                        sysDocumentation[0].setSysUpdatedBy(sysDocumentationRequest[0].getSys_updated_by());
                        sysDocumentation[0].setSysUpdatedOn(sysDocumentationRequest[0].getSys_updated_on());
                        sysDocumentation[0].setUniqueIdentifier(sysDocumentationRequest[0].getName().concat("•").concat(sysDocumentationRequest[0].getElement()));
                        sysDocumentation[0].setActive(true);

                        tagAction[0] = App.CreateConsole();
                        exists[0] = sysDocumentationService.findByUniqueIdentifier(sysDocumentation[0].getUniqueIdentifier());
                        if (exists[0] != null) {
                            sysDocumentation[0].setId(exists[0].getId());
                            tagAction[0] = App.UpdateConsole();
                        }
                        Util.printData(tag, count[0], tagAction[0].concat(Util.getFieldDisplay(sysDocumentation[0])));
                        sysDocumentationService.save(sysDocumentation[0]);
                        count[0] = count[0] + 1;
                    } catch (Exception e) {
                        log.error(tag.concat("Exception (I) : ").concat(String.valueOf(e)));
                    }
                });
                apiResponse = mapper.readValue(result, APIResponse.class);
                status[0].setUri(EndPointSN.Location());
                status[0].setUserAPI(App.SNUser());
                status[0].setPasswordAPI(App.SNPassword());
                status[0].setError(apiResponse.getError());
                status[0].setMessage(apiResponse.getMessage());
                endTime = (System.currentTimeMillis() - startTime);
                status[0].setExecutionTime(endTime);
                statusService.save(status[0]);
            }
        } catch (Exception e) {
            log.error(tag.concat("Exception (II) : ").concat(String.valueOf(e)));
        }
        log.info(App.End());
        return sysDocumentationRequests;
    }
}


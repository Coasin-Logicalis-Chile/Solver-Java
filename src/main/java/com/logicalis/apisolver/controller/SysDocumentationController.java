
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
public class SysDocumentationController {

    @Autowired
    private ISysDocumentationService sysDocumentationService;
    @Autowired
    private IAPIExecutionStatusService statusService;
    Util util = new Util();
    App app = new App();
    EndPointSN endPointSN = new EndPointSN();
    @GetMapping("/sysDocumentations")
    public List<SysDocumentation> index() {
        return sysDocumentationService.findAll();
    }

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

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/sysDocumentation/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {

        Map<String, Object> response = new HashMap<>();
        try {
            sysDocumentationService.delete(id);
        } catch (DataAccessException e) {
            response.put("mensaje", Errors.dataAccessExceptionDelete.get());
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", Messages.DeleteOK.get());
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
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
        System.out.println(app.Start());
        APIResponse apiResponse = null;
        List<SysDocumentationRequest> sysDocumentationRequests = new ArrayList<>();
        String[] sparmOffSets = util.offSets99000();
        long startTime = 0;
        long endTime = 0;
        String tag = "[SysDocumentation] ";
        try {
            Rest rest = new Rest();
            startTime = System.currentTimeMillis();
            final int[] count = {1};
            for (String sparmOffSet : sparmOffSets) {

                String result = rest.responseByEndPoint(endPointSN.SysDocumentation().concat(sparmOffSet));
                System.out.println(tag.concat("(".concat(endPointSN.SysDocumentation().concat(sparmOffSet)).concat(")")));
                ObjectMapper mapper = new ObjectMapper();
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

                JSONParser parser = new JSONParser();
                JSONObject resultJson = new JSONObject();
                JSONArray ListSnSysDocumentationJson = new JSONArray();
                resultJson = (JSONObject) parser.parse(result);
                if (resultJson.get("result") != null)
                    ListSnSysDocumentationJson = (JSONArray) parser.parse(resultJson.get("result").toString());

                ListSnSysDocumentationJson.stream().forEach(snSysDocumentationJson -> {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    SysDocumentationRequest sysDocumentationRequest = new SysDocumentationRequest();

                    try {
                        sysDocumentationRequest = objectMapper.readValue(snSysDocumentationJson.toString(), SysDocumentationRequest.class);
                        SysDocumentation sysDocumentation = new SysDocumentation();

                        sysDocumentationRequests.add(sysDocumentationRequest);

                        sysDocumentation.setElement(sysDocumentationRequest.getElement());
                        sysDocumentation.setEs(sysDocumentationRequest.getLabel());
                        sysDocumentation.setOrigin(sysDocumentationRequest.getName());
                        sysDocumentation.setSysCreatedBy(sysDocumentationRequest.getSys_created_by());
                        sysDocumentation.setSysCreatedOn(sysDocumentationRequest.getSys_created_on());
                        sysDocumentation.setIntegrationId(sysDocumentationRequest.getSys_id());
                        sysDocumentation.setSysUpdatedBy(sysDocumentationRequest.getSys_updated_by());
                        sysDocumentation.setSysUpdatedOn(sysDocumentationRequest.getSys_updated_on());
                        sysDocumentation.setUniqueIdentifier(sysDocumentationRequest.getName().concat("•").concat(sysDocumentationRequest.getElement()));

                        sysDocumentation.setActive(true);

                        String tagAction = app.CreateConsole();
                        SysDocumentation exists = sysDocumentationService.findByUniqueIdentifier(sysDocumentation.getUniqueIdentifier());
                        if (exists != null) {
                            sysDocumentation.setId(exists.getId());
                            tagAction = app.UpdateConsole();

                        }
                        util.printData(tag, count[0], tagAction.concat(util.getFieldDisplay(sysDocumentation)));
                        sysDocumentationService.save(sysDocumentation);
                        count[0] = count[0] + 1;

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
        return sysDocumentationRequests;
    }
}


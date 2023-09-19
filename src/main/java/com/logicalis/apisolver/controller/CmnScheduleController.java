
package com.logicalis.apisolver.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.logicalis.apisolver.model.APIExecutionStatus;
import com.logicalis.apisolver.model.APIResponse;
import com.logicalis.apisolver.model.CmnSchedule;
import com.logicalis.apisolver.model.Domain;
import com.logicalis.apisolver.model.enums.*;
import com.logicalis.apisolver.services.IAPIExecutionStatusService;
import com.logicalis.apisolver.services.ICmnScheduleService;
import com.logicalis.apisolver.services.IDomainService;
import com.logicalis.apisolver.util.Rest;
import com.logicalis.apisolver.util.Util;
import com.logicalis.apisolver.view.CmnScheduleSolver;
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
public class CmnScheduleController {
    @Autowired
    private ICmnScheduleService cmnScheduleService;
    @Autowired
    private IAPIExecutionStatusService statusService;
    @Autowired
    private IDomainService domainService;
    @Autowired
    private Rest rest;

    @GetMapping("/cmnSchedules")
    public List<CmnSchedule> index() {
        return cmnScheduleService.findAll();
    }

    @GetMapping("/cmnSchedule/{id}")
    public ResponseEntity<?> show(@PathVariable Long id) {

        CmnSchedule cmnSchedule = null;
        Map<String, Object> response = new HashMap<>();

        try {
            cmnSchedule = cmnScheduleService.findById(id);
        } catch (DataAccessException e) {
            response.put("mensaje", Errors.dataAccessExceptionQuery.get());
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (cmnSchedule == null) {
            response.put("mensaje", Messages.notExist.get(id.toString()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<CmnSchedule>(cmnSchedule, HttpStatus.OK);
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/cmnSchedule")
    public ResponseEntity<?> create(@RequestBody CmnSchedule cmnSchedule) {
        CmnSchedule newCmnSchedule = null;

        Map<String, Object> response = new HashMap<>();
        try {
            newCmnSchedule = cmnScheduleService.save(cmnSchedule);
        } catch (DataAccessException e) {
            response.put("mensaje", Errors.dataAccessExceptionInsert.get());
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", Messages.createOK.get());
        response.put("cmnSchedule", newCmnSchedule);

        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }

    @Secured("ROLE_ADMIN")
    @PutMapping("/cmnSchedule/{id}")
    public ResponseEntity<?> update(@RequestBody CmnSchedule cmnSchedule, @PathVariable Long id) {

        CmnSchedule currentCmnSchedule = cmnScheduleService.findById(id);
        CmnSchedule cmnScheduleUpdated = null;

        Map<String, Object> response = new HashMap<>();

        if (currentCmnSchedule == null) {
            response.put("mensaje", Errors.dataAccessExceptionUpdate.get(id.toString()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }

        try {
            currentCmnSchedule.setName(cmnSchedule.getName());
            cmnScheduleUpdated = cmnScheduleService.save(currentCmnSchedule);

        } catch (DataAccessException e) {
            response.put("mensaje", Errors.dataAccessExceptionUpdate.get());
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", Messages.UpdateOK.get());
        response.put("cmnSchedule", cmnScheduleUpdated);

        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/cmnSchedule/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {

        Map<String, Object> response = new HashMap<>();
        try {
            cmnScheduleService.delete(id);
        } catch (DataAccessException e) {
            response.put("mensaje", Errors.dataAccessExceptionDelete.get());
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", Messages.DeleteOK.get());
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
    }

    @GetMapping("/cmnSchedule/{integrationId}")
    public ResponseEntity<?> findByIntegrationId(@PathVariable String integrationId) {

        CmnSchedule cmnSchedule = null;
        Map<String, Object> response = new HashMap<>();

        try {
            cmnSchedule = cmnScheduleService.findByIntegrationId(integrationId);
        } catch (DataAccessException e) {
            response.put("mensaje", Errors.dataAccessExceptionQuery.get());
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (cmnSchedule == null) {
            response.put("mensaje", Messages.notExist.get(integrationId.toString()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<CmnSchedule>(cmnSchedule, HttpStatus.OK);
    }

    @GetMapping("/cmnSchedulesSN")
    public List<CmnScheduleSolver> show() {
        System.out.println(App.Start());
        APIResponse apiResponse = null;
        List<CmnScheduleSolver> snCmnSchedulesSolver = new ArrayList<>();
        long startTime = 0;
        long endTime = 0;
        String tag = "[CmnSchedule] ";
        try {
            startTime = System.currentTimeMillis();
            String result = rest.responseByEndPoint(EndPointSN.CmnSchedule());
            endTime = (System.currentTimeMillis() - startTime);
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JSONParser parser = new JSONParser();
            JSONObject resultJson = (JSONObject) parser.parse(result);
            JSONArray ListCmnScheduleJson = new JSONArray();
            if (resultJson.get("result") != null)
                ListCmnScheduleJson = (JSONArray) parser.parse(resultJson.get("result").toString());
            final int[] count = {1};
            ListCmnScheduleJson.forEach(cmnScheduleJson -> {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                try {
                    CmnScheduleSolver cmnScheduleSolver = objectMapper.readValue(cmnScheduleJson.toString(), CmnScheduleSolver.class);
                    snCmnSchedulesSolver.add(cmnScheduleSolver);
                    CmnSchedule cmnSchedule = new CmnSchedule();
                    cmnSchedule.setParent(cmnScheduleSolver.getParent());
                    cmnSchedule.setDocument(cmnScheduleSolver.getDocument());
                    cmnSchedule.setDescription(cmnScheduleSolver.getDescription());
                    cmnSchedule.setSysUpdatedOn(cmnScheduleSolver.getSys_updated_on());
                    cmnSchedule.setType(cmnScheduleSolver.getType());
                    cmnSchedule.setDocumentKey(cmnScheduleSolver.getDocument_key());
                    cmnSchedule.setSysUpdatedBy(cmnScheduleSolver.getSys_updated_by());
                    cmnSchedule.setSysCreatedOn(cmnScheduleSolver.getSys_created_on());
                    cmnSchedule.setSysName(cmnScheduleSolver.getSys_name());
                    cmnSchedule.setSysCreatedBy(cmnScheduleSolver.getSys_created_by());
                    cmnSchedule.setLabel(cmnScheduleSolver.getLabel());
                    cmnSchedule.setCalendarName(cmnScheduleSolver.getCalendar_name());
                    cmnSchedule.setTimeZone(cmnScheduleSolver.getTime_zone());
                    cmnSchedule.setSysUpdateName(cmnScheduleSolver.getSys_update_name());
                    cmnSchedule.setName(cmnScheduleSolver.getName());
                    cmnSchedule.setIntegrationId(cmnScheduleSolver.getSys_id());
                    Domain domain = getDomainByIntegrationId((JSONObject) cmnScheduleJson, SnTable.Domain.get(), App.Value());
                    if (domain != null)
                        cmnSchedule.setDomain(domain);
                    CmnSchedule exists = cmnScheduleService.findByIntegrationId(cmnSchedule.getIntegrationId());
                    String tagAction = App.CreateConsole();
                    if (exists != null) {
                        cmnSchedule.setId(exists.getId());
                        tagAction = App.UpdateConsole();
                    }

                    Util.printData(tag, count[0], tagAction.concat(Util.getFieldDisplay(cmnSchedule)), Util.getFieldDisplay(cmnSchedule.getDomain()));
                    cmnScheduleService.save(cmnSchedule);
                    count[0] = count[0] + 1;
                } catch (JsonProcessingException e) {
                    System.out.println(tag.concat("Exception (I) : ").concat(String.valueOf(e)));
                }
            });

            apiResponse = mapper.readValue(result, APIResponse.class);

            APIExecutionStatus status = new APIExecutionStatus();
            status.setUri(EndPointSN.Catalog());
            status.setUserAPI(App.SNUser());
            status.setPasswordAPI(App.SNPassword());
            status.setError(apiResponse.getError());
            status.setMessage(apiResponse.getMessage());
            status.setExecutionTime(endTime);
            statusService.save(status);


        } catch (Exception e) {
            System.out.println(tag.concat("Exception (II) : ").concat(String.valueOf(e)));
        }
        System.out.println(App.End());
        return snCmnSchedulesSolver;
    }

    public Domain getDomainByIntegrationId(JSONObject jsonObject, String levelOne, String levelTwo) {
        String integrationId = Util.getIdByJson(jsonObject, levelOne, levelTwo);
        if (Util.hasData(integrationId)) {
            return domainService.findByIntegrationId(integrationId);
        } else
            return null;
    }
}


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
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            final CmnSchedule[] cmnSchedule = {new CmnSchedule()};
            final Domain[] domain = new Domain[1];
            final CmnSchedule[] exists = new CmnSchedule[1];
            final String[] tagAction = new String[1];
            final CmnScheduleSolver[] cmnScheduleSolver = {new CmnScheduleSolver()};
            APIExecutionStatus status = new APIExecutionStatus();
            if (resultJson.get("result") != null)
                ListCmnScheduleJson = (JSONArray) parser.parse(resultJson.get("result").toString());
            final int[] count = {1};
            ListCmnScheduleJson.forEach(cmnScheduleJson -> {
                try {
                    cmnScheduleSolver[0] = objectMapper.readValue(cmnScheduleJson.toString(), CmnScheduleSolver.class);
                    snCmnSchedulesSolver.add(cmnScheduleSolver[0]);
                    cmnSchedule[0] = new CmnSchedule();
                    cmnSchedule[0].setParent(cmnScheduleSolver[0].getParent());
                    cmnSchedule[0].setDocument(cmnScheduleSolver[0].getDocument());
                    cmnSchedule[0].setDescription(cmnScheduleSolver[0].getDescription());
                    cmnSchedule[0].setSysUpdatedOn(cmnScheduleSolver[0].getSys_updated_on());
                    cmnSchedule[0].setType(cmnScheduleSolver[0].getType());
                    cmnSchedule[0].setDocumentKey(cmnScheduleSolver[0].getDocument_key());
                    cmnSchedule[0].setSysUpdatedBy(cmnScheduleSolver[0].getSys_updated_by());
                    cmnSchedule[0].setSysCreatedOn(cmnScheduleSolver[0].getSys_created_on());
                    cmnSchedule[0].setSysName(cmnScheduleSolver[0].getSys_name());
                    cmnSchedule[0].setSysCreatedBy(cmnScheduleSolver[0].getSys_created_by());
                    cmnSchedule[0].setLabel(cmnScheduleSolver[0].getLabel());
                    cmnSchedule[0].setCalendarName(cmnScheduleSolver[0].getCalendar_name());
                    cmnSchedule[0].setTimeZone(cmnScheduleSolver[0].getTime_zone());
                    cmnSchedule[0].setSysUpdateName(cmnScheduleSolver[0].getSys_update_name());
                    cmnSchedule[0].setName(cmnScheduleSolver[0].getName());
                    cmnSchedule[0].setIntegrationId(cmnScheduleSolver[0].getSys_id());
                    domain[0] = getDomainByIntegrationId((JSONObject) cmnScheduleJson, SnTable.Domain.get(), App.Value());
                    if (domain[0] != null)
                        cmnSchedule[0].setDomain(domain[0]);
                    exists[0] = cmnScheduleService.findByIntegrationId(cmnSchedule[0].getIntegrationId());
                    tagAction[0] = App.CreateConsole();
                    if (exists[0] != null) {
                        cmnSchedule[0].setId(exists[0].getId());
                        tagAction[0] = App.UpdateConsole();
                    }
                    Util.printData(tag, count[0], tagAction[0].concat(Util.getFieldDisplay(cmnSchedule[0])), Util.getFieldDisplay(cmnSchedule[0].getDomain()));
                    cmnScheduleService.save(cmnSchedule[0]);
                    count[0] = count[0] + 1;
                } catch (JsonProcessingException e) {
                    System.out.println(tag.concat("Exception (I) : ").concat(String.valueOf(e)));
                }
            });
            apiResponse = mapper.readValue(result, APIResponse.class);
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


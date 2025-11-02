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
public class CmnScheduleController {
    @Autowired
    private ICmnScheduleService cmnScheduleService;
    @Autowired
    private IAPIExecutionStatusService statusService;
    @Autowired
    private IDomainService domainService;
    @Autowired
    private Rest rest;

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

        public Domain getDomainByIntegrationId(JSONObject jsonObject, String levelOne, String levelTwo) {
        String integrationId = Util.getIdByJson(jsonObject, levelOne, levelTwo);
        if (Util.hasData(integrationId)) {
            return domainService.findByIntegrationId(integrationId);
        } else
            return null;
    }
}


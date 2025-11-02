package com.logicalis.apisolver.controller;

import com.logicalis.apisolver.model.Choice;
import com.logicalis.apisolver.model.ChoiceFields;
import com.logicalis.apisolver.model.enums.Errors;
import com.logicalis.apisolver.model.enums.Messages;
import com.logicalis.apisolver.services.IChoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@CrossOrigin(origins = {"${app.api.settings.cross-origin.urls}", "*"})
@RestController
@RequestMapping("/api/v1")
public class ChoiceController {
    @Autowired
    private IChoiceService choiceService;

    @GetMapping("/choice/{id}")
    public ResponseEntity<?> show(@PathVariable Long id) {
        Choice choice = null;
        Map<String, Object> response = new HashMap<>();
        try {
            choice = choiceService.findById(id);
        } catch (DataAccessException e) {
            response.put("mensaje", Errors.dataAccessExceptionQuery.get());
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (Objects.isNull(choice)) {
            response.put("mensaje", Messages.notExist.get(id.toString()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Choice>(choice, HttpStatus.OK);
    }

    @GetMapping("/choice/{integrationId}")
    public ResponseEntity<?> show(@PathVariable String integrationId) {
        Choice choice = null;
        Map<String, Object> response = new HashMap<>();

        try {
            choice = choiceService.findByIntegrationId(integrationId);
        } catch (DataAccessException e) {
            response.put("mensaje", Errors.dataAccessExceptionQuery.get());
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (Objects.isNull(choice)) {
            response.put("mensaje", Messages.notExist.get(integrationId.toString()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Choice>(choice, HttpStatus.OK);
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/choice")
    public ResponseEntity<?> create(@RequestBody Choice choice) {
        Choice newChoice = null;

        Map<String, Object> response = new HashMap<>();
        try {
            newChoice = choiceService.save(choice);
        } catch (DataAccessException e) {
            response.put("mensaje", Errors.dataAccessExceptionInsert.get());
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", Messages.createOK.get());
        response.put("choice", newChoice);
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }

    @Secured("ROLE_ADMIN")
    @PutMapping("/choice/{id}")
    public ResponseEntity<?> update(@RequestBody Choice choice, @PathVariable Long id) {
        Choice currentChoice = choiceService.findById(id);
        Choice choiceUpdated = null;
        Map<String, Object> response = new HashMap<>();
        if (Objects.isNull(currentChoice)) {
            response.put("mensaje", Errors.dataAccessExceptionUpdate.get(id.toString()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }
        try {
            currentChoice.setName(choice.getName());
            choiceUpdated = choiceService.save(currentChoice);

        } catch (DataAccessException e) {
            response.put("mensaje", Errors.dataAccessExceptionUpdate.get());
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", Messages.UpdateOK.get());
        response.put("choice", choiceUpdated);
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }

    @GetMapping("/choicesByIncident")
    public ResponseEntity<List<ChoiceFields>> choicesByIncident() {
        List<ChoiceFields> list = new ArrayList<>();
        list = choiceService.choicesByIncident();
        ResponseEntity<List<ChoiceFields>> pageResponseEntity = new ResponseEntity<>(list, HttpStatus.OK);
        return pageResponseEntity;
    }

    @GetMapping("/choicesByScRequestItem")
    public ResponseEntity<List<ChoiceFields>> choicesByScRequestItem() {
        List<ChoiceFields> list = new ArrayList<>();
        list = choiceService.choicesByScRequestItem();
        ResponseEntity<List<ChoiceFields>> pageResponseEntity = new ResponseEntity<>(list, HttpStatus.OK);
        return pageResponseEntity;
    }

    @GetMapping("/choicesByScTask")
    public ResponseEntity<List<ChoiceFields>> choicesByScTask() {
        List<ChoiceFields> list = new ArrayList<>();
        list = choiceService.choicesByScTask();
        ResponseEntity<List<ChoiceFields>> pageResponseEntity = new ResponseEntity<>(list, HttpStatus.OK);
        return pageResponseEntity;
    }
}


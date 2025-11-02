package com.logicalis.apisolver.controller;

import com.logicalis.apisolver.model.Contract;
import com.logicalis.apisolver.model.enums.Errors;
import com.logicalis.apisolver.model.enums.Messages;
import com.logicalis.apisolver.services.IContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = {"${app.api.settings.cross-origin.urls}", "*"})
@RestController
@RequestMapping("/api/v1")
public class ContractController {
    @Autowired
    private IContractService contractService;

    @GetMapping("/contract/{id}")
    public ResponseEntity<?> show(@PathVariable Long id) {

        Contract contract = null;
        Map<String, Object> response = new HashMap<>();

        try {
            contract = contractService.findById(id);
        } catch (DataAccessException e) {
            response.put("mensaje", Errors.dataAccessExceptionQuery.get());
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (contract == null) {
            response.put("mensaje", Messages.notExist.get(id.toString()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<Contract>(contract, HttpStatus.OK);
    }

    @GetMapping("/contract/{integrationId}")
    public ResponseEntity<?> show(@PathVariable String integrationId) {
        Contract contract = null;
        Map<String, Object> response = new HashMap<>();

        try {
            contract = contractService.findByIntegrationId(integrationId);
        } catch (DataAccessException e) {
            response.put("mensaje", Errors.dataAccessExceptionQuery.get());
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (contract == null) {
            response.put("mensaje", Messages.notExist.get(integrationId.toString()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<Contract>(contract, HttpStatus.OK);
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/contract")
    public ResponseEntity<?> create(@RequestBody Contract contract) {
        Contract newContract = null;

        Map<String, Object> response = new HashMap<>();
        try {
            newContract = contractService.save(contract);
        } catch (DataAccessException e) {
            response.put("mensaje", Errors.dataAccessExceptionInsert.get());
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", Messages.createOK.get());
        response.put("contract", newContract);

        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }

    @Secured("ROLE_ADMIN")
    @PutMapping("/contract/{id}")
    public ResponseEntity<?> update(@RequestBody Contract contract, @PathVariable Long id) {
        Contract currentContract = contractService.findById(id);
        Contract contractUpdated = null;

        Map<String, Object> response = new HashMap<>();

        if (currentContract == null) {
            response.put("mensaje", Errors.dataAccessExceptionUpdate.get(id.toString()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }

        try {
            currentContract.setName(contract.getName());
            contractUpdated = contractService.save(currentContract);

        } catch (DataAccessException e) {
            response.put("mensaje", Errors.dataAccessExceptionUpdate.get());
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", Messages.UpdateOK.get());
        response.put("contract", contractUpdated);

        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }

}


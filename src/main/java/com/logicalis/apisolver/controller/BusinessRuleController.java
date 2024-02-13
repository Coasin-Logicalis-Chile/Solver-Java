package com.logicalis.apisolver.controller;

import com.logicalis.apisolver.model.BusinessRule;
import com.logicalis.apisolver.model.enums.Errors;
import com.logicalis.apisolver.model.enums.Messages;
import com.logicalis.apisolver.services.IBusinessRuleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@CrossOrigin(origins = {"${app.api.settings.cross-origin.urls}", "*"})
@RestController
@RequestMapping("/api/v1")
@Slf4j
public class BusinessRuleController {
	@Autowired
	private IBusinessRuleService businessRuleService;
	
	@GetMapping("/businessRules")
	public List<BusinessRule> index() {
		return businessRuleService.findAll();
	}
	
	@GetMapping("/businessRule/{id}")
	public ResponseEntity<?> show(@PathVariable Long id) {
		
		BusinessRule businessRule = null;
		Map<String, Object> response = new HashMap<>();
		
		try{
			businessRule = businessRuleService.findById(id);
		} catch(DataAccessException e){
			response.put("mensaje", Errors.dataAccessExceptionQuery.get());
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		if(Objects.isNull(businessRule)){
			response.put("mensaje", Messages.notExist.get(id.toString()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<BusinessRule>(businessRule, HttpStatus.OK);
	}

	@Secured("ROLE_ADMIN")
	@PostMapping("/businessRule")
	public ResponseEntity<?> create(@RequestBody BusinessRule businessRule) {
		BusinessRule newBusinessRule = null;

		Map<String, Object> response = new HashMap<>();
		try {
			newBusinessRule = businessRuleService.save(businessRule);
		} catch (DataAccessException e) {
			response.put("mensaje", Errors.dataAccessExceptionInsert.get());
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", Messages.createOK.get());
		response.put("businessRule", newBusinessRule);

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}

	@Secured("ROLE_ADMIN")
	@PutMapping("/businessRule/{id}")
	public ResponseEntity<?> update(@RequestBody BusinessRule businessRule, @PathVariable Long id) {

		BusinessRule currentBusinessRule = businessRuleService.findById(id);
		BusinessRule businessRuleUpdated = null;
		
		Map<String, Object> response = new HashMap<>();

		if (Objects.isNull(currentBusinessRule)) {
			response.put("mensaje",Errors.dataAccessExceptionUpdate.get(id.toString()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}

		try {
			currentBusinessRule.setName(businessRule.getName());
			businessRuleUpdated = businessRuleService.save(currentBusinessRule);

		} catch (DataAccessException e) {
			response.put("mensaje", Errors.dataAccessExceptionUpdate.get());
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", Messages.UpdateOK.get());
		response.put("businessRule", businessRuleUpdated);

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}

	@Secured("ROLE_ADMIN")
	@DeleteMapping("/businessRule/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {

		Map<String, Object> response = new HashMap<>();
		try {
			businessRuleService.delete(id);
		} catch (DataAccessException e) {
			response.put("mensaje",Errors.dataAccessExceptionDelete.get());
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", Messages.DeleteOK.get());
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}
}


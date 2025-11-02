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

	
}


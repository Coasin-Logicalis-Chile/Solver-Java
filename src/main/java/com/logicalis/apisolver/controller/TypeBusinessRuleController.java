package com.logicalis.apisolver.controller;

import com.logicalis.apisolver.model.TypeBusinessRule;
import com.logicalis.apisolver.model.enums.Errors;
import com.logicalis.apisolver.model.enums.Messages;
import com.logicalis.apisolver.services.ITypeBusinessRuleService;
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
public class TypeBusinessRuleController {
	@Autowired
	private ITypeBusinessRuleService typeBusinessRuleService;
	
	@GetMapping("/typeBusinessRules")
	public List<TypeBusinessRule> index() {
		return typeBusinessRuleService.findAll();
	}
	
	@GetMapping("/typeBusinessRule/{id}")
	public ResponseEntity<?> show(@PathVariable Long id) {
		TypeBusinessRule typeBusinessRule = null;
		Map<String, Object> response = new HashMap<>();
		try{
			typeBusinessRule = typeBusinessRuleService.findById(id);
		} catch(DataAccessException e){
			response.put("mensaje", Errors.dataAccessExceptionQuery.get());
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if(typeBusinessRule == null){
			response.put("mensaje", Messages.notExist.get(id.toString()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<TypeBusinessRule>(typeBusinessRule, HttpStatus.OK);
	}

	@Secured("ROLE_ADMIN")
	@PostMapping("/typeBusinessRule")
	public ResponseEntity<?> create(@RequestBody TypeBusinessRule typeBusinessRule) {
		TypeBusinessRule newTypeBusinessRule = null;
		Map<String, Object> response = new HashMap<>();
		try {
			newTypeBusinessRule = typeBusinessRuleService.save(typeBusinessRule);
		} catch (DataAccessException e) {
			response.put("mensaje", Errors.dataAccessExceptionInsert.get());
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", Messages.createOK.get());
		response.put("typeBusinessRule", newTypeBusinessRule);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}

	@Secured("ROLE_ADMIN")
	@PutMapping("/typeBusinessRule/{id}")
	public ResponseEntity<?> update(@RequestBody TypeBusinessRule typeBusinessRule, @PathVariable Long id) {
		TypeBusinessRule currentTypeBusinessRule = typeBusinessRuleService.findById(id);
		TypeBusinessRule typeBusinessRuleUpdated = null;
		Map<String, Object> response = new HashMap<>();
		if (currentTypeBusinessRule == null) {
			response.put("mensaje",Errors.dataAccessExceptionUpdate.get(id.toString()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		try {
			currentTypeBusinessRule.setName(typeBusinessRule.getName());
			typeBusinessRuleUpdated = typeBusinessRuleService.save(currentTypeBusinessRule);

		} catch (DataAccessException e) {
			response.put("mensaje", Errors.dataAccessExceptionUpdate.get());
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", Messages.UpdateOK.get());
		response.put("typeBusinessRule", typeBusinessRuleUpdated);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}

	@Secured("ROLE_ADMIN")
	@DeleteMapping("/typeBusinessRule/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		Map<String, Object> response = new HashMap<>();
		try {
			typeBusinessRuleService.delete(id);
		} catch (DataAccessException e) {
			response.put("mensaje",Errors.dataAccessExceptionDelete.get());
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", Messages.DeleteOK.get());
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}
}


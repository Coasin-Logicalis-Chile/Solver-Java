package com.logicalis.apisolver.controller;

import com.logicalis.apisolver.model.ScCategory;
import com.logicalis.apisolver.model.enums.Errors;
import com.logicalis.apisolver.model.enums.Messages;
import com.logicalis.apisolver.services.IScCategoryService;
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
public class ScCategoryController {
	@Autowired
	private IScCategoryService scCategoryService;
	
	@GetMapping("/scCategory/{id}")
	public ResponseEntity<?> show(@PathVariable Long id) {
		ScCategory scCategory = null;
		Map<String, Object> response = new HashMap<>();
		try{
			scCategory = scCategoryService.findById(id);
		} catch(DataAccessException e){
			response.put("mensaje", Errors.dataAccessExceptionQuery.get());
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if(scCategory == null){
			response.put("mensaje", Messages.notExist.get(id.toString()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<ScCategory>(scCategory, HttpStatus.OK);
	}

	@Secured("ROLE_ADMIN")
	@PostMapping("/scCategory")
	public ResponseEntity<?> create(@RequestBody ScCategory scCategory) {
		ScCategory newScCategory = null;
		Map<String, Object> response = new HashMap<>();
		try {
			newScCategory = scCategoryService.save(scCategory);
		} catch (DataAccessException e) {
			response.put("mensaje", Errors.dataAccessExceptionInsert.get());
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", Messages.createOK.get());
		response.put("scCategory", newScCategory);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}

	@Secured("ROLE_ADMIN")
	@PutMapping("/scCategory/{id}")
	public ResponseEntity<?> update(@RequestBody ScCategory scCategory, @PathVariable Long id) {
		ScCategory currentScCategory = scCategoryService.findById(id);
		ScCategory scCategoryUpdated = null;
		Map<String, Object> response = new HashMap<>();
		if (currentScCategory == null) {
			response.put("mensaje",Errors.dataAccessExceptionUpdate.get(id.toString()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		try {
			currentScCategory.setTitle(scCategory.getTitle());
			scCategoryUpdated = scCategoryService.save(currentScCategory);

		} catch (DataAccessException e) {
			response.put("mensaje", Errors.dataAccessExceptionUpdate.get());
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", Messages.UpdateOK.get());
		response.put("scCategory", scCategoryUpdated);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}

	@GetMapping("/scCategory/{integration_id}")
	public ResponseEntity<?> show(@PathVariable String integration_id) {
		ScCategory  scCategory  = null;
		Map<String, Object> response = new HashMap<>();
		try{
			scCategory = scCategoryService.findByIntegrationId(integration_id);
		} catch(DataAccessException e){
			response.put("mensaje", Errors.dataAccessExceptionQuery.get());
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if(scCategory == null){
			response.put("mensaje", "El registro ".concat(integration_id.concat(" no existe en la base de datos!")));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<ScCategory>(scCategory, HttpStatus.OK);
	}
}


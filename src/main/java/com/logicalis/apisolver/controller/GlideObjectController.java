
package com.logicalis.apisolver.controller;

import com.logicalis.apisolver.model.GlideObject;
import com.logicalis.apisolver.model.enums.Errors;
import com.logicalis.apisolver.model.enums.Messages;
import com.logicalis.apisolver.services.IGlideObjectService;
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
public class GlideObjectController {

	@Autowired
	private IGlideObjectService glideObjectService;
	
	@GetMapping("/glideObjects")
	public List<GlideObject> index() {
		return glideObjectService.findAll();
	}
	
	@GetMapping("/glideObject/{id}")
	public ResponseEntity<?> show(@PathVariable Long id) {
		
		GlideObject glideObject = null;
		Map<String, Object> response = new HashMap<>();
		
		try{
			glideObject = glideObjectService.findById(id);
		} catch(DataAccessException e){
			response.put("mensaje", Errors.dataAccessExceptionQuery.get());
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		if(glideObject == null){
			response.put("mensaje", Messages.notExist.get(id.toString()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		
		return new ResponseEntity<GlideObject>(glideObject, HttpStatus.OK);
	}

	@Secured("ROLE_ADMIN")
	@PostMapping("/glideObject")
	public ResponseEntity<?> create(@RequestBody GlideObject glideObject) {
		GlideObject newGlideObject = null;

		Map<String, Object> response = new HashMap<>();
		try {
			newGlideObject = glideObjectService.save(glideObject);
		} catch (DataAccessException e) {
			response.put("mensaje", Errors.dataAccessExceptionInsert.get());
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", Messages.createOK.get());
		response.put("glideObject", newGlideObject);

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}

	@Secured("ROLE_ADMIN")
	@PutMapping("/glideObject/{id}")
	public ResponseEntity<?> update(@RequestBody GlideObject glideObject, @PathVariable Long id) {

		GlideObject currentGlideObject = glideObjectService.findById(id);
		GlideObject glideObjectUpdated = null;
		
		Map<String, Object> response = new HashMap<>();

		if (currentGlideObject == null) {
			response.put("mensaje",Errors.dataAccessExceptionUpdate.get(id.toString()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}

		try {
			currentGlideObject.setName(glideObject.getName());
			glideObjectUpdated = glideObjectService.save(currentGlideObject);

		} catch (DataAccessException e) {
			response.put("mensaje", Errors.dataAccessExceptionUpdate.get());
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", Messages.UpdateOK.get());
		response.put("glideObject", glideObjectUpdated);

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}

	@Secured("ROLE_ADMIN")
	@DeleteMapping("/glideObject/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {

		Map<String, Object> response = new HashMap<>();
		try {
			glideObjectService.delete(id);
		} catch (DataAccessException e) {
			response.put("mensaje",Errors.dataAccessExceptionDelete.get());
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", Messages.DeleteOK.get());
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}
}


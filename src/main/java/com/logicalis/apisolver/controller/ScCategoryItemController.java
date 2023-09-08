
package com.logicalis.apisolver.controller;

import com.logicalis.apisolver.model.ScCategoryItem;
import com.logicalis.apisolver.model.enums.Errors;
import com.logicalis.apisolver.model.enums.Messages;
import com.logicalis.apisolver.services.IScCategoryItemService;
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
public class ScCategoryItemController {

	@Autowired
	private IScCategoryItemService scCategoryItemService;
	
	@GetMapping("/scCategoryItems")
	public List<ScCategoryItem> index() {
		return scCategoryItemService.findAll();
	}
	
	@GetMapping("/scCategoryItem/{id}")
	public ResponseEntity<?> show(@PathVariable Long id) {
		
		ScCategoryItem scCategoryItem = null;
		Map<String, Object> response = new HashMap<>();
		
		try{
			scCategoryItem = scCategoryItemService.findById(id);
		} catch(DataAccessException e){
			response.put("mensaje", Errors.dataAccessExceptionQuery.get());
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		if(scCategoryItem == null){
			response.put("mensaje", Messages.notExist.get(id.toString()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		
		return new ResponseEntity<ScCategoryItem>(scCategoryItem, HttpStatus.OK);
	}

	@Secured("ROLE_ADMIN")
	@PostMapping("/scCategoryItem")
	public ResponseEntity<?> create(@RequestBody ScCategoryItem scCategoryItem) {
		ScCategoryItem newScCategoryItem = null;

		Map<String, Object> response = new HashMap<>();
		try {
			newScCategoryItem = scCategoryItemService.save(scCategoryItem);
		} catch (DataAccessException e) {
			response.put("mensaje", Errors.dataAccessExceptionInsert.get());
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", Messages.createOK.get());
		response.put("scCategoryItem", newScCategoryItem);

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}

	@Secured("ROLE_ADMIN")
	@PutMapping("/scCategoryItem/{id}")
	public ResponseEntity<?> update(@RequestBody ScCategoryItem scCategoryItem, @PathVariable Long id) {

		ScCategoryItem currentScCategoryItem = scCategoryItemService.findById(id);
		ScCategoryItem scCategoryItemUpdated = null;
		
		Map<String, Object> response = new HashMap<>();

		if (currentScCategoryItem == null) {
			response.put("mensaje",Errors.dataAccessExceptionUpdate.get(id.toString()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}

		try {
			currentScCategoryItem.setName(scCategoryItem.getName());
			scCategoryItemUpdated = scCategoryItemService.save(currentScCategoryItem);

		} catch (DataAccessException e) {
			response.put("mensaje", Errors.dataAccessExceptionUpdate.get());
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", Messages.UpdateOK.get());
		response.put("scCategoryItem", scCategoryItemUpdated);

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}

	@Secured("ROLE_ADMIN")
	@DeleteMapping("/scCategoryItem/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {

		Map<String, Object> response = new HashMap<>();
		try {
			scCategoryItemService.delete(id);
		} catch (DataAccessException e) {
			response.put("mensaje",Errors.dataAccessExceptionDelete.get());
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", Messages.DeleteOK.get());
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}


	@GetMapping("/scCategoryItem/{integration_id}")
	public ResponseEntity<?> show(@PathVariable String integration_id) {
		ScCategoryItem scCategoryItem  = null;
		Map<String, Object> response = new HashMap<>();
		try{
			scCategoryItem = scCategoryItemService.findByIntegrationId(integration_id);
		} catch(DataAccessException e){
			response.put("mensaje", Errors.dataAccessExceptionQuery.get());
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if(scCategoryItem == null){
			response.put("mensaje", "El registro ".concat(integration_id.concat(" no existe en la base de datos!")));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<ScCategoryItem>(scCategoryItem, HttpStatus.OK);
	}
}


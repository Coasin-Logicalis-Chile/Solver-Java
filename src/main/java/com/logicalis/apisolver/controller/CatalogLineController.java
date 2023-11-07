package com.logicalis.apisolver.controller;

import com.logicalis.apisolver.model.CatalogLine;
import com.logicalis.apisolver.model.enums.Errors;
import com.logicalis.apisolver.model.enums.Messages;
import com.logicalis.apisolver.services.ICatalogLineService;
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
public class CatalogLineController {
	@Autowired
	private ICatalogLineService catalogLineService;
	
	@GetMapping("/catalogLines")
	public List<CatalogLine> index() {
		return catalogLineService.findAll();
	}
	
	@GetMapping("/catalogLine/{id}")
	public ResponseEntity<?> show(@PathVariable Long id) {
		CatalogLine catalogLine = null;
		Map<String, Object> response = new HashMap<>();
		
		try{
			catalogLine = catalogLineService.findById(id);
		} catch(DataAccessException e){
			response.put("mensaje", Errors.dataAccessExceptionQuery.get());
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		if(Objects.isNull(catalogLine)){
			response.put("mensaje", Messages.notExist.get(id.toString()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<CatalogLine>(catalogLine, HttpStatus.OK);
	}

	@Secured("ROLE_ADMIN")
	@PostMapping("/catalogLine")
	public ResponseEntity<?> create(@RequestBody CatalogLine catalogLine) {
		CatalogLine newCatalogLine = null;

		Map<String, Object> response = new HashMap<>();
		try {
			newCatalogLine = catalogLineService.save(catalogLine);
		} catch (DataAccessException e) {
			response.put("mensaje", Errors.dataAccessExceptionInsert.get());
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", Messages.createOK.get());
		response.put("catalogLine", newCatalogLine);

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}

	@Secured("ROLE_ADMIN")
	@PutMapping("/catalogLine/{id}")
	public ResponseEntity<?> update(@RequestBody CatalogLine catalogLine, @PathVariable Long id) {
		CatalogLine currentCatalogLine = catalogLineService.findById(id);
		CatalogLine catalogLineUpdated = null;
		Map<String, Object> response = new HashMap<>();
		if (Objects.isNull(currentCatalogLine)) {
			response.put("mensaje",Errors.dataAccessExceptionUpdate.get(id.toString()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}

		try {
			currentCatalogLine.setNumber(catalogLine.getNumber());
			catalogLineUpdated = catalogLineService.save(currentCatalogLine);

		} catch (DataAccessException e) {
			response.put("mensaje", Errors.dataAccessExceptionUpdate.get());
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", Messages.UpdateOK.get());
		response.put("catalogLine", catalogLineUpdated);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}

	@Secured("ROLE_ADMIN")
	@DeleteMapping("/catalogLine/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		Map<String, Object> response = new HashMap<>();
		try {
			catalogLineService.delete(id);
		} catch (DataAccessException e) {
			response.put("mensaje",Errors.dataAccessExceptionDelete.get());
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", Messages.DeleteOK.get());
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}

	@GetMapping("/catalogLine/{integration_id}")
	public ResponseEntity<?> show(@PathVariable String integration_id) {
		CatalogLine catalogLine = null;
		Map<String, Object> response = new HashMap<>();
		try{
			catalogLine = catalogLineService.findByIntegrationId(integration_id);
		} catch(DataAccessException e){
			response.put("mensaje", Errors.dataAccessExceptionQuery.get());
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if(Objects.isNull(catalogLine)){
			response.put("mensaje", "El registro ".concat(integration_id.concat(" no existe en la base de datos!")));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<CatalogLine>(catalogLine, HttpStatus.OK);
	}
}


package com.logicalis.apisolver.controller;

import com.logicalis.apisolver.model.Catalog;
import com.logicalis.apisolver.model.enums.Errors;
import com.logicalis.apisolver.model.enums.Messages;
import com.logicalis.apisolver.services.ICatalogService;
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
public class CatalogController {
	@Autowired
	private ICatalogService catalogService;
	
	@GetMapping("/catalogs")
	public List<Catalog> index() {
		return catalogService.findAll();
	}
	
	@GetMapping("/catalog/{id}")
	public ResponseEntity<?> show(@PathVariable Long id) {
		Catalog catalog = null;
		Map<String, Object> response = new HashMap<>();
		
		try{
			catalog = catalogService.findById(id);
		} catch(DataAccessException e){
			response.put("mensaje", Errors.dataAccessExceptionQuery.get());
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		if(Objects.isNull(catalog)){
			response.put("mensaje", Messages.notExist.get(id.toString()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<Catalog>(catalog, HttpStatus.OK);
	}

	@Secured("ROLE_ADMIN")
	@PostMapping("/catalog")
	public ResponseEntity<?> create(@RequestBody Catalog catalog) {
		Catalog newCatalog = null;

		Map<String, Object> response = new HashMap<>();
		try {
			newCatalog = catalogService.save(catalog);
		} catch (DataAccessException e) {
			response.put("mensaje", Errors.dataAccessExceptionInsert.get());
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", Messages.createOK.get());
		response.put("catalog", newCatalog);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}

	@Secured("ROLE_ADMIN")
	@PutMapping("/catalog/{id}")
	public ResponseEntity<?> update(@RequestBody Catalog catalog, @PathVariable Long id) {
		Catalog currentCatalog = catalogService.findById(id);
		Catalog catalogUpdated = null;
		
		Map<String, Object> response = new HashMap<>();

		if (Objects.isNull(currentCatalog)) {
			response.put("mensaje",Errors.dataAccessExceptionUpdate.get(id.toString()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}

		try {
			currentCatalog.setTitle(catalog.getTitle());
			catalogUpdated = catalogService.save(currentCatalog);

		} catch (DataAccessException e) {
			response.put("mensaje", Errors.dataAccessExceptionUpdate.get());
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", Messages.UpdateOK.get());
		response.put("catalog", catalogUpdated);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}

	@Secured("ROLE_ADMIN")
	@DeleteMapping("/catalog/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		Map<String, Object> response = new HashMap<>();
		try {
			catalogService.delete(id);
		} catch (DataAccessException e) {
			response.put("mensaje",Errors.dataAccessExceptionDelete.get());
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", Messages.DeleteOK.get());
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}

	@GetMapping("/catalog/{integration_id}")
	public ResponseEntity<?> show(@PathVariable String integration_id) {
		Catalog catalog = null;
		Map<String, Object> response = new HashMap<>();
		try{
			catalog = catalogService.findByIntegrationId(integration_id);
		} catch(DataAccessException e){
			response.put("mensaje", Errors.dataAccessExceptionQuery.get());
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if(Objects.isNull(catalog)){
			response.put("mensaje", "El registro ".concat(integration_id.concat(" no existe en la base de datos!")));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<Catalog>(catalog, HttpStatus.OK);
	}
}


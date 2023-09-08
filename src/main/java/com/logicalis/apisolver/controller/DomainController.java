
package com.logicalis.apisolver.controller;

import com.logicalis.apisolver.model.Domain;
import com.logicalis.apisolver.model.enums.Errors;
import com.logicalis.apisolver.model.enums.Messages;
import com.logicalis.apisolver.services.IDomainService;
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
public class DomainController {

	@Autowired
	private IDomainService domainService;
	
	@GetMapping("/domains")
	public List<Domain> index() {
		return domainService.findAll();
	}
	
	@GetMapping("/domain/{id}")
	public ResponseEntity<?> show(@PathVariable Long id) {
		
		Domain domain = null;
		Map<String, Object> response = new HashMap<>();
		
		try{
			domain = domainService.findById(id);
		} catch(DataAccessException e){
			response.put("mensaje", Errors.dataAccessExceptionQuery.get());
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		if(domain == null){
			response.put("mensaje", Messages.notExist.get(id.toString()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		
		return new ResponseEntity<Domain>(domain, HttpStatus.OK);
	}

	@Secured("ROLE_ADMIN")
	@PostMapping("/domain")
	public ResponseEntity<?> create(@RequestBody Domain domain) {
		Domain newDomain = null;

		Map<String, Object> response = new HashMap<>();
		try {
			newDomain = domainService.save(domain);
		} catch (DataAccessException e) {
			response.put("mensaje", Errors.dataAccessExceptionInsert.get());
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", Messages.createOK.get());
		response.put("domain", newDomain);

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}

	@Secured("ROLE_ADMIN")
	@PutMapping("/domain/{id}")
	public ResponseEntity<?> update(@RequestBody Domain domain, @PathVariable Long id) {

		Domain currentDomain = domainService.findById(id);
		Domain domainUpdated = null;
		
		Map<String, Object> response = new HashMap<>();

		if (currentDomain == null) {
			response.put("mensaje",Errors.dataAccessExceptionUpdate.get(id.toString()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}

		try {
			currentDomain.setName(domain.getName());
			domainUpdated = domainService.save(currentDomain);

		} catch (DataAccessException e) {
			response.put("mensaje", Errors.dataAccessExceptionUpdate.get());
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", Messages.UpdateOK.get());
		response.put("domain", domainUpdated);

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}

	@Secured("ROLE_ADMIN")
	@DeleteMapping("/domain/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {

		Map<String, Object> response = new HashMap<>();
		try {
			domainService.delete(id);
		} catch (DataAccessException e) {
			response.put("mensaje",Errors.dataAccessExceptionDelete.get());
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", Messages.DeleteOK.get());
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}
	/*@GetMapping("/domain/{name}")
	public ResponseEntity<?> show(@PathVariable String name) {
		Domain domain = null;
		Map<String, Object> response = new HashMap<>();
		try{
			domain = domainService.findByName(name);
		} catch(DataAccessException e){
			response.put("mensaje", Errors.dataAccessExceptionQuery.get());
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if(domain == null){
			//response.put("mensaje", Messages.notExist.get(name));
			response.put("mensaje", "El registro ".concat(name.concat(" no existe en la base de datos!")));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<Domain>(domain, HttpStatus.OK);
	}*/

	@GetMapping("/domain/{integration_id}")
	public ResponseEntity<?> show(@PathVariable String integration_id) {
		Domain domain = null;
		Map<String, Object> response = new HashMap<>();
		try{
			domain = domainService.findByIntegrationId(integration_id);
		} catch(DataAccessException e){
			response.put("mensaje", Errors.dataAccessExceptionQuery.get());
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if(domain == null){
			//response.put("mensaje", Messages.notExist.get(name));
			response.put("mensaje", "El registro ".concat(integration_id.concat(" no existe en la base de datos!")));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<Domain>(domain, HttpStatus.OK);
	}


}


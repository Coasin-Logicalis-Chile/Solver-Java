
package com.logicalis.apisolver.controller;

import com.logicalis.apisolver.model.Location;
import com.logicalis.apisolver.model.enums.Errors;
import com.logicalis.apisolver.model.enums.Messages;
import com.logicalis.apisolver.services.ILocationService;
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
public class LocationController {

	@Autowired
	private ILocationService locationService;
	
	@GetMapping("/locations")
	public List<Location> index() {
		return locationService.findAll();
	}
	
	@GetMapping("/location/{id}")
	public ResponseEntity<?> show(@PathVariable Long id) {
		
		Location location = null;
		Map<String, Object> response = new HashMap<>();
		
		try{
			location = locationService.findById(id);
		} catch(DataAccessException e){
			response.put("mensaje", Errors.dataAccessExceptionQuery.get());
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		if(location == null){
			response.put("mensaje", Messages.notExist.get(id.toString()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		
		return new ResponseEntity<Location>(location, HttpStatus.OK);
	}

	@Secured("ROLE_ADMIN")
	@PostMapping("/location")
	public ResponseEntity<?> create(@RequestBody Location location) {
		Location newLocation = null;

		Map<String, Object> response = new HashMap<>();
		try {
			newLocation = locationService.save(location);
		} catch (DataAccessException e) {
			response.put("mensaje", Errors.dataAccessExceptionInsert.get());
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", Messages.createOK.get());
		response.put("location", newLocation);

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}

	@Secured("ROLE_ADMIN")
	@PutMapping("/location/{id}")
	public ResponseEntity<?> update(@RequestBody Location location, @PathVariable Long id) {

		Location currentLocation = locationService.findById(id);
		Location locationUpdated = null;
		
		Map<String, Object> response = new HashMap<>();

		if (currentLocation == null) {
			response.put("mensaje",Errors.dataAccessExceptionUpdate.get(id.toString()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}

		try {
			currentLocation.setName(location.getName());
			locationUpdated = locationService.save(currentLocation);

		} catch (DataAccessException e) {
			response.put("mensaje", Errors.dataAccessExceptionUpdate.get());
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", Messages.UpdateOK.get());
		response.put("location", locationUpdated);

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}

	@Secured("ROLE_ADMIN")
	@DeleteMapping("/location/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {

		Map<String, Object> response = new HashMap<>();
		try {
			locationService.delete(id);
		} catch (DataAccessException e) {
			response.put("mensaje",Errors.dataAccessExceptionDelete.get());
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", Messages.DeleteOK.get());
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}

	@GetMapping("/location/{integration_id}")
	public ResponseEntity<?> show(@PathVariable String integration_id) {
		Location location = null;
		Map<String, Object> response = new HashMap<>();
		try{
			location = locationService.findByIntegrationId(integration_id);
		} catch(DataAccessException e){
			response.put("mensaje", Errors.dataAccessExceptionQuery.get());
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if(location == null){
			response.put("mensaje", "El registro ".concat(integration_id.concat(" no existe en la base de datos!")));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<Location>(location, HttpStatus.OK);
	}
/*
	@GetMapping("/location/{integration_id}")
	public ResponseEntity<?> findByIntegrationId(@PathVariable String integration_id) {
		Location location = null;
		Map<String, Object> response = new HashMap<>();
		try{
			location = locationService.findByIntegrationId(integration_id);
		} catch(DataAccessException e){
			response.put("mensaje", Errors.dataAccessExceptionQuery.get());
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if(location == null){
			//response.put("mensaje", Messages.notExist.get(name));
			response.put("mensaje", "El registro ".concat(integration_id.concat(" no existe en la base de datos!")));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<Location>(location, HttpStatus.OK);
	}*/
}


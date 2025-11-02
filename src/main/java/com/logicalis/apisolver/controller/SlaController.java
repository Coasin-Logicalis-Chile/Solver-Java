package com.logicalis.apisolver.controller;

import com.logicalis.apisolver.model.Sla;
import com.logicalis.apisolver.model.enums.Errors;
import com.logicalis.apisolver.model.enums.Messages;
import com.logicalis.apisolver.services.ISlaService;
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
public class SlaController {
	@Autowired
	private ISlaService slaService;
	
	@GetMapping("/sla/{id}")
	public ResponseEntity<?> show(@PathVariable Long id) {
		Sla sla = null;
		Map<String, Object> response = new HashMap<>();
		try{
			sla = slaService.findById(id);
		} catch(DataAccessException e){
			response.put("mensaje", Errors.dataAccessExceptionQuery.get());
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if(Objects.isNull(sla)){
			response.put("mensaje", Messages.notExist.get(id.toString()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<Sla>(sla, HttpStatus.OK);
	}

	@Secured("ROLE_ADMIN")
	@PostMapping("/sla")
	public ResponseEntity<?> create(@RequestBody Sla sla) {
		Sla newSla = null;
		Map<String, Object> response = new HashMap<>();
		try {
			newSla = slaService.save(sla);
		} catch (DataAccessException e) {
			response.put("mensaje", Errors.dataAccessExceptionInsert.get());
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", Messages.createOK.get());
		response.put("sla", newSla);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}

	@Secured("ROLE_ADMIN")
	@PutMapping("/sla/{id}")
	public ResponseEntity<?> update(@RequestBody Sla sla, @PathVariable Long id) {
		Sla currentSla = slaService.findById(id);
		Sla slaUpdated = null;
		Map<String, Object> response = new HashMap<>();
		if (currentSla == null) {
			response.put("mensaje",Errors.dataAccessExceptionUpdate.get(id.toString()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		try {
			currentSla.setName(sla.getName());
			slaUpdated = slaService.save(currentSla);
		} catch (DataAccessException e) {
			response.put("mensaje", Errors.dataAccessExceptionUpdate.get());
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", Messages.UpdateOK.get());
		response.put("sla", slaUpdated);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}

}


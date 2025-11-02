package com.logicalis.apisolver.controller;

import com.logicalis.apisolver.model.Company;
import com.logicalis.apisolver.model.enums.Errors;
import com.logicalis.apisolver.model.enums.Messages;
import com.logicalis.apisolver.services.ICompanyService;
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

@CrossOrigin(origins = {"${app.api.settings.cross-origin.urls}", "*"})
@RestController
@RequestMapping("/api/v1")
@Slf4j
public class CompanyController {
	@Autowired
	private ICompanyService companyService;
	
	@GetMapping("/company/{id}")
	public ResponseEntity<?> show(@PathVariable Long id) {
		Company company = null;
		Map<String, Object> response = new HashMap<>();
		try{
			company = companyService.findById(id);
		} catch(DataAccessException e){
			response.put("mensaje", Errors.dataAccessExceptionQuery.get());
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if(company == null){
			response.put("mensaje", Messages.notExist.get(id.toString()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<Company>(company, HttpStatus.OK);
	}

	@Secured("ROLE_ADMIN")
	@PostMapping("/company")
	public ResponseEntity<?> create(@RequestBody Company company) {
		Company newCompany = null;
		Map<String, Object> response = new HashMap<>();
		try {
			newCompany = companyService.save(company);
		} catch (DataAccessException e) {
			response.put("mensaje", Errors.dataAccessExceptionInsert.get());
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", Messages.createOK.get());
		response.put("company", newCompany);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}

	@Secured("ROLE_ADMIN")
	@PutMapping("/company/{id}")
	public ResponseEntity<?> update(@RequestBody Company company, @PathVariable Long id) {
		Company currentCompany = companyService.findById(id);
		Company companyUpdated = null;
		Map<String, Object> response = new HashMap<>();
		if (currentCompany == null) {
			response.put("mensaje",Errors.dataAccessExceptionUpdate.get(id.toString()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		try {
			currentCompany.setName(company.getName());
			companyUpdated = companyService.save(currentCompany);

		} catch (DataAccessException e) {
			response.put("mensaje", Errors.dataAccessExceptionUpdate.get());
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", Messages.UpdateOK.get());
		response.put("company", companyUpdated);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}

	@GetMapping("/company/{integration_id}")
	public ResponseEntity<?> show(@PathVariable String integration_id) {
		Company company = null;
		Map<String, Object> response = new HashMap<>();
		try{
			company = companyService.findByIntegrationId(integration_id);
		} catch(DataAccessException e){
			response.put("mensaje", Errors.dataAccessExceptionQuery.get());
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if(company == null){
			response.put("mensaje", "El registro ".concat(integration_id.concat(" no existe en la base de datos!")));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<Company>(company, HttpStatus.OK);
	}
}





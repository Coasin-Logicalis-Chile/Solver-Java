
package com.logicalis.apisolver.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.logicalis.apisolver.model.*;
import com.logicalis.apisolver.model.enums.*;
import com.logicalis.apisolver.services.*;
import com.logicalis.apisolver.util.Rest;
import com.logicalis.apisolver.util.Util;
import com.logicalis.apisolver.view.ConfigurationItemSolver;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = {"${app.api.settings.cross-origin.urls}", "*"})
@RestController
@RequestMapping("/api/v1")
public class ConfigurationItemController {
	Util util = new Util();
	App app = new App();
	EndPointSN endPointSN = new EndPointSN();
	@Autowired
	private IConfigurationItemService configurationItemService;
	@Autowired
	private IDomainService domainService;
	@Autowired
	private ILocationService locationService;
	@Autowired
	private IAPIExecutionStatusService statusService;
	@Autowired
	private ICompanyService companyService;
	@Autowired
	private ISysUserService sysUserService;
	@Autowired
	private ISysGroupService sysGroupService;
	
	@GetMapping("/configuration_items")
	public List<ConfigurationItem> index() {
		return configurationItemService.findAll();
	}

	@GetMapping("/configurationItem/{id}")
	public ResponseEntity<?> show(@PathVariable Long id) {

		ConfigurationItem configurationItem = null;
		Map<String, Object> response = new HashMap<>();

		try{
			configurationItem = configurationItemService.findById(id);
		} catch(DataAccessException e){
			response.put("mensaje", Errors.dataAccessExceptionQuery.get());
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		if(configurationItem == null){
			response.put("mensaje", Messages.notExist.get(id.toString()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<ConfigurationItem>(configurationItem, HttpStatus.OK);
	}

	@GetMapping("/configuration_item/{integrationId}")
	public ResponseEntity<?> show(@PathVariable String integrationId) {

		ConfigurationItem configurationItem = null;
		Map<String, Object> response = new HashMap<>();

		try{
			configurationItem = configurationItemService.findByIntegrationId(integrationId);
		} catch(DataAccessException e){
			response.put("mensaje", Errors.dataAccessExceptionQuery.get());
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		if(configurationItem == null){
			response.put("mensaje", Messages.notExist.get(integrationId ));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<ConfigurationItem>(configurationItem, HttpStatus.OK);
	}

	@Secured("ROLE_ADMIN")
	@PostMapping("/configurationItem")
	public ResponseEntity<?> create(@RequestBody ConfigurationItem configurationItem) {
		ConfigurationItem newConfigurationItem = null;

		Map<String, Object> response = new HashMap<>();
		try {
			newConfigurationItem = configurationItemService.save(configurationItem);
		} catch (DataAccessException e) {
			response.put("mensaje", Errors.dataAccessExceptionInsert.get());
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", Messages.createOK.get());
		response.put("configurationItem", newConfigurationItem);

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}

	@Secured("ROLE_ADMIN")
	@PutMapping("/configurationItem/{id}")
	public ResponseEntity<?> update(@RequestBody ConfigurationItem configurationItem, @PathVariable Long id) {

		ConfigurationItem currentConfigurationItem = configurationItemService.findById(id);
		ConfigurationItem configurationItemUpdated = null;
		
		Map<String, Object> response = new HashMap<>();

		if (currentConfigurationItem == null) {
			response.put("mensaje",Errors.dataAccessExceptionUpdate.get(id.toString()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}

		try {
			currentConfigurationItem.setName(configurationItem.getName());
			configurationItemUpdated = configurationItemService.save(currentConfigurationItem);

		} catch (DataAccessException e) {
			response.put("mensaje", Errors.dataAccessExceptionUpdate.get());
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", Messages.UpdateOK.get());
		response.put("configurationItem", configurationItemUpdated);

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}

	@Secured("ROLE_ADMIN")
	@DeleteMapping("/configurationItem/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {

		Map<String, Object> response = new HashMap<>();
		try {
			configurationItemService.delete(id);
		} catch (DataAccessException e) {
			response.put("mensaje",Errors.dataAccessExceptionDelete.get());
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", Messages.DeleteOK.get());
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}


	@GetMapping("/configurationItemBySolverAndQuery")
	public List<ConfigurationItem> show(String query, boolean flag) {

		System.out.println(app.Start());
		APIResponse apiResponse = null;
		List<ConfigurationItem> configurationItems = new ArrayList<>();
		String[] sparmOffSets = util.offSets50000();
		long startTime = 0;
		long endTime = 0;
		String tag = "[ConfigurationItem] ";


		try {
			Rest rest = new Rest();
			startTime = System.currentTimeMillis();
			final int[] count = {1};
			for (String sparmOffSet : sparmOffSets) {
				String result = rest.responseByEndPoint(endPointSN.ConfigurationItemByQuery().replace("QUERY", query).concat(sparmOffSet));
				System.out.println(tag.concat("(".concat(endPointSN.ConfigurationItemByQuery().replace("QUERY", query).concat(sparmOffSet)).concat(")")));

				JSONParser parser = new JSONParser();
				JSONObject resultJson = new JSONObject();
				JSONArray ListSnConfigurationItemJson = new JSONArray();
				resultJson = (JSONObject) parser.parse(result);
				if (resultJson.get("result") != null)
					ListSnConfigurationItemJson = (JSONArray) parser.parse(resultJson.get("result").toString());

				ListSnConfigurationItemJson.stream().forEach(configurationItemSolverJson -> {
					String tagAction = app.CreateConsole();
					ConfigurationItemSolver configurationItemSolver = new ConfigurationItemSolver();
					try {

						Gson gson = new Gson();
						ConfigurationItem configurationItem = new ConfigurationItem();

						configurationItemSolver = gson.fromJson(configurationItemSolverJson.toString(), ConfigurationItemSolver.class);
						ConfigurationItem exists = configurationItemService.findByIntegrationId(configurationItemSolver.getSys_id());

						if (exists != null) {
							configurationItem.setId(exists.getId());
							tagAction = app.UpdateConsole();
						}
						configurationItem.setName(configurationItemSolver.getName());
						configurationItem.setIntegrationId(configurationItemSolver.getSys_id());
						configurationItem.setDescription(configurationItemSolver.getDescription());
						configurationItem.setStatus(configurationItemSolver.getStatus());
						configurationItem.setUpdated(configurationItemSolver.getUpdated());
						configurationItem.setCreatedBy(configurationItemSolver.getSys_created_by());
						configurationItem.setAssetTag(configurationItemSolver.getAsset_tag());
						configurationItem.setAssigned(configurationItemSolver.getAssigned());
						configurationItem.setCategory(configurationItemSolver.getCategory());
						configurationItem.setComments(configurationItemSolver.getComments());
						configurationItem.setCorrelationId(configurationItemSolver.getCorrelation_id());
						configurationItem.setCostCurrency(configurationItemSolver.getCost_cc());
						configurationItem.setCreated(configurationItemSolver.getSys_created_on());
						configurationItem.setHostname(configurationItemSolver.getHostname());
						configurationItem.setImpact(configurationItemSolver.getImpact());
						configurationItem.setInstalled(configurationItemSolver.getInstalled());
						configurationItem.setModelNumber(configurationItemSolver.getModel_number());
						configurationItem.setMonitor(configurationItemSolver.getMonitor());
						configurationItem.setOperationalStatus(configurationItemSolver.getOperational_status());
						configurationItem.setSerialNumber(configurationItemSolver.getSerial_number());
						configurationItem.setSpecialInstruction(configurationItemSolver.getSpecial_instruction());
						configurationItem.setSubcategory(configurationItemSolver.getSubcategory());
						configurationItem.setUpdatedBy(configurationItemSolver.getSys_updated_by());
 

						Company company = companyService.findByIntegrationId(util.getIdByJson((JSONObject) configurationItemSolverJson, SnTable.Company.get(), app.Value()));
						configurationItem.setCompany(company);
						configurationItem.setDomain(company.getDomain());


						Location location = locationService.findByIntegrationId(util.getIdByJson((JSONObject) configurationItemSolverJson, SnTable.Location.get(), app.Value()));
						configurationItem.setLocation(location);

						Company manufacturer = companyService.findByIntegrationId(util.getIdByJson((JSONObject) configurationItemSolverJson, "manufacturer", app.Value()));
						configurationItem.setManufacturer(manufacturer);


						SysUser assignedTo = sysUserService.findByIntegrationId(util.getIdByJson((JSONObject) configurationItemSolverJson, "assigned_to", app.Value()));
						configurationItem.setAssignedTo(assignedTo);

						SysUser ownedBy = sysUserService.findByIntegrationId(util.getIdByJson((JSONObject) configurationItemSolverJson, "owned_by", app.Value()));
						configurationItem.setOwnedBy(ownedBy);

		  
						SysGroup supportGroup = sysGroupService.findByIntegrationId(util.getIdByJson((JSONObject) configurationItemSolverJson, "support_group", app.Value()));
						configurationItem.setSupportGroup(supportGroup);
		 


						configurationItemService.save(configurationItem);
						configurationItems.add(configurationItem);
						util.printData(tag, count[0], tagAction.concat(util.getFieldDisplay(configurationItem)), util.getFieldDisplay(company), util.getFieldDisplay(company.getDomain()));
						count[0] = count[0] + 1;
					} catch (Exception e) {
						System.out.println(tag.concat("Exception (I) (").concat(String.valueOf(count[0])).concat(") ").concat(String.valueOf(e)));
					}
				});

			}
			ObjectMapper mapper = new ObjectMapper();
			apiResponse = mapper.readValue("Ended process", APIResponse.class);
			APIExecutionStatus status = new APIExecutionStatus();
			status.setUri(endPointSN.Location());
			status.setUserAPI(app.SNUser());
			status.setPasswordAPI(app.SNPassword());
			status.setError(apiResponse.getError());
			status.setMessage(apiResponse.getMessage());
			endTime = (System.currentTimeMillis() - startTime);
			status.setExecutionTime(endTime);
			statusService.save(status);

		} catch (Exception e) {
			System.out.println(tag.concat("Exception (II) : ").concat(String.valueOf(e)));
		}
		System.out.println(app.End());
		return configurationItems;
	}
}


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
	@Autowired
	private Rest rest;
	
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
		System.out.println(App.Start());
		APIResponse apiResponse = null;
		List<ConfigurationItem> configurationItems = new ArrayList<>();
		String[] sparmOffSets = Util.offSets50000();
		long startTime = 0;
		long endTime = 0;
		String tag = "[ConfigurationItem] ";
		String result;
		JSONParser parser = new JSONParser();
		JSONObject resultJson = new JSONObject();
		JSONArray ListSnConfigurationItemJson = new JSONArray();
		final String[] tagAction = new String[1];
		Gson gson = new Gson();
		final ConfigurationItem[] configurationItem = {new ConfigurationItem()};
		final ConfigurationItemSolver[] configurationItemSolver = {new ConfigurationItemSolver()};
		final ConfigurationItem[] exists = new ConfigurationItem[1];
		final Company[] company = new Company[1];
		final Location[] location = new Location[1];
		final Company[] manufacturer = new Company[1];
		final SysUser[] assignedTo = new SysUser[1];
		final SysUser[] ownedBy = new SysUser[1];
		final SysGroup[] supportGroup = new SysGroup[1];
		ObjectMapper mapper = new ObjectMapper();
		APIExecutionStatus status = new APIExecutionStatus();
		try {
			startTime = System.currentTimeMillis();
			final int[] count = {1};
			for (String sparmOffSet : sparmOffSets) {
				result = rest.responseByEndPoint(EndPointSN.ConfigurationItemByQuery().replace("QUERY", query).concat(sparmOffSet));
				System.out.println(tag.concat("(".concat(EndPointSN.ConfigurationItemByQuery().replace("QUERY", query).concat(sparmOffSet)).concat(")")));
				resultJson = (JSONObject) parser.parse(result);
				ListSnConfigurationItemJson.clear();
				if (resultJson.get("result") != null)
					ListSnConfigurationItemJson = (JSONArray) parser.parse(resultJson.get("result").toString());
				ListSnConfigurationItemJson.stream().forEach(configurationItemSolverJson -> {
					tagAction[0] = App.CreateConsole();
					try {
						configurationItem[0] = new ConfigurationItem();
						configurationItemSolver[0] = gson.fromJson(configurationItemSolverJson.toString(), ConfigurationItemSolver.class);
						exists[0] = configurationItemService.findByIntegrationId(configurationItemSolver[0].getSys_id());
						if (exists[0] != null) {
							configurationItem[0].setId(exists[0].getId());
							tagAction[0] = App.UpdateConsole();
						}
						configurationItem[0].setName(configurationItemSolver[0].getName());
						configurationItem[0].setIntegrationId(configurationItemSolver[0].getSys_id());
						configurationItem[0].setDescription(configurationItemSolver[0].getDescription());
						configurationItem[0].setStatus(configurationItemSolver[0].getStatus());
						configurationItem[0].setUpdated(configurationItemSolver[0].getUpdated());
						configurationItem[0].setCreatedBy(configurationItemSolver[0].getSys_created_by());
						configurationItem[0].setAssetTag(configurationItemSolver[0].getAsset_tag());
						configurationItem[0].setAssigned(configurationItemSolver[0].getAssigned());
						configurationItem[0].setCategory(configurationItemSolver[0].getCategory());
						configurationItem[0].setComments(configurationItemSolver[0].getComments());
						configurationItem[0].setCorrelationId(configurationItemSolver[0].getCorrelation_id());
						configurationItem[0].setCostCurrency(configurationItemSolver[0].getCost_cc());
						configurationItem[0].setCreated(configurationItemSolver[0].getSys_created_on());
						configurationItem[0].setHostname(configurationItemSolver[0].getHostname());
						configurationItem[0].setImpact(configurationItemSolver[0].getImpact());
						configurationItem[0].setInstalled(configurationItemSolver[0].getInstalled());
						configurationItem[0].setModelNumber(configurationItemSolver[0].getModel_number());
						configurationItem[0].setMonitor(configurationItemSolver[0].getMonitor());
						configurationItem[0].setOperationalStatus(configurationItemSolver[0].getOperational_status());
						configurationItem[0].setSerialNumber(configurationItemSolver[0].getSerial_number());
						configurationItem[0].setSpecialInstruction(configurationItemSolver[0].getSpecial_instruction());
						configurationItem[0].setSubcategory(configurationItemSolver[0].getSubcategory());
						configurationItem[0].setUpdatedBy(configurationItemSolver[0].getSys_updated_by());

						company[0] = companyService.findByIntegrationId(Util.getIdByJson((JSONObject) configurationItemSolverJson, SnTable.Company.get(), App.Value()));
						configurationItem[0].setCompany(company[0]);
						configurationItem[0].setDomain(company[0].getDomain());

						location[0] = locationService.findByIntegrationId(Util.getIdByJson((JSONObject) configurationItemSolverJson, SnTable.Location.get(), App.Value()));
						configurationItem[0].setLocation(location[0]);

						manufacturer[0] = companyService.findByIntegrationId(Util.getIdByJson((JSONObject) configurationItemSolverJson, "manufacturer", App.Value()));
						configurationItem[0].setManufacturer(manufacturer[0]);

						assignedTo[0] = sysUserService.findByIntegrationId(Util.getIdByJson((JSONObject) configurationItemSolverJson, "assigned_to", App.Value()));
						configurationItem[0].setAssignedTo(assignedTo[0]);

						ownedBy[0] = sysUserService.findByIntegrationId(Util.getIdByJson((JSONObject) configurationItemSolverJson, "owned_by", App.Value()));
						configurationItem[0].setOwnedBy(ownedBy[0]);

						supportGroup[0] = sysGroupService.findByIntegrationId(Util.getIdByJson((JSONObject) configurationItemSolverJson, "support_group", App.Value()));
						configurationItem[0].setSupportGroup(supportGroup[0]);

						configurationItemService.save(configurationItem[0]);
						configurationItems.add(configurationItem[0]);
						Util.printData(tag, count[0], tagAction[0].concat(Util.getFieldDisplay(configurationItem[0])), Util.getFieldDisplay(company[0]), Util.getFieldDisplay(company[0].getDomain()));
						count[0] = count[0] + 1;
					} catch (Exception e) {
						System.out.println(tag.concat("Exception (I) (").concat(String.valueOf(count[0])).concat(") ").concat(String.valueOf(e)));
					}
				});
			}
			apiResponse = mapper.readValue("Ended process", APIResponse.class);
			status.setUri(EndPointSN.Location());
			status.setUserAPI(App.SNUser());
			status.setPasswordAPI(App.SNPassword());
			status.setError(apiResponse.getError());
			status.setMessage(apiResponse.getMessage());
			endTime = (System.currentTimeMillis() - startTime);
			status.setExecutionTime(endTime);
			statusService.save(status);
		} catch (Exception e) {
			System.out.println(tag.concat("Exception (II) : ").concat(String.valueOf(e)));
		}
		System.out.println(App.End());
		return configurationItems;
	}
}


package com.logicalis.apisolver.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.logicalis.apisolver.model.*;
import com.logicalis.apisolver.model.enums.*;
import com.logicalis.apisolver.services.*;
import com.logicalis.apisolver.util.Rest;
import com.logicalis.apisolver.util.Util;
import com.logicalis.apisolver.view.CiServiceSolver;
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
public class CiServiceController {
    @Autowired
    private ICiServiceService ciServiceService;
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

    @GetMapping("/ci_services")
    public List<CiService> index() {
        return ciServiceService.findAll();
    }

    @GetMapping("/ciService/{id}")
    public ResponseEntity<?> show(@PathVariable Long id) {
        CiService ciService = null;
        Map<String, Object> response = new HashMap<>();
        try {
            ciService = ciServiceService.findById(id);
        } catch (DataAccessException e) {
            response.put("mensaje", Errors.dataAccessExceptionQuery.get());
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (ciService == null) {
            response.put("mensaje", Messages.notExist.get(id.toString()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<CiService>(ciService, HttpStatus.OK);
    }

    @GetMapping("/ci_service/{integrationId}")
    public ResponseEntity<?> show(@PathVariable String integrationId) {
        CiService ciService = null;
        Map<String, Object> response = new HashMap<>();
        try {
            ciService = ciServiceService.findByIntegrationId(integrationId);
        } catch (DataAccessException e) {
            response.put("mensaje", Errors.dataAccessExceptionQuery.get());
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (ciService == null) {
            response.put("mensaje", Messages.notExist.get(integrationId));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<CiService>(ciService, HttpStatus.OK);
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/ciService")
    public ResponseEntity<?> create(@RequestBody CiService ciService) {
        CiService newCiService = null;
        Map<String, Object> response = new HashMap<>();
        try {
            newCiService = ciServiceService.save(ciService);
        } catch (DataAccessException e) {
            response.put("mensaje", Errors.dataAccessExceptionInsert.get());
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", Messages.createOK.get());
        response.put("ciService", newCiService);
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }

    @Secured("ROLE_ADMIN")
    @PutMapping("/ciService/{id}")
    public ResponseEntity<?> update(@RequestBody CiService ciService, @PathVariable Long id) {
        CiService currentCiService = ciServiceService.findById(id);
        CiService ciServiceUpdated = null;
        Map<String, Object> response = new HashMap<>();
        if (currentCiService == null) {
            response.put("mensaje", Errors.dataAccessExceptionUpdate.get(id.toString()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }
        try {
            currentCiService.setName(ciService.getName());
            ciServiceUpdated = ciServiceService.save(currentCiService);

        } catch (DataAccessException e) {
            response.put("mensaje", Errors.dataAccessExceptionUpdate.get());
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", Messages.UpdateOK.get());
        response.put("ciService", ciServiceUpdated);
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/ciService/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            ciServiceService.delete(id);
        } catch (DataAccessException e) {
            response.put("mensaje", Errors.dataAccessExceptionDelete.get());
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", Messages.DeleteOK.get());
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
    }

    @GetMapping("/ciServiceBySolverAndQuery")
    public List<CiService> show(String query, boolean flag) {
        System.out.println(App.Start());
        APIResponse apiResponse = null;
        List<CiService> ciServices = new ArrayList<>();
        String[] sparmOffSets = Util.offSets50000();
        long startTime = 0;
        long endTime = 0;
        String tag = "[CiService] ";
        try {
            startTime = System.currentTimeMillis();
            final int[] count = {1};
            String result;
            JSONParser parser = new JSONParser();
            JSONObject resultJson = new JSONObject();
            JSONArray ListSnCiServiceJson = new JSONArray();
            Gson gson = new Gson();
            final Company[] company = new Company[1];
            final Location[] location = new Location[1];
            final Company[] manufacturer = new Company[1];
            final SysUser[] assignedTo = new SysUser[1];
            final SysUser[] ownedBy = new SysUser[1];
            final SysGroup[] supportGroup = new SysGroup[1];
            final CiService[] ciService = {new CiService()};
            ObjectMapper mapper = new ObjectMapper();
            APIExecutionStatus status = new APIExecutionStatus();
            final String[] tagAction = new String[1];
            final CiServiceSolver[] ciServiceSolver = new CiServiceSolver[1];
            final CiService[] exists = new CiService[1];
            for (String sparmOffSet : sparmOffSets) {
                result = rest.responseByEndPoint(EndPointSN.CiServiceByQuery().replace("QUERY", query).concat(sparmOffSet));
                System.out.println(tag.concat("(".concat(EndPointSN.CiServiceByQuery().replace("QUERY", query).concat(sparmOffSet)).concat(")")));
                ListSnCiServiceJson.clear();
                resultJson = (JSONObject) parser.parse(result);
                if (resultJson.get("result") != null)
                    ListSnCiServiceJson = (JSONArray) parser.parse(resultJson.get("result").toString());
                ListSnCiServiceJson.stream().forEach(ciServiceSolverJson -> {
                    tagAction[0] = App.CreateConsole();
                    try {
                        ciService[0] = new CiService();
                        ciServiceSolver[0] = gson.fromJson(ciServiceSolverJson.toString(), CiServiceSolver.class);
                        exists[0] = ciServiceService.findByIntegrationId(ciServiceSolver[0].getSys_id());
                        if (exists[0] != null) {
                            ciService[0].setId(exists[0].getId());
                            tagAction[0] = App.UpdateConsole();
                        }
                        ciService[0].setName(Util.isNull(ciServiceSolver[0].getName()));
                        ciService[0].setIntegrationId(Util.isNull(ciServiceSolver[0].getSys_id()));
                        ciService[0].setShortDescription(Util.isNull(ciServiceSolver[0].getShort_description()));
                        ciService[0].setStatus(Util.isNull(ciServiceSolver[0].getStatus()));
                        ciService[0].setUpdated(Util.isNull(ciServiceSolver[0].getUpdated()));
                        ciService[0].setCreatedBy(Util.isNull(ciServiceSolver[0].getCreated_by()));
                        ciService[0].setAssetTag(Util.isNull(ciServiceSolver[0].getAsset_tag()));
                        ciService[0].setCategory(Util.isNull(ciServiceSolver[0].getCategory()));
                        ciService[0].setComments(Util.isNull(ciServiceSolver[0].getComments()));
                        ciService[0].setCorrelationId(Util.isNull(ciServiceSolver[0].getCorrelation_id()));
                        ciService[0].setCostCurrency(Util.isNull(ciServiceSolver[0].getCost_cc()));
                        ciService[0].setCreated(Util.isNull(ciServiceSolver[0].getCreated()));
                        ciService[0].setHostname(Util.isNull(ciServiceSolver[0].getHostname()));
                        ciService[0].setImpact(Util.isNull(ciServiceSolver[0].getImpact()));
                        ciService[0].setInstalled(Util.isNull(ciServiceSolver[0].getInstalled()));
                        ciService[0].setModelNumber(Util.isNull(ciServiceSolver[0].getModel_number()));
                        ciService[0].setMonitor(Util.isNull(ciServiceSolver[0].getMonitor()));
                        ciService[0].setOperationalStatus(Util.isNull(ciServiceSolver[0].getOperational_status()));
                        ciService[0].setSerialNumber(Util.isNull(ciServiceSolver[0].getSerial_number()));
                        ciService[0].setSpecialInstruction(Util.isNull(ciServiceSolver[0].getSpecial_instruction()));
                        ciService[0].setSubcategory(Util.isNull(ciServiceSolver[0].getSubcategory()));
                        ciService[0].setUpdatedBy(Util.isNull(ciServiceSolver[0].getUpdated_by()));
                        company[0] = companyService.findByIntegrationId(Util.getIdByJson((JSONObject) ciServiceSolverJson, SnTable.Company.get(), App.Value()));
                        ciService[0].setCompany(company[0]);
                        ciService[0].setDomain(company[0].getDomain());
                        location[0] = locationService.findByIntegrationId(Util.getIdByJson((JSONObject) ciServiceSolverJson, SnTable.Location.get(), App.Value()));
                        ciService[0].setLocation(location[0]);
                        manufacturer[0] = companyService.findByIntegrationId(Util.getIdByJson((JSONObject) ciServiceSolverJson, "manufacturer", App.Value()));
                        ciService[0].setManufacturer(manufacturer[0]);
                        assignedTo[0] = sysUserService.findByIntegrationId(Util.getIdByJson((JSONObject) ciServiceSolverJson, "assigned_to", App.Value()));
                        ciService[0].setAssignedTo(assignedTo[0]);
                        ownedBy[0] = sysUserService.findByIntegrationId(Util.getIdByJson((JSONObject) ciServiceSolverJson, "owned_by", App.Value()));
                        ciService[0].setOwnedBy(ownedBy[0]);
                        supportGroup[0] = sysGroupService.findByIntegrationId(Util.getIdByJson((JSONObject) ciServiceSolverJson, "support_group", App.Value()));
                        ciService[0].setSupportGroup(supportGroup[0]);
                        ciServiceService.save(ciService[0]);
                        ciServices.add(ciService[0]);
                        Util.printData(tag, count[0], tagAction[0].concat(Util.getFieldDisplay(ciService[0])), Util.getFieldDisplay(company[0]), Util.getFieldDisplay(company[0].getDomain()));
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
        return ciServices;
    }
}


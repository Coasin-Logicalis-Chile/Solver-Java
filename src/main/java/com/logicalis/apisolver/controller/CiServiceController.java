
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
    Util util = new Util();
    App app = new App();
    EndPointSN endPointSN = new EndPointSN();

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

        System.out.println(app.Start());
        APIResponse apiResponse = null;
        List<CiService> ciServices = new ArrayList<>();
        String[] sparmOffSets = util.offSets50000();
        long startTime = 0;
        long endTime = 0;
        String tag = "[CiService] ";


        try {
            Rest rest = new Rest();
            startTime = System.currentTimeMillis();
            final int[] count = {1};
            for (String sparmOffSet : sparmOffSets) {
                String result = rest.responseByEndPoint(endPointSN.CiServiceByQuery().replace("QUERY", query).concat(sparmOffSet));
                System.out.println(tag.concat("(".concat(endPointSN.CiServiceByQuery().replace("QUERY", query).concat(sparmOffSet)).concat(")")));

                JSONParser parser = new JSONParser();
                JSONObject resultJson = new JSONObject();
                JSONArray ListSnCiServiceJson = new JSONArray();
                resultJson = (JSONObject) parser.parse(result);
                if (resultJson.get("result") != null)
                    ListSnCiServiceJson = (JSONArray) parser.parse(resultJson.get("result").toString());

                ListSnCiServiceJson.stream().forEach(ciServiceSolverJson -> {
                    String tagAction = app.CreateConsole();
                    CiServiceSolver ciServiceSolver;
                    try {

                        Gson gson = new Gson();
                        CiService ciService = new CiService();

                        ciServiceSolver = gson.fromJson(ciServiceSolverJson.toString(), CiServiceSolver.class);
                        CiService exists = ciServiceService.findByIntegrationId(ciServiceSolver.getSys_id());

                        if (exists != null) {
                            ciService.setId(exists.getId());
                            tagAction = app.UpdateConsole();
                        }
                        ciService.setName(util.isNull(ciServiceSolver.getName()));
                        ciService.setIntegrationId(util.isNull(ciServiceSolver.getSys_id()));
                        ciService.setShortDescription(util.isNull(ciServiceSolver.getShort_description()));
                        ciService.setStatus(util.isNull(ciServiceSolver.getStatus()));
                        ciService.setUpdated(util.isNull(ciServiceSolver.getUpdated()));
                        ciService.setCreatedBy(util.isNull(ciServiceSolver.getCreated_by()));
                        ciService.setAssetTag(util.isNull(ciServiceSolver.getAsset_tag()));
                        ciService.setCategory(util.isNull(ciServiceSolver.getCategory()));
                        ciService.setComments(util.isNull(ciServiceSolver.getComments()));
                        ciService.setCorrelationId(util.isNull(ciServiceSolver.getCorrelation_id()));
                        ciService.setCostCurrency(util.isNull(ciServiceSolver.getCost_cc()));
                        ciService.setCreated(util.isNull(ciServiceSolver.getCreated()));
                        ciService.setHostname(util.isNull(ciServiceSolver.getHostname()));
                        ciService.setImpact(util.isNull(ciServiceSolver.getImpact()));
                        ciService.setInstalled(util.isNull(ciServiceSolver.getInstalled()));
                        ciService.setModelNumber(util.isNull(ciServiceSolver.getModel_number()));
                        ciService.setMonitor(util.isNull(ciServiceSolver.getMonitor()));
                        ciService.setOperationalStatus(util.isNull(ciServiceSolver.getOperational_status()));
                        ciService.setSerialNumber(util.isNull(ciServiceSolver.getSerial_number()));
                        ciService.setSpecialInstruction(util.isNull(ciServiceSolver.getSpecial_instruction()));
                        ciService.setSubcategory(util.isNull(ciServiceSolver.getSubcategory()));
                        ciService.setUpdatedBy(util.isNull(ciServiceSolver.getUpdated_by()));

                        Company company = companyService.findByIntegrationId(util.getIdByJson((JSONObject) ciServiceSolverJson, SnTable.Company.get(), app.Value()));
                        ciService.setCompany(company);
                        ciService.setDomain(company.getDomain());


                        Location location = locationService.findByIntegrationId(util.getIdByJson((JSONObject) ciServiceSolverJson, SnTable.Location.get(), app.Value()));
                        ciService.setLocation(location);

                        Company manufacturer = companyService.findByIntegrationId(util.getIdByJson((JSONObject) ciServiceSolverJson, "manufacturer", app.Value()));
                        ciService.setManufacturer(manufacturer);

                        SysUser assignedTo = sysUserService.findByIntegrationId(util.getIdByJson((JSONObject) ciServiceSolverJson, "assigned_to", app.Value()));
                        ciService.setAssignedTo(assignedTo);

                        SysUser ownedBy = sysUserService.findByIntegrationId(util.getIdByJson((JSONObject) ciServiceSolverJson, "owned_by", app.Value()));
                        ciService.setOwnedBy(ownedBy);


                        SysGroup supportGroup = sysGroupService.findByIntegrationId(util.getIdByJson((JSONObject) ciServiceSolverJson, "support_group", app.Value()));
                        ciService.setSupportGroup(supportGroup);

                        ciServiceService.save(ciService);
                        ciServices.add(ciService);
                        util.printData(tag, count[0], tagAction.concat(util.getFieldDisplay(ciService)), util.getFieldDisplay(company), util.getFieldDisplay(company.getDomain()));
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
        return ciServices;
    }
}


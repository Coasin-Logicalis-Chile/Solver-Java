
package com.logicalis.apisolver.controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.logicalis.apisolver.model.*;
import com.logicalis.apisolver.model.enums.*;
import com.logicalis.apisolver.model.utilities.SortedUnpaged;
import com.logicalis.apisolver.services.*;
import com.logicalis.apisolver.util.Rest;
import com.logicalis.apisolver.util.Util;
import com.logicalis.apisolver.view.IncidentSolver;
import com.logicalis.apisolver.view.ScRequestRequest;
import com.logicalis.apisolver.view.ScRequestSolver;
import com.logicalis.apisolver.view.ScTaskRequest;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = {"${app.api.settings.cross-origin.urls}", "*"})
@RestController
@RequestMapping("/api/v1")
public class ScRequestController {
    @Autowired
    private IScRequestService scRequestService;
    @Autowired
    private IScRequestItemService scRequestItemService;
    @Autowired
    private IScTaskService scTaskService;
    @Autowired
    private IDomainService domainService;
    @Autowired
    private IAPIExecutionStatusService statusService;
    @Autowired
    private ICompanyService companyService;
    @Autowired
    private ISysUserService sysUserService;
    @Autowired
    private ILocationService locationService;
    @Autowired
    private ICiServiceService ciServiceService;
    @Autowired
    private ISysGroupService sysGroupService;
    @Autowired
    private IConfigurationItemService configurationItemService;
    @Autowired
    private Rest rest;

    @GetMapping("/scRequests")
    public List<ScRequest> index() {
        return scRequestService.findAll();
    }

    @GetMapping("/scRequest/{id}")
    public ResponseEntity<?> show(@PathVariable Long id) {

        ScRequest scRequest = null;
        Map<String, Object> response = new HashMap<>();

        try {
            scRequest = scRequestService.findById(id);
        } catch (DataAccessException e) {
            response.put("mensaje", Errors.dataAccessExceptionQuery.get());
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (scRequest == null) {
            response.put("mensaje", Messages.notExist.get(id.toString()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<ScRequest>(scRequest, HttpStatus.OK);
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/scRequest")
    public ResponseEntity<?> create(@RequestBody ScRequest scRequest) {
        ScRequest newScRequest = null;

        Map<String, Object> response = new HashMap<>();
        try {
            newScRequest = scRequestService.save(scRequest);
        } catch (DataAccessException e) {
            response.put("mensaje", Errors.dataAccessExceptionInsert.get());
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", Messages.createOK.get());
        response.put("scRequest", newScRequest);

        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }

    @Secured("ROLE_ADMIN")
    @PutMapping("/scRequest/{id}")
    public ResponseEntity<?> update(@RequestBody ScRequest scRequest, @PathVariable Long id) {

        ScRequest currentScRequest = scRequestService.findById(id);
        ScRequest scRequestUpdated = null;

        Map<String, Object> response = new HashMap<>();

        if (currentScRequest == null) {
            response.put("mensaje", Errors.dataAccessExceptionUpdate.get(id.toString()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }

        try {
            currentScRequest.setNumber(scRequest.getNumber());
            scRequestUpdated = scRequestService.save(currentScRequest);

        } catch (DataAccessException e) {
            response.put("mensaje", Errors.dataAccessExceptionUpdate.get());
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", Messages.UpdateOK.get());
        response.put("scRequest", scRequestUpdated);

        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/scRequest/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {

        Map<String, Object> response = new HashMap<>();
        try {
            scRequestService.delete(id);
        } catch (DataAccessException e) {
            response.put("mensaje", Errors.dataAccessExceptionDelete.get());
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", Messages.DeleteOK.get());
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
    }

    @GetMapping("/scRequest/{integration_id}")
    public ResponseEntity<?> show(@PathVariable String integration_id) {
        ScRequest scRequest = null;
        Map<String, Object> response = new HashMap<>();
        try {
            scRequest = scRequestService.findByIntegrationId(integration_id);
        } catch (DataAccessException e) {
            response.put("mensaje", Errors.dataAccessExceptionQuery.get());
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (scRequest == null) {
            response.put("mensaje", "El registro ".concat(integration_id.concat(" no existe en la base de datos!")));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<ScRequest>(scRequest, HttpStatus.OK);
    }

    @GetMapping("/findPaginatedScRequestsByFilters")
    public ResponseEntity<Page<ScRequestFields>> findPaginatedScRequestsByFilters(@RequestParam(value = "page", required = true, defaultValue = "0") Integer page,
                                                                                  @NotNull @RequestParam(value = "size", required = true, defaultValue = "10") Integer size,
                                                                                  @NotNull @RequestParam(value = "filter", required = true, defaultValue = "") String filter,
                                                                                  @NotNull @RequestParam(value = "assignedTo", required = true, defaultValue = "0") Long assignedTo,
                                                                                  @NotNull @RequestParam(value = "company", required = true, defaultValue = "0") Long company,
                                                                                  @NotNull @RequestParam(value = "state", required = true, defaultValue = "") String state,
                                                                                  String field,
                                                                                  String direction) {
        List<ScRequest> list = new ArrayList<>();
        Sort sort = direction.toUpperCase().equals("DESC") ? Sort.by(field).descending() : Sort.by(field).ascending();

        Pageable pageRequest;
        if (size > 0) {
            pageRequest = PageRequest.of(page, size, sort);
        } else {
            pageRequest = SortedUnpaged.getInstance(sort);
        }
        Page<ScRequestFields> pageResult = scRequestService.findPaginatedScRequestsByFilters(pageRequest, filter.toUpperCase(), assignedTo, company, state);
        ResponseEntity<Page<ScRequestFields>> pageResponseEntity = new ResponseEntity<>(pageResult, HttpStatus.OK);

        return pageResponseEntity;
    }
  /*  @PutMapping("/scRequestSN")
    public ResponseEntity<?> update(@RequestBody String json, String sys_id) {
        Map<String, Object> response = new HashMap<>();
        ScRequest scRequest = new ScRequest();

        try {

            Gson gson = new Gson();
            ScRequestSolver scRequestSolver = gson.fromJson(json, ScRequestSolver.class);
            ScRequest exists = scRequestService.findByIntegrationId(scRequestSolver.getSys_id());
            String tagAction = app.CreateConsole();
            if (exists != null) {
                scRequest.setId(exists.getId());
                tagAction = app.UpdateConsole();
            }

            String tag = "[ScRequest] ";
            scRequest.setApproval(scRequestSolver.getApproval());
            scRequest.setCorrelationDisplay(scRequestSolver.getCorrelation_display());
            scRequest.setCorrelationId(scRequestSolver.getCorrelation_id());
            scRequest.setDescription(scRequestSolver.getDescription());
            scRequest.setEscalation(scRequestSolver.getEscalation());
            scRequest.setExpectedStart(scRequestSolver.getExpected_start());
            scRequest.setNumber(scRequestSolver.getNumber());
            scRequest.setOpenedAt(scRequestSolver.getOpened_at());
            scRequest.setShortDescription(scRequestSolver.getShort_description());
            scRequest.setState(scRequestSolver.getState());
            scRequest.setIntegrationId(scRequestSolver.getSys_id());
            scRequest.setSysUpdatedBy(scRequestSolver.getSys_updated_by());
            scRequest.setSysUpdatedOn(scRequestSolver.getSys_updated_on());

            Company company = companyService.findByIntegrationId(scRequestSolver.getCompany());
            scRequest.setCompany(company);
            scRequest.setDomain(company.getDomain());


            if (util.hasData(scRequestSolver.getLocation())) {
                Location location = locationService.findByIntegrationId(scRequestSolver.getLocation());
                if (location != null)
                    scRequest.setLocation(location);
            }

            if (util.hasData(scRequestSolver.getClosed_by())) {
                SysUser closedBy = sysUserService.findByIntegrationId(scRequestSolver.getClosed_by());
                if (closedBy != null)
                    scRequest.setClosedBy(closedBy);
            }


            if (util.hasData(scRequestSolver.getAssigned_to())) {
                SysUser assignedTo = sysUserService.findByIntegrationId(scRequestSolver.getAssigned_to());
                if (assignedTo != null)
                    scRequest.setAssignedTo(assignedTo);
            }

            if (util.hasData(scRequestSolver.getOpened_by())) {
                SysUser openedBy = sysUserService.findByIntegrationId(scRequestSolver.getOpened_by());
                if (openedBy != null)
                    scRequest.setOpenedBy(openedBy);
            }

            if (util.hasData(scRequestSolver.getTask_for())) {
                SysUser taskFor = sysUserService.findByIntegrationId(scRequestSolver.getTask_for());
                if (taskFor != null)
                    scRequest.setTaskFor(taskFor);
            }

            if (util.hasData(scRequestSolver.getBusiness_service())) {
                CiService businessService = ciServiceService.findByIntegrationId(scRequestSolver.getBusiness_service());
                if (businessService != null)
                    scRequest.setBusinessService(businessService);
            }

            if (util.hasData(scRequestSolver.getRequested_for())) {
                SysUser requestedFor = sysUserService.findByIntegrationId(scRequestSolver.getRequested_for());
                if (requestedFor != null)
                    scRequest.setRequestedFor(requestedFor);
            }

            if (util.hasData(scRequestSolver.getCmdb_ci())) {
                ConfigurationItem configurationItem = configurationItemService.findByIntegrationId(scRequestSolver.getCmdb_ci());
                if (configurationItem != null)
                    scRequest.setConfigurationItem(configurationItem);
            }

            if (util.hasData(scRequestSolver.getAssignment_group())) {
                SysGroup sysGroup = sysGroupService.findByIntegrationId(scRequestSolver.getAssignment_group());
                if (sysGroup != null)
                    scRequest.setAssignmentGroup(sysGroup);
            }

            ScRequest exists = scRequestService.findByIntegrationId(scRequest.getIntegrationId());
            if (exists != null) {
                scRequest.setId(exists.getId());
                tagAction = app.UpdateConsole();
            }


            scRequestUpdated = scRequestService.save(scRequest);
            util.printData(tag, tagAction.concat(util.getFieldDisplay(scRequest)), util.getFieldDisplay(scRequest.getCompany()), util.getFieldDisplay(scRequest.getDomain()));
        } catch (DataAccessException e) {
            System.out.println("error " + e.getMessage());
            response.put("mensaje", Errors.dataAccessExceptionUpdate.get());
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", Messages.UpdateOK.get());
        response.put("scRequest", scRequestUpdated);

        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }
*/

    @PutMapping("/scRequestSN")
    public ResponseEntity<?> update(@RequestBody String json) {
        Map<String, Object> response = new HashMap<>();
        ScRequest scRequest = new ScRequest();

        try {
            String tagAction = App.CreateConsole();
            String tag = "[ScRequest] ";

            Gson gson = new Gson();
            ScRequestRequest scRequestRequest = gson.fromJson(json, ScRequestRequest.class);


            ScRequest exists = scRequestService.findByIntegrationId(scRequestRequest.getSys_id());
            if (exists != null) {
                scRequest.setId(exists.getId());
                tagAction = App.UpdateConsole();
            }
            scRequest.setSysUpdatedOn(scRequestRequest.getSys_updated_on());
            scRequest.setNumber(scRequestRequest.getNumber());
            scRequest.setState(scRequestRequest.getState());
            scRequest.setSysCreatedBy(scRequestRequest.getSys_created_by());
            scRequest.setEscalation(scRequestRequest.getEscalation());
            scRequest.setExpectedStart(scRequestRequest.getExpected_start());
            scRequest.setStage(scRequestRequest.getStage());
            scRequest.setActive(scRequestRequest.getActive());
            scRequest.setShortDescription(scRequestRequest.getShort_description());
            scRequest.setCorrelationDisplay(scRequestRequest.getCorrelation_display());
            scRequest.setCorrelationId(scRequestRequest.getCorrelation_id());
            scRequest.setSysUpdatedBy(scRequestRequest.getSys_updated_by());
            scRequest.setSysCreatedOn(scRequestRequest.getSys_created_on());
            scRequest.setOpenedAt(scRequestRequest.getOpened_at());
            scRequest.setDescription(scRequestRequest.getDescription());
            scRequest.setIntegrationId(scRequestRequest.getSys_id());
            scRequest.setDueDate(scRequestRequest.getDue_date());
            scRequest.setContactType(scRequestRequest.getContact_type());
            scRequest.setApproval(scRequestRequest.getApproval());

            Company company = companyService.findByIntegrationId(scRequestRequest.getCompany());
            scRequest.setCompany(company);
            scRequest.setDomain(company.getDomain());

            SysUser requestedFor = sysUserService.findByIntegrationId(scRequestRequest.getRequested_for());
            scRequest.setRequestedFor(requestedFor);

            SysUser assignedTo = sysUserService.findByIntegrationId(scRequestRequest.getAssigned_to());
            scRequest.setAssignedTo(assignedTo);

            SysUser openedBy = sysUserService.findByIntegrationId(scRequestRequest.getOpened_by());
            scRequest.setOpenedBy(openedBy);

            SysUser taskFor = sysUserService.findByIntegrationId(scRequestRequest.getTask_for());
            scRequest.setTaskFor(taskFor);

            Location location = locationService.findByIntegrationId(scRequestRequest.getLocation());
            scRequest.setLocation(location);


            SysGroup sysGroup = sysGroupService.findByIntegrationId(scRequestRequest.getAssignment_group());
            scRequest.setAssignmentGroup(sysGroup);

            scRequestService.save(scRequest);
            Util.printData(tag, tagAction.concat(Util.getFieldDisplay(scRequest)), Util.getFieldDisplay(scRequest.getCompany()), Util.getFieldDisplay(scRequest.getDomain()));

        } catch (DataAccessException e) {
            System.out.println("error " + e.getMessage());
            response.put("mensaje", Errors.dataAccessExceptionUpdate.get());
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", Messages.UpdateOK.get());
        response.put("scRequest", scRequest);

        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }

/*    @PutMapping("/scRequestSN")
    public ResponseEntity<?> update(@RequestBody ScRequestRequest scRequestRequest) {
        ScRequest currentScRequest = new ScRequest();
        ScRequest scRequestUpdated = null;
        Map<String, Object> response = new HashMap<>();
        if (currentScRequest == null) {
            response.put("mensaje", Errors.dataAccessExceptionUpdate.get(scRequestRequest.getSys_id()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }

        try {
            ScRequest scRequest = new ScRequest();
            String tagAction = App.CreateConsole();
            String tag = "[ScRequest] ";


            ScRequest exists = scRequestService.findByIntegrationId(scRequest.getIntegrationId());
            if (exists != null) {
                scRequest.setId(exists.getId());
                tagAction = App.UpdateConsole();
            }
            scRequest.setApproval(scRequestRequest.getApproval());
            scRequest.setCorrelationDisplay(scRequestRequest.getCorrelation_display());
            scRequest.setCorrelationId(scRequestRequest.getCorrelation_id());
            scRequest.setDescription(scRequestRequest.getDescription());
            scRequest.setEscalation(scRequestRequest.getEscalation());
            scRequest.setExpectedStart(scRequestRequest.getExpected_start());
            scRequest.setNumber(scRequestRequest.getNumber());
            scRequest.setOpenedAt(scRequestRequest.getOpened_at());
            scRequest.setShortDescription(scRequestRequest.getShort_description());
            scRequest.setState(scRequestRequest.getState());
            scRequest.setIntegrationId(scRequestRequest.getSys_id());
            scRequest.setSysUpdatedBy(scRequestRequest.getSys_updated_by());
            scRequest.setSysUpdatedOn(scRequestRequest.getSys_updated_on());

            Company company = companyService.findByIntegrationId(scRequestRequest.getCompany());
                scRequest.setCompany(company);
                scRequest.setDomain(company.getDomain());


            if (util.hasData(scRequestRequest.getLocation())) {
                Location location = locationService.findByIntegrationId(scRequestRequest.getLocation());
                if (location != null)
                    scRequest.setLocation(location);
            }

            if (util.hasData(scRequestRequest.getClosed_by())) {
                SysUser closedBy = sysUserService.findByIntegrationId(scRequestRequest.getClosed_by());
                if (closedBy != null)
                    scRequest.setClosedBy(closedBy);
            }


            if (util.hasData(scRequestRequest.getAssigned_to())) {
                SysUser assignedTo = sysUserService.findByIntegrationId(scRequestRequest.getAssigned_to());
                if (assignedTo != null)
                    scRequest.setAssignedTo(assignedTo);
            }

            if (util.hasData(scRequestRequest.getOpened_by())) {
                SysUser openedBy = sysUserService.findByIntegrationId(scRequestRequest.getOpened_by());
                if (openedBy != null)
                    scRequest.setOpenedBy(openedBy);
            }

            if (util.hasData(scRequestRequest.getTask_for())) {
                SysUser taskFor = sysUserService.findByIntegrationId(scRequestRequest.getTask_for());
                if (taskFor != null)
                    scRequest.setTaskFor(taskFor);
            }

            if (util.hasData(scRequestRequest.getBusiness_service())) {
                CiService businessService = ciServiceService.findByIntegrationId(scRequestRequest.getBusiness_service());
                if (businessService != null)
                    scRequest.setBusinessService(businessService);
            }

            if (util.hasData(scRequestRequest.getRequested_for())) {
                SysUser requestedFor = sysUserService.findByIntegrationId(scRequestRequest.getRequested_for());
                if (requestedFor != null)
                    scRequest.setRequestedFor(requestedFor);
            }

            if (util.hasData(scRequestRequest.getCmdb_ci())) {
                ConfigurationItem configurationItem = configurationItemService.findByIntegrationId(scRequestRequest.getCmdb_ci());
                if (configurationItem != null)
                    scRequest.setConfigurationItem(configurationItem);
            }

            if (util.hasData(scRequestRequest.getAssignment_group())) {
                SysGroup sysGroup = sysGroupService.findByIntegrationId(scRequestRequest.getAssignment_group());
                if (sysGroup != null)
                    scRequest.setAssignmentGroup(sysGroup);
            }




            scRequestUpdated = scRequestService.save(scRequest);
            util.printData(tag, tagAction.concat(util.getFieldDisplay(scRequest)), util.getFieldDisplay(scRequest.getCompany()), util.getFieldDisplay(scRequest.getDomain()));
        } catch (DataAccessException e) {
            System.out.println("error " + e.getMessage());
            response.put("mensaje", Errors.dataAccessExceptionUpdate.get());
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", Messages.UpdateOK.get());
        response.put("scRequest", scRequestUpdated);

        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }*/

    @GetMapping("/scRequestBySolver")
    public List<ScRequest> findByCompany() {
        System.out.println(App.Start());
        APIResponse apiResponse = null;
        List<ScRequest> scRequests = new ArrayList<>();
        String[] sparmOffSets = Util.offSets99000();
        long startTime = 0;
        long endTime = 0;
        String tag = "[ScRequest] ";
        try {
            startTime = System.currentTimeMillis();
            final int[] count = {1};
            for (String sparmOffSet : sparmOffSets) {
                String result = rest.responseByEndPoint(EndPointSN.ScRequestByCompany().concat(sparmOffSet));
                System.out.println(tag.concat("(".concat(EndPointSN.ScRequestByCompany().concat(sparmOffSet)).concat(")")));
                ObjectMapper mapper = new ObjectMapper();
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                JSONParser parser = new JSONParser();
                JSONObject resultJson = new JSONObject();
                JSONArray ListSnScRequestJson = new JSONArray();

                resultJson = (JSONObject) parser.parse(result);
                if (resultJson.get("result") != null)
                    ListSnScRequestJson = (JSONArray) parser.parse(resultJson.get("result").toString());

                ListSnScRequestJson.stream().forEach(snScRequestJson -> {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    ScRequestSolver scRequestSolver = new ScRequestSolver();
                    try {
                        scRequestSolver = objectMapper.readValue(snScRequestJson.toString(), ScRequestSolver.class);
                        ScRequest scRequest = new ScRequest();
                        scRequest.setSysUpdatedOn(scRequestSolver.getSys_updated_on());
                        scRequest.setNumber(scRequestSolver.getNumber());
                        scRequest.setState(scRequestSolver.getState());
                        scRequest.setSysCreatedBy(scRequestSolver.getSys_created_by());
                        scRequest.setEscalation(scRequestSolver.getEscalation());
                        scRequest.setExpectedStart(scRequestSolver.getExpected_start());
                        scRequest.setStage(scRequestSolver.getStage());
                        scRequest.setActive(scRequestSolver.getActive());
                        scRequest.setShortDescription(scRequestSolver.getShort_description());
                        scRequest.setCorrelationDisplay(scRequestSolver.getCorrelation_display());
                        scRequest.setCorrelationId(scRequestSolver.getCorrelation_id());
                        scRequest.setSysUpdatedBy(scRequestSolver.getSys_updated_by());
                        scRequest.setSysCreatedOn(scRequestSolver.getSys_created_on());
                        scRequest.setOpenedAt(scRequestSolver.getOpened_at());
                        scRequest.setDescription(scRequestSolver.getDescription());
                        scRequest.setIntegrationId(scRequestSolver.getSys_id());
                        scRequest.setDueDate(scRequestSolver.getDue_date());
                        scRequest.setContactType(scRequestSolver.getContact_type());
                        scRequest.setApproval(scRequestSolver.getApproval());

                        Domain domain = getDomainByIntegrationId((JSONObject) snScRequestJson, SnTable.Domain.get(), App.Value());
                        if (domain != null)
                            scRequest.setDomain(domain);

                        Company company = getCompanyByIntegrationId((JSONObject) snScRequestJson, SnTable.Company.get(), App.Value());
                        if (company != null)
                            scRequest.setCompany(company);

                        SysUser requestedFor = getSysUserByIntegrationId((JSONObject) snScRequestJson, "requested_for", App.Value());
                        if (requestedFor != null)
                            scRequest.setRequestedFor(requestedFor);

                        SysUser openedBy = getSysUserByIntegrationId((JSONObject) snScRequestJson, "opened_by", App.Value());
                        if (openedBy != null)
                            scRequest.setOpenedBy(openedBy);

                        SysUser assignedTo = getSysUserByIntegrationId((JSONObject) snScRequestJson, "assigned_to", App.Value());
                        if (assignedTo != null)
                            scRequest.setAssignedTo(assignedTo);

                        SysUser taskFor = getSysUserByIntegrationId((JSONObject) snScRequestJson, "task_for", App.Value());
                        if (taskFor != null)
                            scRequest.setTaskFor(taskFor);

                        Location location = getLocationByIntegrationId((JSONObject) snScRequestJson, "location", App.Value());
                        if (location != null)
                            scRequest.setLocation(location);

                        SysGroup sysGroup = getSysGroupByIntegrationId((JSONObject) snScRequestJson, "assignment_group", App.Value());
                        if (sysGroup != null)
                            scRequest.setAssignmentGroup(sysGroup);


                        ScRequest exists = scRequestService.findByIntegrationId(scRequest.getIntegrationId());
                        String tagAction = App.CreateConsole();
                        if (exists != null) {
                            scRequest.setId(exists.getId());
                            tagAction = App.UpdateConsole();
                        }

                        Util.printData(tag, count[0], tagAction.concat(Util.getFieldDisplay(scRequest)), Util.getFieldDisplay(company), Util.getFieldDisplay(domain));
                        scRequests.add(scRequestService.save(scRequest));
                        count[0] = count[0] + 1;
                    } catch (Exception e) {
                        System.out.println(tag.concat("Exception (I) : ").concat(String.valueOf(e)));
                    }
                });

                apiResponse = mapper.readValue(result, APIResponse.class);
                APIExecutionStatus status = new APIExecutionStatus();
                status.setUri(EndPointSN.Location());
                status.setUserAPI(App.SNUser());
                status.setPasswordAPI(App.SNPassword());
                status.setError(apiResponse.getError());
                status.setMessage(apiResponse.getMessage());
                endTime = (System.currentTimeMillis() - startTime);
                status.setExecutionTime(endTime);
                statusService.save(status);
            }

        } catch (Exception e) {
            System.out.println(tag.concat("Exception (II) : ").concat(String.valueOf(e)));
        }
        System.out.println(App.End());
        return scRequests;
    }

    @GetMapping("/scRequestBySolverQueryCreate")
    public List<ScRequest> show(String query, boolean flagCreate) {
        System.out.println(App.Start());
        APIResponse apiResponse = null;
        List<ScRequest> scRequests = new ArrayList<>();
        String[] sparmOffSets = Util.offSets99000();
        long startTime = 0;
        long endTime = 0;
        String tag = "[ScRequest] ";
        try {
            startTime = System.currentTimeMillis();
            final int[] count = {1};
            for (String sparmOffSet : sparmOffSets) {

                String result = rest.responseByEndPoint(EndPointSN.ScRequestByQuery().replace("QUERY", query).concat(sparmOffSet));
                System.out.println(tag.concat("(".concat(EndPointSN.ScRequestByQuery().replace("QUERY", query).concat(sparmOffSet)).concat(")")));


                ObjectMapper mapper = new ObjectMapper();
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                JSONParser parser = new JSONParser();
                JSONObject resultJson;
                JSONArray ListSnScRequestJson = new JSONArray();

                resultJson = (JSONObject) parser.parse(result);
                if (resultJson.get("result") != null)
                    ListSnScRequestJson = (JSONArray) parser.parse(resultJson.get("result").toString());

                ListSnScRequestJson.stream().forEach(snScRequestJson -> {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    ScRequestSolver scRequestSolver;
                    try {
                        scRequestSolver = objectMapper.readValue(snScRequestJson.toString(), ScRequestSolver.class);
                        ScRequest scRequest = new ScRequest();

                        ScRequest exists = scRequestService.findByIntegrationId(scRequest.getIntegrationId());
                        String tagAction = App.CreateConsole();
                        if (exists != null) {
                            scRequest.setId(exists.getId());
                        } else {

                            scRequest.setSysUpdatedOn(scRequestSolver.getSys_updated_on());
                            scRequest.setNumber(scRequestSolver.getNumber());
                            scRequest.setState(scRequestSolver.getState());
                            scRequest.setSysCreatedBy(scRequestSolver.getSys_created_by());
                            scRequest.setEscalation(scRequestSolver.getEscalation());
                            scRequest.setExpectedStart(scRequestSolver.getExpected_start());
                            scRequest.setStage(scRequestSolver.getStage());
                            scRequest.setActive(scRequestSolver.getActive());
                            scRequest.setShortDescription(scRequestSolver.getShort_description());
                            scRequest.setCorrelationDisplay(scRequestSolver.getCorrelation_display());
                            scRequest.setCorrelationId(scRequestSolver.getCorrelation_id());
                            scRequest.setSysUpdatedBy(scRequestSolver.getSys_updated_by());
                            scRequest.setSysCreatedOn(scRequestSolver.getSys_created_on());
                            scRequest.setOpenedAt(scRequestSolver.getOpened_at());
                            scRequest.setDescription(scRequestSolver.getDescription());
                            scRequest.setIntegrationId(scRequestSolver.getSys_id());
                            scRequest.setDueDate(scRequestSolver.getDue_date());
                            scRequest.setContactType(scRequestSolver.getContact_type());
                            scRequest.setApproval(scRequestSolver.getApproval());

                            Domain domain = getDomainByIntegrationId((JSONObject) snScRequestJson, SnTable.Domain.get(), App.Value());
                            if (domain != null)
                                scRequest.setDomain(domain);

                            Company company = getCompanyByIntegrationId((JSONObject) snScRequestJson, SnTable.Company.get(), App.Value());
                            if (company != null)
                                scRequest.setCompany(company);

                            SysUser requestedFor = getSysUserByIntegrationId((JSONObject) snScRequestJson, "requested_for", App.Value());
                            if (requestedFor != null)
                                scRequest.setRequestedFor(requestedFor);

                            SysUser openedBy = getSysUserByIntegrationId((JSONObject) snScRequestJson, "opened_by", App.Value());
                            if (openedBy != null)
                                scRequest.setOpenedBy(openedBy);

                            SysUser assignedTo = getSysUserByIntegrationId((JSONObject) snScRequestJson, "assigned_to", App.Value());
                            if (assignedTo != null)
                                scRequest.setAssignedTo(assignedTo);

                            SysUser taskFor = getSysUserByIntegrationId((JSONObject) snScRequestJson, "task_for", App.Value());
                            if (taskFor != null)
                                scRequest.setTaskFor(taskFor);

                            Location location = getLocationByIntegrationId((JSONObject) snScRequestJson, "location", App.Value());
                            if (location != null)
                                scRequest.setLocation(location);

                            SysGroup sysGroup = getSysGroupByIntegrationId((JSONObject) snScRequestJson, "assignment_group", App.Value());
                            if (sysGroup != null)
                                scRequest.setAssignmentGroup(sysGroup);

                            ScRequest _scRequest = scRequestService.save(scRequest);
                            scRequests.add(_scRequest);
                            Util.printData(tag, count[0], tagAction.concat(Util.getFieldDisplay(scRequest)), Util.getFieldDisplay(company), Util.getFieldDisplay(domain));

                            count[0] = count[0] + 1;
                        }
                    } catch (Exception e) {
                        //  System.out.println(tag.concat("Exception (I) : ").concat(String.valueOf(e)));
                    }
                });

                apiResponse = mapper.readValue(result, APIResponse.class);
                APIExecutionStatus status = new APIExecutionStatus();
                status.setUri(EndPointSN.Location());
                status.setUserAPI(App.SNUser());
                status.setPasswordAPI(App.SNPassword());
                status.setError(apiResponse.getError());
                status.setMessage(apiResponse.getMessage());
                endTime = (System.currentTimeMillis() - startTime);
                status.setExecutionTime(endTime);
                statusService.save(status);
            }

        } catch (Exception e) {
            //  System.out.println(tag.concat("Exception (II) : ").concat(String.valueOf(e)));
        }
        System.out.println(App.End());
        return scRequests;
    }

    public Domain getDomainByIntegrationId(JSONObject jsonObject, String levelOne, String levelTwo) {
        String integrationId = Util.getIdByJson(jsonObject, levelOne, levelTwo);
        if (Util.hasData(integrationId)) {
            return domainService.findByIntegrationId(integrationId);
        } else
            return null;
    }

    public SysUser getSysUserByIntegrationId(JSONObject jsonObject, String levelOne, String levelTwo) {
        String integrationId = Util.getIdByJson(jsonObject, levelOne, levelTwo);
        if (Util.hasData(integrationId)) {
            return sysUserService.findByIntegrationId(integrationId);
        } else
            return null;
    }

    public CiService getCiServiceByIntegrationId(JSONObject jsonObject, String levelOne, String levelTwo) {
        String integrationId = Util.getIdByJson(jsonObject, levelOne, levelTwo);
        if (Util.hasData(integrationId)) {
            return ciServiceService.findByIntegrationId(integrationId);
        } else
            return null;
    }

    public Company getCompanyByIntegrationId(JSONObject jsonObject, String levelOne, String levelTwo) {
        String integrationId = Util.getIdByJson(jsonObject, levelOne, levelTwo);
        if (Util.hasData(integrationId)) {
            return companyService.findByIntegrationId(integrationId);
        } else
            return null;
    }

    public ConfigurationItem getConfigurationItemByIntegrationId(JSONObject jsonObject, String levelOne, String levelTwo) {
        String integrationId = Util.getIdByJson(jsonObject, levelOne, levelTwo);
        if (Util.hasData(integrationId)) {
            return configurationItemService.findByIntegrationId(integrationId);
        } else
            return null;
    }

    public SysGroup getSysGroupByIntegrationId(JSONObject jsonObject, String levelOne, String levelTwo) {
        String integrationId = Util.getIdByJson(jsonObject, levelOne, levelTwo);
        if (Util.hasData(integrationId)) {
            return sysGroupService.findByIntegrationId(integrationId);
        } else
            return null;
    }

    public Location getLocationByIntegrationId(JSONObject jsonObject, String levelOne, String levelTwo) {
        String integrationId = Util.getIdByJson(jsonObject, levelOne, levelTwo);
        if (Util.hasData(integrationId)) {
            return locationService.findByIntegrationId(integrationId);
        } else
            return null;
    }

}



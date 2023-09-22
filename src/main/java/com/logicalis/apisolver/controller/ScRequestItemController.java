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
import com.logicalis.apisolver.view.ScRequestItemRequest;
import com.logicalis.apisolver.view.ScRequestItemSolver;
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
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = {"${app.api.settings.cross-origin.urls}", "*"})
@RestController
@RequestMapping("/api/v1")
public class ScRequestItemController {
    @Autowired
    private IScRequestItemService scRequestItemService;
    @Autowired
    private IIncidentService incidentService;
    @Autowired
    private IScTaskService scTaskService;
    @Autowired
    private ISysUserService sysUserService;
    @Autowired
    private ISysGroupService sysGroupService;
    @Autowired
    private IDomainService domainService;
    @Autowired
    private ILocationService locationService;
    @Autowired
    private IAPIExecutionStatusService statusService;
    @Autowired
    private ICompanyService companyService;
    @Autowired
    private ICiServiceService ciServiceService;
    @Autowired
    private IScRequestService scRequestService;
    @Autowired
    private IScCategoryItemService scCategoryItemService;
    @Autowired
    private IConfigurationItemService configurationItemService;
    @Autowired
    private Rest rest;

    @GetMapping("/scRequestItems")
    public List<ScRequestItem> index() {
        return scRequestItemService.findAll();
    }

    @GetMapping("/scRequestItem/{id}")
    public ResponseEntity<?> show(@PathVariable Long id) {
        ScRequestItem scRequestItem = null;
        Map<String, Object> response = new HashMap<>();
        try {
            scRequestItem = scRequestItemService.findById(id);
        } catch (DataAccessException e) {
            response.put("mensaje", Errors.dataAccessExceptionQuery.get());
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (scRequestItem == null) {
            response.put("mensaje", Messages.notExist.get(id.toString()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<ScRequestItem>(scRequestItem, HttpStatus.OK);
    }

    @GetMapping("/scRequestItem/{scRequest}")
    public ResponseEntity<?> show(@PathVariable ScRequest scRequest) {
        List<ScRequestItem> scRequestItems = null;
        Map<String, Object> response = new HashMap<>();
        try {
            scRequestItems = scRequestItemService.findByScRequest(scRequest);
        } catch (DataAccessException e) {
            response.put("mensaje", Errors.dataAccessExceptionQuery.get());
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (scRequestItems == null) {
            response.put("mensaje", Messages.notExist.get(scRequest.getId().toString()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<List<ScRequestItem>>(scRequestItems, HttpStatus.OK);
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/scRequestItem")
    public ResponseEntity<?> create(@RequestBody ScRequestItem scRequestItem) {
        ScRequestItem newScRequestItem = null;
        Map<String, Object> response = new HashMap<>();
        try {
            newScRequestItem = scRequestItemService.save(scRequestItem);
        } catch (DataAccessException e) {
            response.put("mensaje", Errors.dataAccessExceptionInsert.get());
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", Messages.createOK.get());
        response.put("scRequestItem", newScRequestItem);
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }

    @PutMapping("/scRequestItem/{id}")
    public ResponseEntity<?> update(@RequestBody String json, @PathVariable Long id) {
        ScRequestItem currentScRequestItem = scRequestItemService.findById(id);
        ScRequestItem scRequestItemUpdated = null;
        Map<String, Object> response = new HashMap<>();

        if (currentScRequestItem == null) {
            response.put("mensaje", Errors.dataAccessExceptionUpdate.get(id.toString()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }
        try {
            SysUser assignedTo = getSysUserByIntegrationId(Util.parseJson(json, "scRequestItem", "assigned_to"));
            currentScRequestItem.setAssignedTo(assignedTo);
            SysGroup sysGroup = getSysGroupByIntegrationId(Util.parseJson(json, "scRequestItem", "assignment_group"));
            currentScRequestItem.setAssignmentGroup(sysGroup);
            currentScRequestItem.setDescription(Util.parseJson(json, "scRequestItem", "description"));
            currentScRequestItem.setShortDescription(Util.parseJson(json, "scRequestItem", "short_description"));
            currentScRequestItem.setState(Util.parseJson(json, "scRequestItem", "state"));
            ScRequestItem addScRequestItem = rest.putScRequestItem(EndPointSN.PutScRequestItem(), currentScRequestItem);
            scRequestItemUpdated = scRequestItemService.save(currentScRequestItem);
        } catch (DataAccessException e) {
            response.put("mensaje", Errors.dataAccessExceptionUpdate.get());
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", Messages.UpdateOK.get());
        response.put("scRequestItem", scRequestItemUpdated);
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/scRequestItem/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            scRequestItemService.delete(id);
        } catch (DataAccessException e) {
            response.put("mensaje", Errors.dataAccessExceptionDelete.get());
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", Messages.DeleteOK.get());
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
    }

    @GetMapping("/findPaginatedScRequestItemsByFilters")
    public ResponseEntity<Page<ScRequestItemInfo>> findPaginatedScRequestItemsByFilters(@RequestParam(value = "page", required = true, defaultValue = "0") Integer page,
                                                                                        @NotNull @RequestParam(value = "size", required = true, defaultValue = "10") Integer size,
                                                                                        @NotNull @RequestParam(value = "filter", required = true, defaultValue = "") String filter,
                                                                                        @NotNull @RequestParam(value = "assignedTo", required = true, defaultValue = "0") Long assignedTo,
                                                                                        @NotNull @RequestParam(value = "company", required = true, defaultValue = "0") Long company,
                                                                                        @NotNull @RequestParam(value = "state", required = true, defaultValue = "") String state,
                                                                                        @NotNull @RequestParam(value = "openUnassigned", required = true, defaultValue = "false") boolean openUnassigned,
                                                                                        @NotNull @RequestParam(value = "solved", required = true, defaultValue = "false") boolean solved,
                                                                                        String field,
                                                                                        String direction) {
        Sort sort = direction.toUpperCase().equals("DESC") ? Sort.by(field).descending() : Sort.by(field).ascending();
        Pageable pageRequest;
        if (size > 0) {
            pageRequest = PageRequest.of(page, size, sort);
        } else {
            pageRequest = SortedUnpaged.getInstance(sort);
        }
        Page<ScRequestItemInfo> pageResult = scRequestItemService.findPaginatedScRequestItemsByFilters(pageRequest, filter.toUpperCase(), assignedTo, company, state, openUnassigned, solved);
        ResponseEntity<Page<ScRequestItemInfo>> pageResponseEntity = new ResponseEntity<>(pageResult, HttpStatus.OK);
        return pageResponseEntity;
    }


    @GetMapping("/countScRequestItemsByFilters")
    public ResponseEntity<Long> countScRequestItemsByFilters(@NotNull @RequestParam(value = "assignedTo", required = true, defaultValue = "0") Long assignedTo,
                                                             @NotNull @RequestParam(value = "company", required = true, defaultValue = "0") Long company,
                                                             @NotNull @RequestParam(value = "state", required = true, defaultValue = "") String state) {
        Long pageResult = scRequestItemService.countScRequestItemsByFilters(assignedTo, company, state);
        ResponseEntity<Long> pageResponseEntity = new ResponseEntity<>(pageResult, HttpStatus.OK);
        return pageResponseEntity;
    }

    @PutMapping("/scRequestItemSN")
    public ResponseEntity<?> update(@RequestBody String json) {
        ScRequestItem scRequestItem = new ScRequestItem();
        Map<String, Object> response = new HashMap<>();
        try {
            String tagAction = App.CreateConsole();
            String tag = "[ScRequestItem] ";
            Gson gson = new Gson();
            ScRequestItemRequest scRequestItemSolver = gson.fromJson(json, ScRequestItemRequest.class);
            ScRequestItem exists = scRequestItemService.findByIntegrationId(scRequestItemSolver.getSys_id());
            if (exists != null) {
                scRequestItem.setId(exists.getId());
                tagAction = App.UpdateConsole();
            }
            scRequestItem.setApproval(scRequestItemSolver.getApproval());
            scRequestItem.setStage(scRequestItemSolver.getStage());
            scRequestItem.setContactType(scRequestItemSolver.getContact_type());
            scRequestItem.setDescription(scRequestItemSolver.getDescription());
            scRequestItem.setEscalation(scRequestItemSolver.getEscalation());
            scRequestItem.setEstimatedDelivery(scRequestItemSolver.getEstimated_delivery());
            scRequestItem.setExpectedStart(scRequestItemSolver.getExpected_start());
            scRequestItem.setNumber(scRequestItemSolver.getNumber());
            scRequestItem.setOpenedAt(scRequestItemSolver.getOpened_at());
            scRequestItem.setIncidentParent(scRequestItemSolver.getParent());
            scRequestItem.setPriority(scRequestItemSolver.getPriority());
            scRequestItem.setShortDescription(scRequestItemSolver.getShort_description());
            scRequestItem.setState(scRequestItemSolver.getState());
            scRequestItem.setIntegrationId(scRequestItemSolver.getSys_id());
            scRequestItem.setSysUpdatedBy(scRequestItemSolver.getSys_updated_by());
            scRequestItem.setSysUpdatedOn(scRequestItemSolver.getSys_updated_on());
            scRequestItem.setUrgency(scRequestItemSolver.getUrgency());
            scRequestItem.setScRequestIntegrationId(scRequestItemSolver.getRequest());
            ScRequest scRequest = scRequestService.findByIntegrationId(scRequestItemSolver.getRequest());
            scRequestItem.setScRequest(scRequest);
            Company company = companyService.findByIntegrationId(scRequestItemSolver.getCompany());
            scRequestItem.setCompany(company);
            scRequestItem.setDomain(company.getDomain());
            if (Util.hasData(scRequestItemSolver.getOpened_by())) {
                SysUser openedBy = sysUserService.findByIntegrationId(scRequestItemSolver.getOpened_by());
                scRequestItem.setOpenedBy(openedBy);
            }
            if (Util.hasData(scRequestItemSolver.getTask_for())) {
                SysUser taskFor = sysUserService.findByIntegrationId(scRequestItemSolver.getTask_for());
                scRequestItem.setTaskFor(taskFor);
            }
            if (Util.hasData(scRequestItemSolver.getRequested_for())) {
                SysUser requestedFor = sysUserService.findByIntegrationId(scRequestItemSolver.getRequested_for());
                scRequestItem.setRequestedFor(requestedFor);
            }

            scRequestItemService.save(scRequestItem);
            Util.printData(tag, tagAction.concat(Util.getFieldDisplay(scRequestItem)), Util.getFieldDisplay(company), Util.getFieldDisplay(company.getDomain()));
        } catch (DataAccessException e) {
            System.out.println("error " + e.getMessage());
            response.put("mensaje", Errors.dataAccessExceptionUpdate.get());
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", Messages.UpdateOK.get());
        response.put("scRequestItem", scRequestItem);
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }

    @GetMapping("/scRequestItemBySolver")
    public List<ScRequestItem> findByCompany() {
        System.out.println(App.Start());
        APIResponse apiResponse = null;
        List<ScRequestItem> scRequestItems = new ArrayList<>();
        String[] sparmOffSets = Util.offSets99000();
        long startTime = 0;
        long endTime = 0;
        String tag = "[ScRequestItem] ";
        String result;
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        JSONParser parser = new JSONParser();
        JSONObject resultJson = new JSONObject();
        JSONArray ListSnScRequestItemJson = new JSONArray();
        ScRequestItem scRequestItem = new ScRequestItem();
        final Domain[] domain = new Domain[1];
        final Company[] company = new Company[1];
        final Location[] location = new Location[1];
        final ScCategoryItem[] scCategoryItem = new ScCategoryItem[1];
        final SysUser[] assignedTo = new SysUser[1];
        final SysUser[] openedBy = new SysUser[1];
        final SysUser[] taskFor = new SysUser[1];
        final CiService[] businessService = new CiService[1];
        final ConfigurationItem[] configurationItem = new ConfigurationItem[1];
        final SysGroup[] sysGroup = new SysGroup[1];
        final ScRequest[] scRequest = new ScRequest[1];
        final String[] incidentParent = new String[1];
        final Incident[] parent = new Incident[1];
        final String[] tagAction = new String[1];
        final ScRequestItem[] exists = new ScRequestItem[1];
        APIExecutionStatus status = new APIExecutionStatus();
        final ScRequestItemSolver[] scRequestItemSolver = {new ScRequestItemSolver()};
        try {
            startTime = System.currentTimeMillis();
            final int[] count = {1};
            for (String sparmOffSet : sparmOffSets) {
                result = rest.responseByEndPoint(EndPointSN.ScRequestItemByCompany().concat(sparmOffSet));
                System.out.println(tag.concat("(".concat(EndPointSN.ScRequestItemByCompany().concat(sparmOffSet)).concat(")")));
                resultJson = (JSONObject) parser.parse(result);
                if (resultJson.get("result") != null)
                    ListSnScRequestItemJson = (JSONArray) parser.parse(resultJson.get("result").toString());
                ListSnScRequestItemJson.stream().forEach(snScRequestItemJson -> {
                    try {
                        scRequestItemSolver[0] = mapper.readValue(snScRequestItemJson.toString(), ScRequestItemSolver.class);
                        scRequestItem.setNumber(scRequestItemSolver[0].getNumber());
                        scRequestItem.setActive(scRequestItemSolver[0].getActive());
                        scRequestItem.setState(scRequestItemSolver[0].getState());
                        scRequestItem.setStage(scRequestItemSolver[0].getStage());
                        scRequestItem.setTimeWorked(scRequestItemSolver[0].getTime_worked());
                        scRequestItem.setEscalation(scRequestItemSolver[0].getEscalation());
                        scRequestItem.setExpectedStart(scRequestItemSolver[0].getExpected_start());
                        scRequestItem.setEstimatedDelivery(scRequestItemSolver[0].getEstimated_delivery());
                        scRequestItem.setShortDescription(scRequestItemSolver[0].getShort_description());
                        scRequestItem.setOpenedAt(scRequestItemSolver[0].getOpened_at());
                        scRequestItem.setDescription(scRequestItemSolver[0].getDescription());
                        scRequestItem.setIntegrationId(scRequestItemSolver[0].getSys_id());
                        scRequestItem.setContactType(scRequestItemSolver[0].getContact_type());
                        scRequestItem.setUrgency(scRequestItemSolver[0].getUrgency());
                        scRequestItem.setPriority(scRequestItemSolver[0].getPriority());
                        scRequestItem.setApproval(scRequestItemSolver[0].getApproval());
                        scRequestItem.setSysUpdatedOn(scRequestItemSolver[0].getSys_updated_on());
                        scRequestItem.setSysCreatedBy(scRequestItemSolver[0].getSys_created_by());
                        scRequestItem.setSysUpdatedBy(scRequestItemSolver[0].getSys_updated_by());
                        scRequestItem.setSysCreatedOn(scRequestItemSolver[0].getSys_created_on());
                        domain[0] = getDomainByIntegrationId((JSONObject) snScRequestItemJson, SnTable.Domain.get(), App.Value());
                        if (domain[0] != null)
                            scRequestItem.setDomain(domain[0]);
                        company[0] = getCompanyByIntegrationId((JSONObject) snScRequestItemJson, SnTable.Company.get(), App.Value());
                        if (company[0] != null)
                            scRequestItem.setCompany(company[0]);

                        location[0] = getLocationByIntegrationId((JSONObject) snScRequestItemJson, "location", App.Value());
                        if (location[0] != null)
                            scRequestItem.setLocation(location[0]);

                        scCategoryItem[0] = getScCategoryItemByIntegrationId((JSONObject) snScRequestItemJson, "cat_item", App.Value());
                        if (scCategoryItem[0] != null)
                            scRequestItem.setScCategoryItem(scCategoryItem[0]);

                        assignedTo[0] = getSysUserByIntegrationId((JSONObject) snScRequestItemJson, "assigned_to", App.Value());
                        if (assignedTo[0] != null)
                            scRequestItem.setAssignedTo(assignedTo[0]);
                        openedBy[0] = getSysUserByIntegrationId((JSONObject) snScRequestItemJson, "opened_by", App.Value());
                        if (openedBy[0] != null)
                            scRequestItem.setOpenedBy(openedBy[0]);

                        taskFor[0] = getSysUserByIntegrationId((JSONObject) snScRequestItemJson, "task_for", App.Value());
                        if (taskFor[0] != null)
                            scRequestItem.setTaskFor(taskFor[0]);

                        businessService[0] = getCiServiceByIntegrationId((JSONObject) snScRequestItemJson, "business_service", App.Value());
                        if (businessService[0] != null)
                            scRequestItem.setBusinessService(businessService[0]);

                        configurationItem[0] = getConfigurationItemByIntegrationId((JSONObject) snScRequestItemJson, "cmdb_ci", App.Value());
                        if (configurationItem[0] != null)
                            scRequestItem.setConfigurationItem(configurationItem[0]);

                        sysGroup[0] = getSysGroupByIntegrationId((JSONObject) snScRequestItemJson, "assignment_group", App.Value());
                        if (sysGroup[0] != null)
                            scRequestItem.setAssignmentGroup(sysGroup[0]);

                        scRequest[0] = getScRequestByIntegrationId((JSONObject) snScRequestItemJson, "request", App.Value());
                        if (scRequest[0] != null) {
                            scRequestItem.setScRequest(scRequest[0]);

                            if (scRequest[0].getRequestedFor() != null)
                                scRequestItem.setRequestedFor(scRequest[0].getRequestedFor());
                        }

                        incidentParent[0] = Util.getIdByJson((JSONObject) snScRequestItemJson, "parent", App.Value());
                        if (Util.hasData(incidentParent[0])) {
                            parent[0] = incidentService.findByIntegrationId(incidentParent[0]);
                            if (Util.hasData(parent[0]))
                                scRequestItem.setIncidentParent(incidentParent[0]);
                        }
                        exists[0] = scRequestItemService.findByIntegrationId(scRequestItem.getIntegrationId());
                        tagAction[0] = App.CreateConsole();
                        if (exists[0] != null) {
                            scRequestItem.setId(exists[0].getId());
                            tagAction[0] = App.UpdateConsole();
                        }
                        Util.printData(tag, count[0], tagAction[0].concat(Util.getFieldDisplay(scRequestItem)), Util.getFieldDisplay(company[0]), Util.getFieldDisplay(domain[0]));
                        scRequestItems.add(scRequestItemService.save(scRequestItem));
                        count[0] = count[0] + 1;
                    } catch (Exception e) {
                        System.out.println(tag.concat("Exception (I) : ").concat(String.valueOf(e)));
                    }
                });

                apiResponse = mapper.readValue(result, APIResponse.class);
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
        return scRequestItems;
    }

    @GetMapping("/scRequestItemBySolverQueryCreate")
    public List<ScRequestItem> show(String query, boolean flagCreate) {
        System.out.println(App.Start());
        APIResponse apiResponse = null;
        List<ScRequestItem> scRequestItems = new ArrayList<>();
        String[] sparmOffSets = Util.offSets99000();
        long startTime = 0;
        long endTime = 0;
        String tag = "[ScRequestItem] ";
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        JSONParser parser = new JSONParser();
        JSONObject resultJson;
        JSONArray ListSnScRequestItemJson = new JSONArray();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        final ScRequestItemSolver[] scRequestItemSolver = new ScRequestItemSolver[1];
        String result;
        ScRequestItem scRequestItem = new ScRequestItem();
        final ScRequestItem[] exists = new ScRequestItem[1];
        final String[] tagAction = new String[1];
        final Domain[] domain = new Domain[1];
        final Company[] company = new Company[1];
        final Location[] location = new Location[1];
        final ScCategoryItem[] scCategoryItem = new ScCategoryItem[1];
        final SysUser[] assignedTo = new SysUser[1];
        final SysUser[] openedBy = new SysUser[1];
        final SysUser[] taskFor = new SysUser[1];
        final CiService[] businessService = new CiService[1];
        final ConfigurationItem[] configurationItem = new ConfigurationItem[1];
        final SysGroup[] sysGroup = new SysGroup[1];
        final ScRequest[] scRequest = new ScRequest[1];
        final String[] incidentParent = new String[1];
        final Incident[] parent = new Incident[1];
        final ScRequestItem[] _scRequestItem = new ScRequestItem[1];
        APIExecutionStatus status = new APIExecutionStatus();
        try {
            startTime = System.currentTimeMillis();
            final int[] count = {1};
            for (String sparmOffSet : sparmOffSets) {
                result = rest.responseByEndPoint(EndPointSN.ScRequestItemByQuery().replace("QUERY", query).concat(sparmOffSet));
                System.out.println(tag.concat("(".concat(EndPointSN.ScRequestItemByQuery().replace("QUERY", query).concat(sparmOffSet)).concat(")")));
                resultJson = (JSONObject) parser.parse(result);
                if (resultJson.get("result") != null)
                    ListSnScRequestItemJson = (JSONArray) parser.parse(resultJson.get("result").toString());
                ListSnScRequestItemJson.stream().forEach(snScRequestItemJson -> {
                    try {
                        scRequestItemSolver[0] = objectMapper.readValue(snScRequestItemJson.toString(), ScRequestItemSolver.class);
                        exists[0] = scRequestItemService.findByIntegrationId(scRequestItem.getIntegrationId());
                        tagAction[0] = App.CreateConsole();
                        if (exists[0] != null) {
                            scRequestItem.setId(exists[0].getId());
                        } else {
                            scRequestItem.setNumber(scRequestItemSolver[0].getNumber());
                            scRequestItem.setActive(scRequestItemSolver[0].getActive());
                            scRequestItem.setState(scRequestItemSolver[0].getState());
                            scRequestItem.setStage(scRequestItemSolver[0].getStage());
                            scRequestItem.setTimeWorked(scRequestItemSolver[0].getTime_worked());
                            scRequestItem.setEscalation(scRequestItemSolver[0].getEscalation());
                            scRequestItem.setExpectedStart(scRequestItemSolver[0].getExpected_start());
                            scRequestItem.setEstimatedDelivery(scRequestItemSolver[0].getEstimated_delivery());
                            scRequestItem.setShortDescription(scRequestItemSolver[0].getShort_description());
                            scRequestItem.setOpenedAt(scRequestItemSolver[0].getOpened_at());
                            scRequestItem.setDescription(scRequestItemSolver[0].getDescription());
                            scRequestItem.setIntegrationId(scRequestItemSolver[0].getSys_id());
                            scRequestItem.setContactType(scRequestItemSolver[0].getContact_type());
                            scRequestItem.setUrgency(scRequestItemSolver[0].getUrgency());
                            scRequestItem.setPriority(scRequestItemSolver[0].getPriority());
                            scRequestItem.setApproval(scRequestItemSolver[0].getApproval());
                            scRequestItem.setSysUpdatedOn(scRequestItemSolver[0].getSys_updated_on());
                            scRequestItem.setSysCreatedBy(scRequestItemSolver[0].getSys_created_by());
                            scRequestItem.setSysUpdatedBy(scRequestItemSolver[0].getSys_updated_by());
                            scRequestItem.setSysCreatedOn(scRequestItemSolver[0].getSys_created_on());
                            domain[0] = getDomainByIntegrationId((JSONObject) snScRequestItemJson, SnTable.Domain.get(), App.Value());
                            if (domain[0] != null)
                                scRequestItem.setDomain(domain[0]);
                            company[0] = getCompanyByIntegrationId((JSONObject) snScRequestItemJson, SnTable.Company.get(), App.Value());
                            if (company[0] != null)
                                scRequestItem.setCompany(company[0]);
                            location[0] = getLocationByIntegrationId((JSONObject) snScRequestItemJson, "location", App.Value());
                            if (location[0] != null)
                                scRequestItem.setLocation(location[0]);
                            scCategoryItem[0] = getScCategoryItemByIntegrationId((JSONObject) snScRequestItemJson, "cat_item", App.Value());
                            if (scCategoryItem[0] != null)
                                scRequestItem.setScCategoryItem(scCategoryItem[0]);
                            assignedTo[0] = getSysUserByIntegrationId((JSONObject) snScRequestItemJson, "assigned_to", App.Value());
                            if (assignedTo[0] != null)
                                scRequestItem.setAssignedTo(assignedTo[0]);
                            openedBy[0] = getSysUserByIntegrationId((JSONObject) snScRequestItemJson, "opened_by", App.Value());
                            if (openedBy[0] != null)
                                scRequestItem.setOpenedBy(openedBy[0]);
                            taskFor[0] = getSysUserByIntegrationId((JSONObject) snScRequestItemJson, "task_for", App.Value());
                            if (taskFor[0] != null)
                                scRequestItem.setTaskFor(taskFor[0]);
                            businessService[0] = getCiServiceByIntegrationId((JSONObject) snScRequestItemJson, "business_service", App.Value());
                            if (businessService[0] != null)
                                scRequestItem.setBusinessService(businessService[0]);
                            configurationItem[0] = getConfigurationItemByIntegrationId((JSONObject) snScRequestItemJson, "cmdb_ci", App.Value());
                            if (configurationItem[0] != null)
                                scRequestItem.setConfigurationItem(configurationItem[0]);
                            sysGroup[0] = getSysGroupByIntegrationId((JSONObject) snScRequestItemJson, "assignment_group", App.Value());
                            if (sysGroup[0] != null)
                                scRequestItem.setAssignmentGroup(sysGroup[0]);
                            scRequest[0] = getScRequestByIntegrationId((JSONObject) snScRequestItemJson, "request", App.Value());
                            if (scRequest[0] != null) {
                                scRequestItem.setScRequest(scRequest[0]);
                                if (scRequest[0].getRequestedFor() != null)
                                    scRequestItem.setRequestedFor(scRequest[0].getRequestedFor());
                            }
                            incidentParent[0] = Util.getIdByJson((JSONObject) snScRequestItemJson, "parent", App.Value());
                            if (Util.hasData(incidentParent[0])) {
                                parent[0] = incidentService.findByIntegrationId(incidentParent[0]);
                                if (Util.hasData(parent[0]))
                                    scRequestItem.setIncidentParent(incidentParent[0]);
                            }
                            _scRequestItem[0] = scRequestItemService.save(scRequestItem);
                            scRequestItems.add(_scRequestItem[0]);
                            Util.printData(tag, count[0], tagAction[0].concat(Util.getFieldDisplay(scRequestItem)), Util.getFieldDisplay(company[0]), Util.getFieldDisplay(domain[0]));
                            count[0] = count[0] + 1;
                        }
                    } catch (Exception e) {
                        //     System.out.println(tag.concat("Exception (I) : ").concat(String.valueOf(e)));
                    }
                });
                apiResponse = mapper.readValue(result, APIResponse.class);
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
            //System.out.println(tag.concat("Exception (II) : ").concat(String.valueOf(e)));
        }
        System.out.println(App.End());
        return scRequestItems;
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

    public ScCategoryItem getScCategoryItemByIntegrationId(JSONObject jsonObject, String levelOne, String levelTwo) {
        String integrationId = Util.getIdByJson(jsonObject, levelOne, levelTwo);
        if (Util.hasData(integrationId)) {
            return scCategoryItemService.findByIntegrationId(integrationId);
        } else
            return null;
    }

    public ScRequest getScRequestByIntegrationId(JSONObject jsonObject, String levelOne, String levelTwo) {
        String integrationId = Util.getIdByJson(jsonObject, levelOne, levelTwo);
        if (Util.hasData(integrationId)) {
            return scRequestService.findByIntegrationId(integrationId);
        } else
            return null;
    }

    public SysUser getSysUserByIntegrationId(String integrationId) {
        if (Util.hasData(integrationId)) {
            return sysUserService.findByIntegrationId(integrationId);
        } else
            return null;
    }

    public SysGroup getSysGroupByIntegrationId(String integrationId) {
        if (Util.hasData(integrationId)) {
            return sysGroupService.findByIntegrationId(integrationId);
        } else
            return null;
    }
}


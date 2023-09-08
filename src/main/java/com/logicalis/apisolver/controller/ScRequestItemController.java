
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
    private IContractService contractService;
    @Autowired
    private ICiServiceService ciServiceService;
    @Autowired
    private IScRequestService scRequestService;
    @Autowired
    private IScCategoryItemService scCategoryItemService;
    @Autowired
    private IConfigurationItemService configurationItemService;
    Util util = new Util();
    App app = new App();
    EndPointSN endPointSN = new EndPointSN();

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

    //@Secured("ROLE_ADMIN")
    @PutMapping("/scRequestItem/{id}")
    public ResponseEntity<?> update(@RequestBody String json, @PathVariable Long id) {

        ScRequestItem currentScRequestItem = scRequestItemService.findById(id);
        ScRequestItem scRequestItemUpdated = null;
        Rest rest = new Rest();
        Map<String, Object> response = new HashMap<>();

        if (currentScRequestItem == null) {
            response.put("mensaje", Errors.dataAccessExceptionUpdate.get(id.toString()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }

        try {
            SysUser assignedTo = getSysUserByIntegrationId(util.parseJson(json, "scRequestItem", "assigned_to"));
            currentScRequestItem.setAssignedTo(assignedTo);
            SysGroup sysGroup = getSysGroupByIntegrationId(util.parseJson(json, "scRequestItem", "assignment_group"));
            currentScRequestItem.setAssignmentGroup(sysGroup);
            currentScRequestItem.setDescription(util.parseJson(json, "scRequestItem", "description"));
            currentScRequestItem.setShortDescription(util.parseJson(json, "scRequestItem", "short_description"));
            currentScRequestItem.setState(util.parseJson(json, "scRequestItem", "state"));
            ScRequestItem addScRequestItem = rest.putScRequestItem(endPointSN.PutScRequestItem(), currentScRequestItem);

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

    /*
        @PutMapping("/scRequestItemSN")
        public ResponseEntity<?> update(@RequestBody ScRequestItemRequest scRequestItemRequest) {
            ScRequestItem currentScRequestItem = new ScRequestItem();
            ScRequestItem scRequestItemUpdated = null;
            Map<String, Object> response = new HashMap<>();
            Rest rest = new Rest();
            if (currentScRequestItem == null) {
                response.put("mensaje", Errors.dataAccessExceptionUpdate.get(scRequestItemRequest.getSys_id()));
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
            }

            try {
                ScRequestItem scRequestItem = new ScRequestItem();
                String tagAction = app.CreateConsole();
                String tag = "[ScRequestItem] ";

                ScRequestItem exists = scRequestItemService.findByIntegrationId(scRequestItem.getIntegrationId());
                if (exists != null) {
                    scRequestItem.setId(exists.getId());
                    tagAction = app.UpdateConsole();
                }


                scRequestItem.setApproval(scRequestItemRequest.getApproval());
                scRequestItem.setStage(scRequestItemRequest.getStage());
                scRequestItem.setCloseNotes(scRequestItemRequest.getClose_notes());
                scRequestItem.setClosedAt(scRequestItemRequest.getClosed_at());
                scRequestItem.setContactType(scRequestItemRequest.getContact_type());
                scRequestItem.setCorrelationDisplay(scRequestItemRequest.getCorrelation_display());
                scRequestItem.setCorrelationId(scRequestItemRequest.getCorrelation_id());
                scRequestItem.setDescription(scRequestItemRequest.getDescription());
                scRequestItem.setEscalation(scRequestItemRequest.getEscalation());
                scRequestItem.setEstimatedDelivery(scRequestItemRequest.getEstimated_delivery());
                scRequestItem.setExpectedStart(scRequestItemRequest.getExpected_start());
                scRequestItem.setNumber(scRequestItemRequest.getNumber());
                scRequestItem.setOpenedAt(scRequestItemRequest.getOpened_at());
                scRequestItem.setIncidentParent(scRequestItemRequest.getParent());
                scRequestItem.setPriority(scRequestItemRequest.getPriority());
                scRequestItem.setReassignmentCount(scRequestItemRequest.getReassignment_count());
                scRequestItem.setShortDescription(scRequestItemRequest.getShort_description());
                scRequestItem.setState(scRequestItemRequest.getState());
                scRequestItem.setIntegrationId(scRequestItemRequest.getSys_id());
                scRequestItem.setSysUpdatedBy(scRequestItemRequest.getSys_updated_by());
                scRequestItem.setSysUpdatedOn(scRequestItemRequest.getSys_updated_on());
                scRequestItem.setExternalTicket(scRequestItemRequest.getU_external_ticket());
                scRequestItem.setPartnersOcurrences(scRequestItemRequest.getU_partners_ocurrences());
                scRequestItem.setSource(scRequestItemRequest.getU_source());
                scRequestItem.setUrgency(scRequestItemRequest.getUrgency());

                if (util.hasData(scRequestItemRequest.getContract())) {
                    scRequestItem.setContract(contractService.findByIntegrationId(scRequestItemRequest.getContract()));
                } else {
                    scRequestItem.setContract(null);
                }

                if (util.hasData(scRequestItemRequest.getAssigned_to())) {
                    scRequestItem.setAssignedTo(sysUserService.findByIntegrationId(scRequestItemRequest.getAssigned_to()));
                } else {
                    scRequestItem.setAssignedTo(null);
                }

                if (util.hasData(scRequestItemRequest.getAssignment_group())) {
                    scRequestItem.setAssignmentGroup(sysGroupService.findByIntegrationId(scRequestItemRequest.getAssignment_group()));
                } else {
                    scRequestItem.setAssignmentGroup(null);
                }

                if (util.hasData(scRequestItemRequest.getLocation())) {
                    scRequestItem.setLocation(locationService.findByIntegrationId(scRequestItemRequest.getLocation()));
                } else {
                    scRequestItem.setLocation(null);
                }


                if (util.hasData(scRequestItemRequest.getClosed_by())) {
                    scRequestItem.setClosedBy(sysUserService.findByIntegrationId(scRequestItemRequest.getClosed_by()));
                } else {
                    scRequestItem.setLocation(null);
                }

                scRequestItem.setScRequestIntegrationId(scRequestItemRequest.getRequest());
                ScRequest scRequest = scRequestService.findByIntegrationId(scRequestItemRequest.getRequest());
                scRequestItem.setScRequest(scRequest);
                scRequestItem.setCompany(scRequest.getCompany());
                scRequestItem.setDomain(scRequest.getCompany().getDomain());


                if (util.hasData(scRequestItemRequest.getParent())) {
                    Incident parent = incidentService.findByIntegrationId(scRequestItemRequest.getParent());
                    if (!parent.getMaster()) {
                        parent.setScRequestParent(scRequest.getIntegrationId());
                        scRequestItem.setIncidentParent(parent.getIntegrationId());
                        incidentService.save(parent);
                    }
                }


                if (util.hasData(scRequestItemRequest.getCat_item())) {
                    scRequestItem.setScCategoryItem(scCategoryItemService.findByIntegrationId(scRequestItemRequest.getCat_item()));
                } else {
                    scRequestItem.setScCategoryItem(null);
                }

                if (util.hasData(scRequestItemRequest.getOpened_by())) {
                    scRequestItem.setOpenedBy(sysUserService.findByIntegrationId(scRequestItemRequest.getOpened_by()));
                } else {
                    scRequestItem.setOpenedBy(null);
                }

                if (util.hasData(scRequestItemRequest.getTask_for())) {
                    scRequestItem.setTaskFor(sysUserService.findByIntegrationId(scRequestItemRequest.getTask_for()));
                } else {
                    scRequestItem.setTaskFor(null);
                }


                if (util.hasData(scRequestItemRequest.getBusiness_service())) {
                    scRequestItem.setBusinessService(ciServiceService.findByIntegrationId(scRequestItemRequest.getBusiness_service()));
                } else {
                    scRequestItem.setBusinessService(null);
                }

                if (util.hasData(scRequestItemRequest.getRequested_for())) {
                    scRequestItem.setRequestedFor(sysUserService.findByIntegrationId(scRequestItemRequest.getRequested_for()));
                } else {
                    scRequestItem.setRequestedFor(null);
                }


                if (util.hasData(scRequestItemRequest.getConfiguration_item())) {
                    scRequestItem.setConfigurationItem(configurationItemService.findByIntegrationId(scRequestItemRequest.getConfiguration_item()));
                } else {
                    scRequestItem.setConfigurationItem(null);
                }

                if (util.hasData(scRequestItemRequest.getAssignment_group())) {
                    scRequestItem.setAssignmentGroup(sysGroupService.findByIntegrationId(scRequestItemRequest.getAssignment_group()));
                } else {
                    scRequestItem.setAssignmentGroup(null);
                }

                scRequestItemUpdated = scRequestItemService.save(scRequestItem);
                util.printData(tag, tagAction.concat(util.getFieldDisplay(scRequestItem)), util.getFieldDisplay(scRequestItem.getCompany()), util.getFieldDisplay(scRequestItem.getDomain()));
            } catch (DataAccessException e) {
                System.out.println("error " + e.getMessage());
                response.put("mensaje", Errors.dataAccessExceptionUpdate.get());
                response.put("error", e.getMessage());
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            response.put("mensaje", Messages.UpdateOK.get());
            response.put("scRequestItem", scRequestItemUpdated);

            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
        }
    */
    @PutMapping("/scRequestItemSN")
    public ResponseEntity<?> update(@RequestBody String json) {
        ScRequestItem scRequestItem = new ScRequestItem();
        Map<String, Object> response = new HashMap<>();


        try {
            String tagAction = app.CreateConsole();
            String tag = "[ScRequestItem] ";


            Gson gson = new Gson();
            ScRequestItemRequest scRequestItemSolver = gson.fromJson(json, ScRequestItemRequest.class);
            ScRequestItem exists = scRequestItemService.findByIntegrationId(scRequestItemSolver.getSys_id());
            if (exists != null) {
                scRequestItem.setId(exists.getId());
                tagAction = app.UpdateConsole();
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

            if (util.hasData(scRequestItemSolver.getOpened_by())) {
                SysUser openedBy = sysUserService.findByIntegrationId(scRequestItemSolver.getOpened_by());
                scRequestItem.setOpenedBy(openedBy);
            }

            if (util.hasData(scRequestItemSolver.getTask_for())) {
                SysUser taskFor = sysUserService.findByIntegrationId(scRequestItemSolver.getTask_for());
                scRequestItem.setTaskFor(taskFor);
            }

            if (util.hasData(scRequestItemSolver.getRequested_for())) {
                SysUser requestedFor = sysUserService.findByIntegrationId(scRequestItemSolver.getRequested_for());
                scRequestItem.setRequestedFor(requestedFor);
            }
            /*if (util.hasData(scRequestItemSolver.getCat_item())) {
                ScCategoryItem scCategoryItem = scCategoryItemService.findByIntegrationId(scRequestItemSolver.getCat_item());
                scRequestItem.setScCategoryItem(scCategoryItem);
            }

            if (util.hasData(scRequestItemSolver.getOpened_by())) {
                SysUser openedBy = sysUserService.findByIntegrationId(scRequestItemSolver.getOpened_by());
                scRequestItem.setOpenedBy(openedBy);
            }

            if (util.hasData(scRequestItemSolver.getTask_for())) {
                SysUser taskFor = sysUserService.findByIntegrationId(scRequestItemSolver.getTask_for());
                scRequestItem.setTaskFor(taskFor);
            }

            if (util.hasData(scRequestItemSolver.getBusiness_service())) {
                CiService businessService = ciServiceService.findByIntegrationId(scRequestItemSolver.getBusiness_service());
                scRequestItem.setBusinessService(businessService);
            }

            if (util.hasData(scRequestItemSolver.getRequested_for())) {
                SysUser requestedFor = sysUserService.findByIntegrationId(scRequestItemSolver.getRequested_for());
                scRequestItem.setRequestedFor(requestedFor);
            }

            if (util.hasData(scRequestItemSolver.getConfiguration_item())) {
                ConfigurationItem configurationItem = configurationItemService.findByIntegrationId(scRequestItemSolver.getConfiguration_item());
                scRequestItem.setConfigurationItem(configurationItem);
            }


            if (util.hasData(scRequestItemSolver.getParent())) {
                Incident parent = incidentService.findByIntegrationId(scRequestItemSolver.getParent());
                if (!parent.getMaster()) {
                    parent.setScRequestParent(scRequest.getIntegrationId());
                    scRequestItem.setIncidentParent(scRequestItemSolver.getParent());
                    incidentService.save(parent);
                }
            }*/

            scRequestItemService.save(scRequestItem);
            util.printData(tag, tagAction.concat(util.getFieldDisplay(scRequestItem)), util.getFieldDisplay(company), util.getFieldDisplay(company.getDomain()));

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
        System.out.println(app.Start());
        APIResponse apiResponse = null;
        List<ScRequestItem> scRequestItems = new ArrayList<>();
        String[] sparmOffSets = util.offSets99000();
        long startTime = 0;
        long endTime = 0;
        String tag = "[ScRequestItem] ";
        try {
            Rest rest = new Rest();
            startTime = System.currentTimeMillis();
            final int[] count = {1};
            for (String sparmOffSet : sparmOffSets) {
                String result = rest.responseByEndPoint(endPointSN.ScRequestItemByCompany().concat(sparmOffSet));
                System.out.println(tag.concat("(".concat(endPointSN.ScRequestItemByCompany().concat(sparmOffSet)).concat(")")));
                ObjectMapper mapper = new ObjectMapper();
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                JSONParser parser = new JSONParser();
                JSONObject resultJson = new JSONObject();
                JSONArray ListSnScRequestItemJson = new JSONArray();

                resultJson = (JSONObject) parser.parse(result);
                if (resultJson.get("result") != null)
                    ListSnScRequestItemJson = (JSONArray) parser.parse(resultJson.get("result").toString());

                ListSnScRequestItemJson.stream().forEach(snScRequestItemJson -> {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    ScRequestItemSolver scRequestItemSolver = new ScRequestItemSolver();
                    try {
                        scRequestItemSolver = objectMapper.readValue(snScRequestItemJson.toString(), ScRequestItemSolver.class);

                        ScRequestItem scRequestItem = new ScRequestItem();
                        scRequestItem.setNumber(scRequestItemSolver.getNumber());
                        scRequestItem.setActive(scRequestItemSolver.getActive());
                        scRequestItem.setState(scRequestItemSolver.getState());
                        scRequestItem.setStage(scRequestItemSolver.getStage());
                        scRequestItem.setTimeWorked(scRequestItemSolver.getTime_worked());
                        scRequestItem.setEscalation(scRequestItemSolver.getEscalation());
                        scRequestItem.setExpectedStart(scRequestItemSolver.getExpected_start());
                        scRequestItem.setEstimatedDelivery(scRequestItemSolver.getEstimated_delivery());
                        scRequestItem.setShortDescription(scRequestItemSolver.getShort_description());
                        scRequestItem.setOpenedAt(scRequestItemSolver.getOpened_at());
                        scRequestItem.setDescription(scRequestItemSolver.getDescription());
                        scRequestItem.setIntegrationId(scRequestItemSolver.getSys_id());
                        scRequestItem.setContactType(scRequestItemSolver.getContact_type());
                        scRequestItem.setUrgency(scRequestItemSolver.getUrgency());
                        scRequestItem.setPriority(scRequestItemSolver.getPriority());
                        scRequestItem.setApproval(scRequestItemSolver.getApproval());
                        scRequestItem.setSysUpdatedOn(scRequestItemSolver.getSys_updated_on());
                        scRequestItem.setSysCreatedBy(scRequestItemSolver.getSys_created_by());
                        scRequestItem.setSysUpdatedBy(scRequestItemSolver.getSys_updated_by());
                        scRequestItem.setSysCreatedOn(scRequestItemSolver.getSys_created_on());


                        Domain domain = getDomainByIntegrationId((JSONObject) snScRequestItemJson, SnTable.Domain.get(), app.Value());
                        if (domain != null)
                            scRequestItem.setDomain(domain);

                        Company company = getCompanyByIntegrationId((JSONObject) snScRequestItemJson, SnTable.Company.get(), app.Value());
                        if (company != null)
                            scRequestItem.setCompany(company);

                        /*SysUser requestedFor = getSysUserByIntegrationId((JSONObject) snScRequestItemJson, "requested_for", app.Value());
                        if (requestedFor != null)
                            scRequestItem.setRequestedFor(requestedFor);*/

                        Location location = getLocationByIntegrationId((JSONObject) snScRequestItemJson, "location", app.Value());
                        if (location != null)
                            scRequestItem.setLocation(location);

                        ScCategoryItem scCategoryItem = getScCategoryItemByIntegrationId((JSONObject) snScRequestItemJson, "cat_item", app.Value());
                        if (scCategoryItem != null)
                            scRequestItem.setScCategoryItem(scCategoryItem);


                        SysUser assignedTo = getSysUserByIntegrationId((JSONObject) snScRequestItemJson, "assigned_to", app.Value());
                        if (assignedTo != null)
                            scRequestItem.setAssignedTo(assignedTo);

                        SysUser openedBy = getSysUserByIntegrationId((JSONObject) snScRequestItemJson, "opened_by", app.Value());
                        if (openedBy != null)
                            scRequestItem.setOpenedBy(openedBy);

                        SysUser taskFor = getSysUserByIntegrationId((JSONObject) snScRequestItemJson, "task_for", app.Value());
                        if (taskFor != null)
                            scRequestItem.setTaskFor(taskFor);

                        CiService businessService = getCiServiceByIntegrationId((JSONObject) snScRequestItemJson, "business_service", app.Value());
                        if (businessService != null)
                            scRequestItem.setBusinessService(businessService);

                        ConfigurationItem configurationItem = getConfigurationItemByIntegrationId((JSONObject) snScRequestItemJson, "cmdb_ci", app.Value());
                        if (configurationItem != null)
                            scRequestItem.setConfigurationItem(configurationItem);

                        SysGroup sysGroup = getSysGroupByIntegrationId((JSONObject) snScRequestItemJson, "assignment_group", app.Value());
                        if (sysGroup != null)
                            scRequestItem.setAssignmentGroup(sysGroup);

                        ScRequest scRequest = getScRequestByIntegrationId((JSONObject) snScRequestItemJson, "request", app.Value());
                        if (scRequest != null) {
                            scRequestItem.setScRequest(scRequest);
                           /* if (scRequest != null) {
                                String incidentParent = util.getIdByJson((JSONObject) snScRequestItemJson, "parent", app.Value());
                                if (util.hasData(incidentParent)) {
                                    Incident parent = incidentService.findByIntegrationId(incidentParent);
                                    if (!parent.getMaster()) {
                                        parent.setScRequestParent(scRequest.getIntegrationId());
                                      //  scRequestItem.setIncidentParent(incidentParent);
                                        incidentService.save(parent);
                                    }
                                }
                            }*/
                            if (scRequest.getRequestedFor() != null)
                                scRequestItem.setRequestedFor(scRequest.getRequestedFor());
                        }

                        String incidentParent = util.getIdByJson((JSONObject) snScRequestItemJson, "parent", app.Value());
                        if (util.hasData(incidentParent)) {
                            Incident parent = incidentService.findByIntegrationId(incidentParent);
                            if (util.hasData(parent))
                                scRequestItem.setIncidentParent(incidentParent);
                            //incidentService.save(parent);

                        }

                        ScRequestItem exists = scRequestItemService.findByIntegrationId(scRequestItem.getIntegrationId());
                        String tagAction = app.CreateConsole();
                        if (exists != null) {
                            scRequestItem.setId(exists.getId());
                            tagAction = app.UpdateConsole();
                        }

                        util.printData(tag, count[0], tagAction.concat(util.getFieldDisplay(scRequestItem)), util.getFieldDisplay(company), util.getFieldDisplay(domain));

                        scRequestItems.add(scRequestItemService.save(scRequestItem));


                        count[0] = count[0] + 1;
                    } catch (Exception e) {
                        System.out.println(tag.concat("Exception (I) : ").concat(String.valueOf(e)));
                    }
                });

                apiResponse = mapper.readValue(result, APIResponse.class);
                APIExecutionStatus status = new APIExecutionStatus();
                status.setUri(endPointSN.Location());
                status.setUserAPI(app.SNUser());
                status.setPasswordAPI(app.SNPassword());
                status.setError(apiResponse.getError());
                status.setMessage(apiResponse.getMessage());
                endTime = (System.currentTimeMillis() - startTime);
                status.setExecutionTime(endTime);
                statusService.save(status);
            }

        } catch (Exception e) {
            System.out.println(tag.concat("Exception (II) : ").concat(String.valueOf(e)));
        }
        System.out.println(app.End());
        return scRequestItems;
    }


    @GetMapping("/scRequestItemBySolverQueryCreate")
    public List<ScRequestItem> show(String query, boolean flagCreate) {
        System.out.println(app.Start());
        APIResponse apiResponse = null;
        List<ScRequestItem> scRequestItems = new ArrayList<>();
        String[] sparmOffSets = util.offSets99000();
        long startTime = 0;
        long endTime = 0;
        String tag = "[ScRequestItem] ";
        try {
            Rest rest = new Rest();
            startTime = System.currentTimeMillis();
            final int[] count = {1};
            for (String sparmOffSet : sparmOffSets) {

                String result = rest.responseByEndPoint(endPointSN.ScRequestItemByQuery().replace("QUERY", query).concat(sparmOffSet));
                System.out.println(tag.concat("(".concat(endPointSN.ScRequestItemByQuery().replace("QUERY", query).concat(sparmOffSet)).concat(")")));


                ObjectMapper mapper = new ObjectMapper();
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                JSONParser parser = new JSONParser();
                JSONObject resultJson;
                JSONArray ListSnScRequestItemJson = new JSONArray();

                resultJson = (JSONObject) parser.parse(result);
                if (resultJson.get("result") != null)
                    ListSnScRequestItemJson = (JSONArray) parser.parse(resultJson.get("result").toString());

                ListSnScRequestItemJson.stream().forEach(snScRequestItemJson -> {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    ScRequestItemSolver scRequestItemSolver;
                    try {
                        scRequestItemSolver = objectMapper.readValue(snScRequestItemJson.toString(), ScRequestItemSolver.class);
                        ScRequestItem scRequestItem = new ScRequestItem();

                        ScRequestItem exists = scRequestItemService.findByIntegrationId(scRequestItem.getIntegrationId());
                        String tagAction = app.CreateConsole();
                        if (exists != null) {
                            scRequestItem.setId(exists.getId());
                        } else {

                            scRequestItem.setNumber(scRequestItemSolver.getNumber());
                            scRequestItem.setActive(scRequestItemSolver.getActive());
                            scRequestItem.setState(scRequestItemSolver.getState());
                            scRequestItem.setStage(scRequestItemSolver.getStage());
                            scRequestItem.setTimeWorked(scRequestItemSolver.getTime_worked());
                            scRequestItem.setEscalation(scRequestItemSolver.getEscalation());
                            scRequestItem.setExpectedStart(scRequestItemSolver.getExpected_start());
                            scRequestItem.setEstimatedDelivery(scRequestItemSolver.getEstimated_delivery());
                            scRequestItem.setShortDescription(scRequestItemSolver.getShort_description());
                            scRequestItem.setOpenedAt(scRequestItemSolver.getOpened_at());
                            scRequestItem.setDescription(scRequestItemSolver.getDescription());
                            scRequestItem.setIntegrationId(scRequestItemSolver.getSys_id());
                            scRequestItem.setContactType(scRequestItemSolver.getContact_type());
                            scRequestItem.setUrgency(scRequestItemSolver.getUrgency());
                            scRequestItem.setPriority(scRequestItemSolver.getPriority());
                            scRequestItem.setApproval(scRequestItemSolver.getApproval());
                            scRequestItem.setSysUpdatedOn(scRequestItemSolver.getSys_updated_on());
                            scRequestItem.setSysCreatedBy(scRequestItemSolver.getSys_created_by());
                            scRequestItem.setSysUpdatedBy(scRequestItemSolver.getSys_updated_by());
                            scRequestItem.setSysCreatedOn(scRequestItemSolver.getSys_created_on());


                            Domain domain = getDomainByIntegrationId((JSONObject) snScRequestItemJson, SnTable.Domain.get(), app.Value());
                            if (domain != null)
                                scRequestItem.setDomain(domain);

                            Company company = getCompanyByIntegrationId((JSONObject) snScRequestItemJson, SnTable.Company.get(), app.Value());
                            if (company != null)
                                scRequestItem.setCompany(company);


                            Location location = getLocationByIntegrationId((JSONObject) snScRequestItemJson, "location", app.Value());
                            if (location != null)
                                scRequestItem.setLocation(location);

                            ScCategoryItem scCategoryItem = getScCategoryItemByIntegrationId((JSONObject) snScRequestItemJson, "cat_item", app.Value());
                            if (scCategoryItem != null)
                                scRequestItem.setScCategoryItem(scCategoryItem);


                            SysUser assignedTo = getSysUserByIntegrationId((JSONObject) snScRequestItemJson, "assigned_to", app.Value());
                            if (assignedTo != null)
                                scRequestItem.setAssignedTo(assignedTo);

                            SysUser openedBy = getSysUserByIntegrationId((JSONObject) snScRequestItemJson, "opened_by", app.Value());
                            if (openedBy != null)
                                scRequestItem.setOpenedBy(openedBy);

                            SysUser taskFor = getSysUserByIntegrationId((JSONObject) snScRequestItemJson, "task_for", app.Value());
                            if (taskFor != null)
                                scRequestItem.setTaskFor(taskFor);

                            CiService businessService = getCiServiceByIntegrationId((JSONObject) snScRequestItemJson, "business_service", app.Value());
                            if (businessService != null)
                                scRequestItem.setBusinessService(businessService);

                            ConfigurationItem configurationItem = getConfigurationItemByIntegrationId((JSONObject) snScRequestItemJson, "cmdb_ci", app.Value());
                            if (configurationItem != null)
                                scRequestItem.setConfigurationItem(configurationItem);

                            SysGroup sysGroup = getSysGroupByIntegrationId((JSONObject) snScRequestItemJson, "assignment_group", app.Value());
                            if (sysGroup != null)
                                scRequestItem.setAssignmentGroup(sysGroup);

                            ScRequest scRequest = getScRequestByIntegrationId((JSONObject) snScRequestItemJson, "request", app.Value());
                            if (scRequest != null) {
                                scRequestItem.setScRequest(scRequest);

                                if (scRequest.getRequestedFor() != null)
                                    scRequestItem.setRequestedFor(scRequest.getRequestedFor());
                            }

                            String incidentParent = util.getIdByJson((JSONObject) snScRequestItemJson, "parent", app.Value());
                            if (util.hasData(incidentParent)) {
                                Incident parent = incidentService.findByIntegrationId(incidentParent);
                                if (util.hasData(parent))
                                    scRequestItem.setIncidentParent(incidentParent);

                            }


                            ScRequestItem _scRequestItem = scRequestItemService.save(scRequestItem);
                            scRequestItems.add(_scRequestItem);
                            util.printData(tag, count[0], tagAction.concat(util.getFieldDisplay(scRequestItem)), util.getFieldDisplay(company), util.getFieldDisplay(domain));
                            count[0] = count[0] + 1;
                        }
                    } catch (Exception e) {
                        //     System.out.println(tag.concat("Exception (I) : ").concat(String.valueOf(e)));
                    }
                });

                apiResponse = mapper.readValue(result, APIResponse.class);
                APIExecutionStatus status = new APIExecutionStatus();
                status.setUri(endPointSN.Location());
                status.setUserAPI(app.SNUser());
                status.setPasswordAPI(app.SNPassword());
                status.setError(apiResponse.getError());
                status.setMessage(apiResponse.getMessage());
                endTime = (System.currentTimeMillis() - startTime);
                status.setExecutionTime(endTime);
                statusService.save(status);
            }

        } catch (Exception e) {
            //System.out.println(tag.concat("Exception (II) : ").concat(String.valueOf(e)));
        }
        System.out.println(app.End());
        return scRequestItems;
    }

    public Domain getDomainByIntegrationId(JSONObject jsonObject, String levelOne, String levelTwo) {
        String integrationId = util.getIdByJson(jsonObject, levelOne, levelTwo);
        if (util.hasData(integrationId)) {
            return domainService.findByIntegrationId(integrationId);
        } else
            return null;
    }

    public SysUser getSysUserByIntegrationId(JSONObject jsonObject, String levelOne, String levelTwo) {
        String integrationId = util.getIdByJson(jsonObject, levelOne, levelTwo);
        if (util.hasData(integrationId)) {
            return sysUserService.findByIntegrationId(integrationId);
        } else
            return null;
    }

    public CiService getCiServiceByIntegrationId(JSONObject jsonObject, String levelOne, String levelTwo) {
        String integrationId = util.getIdByJson(jsonObject, levelOne, levelTwo);
        if (util.hasData(integrationId)) {
            return ciServiceService.findByIntegrationId(integrationId);
        } else
            return null;
    }

    public Company getCompanyByIntegrationId(JSONObject jsonObject, String levelOne, String levelTwo) {
        String integrationId = util.getIdByJson(jsonObject, levelOne, levelTwo);
        if (util.hasData(integrationId)) {
            return companyService.findByIntegrationId(integrationId);
        } else
            return null;
    }

    public ConfigurationItem getConfigurationItemByIntegrationId(JSONObject jsonObject, String levelOne, String levelTwo) {
        String integrationId = util.getIdByJson(jsonObject, levelOne, levelTwo);
        if (util.hasData(integrationId)) {
            return configurationItemService.findByIntegrationId(integrationId);
        } else
            return null;
    }

    public SysGroup getSysGroupByIntegrationId(JSONObject jsonObject, String levelOne, String levelTwo) {
        String integrationId = util.getIdByJson(jsonObject, levelOne, levelTwo);
        if (util.hasData(integrationId)) {
            return sysGroupService.findByIntegrationId(integrationId);
        } else
            return null;
    }

    public Location getLocationByIntegrationId(JSONObject jsonObject, String levelOne, String levelTwo) {
        String integrationId = util.getIdByJson(jsonObject, levelOne, levelTwo);
        if (util.hasData(integrationId)) {
            return locationService.findByIntegrationId(integrationId);
        } else
            return null;
    }

    public ScCategoryItem getScCategoryItemByIntegrationId(JSONObject jsonObject, String levelOne, String levelTwo) {
        String integrationId = util.getIdByJson(jsonObject, levelOne, levelTwo);
        if (util.hasData(integrationId)) {
            return scCategoryItemService.findByIntegrationId(integrationId);
        } else
            return null;
    }

    public ScRequest getScRequestByIntegrationId(JSONObject jsonObject, String levelOne, String levelTwo) {
        String integrationId = util.getIdByJson(jsonObject, levelOne, levelTwo);
        if (util.hasData(integrationId)) {
            return scRequestService.findByIntegrationId(integrationId);
        } else
            return null;
    }


    public SysUser getSysUserByIntegrationId(String integrationId) {
        //String integrationId = util.getIdByJson(jsonObject, levelOne, levelTwo);
        if (util.hasData(integrationId)) {
            return sysUserService.findByIntegrationId(integrationId);
        } else
            return null;
    }

    public SysGroup getSysGroupByIntegrationId(String integrationId) {
        //String integrationId = util.getIdByJson(jsonObject, levelOne, levelTwo);
        if (util.hasData(integrationId)) {
            return sysGroupService.findByIntegrationId(integrationId);
        } else
            return null;
    }
}


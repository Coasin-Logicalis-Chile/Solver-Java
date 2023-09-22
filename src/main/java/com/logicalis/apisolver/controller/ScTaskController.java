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
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = {"${app.api.settings.cross-origin.urls}", "*"})
@RestController
@RequestMapping("/api/v1")
public class ScTaskController {
    @Autowired
    private IScTaskService scTaskService;
    @Autowired
    private IScRequestItemService scRequestItemService;
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

    @GetMapping("/scTasks")
    public List<ScTask> index() {
        return scTaskService.findAll();
    }

    @GetMapping("/scTask/{id}")
    public ResponseEntity<?> show(@PathVariable Long id) {
        ScTask scTask = null;
        Map<String, Object> response = new HashMap<>();
        try {
            scTask = scTaskService.findById(id);
        } catch (DataAccessException e) {
            response.put("mensaje", Errors.dataAccessExceptionQuery.get());
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (scTask == null) {
            response.put("mensaje", Messages.notExist.get(id.toString()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<ScTask>(scTask, HttpStatus.OK);
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/scTask")
    public ResponseEntity<?> create(@RequestBody ScTask scTask) {
        ScTask newScTask = null;
        Map<String, Object> response = new HashMap<>();
        try {
            newScTask = scTaskService.save(scTask);
        } catch (DataAccessException e) {
            response.put("mensaje", Errors.dataAccessExceptionInsert.get());
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", Messages.createOK.get());
        response.put("scTask", newScTask);
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }

    @PutMapping("/scTask/{id}")
    public ResponseEntity<?> update(@RequestBody String json, @PathVariable Long id) {
        ScTask currentScTask = scTaskService.findById(id);
        ScTask scTaskUpdated = null;
        Map<String, Object> response = new HashMap<>();
        if (currentScTask == null) {
            response.put("mensaje", Errors.dataAccessExceptionUpdate.get(id.toString()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }
        try {
            String levelOne = "scTask";
            SysUser assignedTo = getSysUserByIntegrationId(Util.parseJson(json, levelOne, "assigned_to"));
            currentScTask.setAssignedTo(assignedTo);
            SysGroup sysGroup = getSysGroupByIntegrationId(Util.parseJson(json, levelOne, "assignment_group"));
            currentScTask.setAssignmentGroup(sysGroup);
            SysUser scalingAssignedTo = getSysUserByIntegrationId(Util.parseJson(json, levelOne, "scaling_assigned_to"));
            currentScTask.setScalingAssignedTo(scalingAssignedTo);
            SysGroup scalingAssignmentGroup = getSysGroupByIntegrationId(Util.parseJson(json, levelOne, "scaling_assignment_group"));
            currentScTask.setScalingAssignmentGroup(scalingAssignmentGroup);
            currentScTask.setScaling(Util.parseBooleanJson(json, levelOne, "scaling"));
            currentScTask.setDescription(Util.parseJson(json, levelOne, "description"));
            currentScTask.setShortDescription(Util.parseJson(json, levelOne, "short_description"));
            currentScTask.setPriority(Util.parseJson(json, levelOne, "priority"));
            currentScTask.setState(Util.parseJson(json, levelOne, "state"));
            currentScTask.setCloseNotes(Util.parseJson(json, levelOne, "close_notes"));
            currentScTask.setClosedAt(Util.parseJson(json, levelOne, "closed_at"));
            SysUser closedBy = getSysUserByIntegrationId(Util.parseJson(json, levelOne, "closed_by"));
            currentScTask.setReasonPending(Util.parseJson(json, levelOne, Field.ReasonPending.get()));
            currentScTask.setClosedBy(closedBy);
            ScTask addScTask = rest.putScTask(EndPointSN.PutScTask(), currentScTask);
            scTaskUpdated = scTaskService.save(currentScTask);
            String tag = "[ScTask]";
            String tagAction = "Update in ServiceNow";
            Util.printData(tag, tagAction, Util.getFieldDisplay(currentScTask), Util.getFieldDisplay(currentScTask.getAssignedTo()));
        } catch (DataAccessException e) {
            response.put("mensaje", Errors.dataAccessExceptionUpdate.get());
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", Messages.UpdateOK.get());
        response.put("scTask", scTaskUpdated);
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/scTask/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            scTaskService.delete(id);
        } catch (DataAccessException e) {
            response.put("mensaje", Errors.dataAccessExceptionDelete.get());
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", Messages.DeleteOK.get());
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
    }

    @GetMapping("/scTaskIntegration/{integration_id}")
    public ResponseEntity<?> findByIntegrationId(@PathVariable String integration_id) {
        ScTask scTask = null;
        Map<String, Object> response = new HashMap<>();
        try {
            scTask = scTaskService.findByIntegrationId(integration_id);
        } catch (DataAccessException e) {
            response.put("mensaje", Errors.dataAccessExceptionQuery.get());
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (scTask == null) {
            response.put("mensaje", "El registro ".concat(integration_id.concat(" no existe en la base de datos!")));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<ScTask>(scTask, HttpStatus.OK);
    }

    @GetMapping("/scTaskBySolver")
    public List<ScTask> findByCompany() {
        System.out.println(App.Start());
        APIResponse apiResponse = null;
        List<ScTask> scTasks = new ArrayList<>();
        String[] sparmOffSets = Util.offSets99000();
        long startTime = 0;
        long endTime = 0;
        String tag = "[ScTask] ";
        final ScTaskRequest[] scTaskSolver = {new ScTaskRequest()};
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        JSONParser parser = new JSONParser();
        JSONObject resultJson = new JSONObject();
        JSONArray ListScTaskJson = new JSONArray();
        ScTask scTask = new ScTask();
        final Domain[] domain = new Domain[1];
        final Company[] company = new Company[1];
        final SysUser[] closedBy = new SysUser[1];
        final SysUser[] requestedfor = new SysUser[1];
        final SysUser[] scalingAssignedTo = new SysUser[1];
        final Location[] location = new Location[1];
        final SysUser[] assignedTo = new SysUser[1];
        final SysUser[] openedBy = new SysUser[1];
        String result;
        final CiService[] businessService = new CiService[1];
        final ConfigurationItem[] configurationItem = new ConfigurationItem[1];
        final SysGroup[] sysGroup = new SysGroup[1];
        final SysGroup[] scalingAssignmentGroup = new SysGroup[1];
        final ScRequest[] scRequest = new ScRequest[1];
        final ScRequestItem[] scRequestItem = new ScRequestItem[1];
        final ScTask[] exists = new ScTask[1];
        final String[] tagAction = new String[1];
        APIExecutionStatus status = new APIExecutionStatus();
        try {
            startTime = System.currentTimeMillis();
            final int[] count = {1};
            for (String sparmOffSet : sparmOffSets) {
                result = rest.responseByEndPoint(EndPointSN.ScTaskByCompany().concat(sparmOffSet));
                System.out.println(tag.concat("(".concat(EndPointSN.ScTaskByCompany().concat(sparmOffSet)).concat(")")));
                resultJson = (JSONObject) parser.parse(result);
                if (resultJson.get("result") != null)
                    ListScTaskJson = (JSONArray) parser.parse(resultJson.get("result").toString());
                ListScTaskJson.stream().forEach(scTaskJson -> {
                    try {
                        scTaskSolver[0] = mapper.readValue(scTaskJson.toString(), ScTaskRequest.class);
                        scTask.setActionStatus(Util.isNull(scTaskSolver[0].getAction_status()));
                        scTask.setActive(scTaskSolver[0].getActive());
                        scTask.setActivityDue(Util.isNull(scTaskSolver[0].getActivity_due()));
                        scTask.setAdditionalAssigneeList(Util.isNull(scTaskSolver[0].getAdditional_assignee_list()));
                        scTask.setApproval(Util.isNull(scTaskSolver[0].getApproval()));
                        scTask.setApprovalHistory(Util.isNull(scTaskSolver[0].getApproval_history()));
                        scTask.setApprovalSet(Util.isNull(scTaskSolver[0].getApproval_set()));
                        scTask.setBusinessDuration(Util.isNull(scTaskSolver[0].getBusiness_duration()));
                        scTask.setCalendarDuration(Util.isNull(scTaskSolver[0].getCalendar_duration()));
                        scTask.setCalendarStc(Util.isNull(scTaskSolver[0].getCalendar_stc()));
                        scTask.setCloseNotes(Util.isNull(scTaskSolver[0].getClose_notes()));
                        scTask.setClosedAt(Util.isNull(scTaskSolver[0].getClosed_at()));
                        scTask.setComments(Util.isNull(scTaskSolver[0].getComments()));
                        scTask.setCommentsAndWorkNotes(Util.isNull(scTaskSolver[0].getComments_and_work_notes()));
                        scTask.setContactType(Util.isNull(scTaskSolver[0].getContact_type()));
                        scTask.setCorrelationDisplay(Util.isNull(scTaskSolver[0].getCorrelation_display()));
                        scTask.setCorrelationId(Util.isNull(scTaskSolver[0].getCorrelation_id()));
                        scTask.setDeliveryPlan(Util.isNull(scTaskSolver[0].getDelivery_plan()));
                        scTask.setDeliveryTask(Util.isNull(scTaskSolver[0].getDelivery_task()));
                        scTask.setDescription(Util.isNull(scTaskSolver[0].getDescription()));
                        scTask.setDueDate(Util.isNull(scTaskSolver[0].getDue_date()));
                        scTask.setEscalation(Util.isNull(scTaskSolver[0].getEscalation()));
                        scTask.setExpectedStart(Util.isNull(scTaskSolver[0].getExpected_start()));
                        scTask.setFollowUp(Util.isNull(scTaskSolver[0].getFollow_up()));
                        scTask.setGroupList(Util.isNull(scTaskSolver[0].getGroup_list()));
                        scTask.setImpact(Util.isNull(scTaskSolver[0].getImpact()));
                        scTask.setKnowledge(Util.isNull(scTaskSolver[0].getKnowledge()));
                        scTask.setMadeSla(Util.isNull(scTaskSolver[0].getMade_sla()));
                        scTask.setNeedsAttention(Util.isNull(scTaskSolver[0].getNeeds_attention()));
                        scTask.setNumber(Util.isNull(scTaskSolver[0].getNumber()));
                        scTask.setOpenedAt(Util.isNull(scTaskSolver[0].getOpened_at()));
                        scTask.setPriority(Util.isNull(scTaskSolver[0].getPriority()));
                        scTask.setReassignmentCount(Util.isNull(scTaskSolver[0].getReassignment_count()));
                        scTask.setRouteReason(Util.isNull(scTaskSolver[0].getRoute_reason()));
                        scTask.setServiceOffering(Util.isNull(scTaskSolver[0].getService_offering()));
                        scTask.setShortDescription(Util.isNull(scTaskSolver[0].getShort_description()));
                        scTask.setSkills(Util.isNull(scTaskSolver[0].getSkills()));
                        scTask.setSlaDue(Util.isNull(scTaskSolver[0].getSla_due()));
                        scTask.setState(Util.isNull(scTaskSolver[0].getState()));
                        scTask.setSysClassName(Util.isNull(scTaskSolver[0].getSys_class_name()));
                        scTask.setSysCreatedBy(Util.isNull(scTaskSolver[0].getSys_created_by()));
                        scTask.setSysCreatedOn(Util.isNull(scTaskSolver[0].getSys_created_on()));
                        scTask.setCreatedOn(Util.getLocalDateTime(Util.isNull(scTaskSolver[0].getSys_created_on())));
                        scTask.setIntegrationId(Util.isNull(scTaskSolver[0].getSys_id()));
                        scTask.setSysModCount(Util.isNull(scTaskSolver[0].getSys_mod_count()));
                        scTask.setSysUpdatedBy(Util.isNull(scTaskSolver[0].getSys_updated_by()));
                        scTask.setSysUpdatedOn(Util.isNull(scTaskSolver[0].getSys_updated_on()));
                        scTask.setUpdatedOn(Util.getLocalDateTime(Util.isNull(scTaskSolver[0].getSys_updated_on())));
                        scTask.setTaskEffectiveNumber(Util.isNull(scTaskSolver[0].getTask_effective_number()));
                        scTask.setTimeWorked(Util.isNull(scTaskSolver[0].getTime_worked()));
                        scTask.setUniversalRequest(Util.isNull(scTaskSolver[0].getUniversal_request()));
                        scTask.setUponApproval(Util.isNull(scTaskSolver[0].getUpon_approval()));
                        scTask.setUponReject(Util.isNull(scTaskSolver[0].getUpon_reject()));
                        scTask.setUrgency(Util.isNull(scTaskSolver[0].getUrgency()));
                        scTask.setUserInput(Util.isNull(scTaskSolver[0].getUser_input()));
                        scTask.setWorkEnd(Util.isNull(scTaskSolver[0].getWork_end()));
                        scTask.setWorkStart(Util.isNull(scTaskSolver[0].getWork_start()));
                        scTask.setScaling(scTaskSolver[0].getU_pending_other_group());

                        domain[0] = getDomainByIntegrationId((JSONObject) scTaskJson, SnTable.Domain.get(), App.Value());
                        if (domain[0] != null)
                            scTask.setDomain(domain[0]);
                        company[0] = getCompanyByIntegrationId((JSONObject) scTaskJson, SnTable.Company.get(), App.Value());
                        if (company[0] != null)
                            scTask.setCompany(company[0]);
                        closedBy[0] = getSysUserByIntegrationId((JSONObject) scTaskJson, "closed_by", App.Value());
                        if (closedBy[0] != null)
                            scTask.setClosedBy(closedBy[0]);
                        requestedfor[0] = getSysUserByIntegrationId((JSONObject) scTaskJson, "requested_for", App.Value());
                        if (requestedfor[0] != null)
                            scTask.setRequestedFor(requestedfor[0]);
                        scalingAssignedTo[0] = getSysUserByIntegrationId((JSONObject) scTaskJson, "u_scaling_assigned_to", App.Value());
                        if (scalingAssignedTo[0] != null)
                            scTask.setScalingAssignedTo(scalingAssignedTo[0]);
                        location[0] = getLocationByIntegrationId((JSONObject) scTaskJson, "location", App.Value());
                        if (location[0] != null)
                            scTask.setLocation(location[0]);
                        assignedTo[0] = getSysUserByIntegrationId((JSONObject) scTaskJson, "u_solver_assigned_to", App.Value());
                        if (assignedTo[0] != null)
                            scTask.setAssignedTo(assignedTo[0]);
                        openedBy[0] = getSysUserByIntegrationId((JSONObject) scTaskJson, "opened_by", App.Value());
                        if (openedBy[0] != null)
                            scTask.setOpenedBy(openedBy[0]);

                        businessService[0] = getCiServiceByIntegrationId((JSONObject) scTaskJson, "business_service", App.Value());
                        if (businessService[0] != null)
                            scTask.setBusinessService(businessService[0]);

                        configurationItem[0] = getConfigurationItemByIntegrationId((JSONObject) scTaskJson, "cmdb_ci", App.Value());
                        if (configurationItem[0] != null)
                            scTask.setConfigurationItem(configurationItem[0]);
                        sysGroup[0] = getSysGroupByIntegrationId((JSONObject) scTaskJson, "assignment_group", App.Value());
                        if (sysGroup[0] != null)
                            scTask.setAssignmentGroup(sysGroup[0]);
                        scalingAssignmentGroup[0] = getSysGroupByIntegrationId((JSONObject) scTaskJson, "u_scaling_group", App.Value());
                        if (scalingAssignmentGroup[0] != null)
                            scTask.setScalingAssignmentGroup(scalingAssignmentGroup[0]);

                        scRequest[0] = getScRequestByIntegrationId((JSONObject) scTaskJson, "request", App.Value());
                        if (scRequest[0] != null)
                            scTask.setScRequest(scRequest[0]);

                        scRequestItem[0] = getScRequestItemByIntegrationId((JSONObject) scTaskJson, "request_item", App.Value());
                        if (scRequestItem[0] != null) {
                            scTask.setScRequestItem(scRequestItem[0]);
                            if (scRequestItem[0].getRequestedFor() != null)
                                scTask.setRequestedFor(scRequestItem[0].getRequestedFor());
                            if (scRequestItem[0].getTaskFor() != null)
                                scTask.setTaskFor(scRequestItem[0].getTaskFor());
                        }
                        exists[0] = scTaskService.findByIntegrationId(scTask.getIntegrationId());
                        tagAction[0] = App.CreateConsole();
                        if (exists[0] != null) {
                            scTask.setId(exists[0].getId());
                            tagAction[0] = App.UpdateConsole();
                        }
                        Util.printData(tag, count[0], tagAction[0].concat(Util.getFieldDisplay(scTask)), Util.getFieldDisplay(company[0]), Util.getFieldDisplay(domain[0]));
                        scTasks.add(scTaskService.save(scTask));
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
        return scTasks;
    }

    @GetMapping("/scTaskBySolverAndQuery")
    public List<ScTask> findByCompany(String query) {
        System.out.println(App.Start());
        APIResponse apiResponse = null;
        List<ScTask> scTasks = new ArrayList<>();
        String[] sparmOffSets = Util.offSets99000();
        long startTime = 0;
        long endTime = 0;
        String tag = "[ScTask] ";
        String result;
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        JSONParser parser = new JSONParser();
        JSONObject resultJson = new JSONObject();
        JSONArray ListScTaskJson = new JSONArray();
        final ScTaskRequest[] scTaskSolver = {new ScTaskRequest()};
        Gson gson = new Gson();
        ScTask scTask = new ScTask();
        final Domain[] domain = new Domain[1];
        final Company[] company = new Company[1];
        final SysUser[] closedBy = new SysUser[1];
        final SysUser[] scalingAssignedTo = new SysUser[1];
        final Location[] location = new Location[1];
        final SysUser[] assignedTo = new SysUser[1];
        final SysUser[] openedBy = new SysUser[1];
        final CiService[] businessService = new CiService[1];
        final ConfigurationItem[] configurationItem = new ConfigurationItem[1];
        final SysGroup[] sysGroup = new SysGroup[1];
        final SysGroup[] scalingAssignmentGroup = new SysGroup[1];
        final ScRequest[] scRequest = new ScRequest[1];
        final ScRequestItem[] scRequestItem = new ScRequestItem[1];
        final ScTask[] exists = new ScTask[1];
        final String[] tagAction = new String[1];
        APIExecutionStatus status = new APIExecutionStatus();
        try {
            startTime = System.currentTimeMillis();
            final int[] count = {1};
            for (String sparmOffSet : sparmOffSets) {
                result= rest.responseByEndPoint(EndPointSN.ScTaskByQuery().replace("QUERY", query).concat(sparmOffSet));
                System.out.println(tag.concat("(".concat(EndPointSN.ScTaskByQuery().replace("QUERY", query).concat(sparmOffSet)).concat(")")));
                resultJson = (JSONObject) parser.parse(result);
                if (resultJson.get("result") != null)
                    ListScTaskJson = (JSONArray) parser.parse(resultJson.get("result").toString());
                ListScTaskJson.stream().forEach(scTaskJson -> {
                    try {
                        scTaskSolver[0] = gson.fromJson(scTaskJson.toString(), ScTaskRequest.class);
                        scTask.setActionStatus(Util.isNull(scTaskSolver[0].getAction_status()));
                        scTask.setActive(scTaskSolver[0].getActive());
                        scTask.setActivityDue(Util.isNull(scTaskSolver[0].getActivity_due()));
                        scTask.setAdditionalAssigneeList(Util.isNull(scTaskSolver[0].getAdditional_assignee_list()));
                        scTask.setApproval(Util.isNull(scTaskSolver[0].getApproval()));
                        scTask.setApprovalHistory(Util.isNull(scTaskSolver[0].getApproval_history()));
                        scTask.setApprovalSet(Util.isNull(scTaskSolver[0].getApproval_set()));
                        scTask.setBusinessDuration(Util.isNull(scTaskSolver[0].getBusiness_duration()));
                        scTask.setCalendarDuration(Util.isNull(scTaskSolver[0].getCalendar_duration()));
                        scTask.setCalendarStc(Util.isNull(scTaskSolver[0].getCalendar_stc()));
                        scTask.setCloseNotes(Util.isNull(scTaskSolver[0].getClose_notes()));
                        scTask.setClosedAt(Util.isNull(scTaskSolver[0].getClosed_at()));
                        scTask.setComments(Util.isNull(scTaskSolver[0].getComments()));
                        scTask.setCommentsAndWorkNotes(Util.isNull(scTaskSolver[0].getComments_and_work_notes()));
                        scTask.setContactType(Util.isNull(scTaskSolver[0].getContact_type()));
                        scTask.setCorrelationDisplay(Util.isNull(scTaskSolver[0].getCorrelation_display()));
                        scTask.setCorrelationId(Util.isNull(scTaskSolver[0].getCorrelation_id()));
                        scTask.setDeliveryPlan(Util.isNull(scTaskSolver[0].getDelivery_plan()));
                        scTask.setDeliveryTask(Util.isNull(scTaskSolver[0].getDelivery_task()));
                        scTask.setDescription(Util.isNull(scTaskSolver[0].getDescription()));
                        scTask.setDueDate(Util.isNull(scTaskSolver[0].getDue_date()));
                        scTask.setEscalation(Util.isNull(scTaskSolver[0].getEscalation()));
                        scTask.setExpectedStart(Util.isNull(scTaskSolver[0].getExpected_start()));
                        scTask.setFollowUp(Util.isNull(scTaskSolver[0].getFollow_up()));
                        scTask.setGroupList(Util.isNull(scTaskSolver[0].getGroup_list()));
                        scTask.setImpact(Util.isNull(scTaskSolver[0].getImpact()));
                        scTask.setKnowledge(Util.isNull(scTaskSolver[0].getKnowledge()));
                        scTask.setMadeSla(Util.isNull(scTaskSolver[0].getMade_sla()));
                        scTask.setNeedsAttention(Util.isNull(scTaskSolver[0].getNeeds_attention()));
                        scTask.setNumber(Util.isNull(scTaskSolver[0].getNumber()));
                        scTask.setOpenedAt(Util.isNull(scTaskSolver[0].getOpened_at()));
                        scTask.setPriority(Util.isNull(scTaskSolver[0].getPriority()));
                        scTask.setReassignmentCount(Util.isNull(scTaskSolver[0].getReassignment_count()));
                        scTask.setRouteReason(Util.isNull(scTaskSolver[0].getRoute_reason()));
                        scTask.setServiceOffering(Util.isNull(scTaskSolver[0].getService_offering()));
                        scTask.setShortDescription(Util.isNull(scTaskSolver[0].getShort_description()));
                        scTask.setSkills(Util.isNull(scTaskSolver[0].getSkills()));
                        scTask.setSlaDue(Util.isNull(scTaskSolver[0].getSla_due()));
                        scTask.setState(Util.isNull(scTaskSolver[0].getState()));
                        scTask.setSysClassName(Util.isNull(scTaskSolver[0].getSys_class_name()));
                        scTask.setSysCreatedBy(Util.isNull(scTaskSolver[0].getSys_created_by()));
                        scTask.setSysCreatedOn(Util.isNull(scTaskSolver[0].getSys_created_on()));
                        if (!Util.isNull(scTaskSolver[0].getSys_created_on()).equals(""))
                            scTask.setCreatedOn(Util.getLocalDateTime(Util.isNull(scTaskSolver[0].getSys_created_on())));
                        scTask.setIntegrationId(Util.isNull(scTaskSolver[0].getSys_id()));
                        scTask.setSysModCount(Util.isNull(scTaskSolver[0].getSys_mod_count()));
                        scTask.setSysUpdatedBy(Util.isNull(scTaskSolver[0].getSys_updated_by()));
                        scTask.setSysUpdatedOn(Util.isNull(scTaskSolver[0].getSys_updated_on()));
                        if (!Util.isNull(scTaskSolver[0].getClosed_at()).equals(""))
                            scTask.setUpdatedOn(Util.getLocalDateTime(Util.isNull(scTaskSolver[0].getClosed_at())));
                        scTask.setTaskEffectiveNumber(Util.isNull(scTaskSolver[0].getTask_effective_number()));
                        scTask.setTimeWorked(Util.isNull(scTaskSolver[0].getTime_worked()));
                        scTask.setUniversalRequest(Util.isNull(scTaskSolver[0].getUniversal_request()));
                        scTask.setUponApproval(Util.isNull(scTaskSolver[0].getUpon_approval()));
                        scTask.setUponReject(Util.isNull(scTaskSolver[0].getUpon_reject()));
                        scTask.setUrgency(Util.isNull(scTaskSolver[0].getUrgency()));
                        scTask.setUserInput(Util.isNull(scTaskSolver[0].getUser_input()));
                        scTask.setWorkEnd(Util.isNull(scTaskSolver[0].getWork_end()));
                        scTask.setWorkStart(Util.isNull(scTaskSolver[0].getWork_start()));
                        scTask.setScaling(scTaskSolver[0].getU_pending_other_group());
                        domain[0] = getDomainByIntegrationId((JSONObject) scTaskJson, SnTable.Domain.get(), App.Value());
                        if (domain[0] != null)
                            scTask.setDomain(domain[0]);
                        company[0] = getCompanyByIntegrationId((JSONObject) scTaskJson, SnTable.Company.get(), App.Value());
                        if (company[0] != null)
                            scTask.setCompany(company[0]);
                        closedBy[0] = getSysUserByIntegrationId((JSONObject) scTaskJson, "closed_by", App.Value());
                        if (closedBy[0] != null)
                            scTask.setClosedBy(closedBy[0]);
                        scalingAssignedTo[0] = getSysUserByIntegrationId((JSONObject) scTaskJson, "u_scaling_assigned_to", App.Value());
                        if (scalingAssignedTo[0] != null)
                            scTask.setScalingAssignedTo(scalingAssignedTo[0]);
                        location[0] = getLocationByIntegrationId((JSONObject) scTaskJson, "location", App.Value());
                        if (location[0] != null)
                            scTask.setLocation(location[0]);
                        assignedTo[0] = getSysUserByIntegrationId((JSONObject) scTaskJson, "u_solver_assigned_to", App.Value());
                        if (assignedTo[0] != null)
                            scTask.setAssignedTo(assignedTo[0]);
                        openedBy[0] = getSysUserByIntegrationId((JSONObject) scTaskJson, "opened_by", App.Value());
                        if (openedBy[0] != null)
                            scTask.setOpenedBy(openedBy[0]);
                        businessService[0] = getCiServiceByIntegrationId((JSONObject) scTaskJson, "business_service", App.Value());
                        if (businessService[0] != null)
                            scTask.setBusinessService(businessService[0]);
                        configurationItem[0] = getConfigurationItemByIntegrationId((JSONObject) scTaskJson, "cmdb_ci", App.Value());
                        if (configurationItem[0] != null)
                            scTask.setConfigurationItem(configurationItem[0]);
                        sysGroup[0] = getSysGroupByIntegrationId((JSONObject) scTaskJson, "assignment_group", App.Value());
                        if (sysGroup[0] != null)
                            scTask.setAssignmentGroup(sysGroup[0]);

                        scalingAssignmentGroup[0] = getSysGroupByIntegrationId((JSONObject) scTaskJson, "u_scaling_group", App.Value());
                        if (scalingAssignmentGroup[0] != null)
                            scTask.setScalingAssignmentGroup(scalingAssignmentGroup[0]);
                        scRequest[0] = getScRequestByIntegrationId((JSONObject) scTaskJson, "request", App.Value());
                        if (scRequest[0] != null)
                            scTask.setScRequest(scRequest[0]);
                        scRequestItem[0] = getScRequestItemByIntegrationId((JSONObject) scTaskJson, "request_item", App.Value());
                        if (scRequestItem[0] != null) {
                            scTask.setScRequestItem(scRequestItem[0]);
                            if (scRequestItem[0].getRequestedFor() != null)
                                scTask.setRequestedFor(scRequestItem[0].getRequestedFor());
                            if (scRequestItem[0].getTaskFor() != null)
                                scTask.setTaskFor(scRequestItem[0].getTaskFor());
                        }
                        exists[0] = scTaskService.findByIntegrationId(scTask.getIntegrationId());
                        tagAction[0] = App.CreateConsole();
                        if (exists[0] != null) {
                            scTask.setId(exists[0].getId());
                            tagAction[0] = App.UpdateConsole();
                        }
                        Util.printData(tag, count[0], tagAction[0].concat(Util.getFieldDisplay(scTask)), Util.getFieldDisplay(company[0]), Util.getFieldDisplay(domain[0]));
                        scTasks.add(scTaskService.save(scTask));
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
        return scTasks;
    }

    @GetMapping("/scTaskBySolverAndQueryCreate")
    public List<ScTask> show(String query, boolean flagCreate) {
        System.out.println(App.Start());
        APIResponse apiResponse = null;
        List<ScTask> scTasks = new ArrayList<>();
        String[] sparmOffSets = Util.offSets99000();
        long startTime = 0;
        long endTime = 0;
        String tag = "[ScTask] ";
        String result;
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        JSONParser parser = new JSONParser();
        JSONObject resultJson;
        JSONArray ListScTaskJson = new JSONArray();
        final ScTaskRequest[] scTaskSolver = new ScTaskRequest[1];
        Gson gson = new Gson();
        ScTask scTask = new ScTask();
        final ScTask[] exists = new ScTask[1];
        final String[] tagAction = new String[1];
        final Domain[] domain = new Domain[1];
        final Company[] company = new Company[1];
        final SysUser[] closedBy = new SysUser[1];
        final SysUser[] scalingAssignedTo = new SysUser[1];
        final Location[] location = new Location[1];
        final SysUser[] assignedTo = new SysUser[1];
        final SysUser[] openedBy = new SysUser[1];
        final CiService[] businessService = new CiService[1];
        final ConfigurationItem[] configurationItem = new ConfigurationItem[1];
        final SysGroup[] sysGroup = new SysGroup[1];
        final SysGroup[] scalingAssignmentGroup = new SysGroup[1];
        final ScRequest[] scRequest = new ScRequest[1];
        final ScRequestItem[] scRequestItem = new ScRequestItem[1];
        final ScTask[] _scTask = new ScTask[1];
        APIExecutionStatus status = new APIExecutionStatus();
        try {
            startTime = System.currentTimeMillis();
            final int[] count = {1};
            for (String sparmOffSet : sparmOffSets) {
                result = rest.responseByEndPoint(EndPointSN.ScTaskByQuery().replace("QUERY", query).concat(sparmOffSet));
                System.out.println(tag.concat("(".concat(EndPointSN.ScTaskByQuery().replace("QUERY", query).concat(sparmOffSet)).concat(")")));
                resultJson = (JSONObject) parser.parse(result);
                if (resultJson.get("result") != null)
                    ListScTaskJson = (JSONArray) parser.parse(resultJson.get("result").toString());
                ListScTaskJson.stream().forEach(scTaskJson -> {
                    try {
                        scTaskSolver[0] = gson.fromJson(scTaskJson.toString(), ScTaskRequest.class);
                        exists[0] = scTaskService.findByIntegrationId(scTask.getIntegrationId());
                        tagAction[0] = App.CreateConsole();
                        if (exists[0] != null) {
                            scTask.setId(exists[0].getId());
                        } else {
                            scTask.setActionStatus(Util.isNull(scTaskSolver[0].getAction_status()));
                            scTask.setActive(scTaskSolver[0].getActive());
                            scTask.setActivityDue(Util.isNull(scTaskSolver[0].getActivity_due()));
                            scTask.setAdditionalAssigneeList(Util.isNull(scTaskSolver[0].getAdditional_assignee_list()));
                            scTask.setApproval(Util.isNull(scTaskSolver[0].getApproval()));
                            scTask.setApprovalHistory(Util.isNull(scTaskSolver[0].getApproval_history()));
                            scTask.setApprovalSet(Util.isNull(scTaskSolver[0].getApproval_set()));
                            scTask.setBusinessDuration(Util.isNull(scTaskSolver[0].getBusiness_duration()));
                            scTask.setCalendarDuration(Util.isNull(scTaskSolver[0].getCalendar_duration()));
                            scTask.setCalendarStc(Util.isNull(scTaskSolver[0].getCalendar_stc()));
                            scTask.setCloseNotes(Util.isNull(scTaskSolver[0].getClose_notes()));
                            scTask.setClosedAt(Util.isNull(scTaskSolver[0].getClosed_at()));
                            scTask.setComments(Util.isNull(scTaskSolver[0].getComments()));
                            scTask.setCommentsAndWorkNotes(Util.isNull(scTaskSolver[0].getComments_and_work_notes()));
                            scTask.setContactType(Util.isNull(scTaskSolver[0].getContact_type()));
                            scTask.setCorrelationDisplay(Util.isNull(scTaskSolver[0].getCorrelation_display()));
                            scTask.setCorrelationId(Util.isNull(scTaskSolver[0].getCorrelation_id()));
                            scTask.setDeliveryPlan(Util.isNull(scTaskSolver[0].getDelivery_plan()));
                            scTask.setDeliveryTask(Util.isNull(scTaskSolver[0].getDelivery_task()));
                            scTask.setDescription(Util.isNull(scTaskSolver[0].getDescription()));
                            scTask.setDueDate(Util.isNull(scTaskSolver[0].getDue_date()));
                            scTask.setEscalation(Util.isNull(scTaskSolver[0].getEscalation()));
                            scTask.setExpectedStart(Util.isNull(scTaskSolver[0].getExpected_start()));
                            scTask.setFollowUp(Util.isNull(scTaskSolver[0].getFollow_up()));
                            scTask.setGroupList(Util.isNull(scTaskSolver[0].getGroup_list()));
                            scTask.setImpact(Util.isNull(scTaskSolver[0].getImpact()));
                            scTask.setKnowledge(Util.isNull(scTaskSolver[0].getKnowledge()));
                            scTask.setMadeSla(Util.isNull(scTaskSolver[0].getMade_sla()));
                            scTask.setNeedsAttention(Util.isNull(scTaskSolver[0].getNeeds_attention()));
                            scTask.setNumber(Util.isNull(scTaskSolver[0].getNumber()));
                            scTask.setOpenedAt(Util.isNull(scTaskSolver[0].getOpened_at()));
                            scTask.setPriority(Util.isNull(scTaskSolver[0].getPriority()));
                            scTask.setReassignmentCount(Util.isNull(scTaskSolver[0].getReassignment_count()));
                            scTask.setRouteReason(Util.isNull(scTaskSolver[0].getRoute_reason()));
                            scTask.setServiceOffering(Util.isNull(scTaskSolver[0].getService_offering()));
                            scTask.setShortDescription(Util.isNull(scTaskSolver[0].getShort_description()));
                            scTask.setSkills(Util.isNull(scTaskSolver[0].getSkills()));
                            scTask.setSlaDue(Util.isNull(scTaskSolver[0].getSla_due()));
                            scTask.setState(Util.isNull(scTaskSolver[0].getState()));
                            scTask.setSysClassName(Util.isNull(scTaskSolver[0].getSys_class_name()));
                            scTask.setSysCreatedBy(Util.isNull(scTaskSolver[0].getSys_created_by()));
                            scTask.setSysCreatedOn(Util.isNull(scTaskSolver[0].getSys_created_on()));
                            if (!Util.isNull(scTaskSolver[0].getSys_created_on()).equals(""))
                                scTask.setCreatedOn(Util.getLocalDateTime(Util.isNull(scTaskSolver[0].getSys_created_on())));
                            scTask.setIntegrationId(Util.isNull(scTaskSolver[0].getSys_id()));
                            scTask.setSysModCount(Util.isNull(scTaskSolver[0].getSys_mod_count()));
                            scTask.setSysUpdatedBy(Util.isNull(scTaskSolver[0].getSys_updated_by()));
                            scTask.setSysUpdatedOn(Util.isNull(scTaskSolver[0].getSys_updated_on()));
                            if (!Util.isNull(scTaskSolver[0].getClosed_at()).equals(""))
                                scTask.setUpdatedOn(Util.getLocalDateTime(Util.isNull(scTaskSolver[0].getClosed_at())));
                            scTask.setTaskEffectiveNumber(Util.isNull(scTaskSolver[0].getTask_effective_number()));
                            scTask.setTimeWorked(Util.isNull(scTaskSolver[0].getTime_worked()));
                            scTask.setUniversalRequest(Util.isNull(scTaskSolver[0].getUniversal_request()));
                            scTask.setUponApproval(Util.isNull(scTaskSolver[0].getUpon_approval()));
                            scTask.setUponReject(Util.isNull(scTaskSolver[0].getUpon_reject()));
                            scTask.setUrgency(Util.isNull(scTaskSolver[0].getUrgency()));
                            scTask.setUserInput(Util.isNull(scTaskSolver[0].getUser_input()));
                            scTask.setWorkEnd(Util.isNull(scTaskSolver[0].getWork_end()));
                            scTask.setWorkStart(Util.isNull(scTaskSolver[0].getWork_start()));
                            scTask.setScaling(scTaskSolver[0].getU_pending_other_group());
                            domain[0] = getDomainByIntegrationId((JSONObject) scTaskJson, SnTable.Domain.get(), App.Value());
                            if (domain[0] != null)
                                scTask.setDomain(domain[0]);
                            company[0] = getCompanyByIntegrationId((JSONObject) scTaskJson, SnTable.Company.get(), App.Value());
                            if (company[0] != null)
                                scTask.setCompany(company[0]);
                            closedBy[0] = getSysUserByIntegrationId((JSONObject) scTaskJson, "closed_by", App.Value());
                            if (closedBy[0] != null)
                                scTask.setClosedBy(closedBy[0]);
                            scalingAssignedTo[0] = getSysUserByIntegrationId((JSONObject) scTaskJson, "u_scaling_assigned_to", App.Value());
                            if (scalingAssignedTo[0] != null)
                                scTask.setScalingAssignedTo(scalingAssignedTo[0]);
                            location[0] = getLocationByIntegrationId((JSONObject) scTaskJson, "location", App.Value());
                            if (location[0] != null)
                                scTask.setLocation(location[0]);
                            assignedTo[0] = getSysUserByIntegrationId((JSONObject) scTaskJson, "u_solver_assigned_to", App.Value());
                            if (assignedTo[0] != null)
                                scTask.setAssignedTo(assignedTo[0]);
                            openedBy[0] = getSysUserByIntegrationId((JSONObject) scTaskJson, "opened_by", App.Value());
                            if (openedBy[0] != null)
                                scTask.setOpenedBy(openedBy[0]);
                            businessService[0] = getCiServiceByIntegrationId((JSONObject) scTaskJson, "business_service", App.Value());
                            if (businessService[0] != null)
                                scTask.setBusinessService(businessService[0]);
                            configurationItem[0] = getConfigurationItemByIntegrationId((JSONObject) scTaskJson, "cmdb_ci", App.Value());
                            if (configurationItem[0] != null)
                                scTask.setConfigurationItem(configurationItem[0]);
                            sysGroup[0] = getSysGroupByIntegrationId((JSONObject) scTaskJson, "assignment_group", App.Value());
                            if (sysGroup[0] != null)
                                scTask.setAssignmentGroup(sysGroup[0]);
                            scalingAssignmentGroup[0] = getSysGroupByIntegrationId((JSONObject) scTaskJson, "u_scaling_group", App.Value());
                            if (scalingAssignmentGroup[0] != null)
                                scTask.setScalingAssignmentGroup(scalingAssignmentGroup[0]);
                            scRequest[0] = getScRequestByIntegrationId((JSONObject) scTaskJson, "request", App.Value());
                            if (scRequest[0] != null)
                                scTask.setScRequest(scRequest[0]);
                            scRequestItem[0] = getScRequestItemByIntegrationId((JSONObject) scTaskJson, "request_item", App.Value());
                            if (scRequestItem[0] != null) {
                                scTask.setScRequestItem(scRequestItem[0]);
                                if (scRequestItem[0].getRequestedFor() != null)
                                    scTask.setRequestedFor(scRequestItem[0].getRequestedFor());
                                if (scRequestItem[0].getTaskFor() != null)
                                    scTask.setTaskFor(scRequestItem[0].getTaskFor());
                            }
                            _scTask[0] = scTaskService.save(scTask);
                            scTasks.add(_scTask[0]);
                            Util.printData(tag, count[0], tagAction[0].concat(Util.getFieldDisplay(scTask)), Util.getFieldDisplay(company[0]), Util.getFieldDisplay(domain[0]));
                            count[0] = count[0] + 1;
                        }
                    } catch (Exception e) {
                        //   System.out.println(tag.concat("Exception (I) : ").concat(String.valueOf(e)));
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
            // System.out.println(tag.concat("Exception (II) : ").concat(String.valueOf(e)));
        }
        System.out.println(App.End());
        return scTasks;
    }

    @GetMapping("/findPaginatedScTasksByFilters")
    public ResponseEntity<Page<ScTaskFields>> findPaginatedScTasksByFilters(@RequestParam(value = "page", required = true, defaultValue = "0") Integer page,
                                                                            @NotNull @RequestParam(value = "size", required = true, defaultValue = "10") Integer size,
                                                                            @NotNull @RequestParam(value = "filter", required = true, defaultValue = "") String filter,
                                                                            @NotNull @RequestParam(value = "assignedTo", required = true, defaultValue = "0") Long assignedTo,
                                                                            @NotNull @RequestParam(value = "company", required = true, defaultValue = "0") Long company,
                                                                            @NotNull @RequestParam(value = "state", required = true, defaultValue = "") String state,
                                                                            @NotNull @RequestParam(value = "openUnassigned", required = true, defaultValue = "false") boolean openUnassigned,
                                                                            @NotNull @RequestParam(value = "solved", required = true, defaultValue = "false") boolean solved,
                                                                            @NotNull @RequestParam(value = "scaling", required = true, defaultValue = "false") boolean scaling,
                                                                            @NotNull @RequestParam(value = "scalingAssignedTo", required = true, defaultValue = "0") Long scalingAssignedTo,
                                                                            @NotNull @RequestParam(value = "assignedToGroup", required = true, defaultValue = "0") Long assignedToGroup,
                                                                            @NotNull @RequestParam(value = "closed", required = true, defaultValue = "false") boolean closed,
                                                                            @NotNull @RequestParam(value = "open", required = true, defaultValue = "false") boolean open,
                                                                            @RequestParam(required = false, name = "sysGroups") List<Long> sysGroups,
                                                                            @RequestParam(required = false, name = "sysUsers") List<Long> sysUsers,
                                                                            @RequestParam(required = false, name = "states") List<String> states,
                                                                            @RequestParam(required = false, name = "priorities") List<String> priorities,
                                                                            @NotNull @RequestParam(value = "createdOnFrom", required = true, defaultValue = "") String createdOnFrom,
                                                                            @NotNull @RequestParam(value = "createdOnTo", required = true, defaultValue = "") String createdOnTo,
                                                                            String field,
                                                                            String direction) {
        List<ScTask> list = new ArrayList<>();
        Sort sort = direction.toUpperCase().equals("DESC") ? Sort.by(field).descending() : Sort.by(field).ascending();
        Pageable pageRequest;
        if (size > 0) {
            pageRequest = PageRequest.of(page, size, sort);
        } else {
            pageRequest = SortedUnpaged.getInstance(sort);
        }
        Page<ScTaskFields> pageResult = scTaskService.findPaginatedScTasksByFilters(pageRequest, filter.toUpperCase(), assignedTo, company, state, openUnassigned, solved, scaling, scalingAssignedTo, assignedToGroup, closed, open, sysGroups, sysUsers, states, priorities, createdOnFrom, createdOnTo);
        ResponseEntity<Page<ScTaskFields>> pageResponseEntity = new ResponseEntity<>(pageResult, HttpStatus.OK);
        return pageResponseEntity;
    }

    @GetMapping("/findPaginatedScTasksScalingByFilters")
    public ResponseEntity<Page<ScTaskFields>> findPaginatedScTasksScalingByFilters(@RequestParam(value = "page", required = true, defaultValue = "0") Integer page,
                                                                                   @NotNull @RequestParam(value = "size", required = true, defaultValue = "10") Integer size,
                                                                                   @NotNull @RequestParam(value = "filter", required = true, defaultValue = "") String filter,
                                                                                   @NotNull @RequestParam(value = "company", required = true, defaultValue = "0") Long company,
                                                                                   @NotNull @RequestParam(value = "sysUser", required = true, defaultValue = "0") Long sysUser,
                                                                                   @RequestParam(required = false, name = "sysGroups") List<Long> sysGroups,
                                                                                   @RequestParam(required = false, name = "sysUsers") List<Long> sysUsers,
                                                                                   @RequestParam(required = false, name = "states") List<String> states,
                                                                                   @RequestParam(required = false, name = "priorities") List<String> priorities,
                                                                                   @NotNull @RequestParam(value = "createdOnFrom", required = true, defaultValue = "") String createdOnFrom,
                                                                                   @NotNull @RequestParam(value = "createdOnTo", required = true, defaultValue = "") String createdOnTo,
                                                                                   String field,
                                                                                   String direction) {
        List<ScTask> list = new ArrayList<>();
        Sort sort = direction.toUpperCase().equals("DESC") ? Sort.by(field).descending() : Sort.by(field).ascending();
        Pageable pageRequest;
        if (size > 0) {
            pageRequest = PageRequest.of(page, size, sort);
        } else {
            pageRequest = SortedUnpaged.getInstance(sort);
        }
        Page<ScTaskFields> pageResult = scTaskService.findPaginatedScTasksScalingByFilters(pageRequest, filter.toUpperCase(), company, sysUser, sysGroups, sysUsers, states, priorities, createdOnFrom, createdOnTo);
        ResponseEntity<Page<ScTaskFields>> pageResponseEntity = new ResponseEntity<>(pageResult, HttpStatus.OK);
        return pageResponseEntity;
    }

    @GetMapping("/findPaginatedScTasksSLAByFilters")
    public ResponseEntity<Page<ScTaskFields>> findPaginatedScTasksSLAByFilters(@RequestParam(value = "page", required = true, defaultValue = "0") Integer page,
                                                                               @NotNull @RequestParam(value = "size", required = true, defaultValue = "10") Integer size,
                                                                               @NotNull @RequestParam(value = "filter", required = true, defaultValue = "") String filter,
                                                                               @NotNull @RequestParam(value = "assignedTo", required = true, defaultValue = "0") Long assignedTo,
                                                                               @NotNull @RequestParam(value = "company", required = true, defaultValue = "0") Long company,
                                                                               @NotNull @RequestParam(value = "state", required = true, defaultValue = "") String state,
                                                                               @NotNull @RequestParam(value = "openUnassigned", required = true, defaultValue = "false") boolean openUnassigned,
                                                                               @NotNull @RequestParam(value = "solved", required = true, defaultValue = "false") boolean solved,
                                                                               @NotNull @RequestParam(value = "scaling", required = true, defaultValue = "false") boolean scaling,
                                                                               @NotNull @RequestParam(value = "scalingAssignedTo", required = true, defaultValue = "0") Long scalingAssignedTo,
                                                                               @NotNull @RequestParam(value = "assignedToGroup", required = true, defaultValue = "0") Long assignedToGroup,
                                                                               @NotNull @RequestParam(value = "closed", required = true, defaultValue = "false") boolean closed,
                                                                               @NotNull @RequestParam(value = "open", required = true, defaultValue = "false") boolean open,
                                                                               @RequestParam(required = false, name = "sysGroups") List<Long> sysGroups,
                                                                               @RequestParam(required = false, name = "sysUsers") List<Long> sysUsers,
                                                                               @RequestParam(required = false, name = "states") List<String> states,
                                                                               @RequestParam(required = false, name = "priorities") List<String> priorities,
                                                                               @NotNull @RequestParam(value = "createdOnFrom", required = true, defaultValue = "") String createdOnFrom,
                                                                               @NotNull @RequestParam(value = "createdOnTo", required = true, defaultValue = "") String createdOnTo,
                                                                               String field,
                                                                               String direction) {
        List<ScTask> list = new ArrayList<>();
        Sort sort = direction.toUpperCase().equals("DESC") ? Sort.by(field).descending() : Sort.by(field).ascending();
        Pageable pageRequest;
        if (size > 0) {
            pageRequest = PageRequest.of(page, size, sort);
        } else {
            pageRequest = SortedUnpaged.getInstance(sort);
        }
        Page<ScTaskFields> pageResult = scTaskService.findPaginatedScTasksSLAByFilters(pageRequest, filter.toUpperCase(), assignedTo, company, state, openUnassigned, solved, scaling, scalingAssignedTo, assignedToGroup, closed, open, sysGroups, sysUsers, states, priorities, createdOnFrom, createdOnTo);
        ResponseEntity<Page<ScTaskFields>> pageResponseEntity = new ResponseEntity<>(pageResult, HttpStatus.OK);
        return pageResponseEntity;
    }

    @GetMapping("/countScTasksByFilters")
    public ResponseEntity<Long> countScTasksByFilters(@NotNull @RequestParam(value = "assignedTo", required = true, defaultValue = "0") Long assignedTo,
                                                      @NotNull @RequestParam(value = "company", required = true, defaultValue = "0") Long company,
                                                      @NotNull @RequestParam(value = "state", required = true, defaultValue = "") String state,
                                                      @NotNull @RequestParam(value = "openUnassigned", required = true, defaultValue = "false") boolean openUnassigned,
                                                      @NotNull @RequestParam(value = "solved", required = true, defaultValue = "false") boolean solved,
                                                      @NotNull @RequestParam(value = "scaling", required = true, defaultValue = "false") boolean scaling,
                                                      @NotNull @RequestParam(value = "scalingAssignedTo", required = true, defaultValue = "0") Long scalingAssignedTo,
                                                      @NotNull @RequestParam(value = "assignedToGroup", required = true, defaultValue = "0") Long assignedToGroup,
                                                      @NotNull @RequestParam(value = "closed", required = true, defaultValue = "false") boolean closed,
                                                      @NotNull @RequestParam(value = "open", required = true, defaultValue = "false") boolean open

    ) {
        Long pageResult = scTaskService.countScTasksByFilters(assignedTo, company, state, openUnassigned, solved, scaling, scalingAssignedTo, assignedToGroup, closed, open);
        ResponseEntity<Long> pageResponseEntity = new ResponseEntity<>(pageResult, HttpStatus.OK);
        return pageResponseEntity;
    }

    @PutMapping("/scTaskSN")
    public ResponseEntity<?> update(@RequestBody String json) {
        Map<String, Object> response = new HashMap<>();
        ScTask scTask = new ScTask();
        try {
            Gson gson = new Gson();
            ScTaskRequest scTaskSolver = gson.fromJson(json, ScTaskRequest.class);
            ScTask exists = scTaskService.findByIntegrationId(scTaskSolver.getSys_id());
            String tagAction = App.CreateConsole();
            if (exists != null) {
                scTask.setId(exists.getId());
                tagAction = App.UpdateConsole();
            }
            scTask.setActionStatus(Util.isNull(scTaskSolver.getAction_status()));
            scTask.setActive(scTaskSolver.getActive());
            scTask.setActivityDue(Util.isNull(scTaskSolver.getActivity_due()));
            scTask.setAdditionalAssigneeList(Util.isNull(scTaskSolver.getAdditional_assignee_list()));
            scTask.setApproval(Util.isNull(scTaskSolver.getApproval()));
            scTask.setApprovalHistory(Util.isNull(scTaskSolver.getApproval_history()));
            scTask.setApprovalSet(Util.isNull(scTaskSolver.getApproval_set()));
            scTask.setBusinessDuration(Util.isNull(scTaskSolver.getBusiness_duration()));
            scTask.setCalendarDuration(Util.isNull(scTaskSolver.getCalendar_duration()));
            scTask.setCalendarStc(Util.isNull(scTaskSolver.getCalendar_stc()));
            scTask.setCloseNotes(Util.isNull(scTaskSolver.getClose_notes()));
            scTask.setClosedAt(Util.isNull(scTaskSolver.getClosed_at()));
            scTask.setComments(Util.isNull(scTaskSolver.getComments()));
            scTask.setCommentsAndWorkNotes(Util.isNull(scTaskSolver.getComments_and_work_notes()));
            scTask.setContactType(Util.isNull(scTaskSolver.getContact_type()));
            scTask.setCorrelationDisplay(Util.isNull(scTaskSolver.getCorrelation_display()));
            scTask.setCorrelationId(Util.isNull(scTaskSolver.getCorrelation_id()));
            scTask.setDeliveryPlan(Util.isNull(scTaskSolver.getDelivery_plan()));
            scTask.setDeliveryTask(Util.isNull(scTaskSolver.getDelivery_task()));
            scTask.setDescription(Util.isNull(scTaskSolver.getDescription()));
            scTask.setDueDate(Util.isNull(scTaskSolver.getDue_date()));
            scTask.setEscalation(Util.isNull(scTaskSolver.getEscalation()));
            scTask.setExpectedStart(Util.isNull(scTaskSolver.getExpected_start()));
            scTask.setFollowUp(Util.isNull(scTaskSolver.getFollow_up()));
            scTask.setGroupList(Util.isNull(scTaskSolver.getGroup_list()));
            scTask.setImpact(Util.isNull(scTaskSolver.getImpact()));
            scTask.setKnowledge(Util.isNull(scTaskSolver.getKnowledge()));
            scTask.setMadeSla(Util.isNull(scTaskSolver.getMade_sla()));
            scTask.setNeedsAttention(Util.isNull(scTaskSolver.getNeeds_attention()));
            scTask.setNumber(Util.isNull(scTaskSolver.getNumber()));
            scTask.setOpenedAt(Util.isNull(scTaskSolver.getOpened_at()));
            scTask.setPriority(Util.isNull(scTaskSolver.getPriority()));
            scTask.setReassignmentCount(Util.isNull(scTaskSolver.getReassignment_count()));
            scTask.setRouteReason(Util.isNull(scTaskSolver.getRoute_reason()));
            scTask.setServiceOffering(Util.isNull(scTaskSolver.getService_offering()));
            scTask.setShortDescription(Util.isNull(scTaskSolver.getShort_description()));
            scTask.setSkills(Util.isNull(scTaskSolver.getSkills()));
            scTask.setSlaDue(Util.isNull(scTaskSolver.getSla_due()));
            scTask.setState(Util.isNull(scTaskSolver.getState()));
            scTask.setSysClassName(Util.isNull(scTaskSolver.getSys_class_name()));
            scTask.setSysCreatedBy(Util.isNull(scTaskSolver.getSys_created_by()));
            scTask.setSysCreatedOn(Util.isNull(scTaskSolver.getSys_created_on()));
            if (!Util.isNull(scTaskSolver.getSys_created_on()).equals(""))
                scTask.setCreatedOn(Util.getLocalDateTime(Util.isNull(scTaskSolver.getSys_created_on())));
            scTask.setIntegrationId(Util.isNull(scTaskSolver.getSys_id()));
            scTask.setSysModCount(Util.isNull(scTaskSolver.getSys_mod_count()));
            scTask.setSysUpdatedBy(Util.isNull(scTaskSolver.getSys_updated_by()));
            scTask.setSysUpdatedOn(Util.isNull(scTaskSolver.getSys_updated_on()));
            if (!Util.isNull(scTaskSolver.getClosed_at()).equals(""))
                scTask.setUpdatedOn(Util.getLocalDateTime(Util.isNull(scTaskSolver.getClosed_at())));
            scTask.setTaskEffectiveNumber(Util.isNull(scTaskSolver.getTask_effective_number()));
            scTask.setTimeWorked(Util.isNull(scTaskSolver.getTime_worked()));
            scTask.setUniversalRequest(Util.isNull(scTaskSolver.getUniversal_request()));
            scTask.setUponApproval(Util.isNull(scTaskSolver.getUpon_approval()));
            scTask.setUponReject(Util.isNull(scTaskSolver.getUpon_reject()));
            scTask.setUrgency(Util.isNull(scTaskSolver.getUrgency()));
            scTask.setUserInput(Util.isNull(scTaskSolver.getUser_input()));
            scTask.setWorkEnd(Util.isNull(scTaskSolver.getWork_end()));
            scTask.setWorkStart(Util.isNull(scTaskSolver.getWork_start()));
            scTask.setScaling(scTaskSolver.getU_pending_other_group());
            scTask.setScRequestIntegrationId(scTaskSolver.getRequest());
            scTask.setScRequestItemIntegrationId(scTaskSolver.getRequest_item());
            Company company = companyService.findByIntegrationId(scTaskSolver.getCompany());
            scTask.setCompany(company);
            scTask.setDomain(company.getDomain());
            SysUser closedBy = sysUserService.findByIntegrationId(scTaskSolver.getClosed_by());
            scTask.setClosedBy(closedBy);
            SysUser scalingAssignedTo = sysUserService.findByIntegrationId(scTaskSolver.getScaling_assigned_to());
            scTask.setScalingAssignedTo(scalingAssignedTo);
            Location location = locationService.findByIntegrationId(scTaskSolver.getLocation());
            scTask.setLocation(location);
            SysUser assignedTo = sysUserService.findByIntegrationId(scTaskSolver.getAssigned_to());
            scTask.setAssignedTo(assignedTo);
            SysUser openedBy = sysUserService.findByIntegrationId(scTaskSolver.getOpened_by());
            scTask.setOpenedBy(openedBy);
            CiService businessService = ciServiceService.findByIntegrationId(scTaskSolver.getBusiness_service());
            scTask.setBusinessService(businessService);
            ConfigurationItem configurationItem = configurationItemService.findByIntegrationId(scTaskSolver.getCmdb_ci());
            scTask.setConfigurationItem(configurationItem);
            SysGroup sysGroup = sysGroupService.findByIntegrationId(scTaskSolver.getAssignment_group());
            scTask.setAssignmentGroup(sysGroup);
            SysGroup scalingAssignmentGroup = sysGroupService.findByIntegrationId(scTaskSolver.getScaling_assignment_group());
            scTask.setScalingAssignmentGroup(scalingAssignmentGroup);
            ScRequest scRequest = scRequestService.findByIntegrationId(scTaskSolver.getRequest());
            scTask.setScRequest(scRequest);
            ScRequestItem scRequestItem = scRequestItemService.findByIntegrationId(scTaskSolver.getRequest_item());
            scTask.setScRequestItem(scRequestItem);
            SysUser requestedFor = sysUserService.findByIntegrationId(scTaskSolver.getRequested_for());
            scTask.setRequestedFor(requestedFor);
            scTask.setTaskFor(requestedFor);
            if (scRequestItem != null) {
                scTask.setScRequestItem(scRequestItem);
                if (scRequestItem.getRequestedFor() != null)
                    scTask.setRequestedFor(scRequestItem.getRequestedFor());
                if (scRequestItem.getTaskFor() != null)
                    scTask.setTaskFor(scRequestItem.getTaskFor());
            }
            String tag = "[Sc Task]";
            scTaskService.save(scTask);
            Util.printData(tag, tagAction.concat(Util.getFieldDisplay(scTask)), Util.getFieldDisplay(company), Util.getFieldDisplay(company.getDomain()));
        } catch (DataAccessException e) {
            response.put("mensaje", Errors.dataAccessExceptionUpdate.get());
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", Messages.UpdateOK.get());
        response.put("scTask", scTask);
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }

    @GetMapping("/countTaskSLAByFilters")
    public ResponseEntity<Long> countTaskSLAByFilters(Long company, Long assignedTo) {
        Long pageResult = scTaskService.countTaskSLAByFilters(company, assignedTo);
        ResponseEntity<Long> pageResponseEntity = new ResponseEntity<>(pageResult, HttpStatus.OK);
        return pageResponseEntity;
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

    public ScRequestItem getScRequestItemByIntegrationId(JSONObject jsonObject, String levelOne, String levelTwo) {
        String integrationId = Util.getIdByJson(jsonObject, levelOne, levelTwo);
        if (Util.hasData(integrationId)) {
            return scRequestItemService.findByIntegrationId(integrationId);
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


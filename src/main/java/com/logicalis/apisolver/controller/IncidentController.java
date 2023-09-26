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
import com.logicalis.apisolver.view.IncidentRequest;
import com.logicalis.apisolver.view.IncidentSolver;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class IncidentController {
    @Autowired
    private IIncidentService incidentService;
    @Autowired
    private IChoiceService choiceService;
    @Autowired
    private ISysUserService sysUserService;
    @Autowired
    private ISysGroupService sysGroupService;
    @Autowired
    private ILocationService locationService;
    @Autowired
    private IDomainService domainService;
    @Autowired
    private IScRequestService scRequestService;
    @Autowired
    private IScRequestItemService scRequestItemService;
    @Autowired
    private IAPIExecutionStatusService statusService;
    @Autowired
    private ICompanyService companyService;
    @Autowired
    private ICiServiceService ciServiceService;
    @Autowired
    private IConfigurationItemService configurationItemService;
    @Autowired
    private ITaskSlaService taskSlaService;
    @Autowired
    private Rest rest;

    @GetMapping("/incidentsByFilter2")
    public ResponseEntity<Page<Incident>> incidentsByFilter() {
        Integer size = 10;
        Integer page = 0;
        List<Incident> list = new ArrayList<>();
        Sort sort = Sort.by("id").descending();
        Pageable pageRequest;
        if (size > 0) {
            pageRequest = PageRequest.of(page, size, sort);
        } else {
            pageRequest = SortedUnpaged.getInstance(sort);
        }
        Page<Incident> pageResult = incidentService.findAll(pageRequest);
        ResponseEntity<Page<Incident>> pageResponseEntity = new ResponseEntity<>(pageResult, HttpStatus.OK);
        return pageResponseEntity;
    }

    @GetMapping("/incidentsByFilter")
    public ResponseEntity<Page<Incident>> incidentsByFilter(@RequestParam(value = "page", required = true, defaultValue = "0") Integer page,
                                                            @NotNull @RequestParam(value = "size", required = true, defaultValue = "10") Integer size) {
        List<Incident> list = new ArrayList<>();
        Sort sort = Sort.by("id").descending();
        Pageable pageRequest;
        if (size > 0) {
            pageRequest = PageRequest.of(page, size, sort);
        } else {
            pageRequest = SortedUnpaged.getInstance(sort);
        }
        Page<Incident> pageResult = incidentService.findAll(pageRequest);
        ResponseEntity<Page<Incident>> pageResponseEntity = new ResponseEntity<>(pageResult, HttpStatus.OK);
        return pageResponseEntity;
    }

    @GetMapping("/findPaginatedIncidentsByFilters")
    public ResponseEntity<Page<IncidentFields>> findPaginatedIncidentsByFilters(@RequestParam(value = "page", required = true, defaultValue = "0") Integer page,
                                                                                @NotNull @RequestParam(value = "size", required = true, defaultValue = "10") Integer size,
                                                                                @NotNull @RequestParam(value = "filter", required = true, defaultValue = "") String filter,
                                                                                @NotNull @RequestParam(value = "assignedTo", required = true, defaultValue = "0") Long assignedTo,
                                                                                @NotNull @RequestParam(value = "company", required = true, defaultValue = "0") Long company,
                                                                                @NotNull @RequestParam(value = "state", required = true, defaultValue = "") String state,
                                                                                @NotNull @RequestParam(value = "openUnassigned", required = true, defaultValue = "false") boolean openUnassigned,
                                                                                @NotNull @RequestParam(value = "solved", required = true, defaultValue = "false") boolean solved,
                                                                                @NotNull @RequestParam(value = "incidentParent", required = true, defaultValue = "") String incidentParent,
                                                                                @NotNull @RequestParam(value = "scRequestParent", required = true, defaultValue = "") String scRequestParent,
                                                                                @NotNull @RequestParam(value = "scRequestItemParent", required = true, defaultValue = "") String scRequestItemParent,
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

        Sort sort = direction.toUpperCase().equals("DESC") ? Sort.by(field) : Sort.by(field).ascending();
        Pageable pageRequest;
        if (size > 0) {
            pageRequest = PageRequest.of(page, size, sort);
        } else {
            pageRequest = SortedUnpaged.getInstance(sort);
        }
        Page<IncidentFields> pageResult = incidentService.findPaginatedIncidentsByFilters(pageRequest, filter.toUpperCase(), assignedTo, company, state, openUnassigned, solved, incidentParent, scRequestParent, scRequestItemParent, assignedToGroup, closed, open, sysGroups, sysUsers, states, priorities, createdOnFrom, createdOnTo);
        ResponseEntity<Page<IncidentFields>> pageResponseEntity = new ResponseEntity<>(pageResult, HttpStatus.OK);

        return pageResponseEntity;
    }

    @GetMapping("/findIncidentsSlaByFilters")
    public ResponseEntity<Page<IncidentFields>> findPaginatedIncidentsSlaByFilters(@RequestParam(value = "page", required = true, defaultValue = "0") Integer page,
                                                                                   @NotNull @RequestParam(value = "size", required = true, defaultValue = "10") Integer size,
                                                                                   @NotNull @RequestParam(value = "filter", required = true, defaultValue = "") String filter,
                                                                                   @NotNull @RequestParam(value = "assignedTo", required = true, defaultValue = "0") Long assignedTo,
                                                                                   @NotNull @RequestParam(value = "company", required = true, defaultValue = "0") Long company,
                                                                                   @NotNull @RequestParam(value = "state", required = true, defaultValue = "") String state,
                                                                                   @NotNull @RequestParam(value = "openUnassigned", required = true, defaultValue = "false") boolean openUnassigned,
                                                                                   @NotNull @RequestParam(value = "incidentParent", required = true, defaultValue = "") String incidentParent,
                                                                                   @NotNull @RequestParam(value = "scRequestParent", required = true, defaultValue = "") String scRequestParent,
                                                                                   @NotNull @RequestParam(value = "scRequestItemParent", required = true, defaultValue = "") String scRequestItemParent,
                                                                                   @NotNull @RequestParam(value = "assignedToGroup", required = true, defaultValue = "0") Long assignedToGroup,
                                                                                   @RequestParam(required = false, name = "sysGroups") List<Long> sysGroups,
                                                                                   @RequestParam(required = false, name = "sysUsers") List<Long> sysUsers,
                                                                                   @RequestParam(required = false, name = "states") List<String> states,
                                                                                   @RequestParam(required = false, name = "priorities") List<String> priorities,
                                                                                   @NotNull @RequestParam(value = "createdOnFrom", required = true, defaultValue = "") String createdOnFrom,
                                                                                   @NotNull @RequestParam(value = "createdOnTo", required = true, defaultValue = "") String createdOnTo,
                                                                                   String field,
                                                                                   String direction) {


        Sort sort = direction.toUpperCase().equals("DESC") ? Sort.by(field).descending() : Sort.by(field).ascending();
        Pageable pageRequest;
        if (size > 0) {
            pageRequest = PageRequest.of(page, size, sort);
        } else {
            pageRequest = SortedUnpaged.getInstance(sort);
        }
        Page<IncidentFields> pageResult = incidentService.findPaginatedIncidentsSlaByFilters(pageRequest, filter, assignedTo, company, state, openUnassigned, incidentParent, scRequestParent, scRequestItemParent, assignedToGroup, sysGroups, sysUsers, states, priorities, createdOnFrom, createdOnTo);
        ResponseEntity<Page<IncidentFields>> pageResponseEntity = new ResponseEntity<>(pageResult, HttpStatus.OK);
        return pageResponseEntity;
    }

    @GetMapping("/findIncidentsByFilters")
    public ResponseEntity<List<IncidentFields>> findIncidentsByFilters(
                                                                       @NotNull @RequestParam(value = "filter", required = true, defaultValue = "") String filter,
                                                                       @NotNull @RequestParam(value = "assignedTo", required = true, defaultValue = "0") Long assignedTo,
                                                                       @NotNull @RequestParam(value = "company", required = true, defaultValue = "0") Long company,
                                                                       @NotNull @RequestParam(value = "state", required = true, defaultValue = "") String state,
                                                                       @NotNull @RequestParam(value = "openUnassigned", required = true, defaultValue = "false") boolean openUnassigned,
                                                                       @NotNull @RequestParam(value = "solved", required = true, defaultValue = "false") boolean solved,
                                                                       @NotNull @RequestParam(value = "incidentParent", required = true, defaultValue = "") String incidentParent,
                                                                       @NotNull @RequestParam(value = "scRequestParent", required = true, defaultValue = "") String scRequestParent,
                                                                       @NotNull @RequestParam(value = "scRequestItemParent", required = true, defaultValue = "") String scRequestItemParent,
                                                                       @NotNull @RequestParam(value = "assignedToGroup", required = true, defaultValue = "0") Long assignedToGroup,
                                                                       @NotNull @RequestParam(value = "closed", required = true, defaultValue = "false") boolean closed,
                                                                       @NotNull @RequestParam(value = "open", required = true, defaultValue = "false") boolean open) {
        List<IncidentFields> pageResult = incidentService.findIncidentsByFilters(filter.toUpperCase(), assignedTo, company, state, openUnassigned, solved, incidentParent, scRequestParent, scRequestItemParent, assignedToGroup, closed, open);
        ResponseEntity<List<IncidentFields>> pageResponseEntity = new ResponseEntity<>(pageResult, HttpStatus.OK);
        return pageResponseEntity;
    }

    @GetMapping("/countIncidentsSLAByFilters")
    public ResponseEntity<Long> countIncidentsSLAByFilters(Long company, Long assignedTo) {
        Long pageResult = incidentService.countIncidentsSLAByFilters(company, assignedTo);
        ResponseEntity<Long> pageResponseEntity = new ResponseEntity<>(pageResult, HttpStatus.OK);
        return pageResponseEntity;
    }

    @GetMapping("/countIncidentsByFilters")
    public ResponseEntity<Long> countIncidentsByFilters(@NotNull @RequestParam(value = "assignedTo", required = true, defaultValue = "0") Long assignedTo,
                                                        @NotNull @RequestParam(value = "company", required = true, defaultValue = "0") Long company,
                                                        @NotNull @RequestParam(value = "state", required = true, defaultValue = "") String state,
                                                        @NotNull @RequestParam(value = "openUnassigned", required = true, defaultValue = "false") boolean openUnassigned,
                                                        @NotNull @RequestParam(value = "solved", required = true, defaultValue = "false") boolean solved,
                                                        @NotNull @RequestParam(value = "closed", required = true, defaultValue = "false") boolean closed,
                                                        @NotNull @RequestParam(value = "assignedToGroup", required = true, defaultValue = "0") Long assignedToGroup,
                                                        @NotNull @RequestParam(value = "open", required = true, defaultValue = "false") boolean open
    ) {
        Long pageResult = incidentService.countIncidentsByFilters(assignedTo, company, state, openUnassigned, solved, closed, assignedToGroup, open);
        ResponseEntity<Long> pageResponseEntity = new ResponseEntity<>(pageResult, HttpStatus.OK);
        return pageResponseEntity;
    }

    @GetMapping("/findBySummary")
    public ResponseEntity<List<IncidentSummary>> findBySummary(@NotNull @RequestParam(value = "assignedTo", required = true, defaultValue = "0") Long assignedTo,
                                                               @NotNull @RequestParam(value = "company", required = true, defaultValue = "0") Long company,
                                                               @NotNull @RequestParam(value = "state", required = true, defaultValue = "") String state,
                                                               @NotNull @RequestParam(value = "openUnassigned", required = true, defaultValue = "false") boolean openUnassigned,
                                                               @NotNull @RequestParam(value = "solved", required = true, defaultValue = "false") boolean solved,
                                                               @NotNull @RequestParam(value = "currentDate", required = true, defaultValue = "") String currentDate,
                                                               @NotNull @RequestParam(value = "lastDate", required = true, defaultValue = "") String lastDate,
                                                               @NotNull @RequestParam(value = "closedBy", required = true, defaultValue = "0") Long closedBy
    ) {
        List<IncidentSummary> pageResult = incidentService.findBySummary(assignedTo, company, state, openUnassigned, solved, currentDate, lastDate, closedBy);
        ResponseEntity<List<IncidentSummary>> pageResponseEntity = new ResponseEntity<>(pageResult, HttpStatus.OK);
        return pageResponseEntity;
    }

    @GetMapping("/incidents")
    public List<Incident> index() {
        return incidentService.findAll();
    }

    @GetMapping("/incident/{id}")
    public ResponseEntity<?> show(@PathVariable Long id) {
        Incident incident = null;
        Map<String, Object> response = new HashMap<>();
        try {
            incident = incidentService.findById(id);
        } catch (DataAccessException e) {
            response.put("mensaje", Errors.dataAccessExceptionQuery.get());
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (incident == null) {
            response.put("mensaje", Messages.notExist.get(id.toString()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Incident>(incident, HttpStatus.OK);
    }

    @GetMapping("/incidentIntegration/{integrationId}")
    public ResponseEntity<?> show(@PathVariable String integrationId) {
        Incident incident = null;
        Map<String, Object> response = new HashMap<>();
        try {
            incident = incidentService.findByIntegrationId(integrationId);
        } catch (DataAccessException e) {
            response.put("mensaje", Errors.dataAccessExceptionQuery.get());
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (incident == null) {
            response.put("mensaje", Messages.notExist.get(integrationId));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Incident>(incident, HttpStatus.OK);
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/incident")
    public ResponseEntity<?> create(@RequestBody Incident incident) {
        Incident newIncident = null;
        Map<String, Object> response = new HashMap<>();
        try {
            newIncident = incidentService.save(incident);
        } catch (DataAccessException e) {
            response.put("mensaje", Errors.dataAccessExceptionInsert.get());
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", Messages.createOK.get());
        response.put("incident", newIncident);
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }

    @PutMapping("/incident/{id}")
    public ResponseEntity<?> update(@RequestBody String json, @PathVariable Long id) {
        Incident currentIncident = incidentService.findById(id);
        Incident incidentUpdated = null;
        Map<String, Object> response = new HashMap<>();
        if (currentIncident == null) {
            response.put("mensaje", Errors.dataAccessExceptionUpdate.get(id.toString()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }
        try {
            String levelOne = SnTable.Incident.get();
            SysUser assignedTo = getSysUserByIntegrationId(Util.parseJson(json, levelOne, Field.AssignedTo.get()));
            currentIncident.setAssignedTo(assignedTo);
            SysGroup sysGroup = getSysGroupByIntegrationId(Util.parseJson(json, levelOne, Field.AssignmentGroup.get()));
            currentIncident.setAssignmentGroup(sysGroup);
            SysUser resolvedBy = getSysUserByIntegrationId(Util.parseJson(json, levelOne, Field.ResolvedBy.get()));
            currentIncident.setResolvedBy(resolvedBy);
            currentIncident.setCloseNotes(Util.parseJson(json, levelOne, Field.CloseNotes.get()));
            currentIncident.setCloseCode(Util.parseJson(json, levelOne, Field.CloseCode.get()));
            currentIncident.setResolvedAt(Util.parseJson(json, levelOne, Field.ResolvedAt.get()));
            currentIncident.setDescription(Util.parseJson(json, levelOne, Field.Description.get()));
            currentIncident.setShortDescription(Util.parseJson(json, levelOne, Field.ShortDescription.get()));
            currentIncident.setState(Util.parseJson(json, levelOne, Field.State.get()));
            currentIncident.setIncidentState(Util.parseJson(json, levelOne, Field.IncidentState.get()));
            currentIncident.setReasonPending(Util.parseJson(json, levelOne, Field.ReasonPending.get()));
            currentIncident.setImpact(Util.parseJson(json, levelOne,Field.Impact.get()));
            currentIncident.setUrgency(Util.parseJson(json, levelOne,Field.Urgency.get()));
            currentIncident.setPriority(Util.parseJson(json, levelOne, Field.Priority.get()));
            String knowledge = Util.parseJson(json, levelOne, Field.Knowledge.get());
            currentIncident.setKnowledge(Boolean.parseBoolean(knowledge != null && knowledge != "" ? knowledge : "false"));
            incidentUpdated = incidentService.save(currentIncident);
            Incident incidentServiceNow = rest.putIncident(EndPointSN.PutIncident(), currentIncident);
        } catch (DataAccessException e) {
            response.put("mensaje", Errors.dataAccessExceptionUpdate.get());
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", Messages.UpdateOK.get());
        response.put("incident", currentIncident);
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }

    @PutMapping("/incidentSN")
    public ResponseEntity<?> update(@RequestBody IncidentRequest incidentRequest) {
        Incident incident = new Incident();
        Map<String, Object> response = new HashMap<>();
        try {
            String tagAction = App.CreateConsole();
            String tag = "[Incident] ";
            Incident exists = incidentService.findByIntegrationId(incidentRequest.getSys_id());
            if (exists != null) {
                incident.setId(exists.getId());
                incident.setScRequestParent(exists.getScRequestParent());
                incident.setMaster(exists.getMaster());
                tagAction = App.UpdateConsole();
            }
            incident.setSysUpdatedOn(incidentRequest.getSys_updated_on());
            incident.setUpdatedOn(Util.getLocalDateTime(Util.isNull(incidentRequest.getSys_updated_on())));
            incident.setNumber(incidentRequest.getNumber());
            incident.setState(incidentRequest.getState());
            incident.setIncidentState(incidentRequest.getIncident_state());
            incident.setReasonPending(incidentRequest.getReason_pending());
            incident.setSysCreatedBy(incidentRequest.getSys_created_by());
            incident.setImpact(incidentRequest.getImpact());
            incident.setActive(incidentRequest.getActive());
            incident.setKnowledge(incidentRequest.getKnowledge());
            incident.setPriority(incidentRequest.getPriority());
            incident.setBusinessDuration(incidentRequest.getBusiness_duration());
            incident.setShortDescription(incidentRequest.getShort_description());
            incident.setCorrelationDisplay(incidentRequest.getCorrelation_display());
            incident.setNotify(incidentRequest.getNotify());
            incident.setReassignmentCount(incidentRequest.getReassignment_count());
            incident.setUponApproval(incidentRequest.getUpon_approval());
            incident.setCorrelationId(incidentRequest.getCorrelation_id());
            incident.setSysUpdatedBy(incidentRequest.getSys_updated_by());
            incident.setSysCreatedOn(incidentRequest.getSys_created_on());
            incident.setCreatedOn(Util.getLocalDateTime(Util.isNull(incidentRequest.getSys_created_on())));
            incident.setClosedAt(incidentRequest.getClosed_at());
            incident.setOpenedAt(incidentRequest.getOpened_at());
            incident.setReopenedTime(incidentRequest.getReopened_time());
            incident.setResolvedAt(incidentRequest.getResolved_at());
            incident.setSubcategory(incidentRequest.getSubcategory());
            incident.setCloseCode(incidentRequest.getClose_code());
            incident.setDescription(incidentRequest.getDescription());
            incident.setCalendarDuration(incidentRequest.getCalendar_duration());
            incident.setCloseNotes(incidentRequest.getClose_notes());
            incident.setIntegrationId(incidentRequest.getSys_id());
            incident.setContactType(incidentRequest.getContact_type());
            incident.setIncidentState(incidentRequest.getIncident_state());
            incident.setUrgency(incidentRequest.getUrgency());
            incident.setSeverity(incidentRequest.getSeverity());
            incident.setApproval(incidentRequest.getApproval());
            incident.setSysModCount(incidentRequest.getSys_mod_count());
            incident.setReopenCount(incidentRequest.getReopen_count());
            incident.setCategory(incidentRequest.getCategory());
            Choice category = choiceService.findByValueAndElementAndName(incident.getCategory(), Field.Category.get(), SnTable.Incident.get());
            if (category != null) {
                incident.setCategory(category.getLabel());
            }
            incident.setServiceLevel(incidentRequest.getU_service());
            incident.setAmbiteLevel(incidentRequest.getU_ambite());
            incident.setPlatformLevel(incidentRequest.getU_platform());
            incident.setSpecificationLevel(incidentRequest.getU_specification());
            incident.setIncidentParent(incidentRequest.getIncident_parent());
            incident.setSolverFlagAssignedTo(incidentRequest.getU_solver_flag_assigned_to());
            incident.setSolverFlagResolvedBy(incidentRequest.getU_solver_flag_resolved_by());
            Company company = companyService.findByIntegrationId(incidentRequest.getCompany());
            incident.setCompany(company);
            incident.setDomain(company.getDomain());
            if (Util.hasData(incidentRequest.getIncident_parent()) || Util.hasData(incidentRequest.getParent_incident())) {
                String incidentParent = Util.hasData(incidentRequest.getIncident_parent()) ? incidentRequest.getIncident_parent() : incidentRequest.getParent_incident();
                Incident master = incidentService.findByIntegrationId(incidentParent);
                if (!master.getMaster()) {
                    master.setMaster(true);
                    incidentService.save(master);
                }
            }
            incident.setClosedBy(null);
            if (Util.hasData(incidentRequest.getCaused_by())) {
                incident.setCausedBy(sysUserService.findByIntegrationId(incidentRequest.getCaused_by()));
            }
            incident.setReopenedBy(null);
            if (Util.hasData(incidentRequest.getReopened_by())) {
                incident.setReopenedBy(sysUserService.findByIntegrationId(incidentRequest.getReopened_by()));
            }
            incident.setAssignedTo(null);
            if (Util.hasData(incidentRequest.getAssigned_to())) {
                incident.setAssignedTo(sysUserService.findByIntegrationId(incidentRequest.getAssigned_to()));
            }
            incident.setSolverAssignedTo(null);
            if (Util.hasData(incidentRequest.getU_solver_assigned_to())) {
                incident.setSolverAssignedTo(sysUserService.findByIntegrationId(incidentRequest.getU_solver_assigned_to()));
            }
            incident.setSolverResolvedBy(null);
            if (Util.hasData(incidentRequest.getU_solver_resolved_by())) {
                incident.setSolverResolvedBy(sysUserService.findByIntegrationId(incidentRequest.getU_solver_resolved_by()));
            }
            incident.setOpenedBy(null);
            if (Util.hasData(incidentRequest.getOpened_by())) {
                incident.setOpenedBy(sysUserService.findByIntegrationId(incidentRequest.getOpened_by()));
            }
            incident.setTaskFor(null);
            if (Util.hasData(incidentRequest.getTask_for())) {
                incident.setTaskFor(sysUserService.findByIntegrationId(incidentRequest.getTask_for()));
            }
            incident.setBusinessService(null);
            if (Util.hasData(incidentRequest.getBusiness_service())) {
                incident.setBusinessService(ciServiceService.findByIntegrationId(incidentRequest.getBusiness_service()));
            }
            incident.setCaller(null);
            if (Util.hasData(incidentRequest.getCaller_id())) {
                incident.setCaller(sysUserService.findByIntegrationId(incidentRequest.getCaller_id()));
            }
            incident.setConfigurationItem(null);
            if (Util.hasData(incidentRequest.getConfiguration_item())) {
                incident.setConfigurationItem(configurationItemService.findByIntegrationId(incidentRequest.getConfiguration_item()));
            }
            incident.setAssignmentGroup(null);
            if (Util.hasData(incidentRequest.getAssignment_group())) {
                incident.setAssignmentGroup(sysGroupService.findByIntegrationId(incidentRequest.getAssignment_group()));
            }
            incident.setLocation(null);
            if (Util.hasData(incidentRequest.getLocation())) {
                incident.setLocation(locationService.findByIntegrationId(incidentRequest.getLocation()));
            }
            incidentService.save(incident);
            Util.printData(tag, tagAction.concat(Util.getFieldDisplay(incident)), Util.getFieldDisplay(incident.getCompany()), Util.getFieldDisplay(incident.getDomain()));
        } catch (DataAccessException e) {
            //System.out.println("error " + e.getMessage());
            log.error("error " + e.getMessage());
            response.put("mensaje", Errors.dataAccessExceptionUpdate.get());
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", Messages.UpdateOK.get());
        response.put("incident", incident);

        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }
    @PutMapping("/incidentDelete")
    public ResponseEntity<?> delete(String element, String integrationId) {
        /*
        System.out.println("element: ".concat(Util.isNull(element)));
        System.out.println("integrationId: ".concat(Util.isNull(integrationId)));
         */
        log.info("element: ".concat(Util.isNull(element)));
        log.info("integrationId: ".concat(Util.isNull(integrationId)));
        Map<String, Object> response = new HashMap<>();
        try {
            if (SnTable.TaskSla.get().equals(element)) {
                TaskSla taskSla = taskSlaService.findByIntegrationId(integrationId);
                if (taskSla != null) {
                    taskSla.setActive(false);
                    taskSlaService.save(taskSla);
                    //System.out.println("[TaskSla] (Disabled) ".concat(taskSla.getIntegrationId()));
                    log.info("[TaskSla] (Disabled) ".concat(taskSla.getIntegrationId()));
                } else
                    //System.out.println("[TaskSla] (Not exist) ".concat(integrationId));
                    log.info("[TaskSla] (Not exist) ".concat(integrationId));
            }
        } catch (DataAccessException e) {
            response.put("mensaje", Errors.dataAccessExceptionDelete.get());
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", Messages.DeleteOK.get());
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
    }

    @GetMapping("/incidenBySolver")
    public List<Incident> findByCompany() {
        System.out.println(App.Start());
        APIResponse apiResponse = null;
        List<Incident> incidents = new ArrayList<>();
        String[] sparmOffSets = Util.offSets500000();
        long startTime = 0;
        long endTime = 0;
        String tag = "[Incident] ";
        String result;
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        JSONParser parser = new JSONParser();
        JSONObject resultJson = new JSONObject();
        JSONArray ListSnIncidentJson = new JSONArray();
        final String[] tagAction = new String[1];
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        final IncidentSolver[] incidentSolver = {new IncidentSolver()};
        final Incident[] incident = {new Incident()};
        final Choice[] category = new Choice[1];
        final Incident[] master = new Incident[1];
        final String[] element = new String[1];
        final ScRequest[] scRequest = new ScRequest[1];
        final ScRequestItem[] scRequestItem = new ScRequestItem[1];
        final Domain[] domain = new Domain[1];
        final Company[] company = new Company[1];
        final SysUser[] causedBy = new SysUser[1];
        final SysUser[] closedBy = new SysUser[1];
        final SysUser[] reopenedBy = new SysUser[1];
        final SysUser[] assignedTo = new SysUser[1];
        final SysUser[] solverAssignedTo = new SysUser[1];
        final SysUser[] resolvedBy = new SysUser[1];
        final SysUser[] solverResolvedBy = new SysUser[1];
        final SysUser[] openedBy = new SysUser[1];
        final SysUser[] taskFor = new SysUser[1];
        final SysUser[] caller = new SysUser[1];
        final ConfigurationItem[] configurationItem = new ConfigurationItem[1];
        final SysGroup[] sysGroup = new SysGroup[1];
        final Location[] location = new Location[1];
        final Incident[] exists = new Incident[1];
        final CiService[] businessService = new CiService[1];
        APIExecutionStatus status = new APIExecutionStatus();
        try {
            startTime = System.currentTimeMillis();
            final int[] count = {1};
            for (String sparmOffSet : sparmOffSets) {
                result = rest.responseByEndPoint(EndPointSN.IncidentByCompany().concat(sparmOffSet));
                System.out.println(tag.concat("(".concat(EndPointSN.IncidentByCompany().concat(sparmOffSet)).concat(")")));
                resultJson = (JSONObject) parser.parse(result);
                ListSnIncidentJson.clear();
                if (resultJson.get("result") != null)
                    ListSnIncidentJson = (JSONArray) parser.parse(resultJson.get("result").toString());
                ListSnIncidentJson.stream().forEach(snIncidentJson -> {
                    tagAction[0] = App.CreateConsole();
                    try {
                        incidentSolver[0] = objectMapper.readValue(snIncidentJson.toString(), IncidentSolver.class);
                        incident[0] = new Incident();
                        incident[0].setSysUpdatedOn(Util.isNull(incidentSolver[0].getSys_updated_on()));
                        incident[0].setUpdatedOn(Util.getLocalDateTime(Util.isNull(incidentSolver[0].getSys_updated_on())));
                        incident[0].setNumber(Util.isNull(incidentSolver[0].getNumber()));
                        incident[0].setState(Util.isNull(incidentSolver[0].getState()));
                        incident[0].setSysCreatedBy(Util.isNull(incidentSolver[0].getSys_created_by()));
                        incident[0].setImpact(Util.isNull(incidentSolver[0].getImpact()));
                        incident[0].setReasonPending(Util.isNull(incidentSolver[0].getU_sbk_pendiente()));
                        incident[0].setActive(incidentSolver[0].isActive());
                        incident[0].setKnowledge(incidentSolver[0].getKnowledge());
                        incident[0].setPriority(Util.isNull(incidentSolver[0].getPriority()));
                        incident[0].setBusinessDuration(Util.isNull(incidentSolver[0].getBusiness_duration()));
                        incident[0].setShortDescription(Util.isNull(incidentSolver[0].getShort_description()));
                        incident[0].setCorrelationDisplay(Util.isNull(incidentSolver[0].getCorrelation_display()));
                        incident[0].setNotify(Util.isNull(incidentSolver[0].getNotify()));
                        incident[0].setReassignmentCount(Util.isNull(incidentSolver[0].getReassignment_count()));
                        incident[0].setUponApproval(Util.isNull(incidentSolver[0].getUpon_approval()));
                        incident[0].setCorrelationId(Util.isNull(incidentSolver[0].getCorrelation_id()));
                        incident[0].setMadeSla(incidentSolver[0].getMade_sla());
                        incident[0].setSysUpdatedBy(Util.isNull(incidentSolver[0].getSys_updated_by()));
                        incident[0].setSysCreatedOn(Util.isNull(incidentSolver[0].getSys_created_on()));
                        incident[0].setCreatedOn(Util.getLocalDateTime(Util.isNull(incidentSolver[0].getSys_created_on())));
                        incident[0].setClosedAt(Util.isNull(incidentSolver[0].getClosed_at()));
                        incident[0].setClosed(Util.getLocalDateTime(Util.isNull(incidentSolver[0].getClosed_at())));
                        incident[0].setOpenedAt(Util.isNull(incidentSolver[0].getOpened_at()));
                        incident[0].setOpened(Util.getLocalDateTime(Util.isNull(incidentSolver[0].getOpened_at())));
                        incident[0].setReopenedTime(Util.isNull(incidentSolver[0].getReopened_time()));
                        incident[0].setResolvedAt(Util.isNull(incidentSolver[0].getResolved_at()));
                        incident[0].setResolved(Util.getLocalDateTime(Util.isNull(incidentSolver[0].getResolved_at())));
                        incident[0].setSubcategory(Util.isNull(incidentSolver[0].getSubcategory()));
                        incident[0].setCloseCode(Util.isNull(incidentSolver[0].getClose_code()));
                        incident[0].setDescription(Util.isNull(incidentSolver[0].getDescription()));
                        incident[0].setCalendarDuration(Util.isNull(incidentSolver[0].getCalendar_duration()));
                        incident[0].setCloseNotes(Util.isNull(incidentSolver[0].getClose_notes()));
                        incident[0].setIntegrationId(Util.isNull(incidentSolver[0].getSys_id()));
                        incident[0].setContactType(Util.isNull(incidentSolver[0].getContact_type()));
                        incident[0].setIncidentState(Util.isNull(incidentSolver[0].getIncident_state()));
                        incident[0].setUrgency(Util.isNull(incidentSolver[0].getUrgency()));
                        incident[0].setSeverity(Util.isNull(incidentSolver[0].getSeverity()));
                        incident[0].setApproval(Util.isNull(incidentSolver[0].getApproval()));
                        incident[0].setSysModCount(Util.isNull(incidentSolver[0].getSys_mod_count()));
                        incident[0].setReopenCount(Util.isNull(incidentSolver[0].getReopen_count()));
                        incident[0].setCategory(Util.isNull(incidentSolver[0].getCategory()));
                        category[0] = choiceService.findByValueAndElementAndName(incident[0].getCategory(), Field.Category.get(), SnTable.Incident.get());
                        if (category[0] != null) {
                            incident[0].setCategory(category[0].getLabel());
                        }
                        incident[0].setServiceLevel(Util.isNull(incidentSolver[0].getU_service()));
                        incident[0].setAmbiteLevel(Util.isNull(incidentSolver[0].getU_ambite()));
                        incident[0].setPlatformLevel(Util.isNull(incidentSolver[0].getU_platform()));
                        incident[0].setSpecificationLevel(Util.isNull(incidentSolver[0].getU_specification()));
                        incident[0].setIncidentParent(Util.getIdByJson((JSONObject) snIncidentJson, "parent_incident", App.Value()));
                        incident[0].setSolverFlagAssignedTo(incidentSolver[0].getU_solver_flag_assigned_to());
                        incident[0].setSolverFlagResolvedBy(incidentSolver[0].getU_solver_flag_resolved_by());
                        incident[0].setDelete(false);
                        if (Util.hasData(incident[0].getIncidentParent())) {
                            master[0] = incidentService.findByIntegrationId(incident[0].getIncidentParent());
                            if (!master[0].getMaster()) {
                                master[0].setMaster(true);
                                incidentService.save(master[0]);
                            }
                        }
                        element[0] = Util.getIdByJson((JSONObject) snIncidentJson, "parent", App.Value());
                        if (element[0] != "") {
                            scRequest[0] = scRequestService.findByIntegrationId(element[0]);
                            if (scRequest[0] != null)
                                incident[0].setScRequestParent(element[0]);
                            else {
                                scRequestItem[0] = scRequestItemService.findByIntegrationId(element[0]);
                                if (scRequestItem[0] != null)
                                    incident[0].setScRequestItemParent(element[0]);
                            }
                        }
                        domain[0] = getDomainByIntegrationId((JSONObject) snIncidentJson, SnTable.Domain.get(), App.Value());
                        incident[0].setDomain(domain[0]);
                        company[0] = getCompanyByIntegrationId((JSONObject) snIncidentJson, SnTable.Company.get(), App.Value());
                        incident[0].setCompany(company[0]);
                        causedBy[0] = getSysUserByIntegrationId((JSONObject) snIncidentJson, "caused_by", App.Value());
                        incident[0].setCausedBy(causedBy[0]);
                        closedBy[0] = getSysUserByIntegrationId((JSONObject) snIncidentJson, "closed_by", App.Value());
                        incident[0].setClosedBy(closedBy[0]);
                        reopenedBy[0] = getSysUserByIntegrationId((JSONObject) snIncidentJson, "reopened_by", App.Value());
                        incident[0].setReopenedBy(reopenedBy[0]);
                        assignedTo[0] = getSysUserByIntegrationId((JSONObject) snIncidentJson, "u_solver_assigned_to", App.Value());
                        incident[0].setAssignedTo(assignedTo[0]);
                        solverAssignedTo[0] = getSysUserByIntegrationId((JSONObject) snIncidentJson, "u_solver_assigned_to", App.Value());
                        incident[0].setSolverAssignedTo(solverAssignedTo[0]);
                        resolvedBy[0] = getSysUserByIntegrationId((JSONObject) snIncidentJson, "resolved_by", App.Value());
                        incident[0].setResolvedBy(resolvedBy[0]);
                        solverResolvedBy[0] = getSysUserByIntegrationId((JSONObject) snIncidentJson, "u_solver_resolved_by", App.Value());
                        incident[0].setSolverResolvedBy(solverResolvedBy[0]);
                        openedBy[0] = getSysUserByIntegrationId((JSONObject) snIncidentJson, "opened_by", App.Value());
                        incident[0].setOpenedBy(openedBy[0]);
                        taskFor[0] = getSysUserByIntegrationId((JSONObject) snIncidentJson, "task_for", App.Value());
                        incident[0].setTaskFor(taskFor[0]);
                        businessService[0] = getCiServiceByIntegrationId((JSONObject) snIncidentJson, "business_service", App.Value());
                        incident[0].setBusinessService(businessService[0]);
                        caller[0] = getSysUserByIntegrationId((JSONObject) snIncidentJson, "caller_id", App.Value());
                        incident[0].setCaller(caller[0]);
                        configurationItem[0] = getConfigurationItemByIntegrationId((JSONObject) snIncidentJson, "cmdb_ci", App.Value());
                        incident[0].setConfigurationItem(configurationItem[0]);
                        sysGroup[0] = getSysGroupByIntegrationId((JSONObject) snIncidentJson, "assignment_group", App.Value());
                        incident[0].setAssignmentGroup(sysGroup[0]);
                        location[0] = getLocationByIntegrationId((JSONObject) snIncidentJson, "location", App.Value());
                        incident[0].setLocation(location[0]);
                        exists[0] = incidentService.findByIntegrationId(incident[0].getIntegrationId());
                        if (exists[0] != null) {
                            incident[0].setId(exists[0].getId());
                            incident[0].setScRequestParent(exists[0].getScRequestParent());
                            incident[0].setMaster(exists[0].getMaster());
                            tagAction[0] = App.UpdateConsole();
                        }
                        incidents.add(incidentService.save(incident[0]));
                        Util.printData(tag, count[0], tagAction[0].concat(Util.getFieldDisplay(incident[0])), Util.getFieldDisplay(company[0]), Util.getFieldDisplay(domain[0]));
                        count[0] = count[0] + 1;
                    } catch (Exception e) {
                        System.out.println(tag.concat("Exception (I) (").concat(String.valueOf(count[0])).concat(") ").concat(String.valueOf(e)));
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
        return incidents;
    }

    @GetMapping("/incidenBySolverAndQuery")
    public List<Incident> findByCompany(String query) {
        System.out.println(App.Start());
        APIResponse apiResponse = null;
        List<Incident> incidents = new ArrayList<>();
        String[] sparmOffSets = Util.offSets50000();
        long startTime = 0;
        long endTime = 0;
        String tag = "[Incident] ";
        String result;
        JSONParser parser = new JSONParser();
        JSONObject resultJson = new JSONObject();
        JSONArray ListSnIncidentJson = new JSONArray();
        final String[] tagAction = new String[1];
        final IncidentSolver[] incidentSolver = {new IncidentSolver()};
        final Choice[] category = new Choice[1];
        final Incident[] master = new Incident[1];
        final String[] element = new String[1];
        final ScRequest[] scRequest = new ScRequest[1];
        final ScRequestItem[] scRequestItem = new ScRequestItem[1];
        final Domain[] domain = new Domain[1];
        final Company[] company = new Company[1];
        final SysUser[] causedBy = new SysUser[1];
        final SysUser[] closedBy = new SysUser[1];
        final SysUser[] reopenedBy = new SysUser[1];
        final SysUser[] assignedTo = new SysUser[1];
        final SysUser[] solverAssignedTo = new SysUser[1];
        final SysUser[] resolvedBy = new SysUser[1];
        final SysUser[] solverResolvedBy = new SysUser[1];
        final SysUser[] taskFor = new SysUser[1];
        final SysUser[] openedBy = new SysUser[1];
        final CiService[] businessService = new CiService[1];
        final SysUser[] caller = new SysUser[1];
        final ConfigurationItem[] configurationItem = new ConfigurationItem[1];
        final SysGroup[] sysGroup = new SysGroup[1];
        final Location[] location = new Location[1];
        final Incident[] exists = new Incident[1];
        ObjectMapper mapper = new ObjectMapper();
        APIExecutionStatus status = new APIExecutionStatus();
        try {
            startTime = System.currentTimeMillis();
            final int[] count = {1};
            Gson gson = new Gson();
            final Incident[] incident = {new Incident()};
            for (String sparmOffSet : sparmOffSets) {
                result = rest.responseByEndPoint(EndPointSN.IncidentByQuery().replace("QUERY", query).concat(sparmOffSet));
                System.out.println(tag.concat("(".concat(EndPointSN.IncidentByQuery().replace("QUERY", query).concat(sparmOffSet)).concat(")")));
                resultJson = (JSONObject) parser.parse(result);
                ListSnIncidentJson.clear();
                if (resultJson.get("result") != null)
                    ListSnIncidentJson = (JSONArray) parser.parse(resultJson.get("result").toString());
                ListSnIncidentJson.stream().forEach(snIncidentJson -> {
                    tagAction[0] = App.CreateConsole();
                    try {
                        incidentSolver[0] = gson.fromJson(snIncidentJson.toString(), IncidentSolver.class);
                        incident[0] =  new Incident();
                        incident[0].setSysUpdatedOn(Util.isNull(incidentSolver[0].getSys_updated_on()));
                        incident[0].setUpdatedOn(Util.getLocalDateTime(Util.isNull(incidentSolver[0].getSys_updated_on())));
                        incident[0].setNumber(Util.isNull(incidentSolver[0].getNumber()));
                        incident[0].setState(Util.isNull(incidentSolver[0].getState()));
                        incident[0].setSysCreatedBy(Util.isNull(incidentSolver[0].getSys_created_by()));
                        incident[0].setImpact(Util.isNull(incidentSolver[0].getImpact()));
                        incident[0].setActive(incidentSolver[0].isActive());
                        incident[0].setKnowledge(incidentSolver[0].getKnowledge());
                        incident[0].setPriority(Util.isNull(incidentSolver[0].getPriority()));
                        incident[0].setBusinessDuration(Util.isNull(incidentSolver[0].getBusiness_duration()));
                        incident[0].setShortDescription(Util.isNull(incidentSolver[0].getShort_description()));
                        incident[0].setCorrelationDisplay(Util.isNull(incidentSolver[0].getCorrelation_display()));
                        incident[0].setNotify(Util.isNull(incidentSolver[0].getNotify()));
                        incident[0].setReassignmentCount(Util.isNull(incidentSolver[0].getReassignment_count()));
                        incident[0].setUponApproval(Util.isNull(incidentSolver[0].getUpon_approval()));
                        incident[0].setCorrelationId(Util.isNull(incidentSolver[0].getCorrelation_id()));
                        incident[0].setMadeSla(incidentSolver[0].getMade_sla());
                        incident[0].setSysUpdatedBy(Util.isNull(incidentSolver[0].getSys_updated_by()));
                        incident[0].setSysCreatedOn(Util.isNull(incidentSolver[0].getSys_created_on()));
                        if (!Util.isNull(incidentSolver[0].getSys_created_on()).equals(""))
                            incident[0].setCreatedOn(Util.getLocalDateTime(Util.isNull(incidentSolver[0].getSys_created_on())));
                        incident[0].setClosedAt(Util.isNull(incidentSolver[0].getClosed_at()));
                        if (!Util.isNull(incidentSolver[0].getClosed_at()).equals(""))
                            incident[0].setClosed(Util.getLocalDateTime(Util.isNull(incidentSolver[0].getClosed_at())));
                        incident[0].setOpenedAt(Util.isNull(incidentSolver[0].getOpened_at()));
                        if (!Util.isNull(incidentSolver[0].getOpened_at()).equals(""))
                            incident[0].setOpened(Util.getLocalDateTime(Util.isNull(incidentSolver[0].getOpened_at())));
                        incident[0].setReopenedTime(Util.isNull(incidentSolver[0].getReopened_time()));
                        incident[0].setResolvedAt(Util.isNull(incidentSolver[0].getResolved_at()));
                        if (!Util.isNull(incidentSolver[0].getResolved_at()).equals(""))
                            incident[0].setResolved(Util.getLocalDateTime(Util.isNull(incidentSolver[0].getResolved_at())));
                        incident[0].setSubcategory(Util.isNull(incidentSolver[0].getSubcategory()));
                        incident[0].setCloseCode(Util.isNull(incidentSolver[0].getClose_code()));
                        incident[0].setDescription(Util.isNull(incidentSolver[0].getDescription()));
                        incident[0].setCalendarDuration(Util.isNull(incidentSolver[0].getCalendar_duration()));
                        incident[0].setCloseNotes(Util.isNull(incidentSolver[0].getClose_notes()));
                        incident[0].setIntegrationId(Util.isNull(incidentSolver[0].getSys_id()));
                        incident[0].setContactType(Util.isNull(incidentSolver[0].getContact_type()));
                        incident[0].setIncidentState(Util.isNull(incidentSolver[0].getIncident_state()));
                        incident[0].setUrgency(Util.isNull(incidentSolver[0].getUrgency()));
                        incident[0].setSeverity(Util.isNull(incidentSolver[0].getSeverity()));
                        incident[0].setApproval(Util.isNull(incidentSolver[0].getApproval()));
                        incident[0].setSysModCount(Util.isNull(incidentSolver[0].getSys_mod_count()));
                        incident[0].setReopenCount(Util.isNull(incidentSolver[0].getReopen_count()));
                        incident[0].setCategory(Util.isNull(incidentSolver[0].getCategory()));
                        category[0] = choiceService.findByValueAndElementAndName(incident[0].getCategory(), Field.Category.get(), SnTable.Incident.get());
                        if (category[0] != null) {
                            incident[0].setCategory(category[0].getLabel());
                        }
                        incident[0].setServiceLevel(Util.isNull(incidentSolver[0].getU_service()));
                        incident[0].setAmbiteLevel(Util.isNull(incidentSolver[0].getU_ambite()));
                        incident[0].setPlatformLevel(Util.isNull(incidentSolver[0].getU_platform()));
                        incident[0].setSpecificationLevel(Util.isNull(incidentSolver[0].getU_specification()));
                        incident[0].setIncidentParent(Util.getIdByJson((JSONObject) snIncidentJson, "parent_incident", App.Value()));
                        incident[0].setSolverFlagAssignedTo(incidentSolver[0].getU_solver_flag_assigned_to());
                        incident[0].setSolverFlagResolvedBy(incidentSolver[0].getU_solver_flag_resolved_by());
                        incident[0].setReasonPending(Util.isNull(incidentSolver[0].getU_sbk_pendiente()));
                        incident[0].setDelete(false);
                        if (Util.hasData(incident[0].getIncidentParent())) {
                            master[0] = incidentService.findByIntegrationId(incident[0].getIncidentParent());
                            if (!master[0].getMaster()) {
                                master[0].setMaster(true);
                                incidentService.save(master[0]);
                            }
                        }
                        element[0] = Util.getIdByJson((JSONObject) snIncidentJson, "parent", App.Value());
                        if (element[0] != "") {
                            scRequest[0] = scRequestService.findByIntegrationId(element[0]);
                            if (scRequest[0] != null)
                                incident[0].setScRequestParent(element[0]);
                            else {
                                scRequestItem[0] = scRequestItemService.findByIntegrationId(element[0]);
                                if (scRequestItem[0] != null)
                                    incident[0].setScRequestItemParent(element[0]);
                            }
                        }
                        domain[0] = getDomainByIntegrationId((JSONObject) snIncidentJson, SnTable.Domain.get(), App.Value());
                        incident[0].setDomain(domain[0]);
                        company[0] = getCompanyByIntegrationId((JSONObject) snIncidentJson, SnTable.Company.get(), App.Value());
                        incident[0].setCompany(company[0]);
                        causedBy[0] = getSysUserByIntegrationId((JSONObject) snIncidentJson, "caused_by", App.Value());
                        incident[0].setCausedBy(causedBy[0]);
                        closedBy[0] = getSysUserByIntegrationId((JSONObject) snIncidentJson, "closed_by", App.Value());
                        incident[0].setClosedBy(closedBy[0]);
                        reopenedBy[0] = getSysUserByIntegrationId((JSONObject) snIncidentJson, "reopened_by", App.Value());
                        incident[0].setReopenedBy(reopenedBy[0]);
                        assignedTo[0] = getSysUserByIntegrationId((JSONObject) snIncidentJson, "u_solver_assigned_to", App.Value());
                        incident[0].setAssignedTo(assignedTo[0]);
                        solverAssignedTo[0] = getSysUserByIntegrationId((JSONObject) snIncidentJson, "u_solver_assigned_to", App.Value());
                        incident[0].setSolverAssignedTo(solverAssignedTo[0]);
                        resolvedBy[0] = getSysUserByIntegrationId((JSONObject) snIncidentJson, "resolved_by", App.Value());
                        incident[0].setResolvedBy(resolvedBy[0]);
                        solverResolvedBy[0] = getSysUserByIntegrationId((JSONObject) snIncidentJson, "u_solver_resolved_by", App.Value());
                        incident[0].setSolverResolvedBy(solverResolvedBy[0]);
                        openedBy[0] = getSysUserByIntegrationId((JSONObject) snIncidentJson, "opened_by", App.Value());
                        incident[0].setOpenedBy(openedBy[0]);
                        taskFor[0] = getSysUserByIntegrationId((JSONObject) snIncidentJson, "task_for", App.Value());
                        incident[0].setTaskFor(taskFor[0]);
                        businessService[0] = getCiServiceByIntegrationId((JSONObject) snIncidentJson, "business_service", App.Value());
                        incident[0].setBusinessService(businessService[0]);
                        caller[0] = getSysUserByIntegrationId((JSONObject) snIncidentJson, "caller_id", App.Value());
                        incident[0].setCaller(caller[0]);
                        configurationItem[0] = getConfigurationItemByIntegrationId((JSONObject) snIncidentJson, "cmdb_ci", App.Value());
                        incident[0].setConfigurationItem(configurationItem[0]);
                        sysGroup[0] = getSysGroupByIntegrationId((JSONObject) snIncidentJson, "assignment_group", App.Value());
                        incident[0].setAssignmentGroup(sysGroup[0]);
                        location[0] = getLocationByIntegrationId((JSONObject) snIncidentJson, "location", App.Value());
                        incident[0].setLocation(location[0]);
                        exists[0] = incidentService.findByIntegrationId(incident[0].getIntegrationId());
                        if (exists[0] != null) {
                            incident[0].setId(exists[0].getId());
                            incident[0].setScRequestParent(exists[0].getScRequestParent());
                            incident[0].setMaster(exists[0].getMaster());
                            tagAction[0] = App.UpdateConsole();
                        }
                        incidents.add(incidentService.save(incident[0]));
                        Util.printData(tag, count[0], tagAction[0].concat(Util.getFieldDisplay(incident[0])), Util.getFieldDisplay(company[0]), Util.getFieldDisplay(domain[0]));
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
        return incidents;
    }

    @GetMapping("/incidenBySolverAndQueryCreate")
    public List<Incident> findByCompany(String query, boolean flagCreate) {
        System.out.println(App.Start());
        APIResponse apiResponse = null;
        List<Incident> incidents = new ArrayList<>();
        String[] sparmOffSets = Util.offSets50000();
        long startTime = 0;
        long endTime = 0;
        String tag = "[Incident] ";
        JSONParser parser = new JSONParser();
        JSONObject resultJson;
        JSONArray ListSnIncidentJson = new JSONArray();
        final Incident[] incident = {new Incident()};
        String result;
        final String[] tagAction = new String[1];
        final IncidentSolver[] incidentSolver = new IncidentSolver[1];
        final Incident[] exists = new Incident[1];
        final Choice[] category = new Choice[1];
        final Incident[] master = new Incident[1];
        final String[] element = new String[1];
        final ScRequest[] scRequest = new ScRequest[1];
        final ScRequestItem[] scRequestItem = new ScRequestItem[1];
        final Domain[] domain = new Domain[1];
        final Company[] company = new Company[1];
        final SysUser[] causedBy = new SysUser[1];
        final SysUser[] closedBy = new SysUser[1];
        final SysUser[] reopenedBy = new SysUser[1];
        final SysUser[] assignedTo = new SysUser[1];
        final SysUser[] solverAssignedTo = new SysUser[1];
        final SysUser[] resolvedBy = new SysUser[1];
        final SysUser[] solverResolvedBy = new SysUser[1];
        final SysUser[] openedBy = new SysUser[1];
        final SysUser[] taskFor = new SysUser[1];
        final CiService[] businessService = new CiService[1];
        final SysUser[] caller = new SysUser[1];
        final ConfigurationItem[] configurationItem = new ConfigurationItem[1];
        final SysGroup[] sysGroup = new SysGroup[1];
        final Location[] location = new Location[1];
        ObjectMapper mapper = new ObjectMapper();
        APIExecutionStatus status = new APIExecutionStatus();
        try {
            startTime = System.currentTimeMillis();
            final int[] count = {1};
            Gson gson = new Gson();
            for (String sparmOffSet : sparmOffSets) {
                result = rest.responseByEndPoint(EndPointSN.IncidentByQuery().replace("QUERY", query).concat(sparmOffSet));
                System.out.println(tag.concat("(".concat(EndPointSN.IncidentByQuery().replace("QUERY", query).concat(sparmOffSet)).concat(")")));
                resultJson = (JSONObject) parser.parse(result);
                ListSnIncidentJson.clear();
                if (resultJson.get("result") != null)
                    ListSnIncidentJson = (JSONArray) parser.parse(resultJson.get("result").toString());
                ListSnIncidentJson.stream().forEach(snIncidentJson -> {
                    try {
                        incidentSolver[0] = gson.fromJson(snIncidentJson.toString(), IncidentSolver.class);
                        incident[0] = new Incident();
                        exists[0] = incidentService.findByIntegrationId(incident[0].getIntegrationId());
                        if (exists[0] != null) {
                            incident[0].setId(exists[0].getId());
                            incident[0].setScRequestParent(exists[0].getScRequestParent());
                            incident[0].setMaster(exists[0].getMaster());
                        } else {
                            incident[0].setSysUpdatedOn(Util.isNull(incidentSolver[0].getSys_updated_on()));
                            incident[0].setUpdatedOn(Util.getLocalDateTime(Util.isNull(incidentSolver[0].getSys_updated_on())));
                            incident[0].setNumber(Util.isNull(incidentSolver[0].getNumber()));
                            incident[0].setState(Util.isNull(incidentSolver[0].getState()));
                            incident[0].setSysCreatedBy(Util.isNull(incidentSolver[0].getSys_created_by()));
                            incident[0].setImpact(Util.isNull(incidentSolver[0].getImpact()));
                            incident[0].setActive(incidentSolver[0].isActive());
                            incident[0].setKnowledge(incidentSolver[0].getKnowledge());
                            incident[0].setPriority(Util.isNull(incidentSolver[0].getPriority()));
                            incident[0].setBusinessDuration(Util.isNull(incidentSolver[0].getBusiness_duration()));
                            incident[0].setShortDescription(Util.isNull(incidentSolver[0].getShort_description()));
                            incident[0].setCorrelationDisplay(Util.isNull(incidentSolver[0].getCorrelation_display()));
                            incident[0].setNotify(Util.isNull(incidentSolver[0].getNotify()));
                            incident[0].setReassignmentCount(Util.isNull(incidentSolver[0].getReassignment_count()));
                            incident[0].setUponApproval(Util.isNull(incidentSolver[0].getUpon_approval()));
                            incident[0].setCorrelationId(Util.isNull(incidentSolver[0].getCorrelation_id()));
                            incident[0].setMadeSla(incidentSolver[0].getMade_sla());
                            incident[0].setSysUpdatedBy(Util.isNull(incidentSolver[0].getSys_updated_by()));
                            incident[0].setSysCreatedOn(Util.isNull(incidentSolver[0].getSys_created_on()));
                            if (!Util.isNull(incidentSolver[0].getSys_created_on()).equals(""))
                                incident[0].setCreatedOn(Util.getLocalDateTime(Util.isNull(incidentSolver[0].getSys_created_on())));
                            incident[0].setClosedAt(Util.isNull(incidentSolver[0].getClosed_at()));
                            if (!Util.isNull(incidentSolver[0].getClosed_at()).equals(""))
                                incident[0].setClosed(Util.getLocalDateTime(Util.isNull(incidentSolver[0].getClosed_at())));
                            incident[0].setOpenedAt(Util.isNull(incidentSolver[0].getOpened_at()));
                            if (!Util.isNull(incidentSolver[0].getOpened_at()).equals(""))
                                incident[0].setOpened(Util.getLocalDateTime(Util.isNull(incidentSolver[0].getOpened_at())));
                            incident[0].setReopenedTime(Util.isNull(incidentSolver[0].getReopened_time()));
                            incident[0].setResolvedAt(Util.isNull(incidentSolver[0].getResolved_at()));
                            if (!Util.isNull(incidentSolver[0].getResolved_at()).equals(""))
                                incident[0].setResolved(Util.getLocalDateTime(Util.isNull(incidentSolver[0].getResolved_at())));
                            incident[0].setSubcategory(Util.isNull(incidentSolver[0].getSubcategory()));
                            incident[0].setCloseCode(Util.isNull(incidentSolver[0].getClose_code()));
                            incident[0].setDescription(Util.isNull(incidentSolver[0].getDescription()));
                            incident[0].setCalendarDuration(Util.isNull(incidentSolver[0].getCalendar_duration()));
                            incident[0].setCloseNotes(Util.isNull(incidentSolver[0].getClose_notes()));
                            incident[0].setIntegrationId(Util.isNull(incidentSolver[0].getSys_id()));
                            incident[0].setContactType(Util.isNull(incidentSolver[0].getContact_type()));
                            incident[0].setIncidentState(Util.isNull(incidentSolver[0].getIncident_state()));
                            incident[0].setUrgency(Util.isNull(incidentSolver[0].getUrgency()));
                            incident[0].setSeverity(Util.isNull(incidentSolver[0].getSeverity()));
                            incident[0].setApproval(Util.isNull(incidentSolver[0].getApproval()));
                            incident[0].setSysModCount(Util.isNull(incidentSolver[0].getSys_mod_count()));
                            incident[0].setReopenCount(Util.isNull(incidentSolver[0].getReopen_count()));
                            incident[0].setCategory(Util.isNull(incidentSolver[0].getCategory()));
                            category[0] = choiceService.findByValueAndElementAndName(incident[0].getCategory(), Field.Category.get(), SnTable.Incident.get());
                            if (category[0] != null) {
                                incident[0].setCategory(category[0].getLabel());
                            }
                            incident[0].setServiceLevel(Util.isNull(incidentSolver[0].getU_service()));
                            incident[0].setAmbiteLevel(Util.isNull(incidentSolver[0].getU_ambite()));
                            incident[0].setPlatformLevel(Util.isNull(incidentSolver[0].getU_platform()));
                            incident[0].setSpecificationLevel(Util.isNull(incidentSolver[0].getU_specification()));
                            incident[0].setIncidentParent(Util.getIdByJson((JSONObject) snIncidentJson, "parent_incident", App.Value()));

                            incident[0].setSolverFlagAssignedTo(incidentSolver[0].getU_solver_flag_assigned_to());
                            incident[0].setSolverFlagResolvedBy(incidentSolver[0].getU_solver_flag_resolved_by());
                            incident[0].setReasonPending(Util.isNull(incidentSolver[0].getU_sbk_pendiente()));
                            incident[0].setDelete(false);

                            if (Util.hasData(incident[0].getIncidentParent())) {
                                master[0] = incidentService.findByIntegrationId(incident[0].getIncidentParent());
                                if (!master[0].getMaster()) {
                                    master[0].setMaster(true);
                                    incidentService.save(master[0]);
                                }
                            }
                            element[0] = Util.getIdByJson((JSONObject) snIncidentJson, "parent", App.Value());
                            if (element[0] != "") {
                                scRequest[0] = scRequestService.findByIntegrationId(element[0]);
                                if (scRequest[0] != null)
                                    incident[0].setScRequestParent(element[0]);
                                else {
                                    scRequestItem[0] = scRequestItemService.findByIntegrationId(element[0]);
                                    if (scRequestItem[0] != null)
                                        incident[0].setScRequestItemParent(element[0]);
                                }
                            }
                            domain[0] = getDomainByIntegrationId((JSONObject) snIncidentJson, SnTable.Domain.get(), App.Value());
                            incident[0].setDomain(domain[0]);
                            company[0] = getCompanyByIntegrationId((JSONObject) snIncidentJson, SnTable.Company.get(), App.Value());
                            incident[0].setCompany(company[0]);
                            causedBy[0] = getSysUserByIntegrationId((JSONObject) snIncidentJson, "caused_by", App.Value());
                            incident[0].setCausedBy(causedBy[0]);
                            closedBy[0] = getSysUserByIntegrationId((JSONObject) snIncidentJson, "closed_by", App.Value());
                            incident[0].setClosedBy(closedBy[0]);
                            reopenedBy[0] = getSysUserByIntegrationId((JSONObject) snIncidentJson, "reopened_by", App.Value());
                            incident[0].setReopenedBy(reopenedBy[0]);
                            assignedTo[0] = getSysUserByIntegrationId((JSONObject) snIncidentJson, "u_solver_assigned_to", App.Value());
                            incident[0].setAssignedTo(assignedTo[0]);
                            solverAssignedTo[0] = getSysUserByIntegrationId((JSONObject) snIncidentJson, "u_solver_assigned_to", App.Value());
                            incident[0].setSolverAssignedTo(solverAssignedTo[0]);
                            resolvedBy[0] = getSysUserByIntegrationId((JSONObject) snIncidentJson, "resolved_by", App.Value());
                            incident[0].setResolvedBy(resolvedBy[0]);
                            solverResolvedBy[0] = getSysUserByIntegrationId((JSONObject) snIncidentJson, "u_solver_resolved_by", App.Value());
                            incident[0].setSolverResolvedBy(solverResolvedBy[0]);
                            openedBy[0] = getSysUserByIntegrationId((JSONObject) snIncidentJson, "opened_by", App.Value());
                            incident[0].setOpenedBy(openedBy[0]);
                            taskFor[0] = getSysUserByIntegrationId((JSONObject) snIncidentJson, "task_for", App.Value());
                            incident[0].setTaskFor(taskFor[0]);
                            businessService[0] = getCiServiceByIntegrationId((JSONObject) snIncidentJson, "business_service", App.Value());
                            incident[0].setBusinessService(businessService[0]);
                            caller[0] = getSysUserByIntegrationId((JSONObject) snIncidentJson, "caller_id", App.Value());
                            incident[0].setCaller(caller[0]);
                            configurationItem[0] = getConfigurationItemByIntegrationId((JSONObject) snIncidentJson, "cmdb_ci", App.Value());
                            incident[0].setConfigurationItem(configurationItem[0]);
                            sysGroup[0] = getSysGroupByIntegrationId((JSONObject) snIncidentJson, "assignment_group", App.Value());
                            incident[0].setAssignmentGroup(sysGroup[0]);
                            location[0] = getLocationByIntegrationId((JSONObject) snIncidentJson, "location", App.Value());
                            incident[0].setLocation(location[0]);
                            incidents.add(incidentService.save(incident[0]));
                            Util.printData(tag, count[0], tagAction[0].concat(Util.getFieldDisplay(incident[0])), Util.getFieldDisplay(company[0]), Util.getFieldDisplay(domain[0]));
                        }
                        count[0] = count[0] + 1;
                    } catch (Exception e) {
                        //        System.out.println(tag.concat("Exception (I) (").concat(String.valueOf(count[0])).concat(") ").concat(String.valueOf(e)));
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
            //     System.out.println(tag.concat("Exception (II) : ").concat(String.valueOf(e)));
        }
        System.out.println(App.End());
        return incidents;
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


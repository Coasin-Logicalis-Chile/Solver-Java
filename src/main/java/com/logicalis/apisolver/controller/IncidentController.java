
package com.logicalis.apisolver.controller;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.logicalis.apisolver.model.*;
import com.logicalis.apisolver.model.enums.*;
import com.logicalis.apisolver.model.utilities.SortedUnpaged;
import com.logicalis.apisolver.services.*;
import com.logicalis.apisolver.util.Rest;
import com.logicalis.apisolver.util.Util;
import com.logicalis.apisolver.view.IncidentRequest;
import com.logicalis.apisolver.view.IncidentSolver;
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
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = {"${app.api.settings.cross-origin.urls}", "*"})
@RestController
@RequestMapping("/api/v1")
public class IncidentController {

    @Autowired
    private IIncidentService incidentService;
    @Autowired
    private IChoiceService choiceService;
    @Autowired
    private IJournalService journalService;
    @Autowired
    private IAttachmentService attachmentService;
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
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private Rest rest;

    Util util = new Util();
    App app = new App();
    EndPointSN endPointSN = new EndPointSN();

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
    public ResponseEntity<List<IncidentFields>> findIncidentsByFilters(//@RequestParam(value = "page", required = true, defaultValue = "0") Integer page,
                                                                       // @NotNull @RequestParam(value = "size", required = true, defaultValue = "10") Integer size,

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
            SysUser assignedTo = getSysUserByIntegrationId(util.parseJson(json, levelOne, Field.AssignedTo.get()));
            currentIncident.setAssignedTo(assignedTo);
            SysGroup sysGroup = getSysGroupByIntegrationId(util.parseJson(json, levelOne, Field.AssignmentGroup.get()));
            currentIncident.setAssignmentGroup(sysGroup);
            SysUser resolvedBy = getSysUserByIntegrationId(util.parseJson(json, levelOne, Field.ResolvedBy.get()));
            currentIncident.setResolvedBy(resolvedBy);
            currentIncident.setCloseNotes(util.parseJson(json, levelOne, Field.CloseNotes.get()));
            currentIncident.setCloseCode(util.parseJson(json, levelOne, Field.CloseCode.get()));
            currentIncident.setResolvedAt(util.parseJson(json, levelOne, Field.ResolvedAt.get()));
            currentIncident.setDescription(util.parseJson(json, levelOne, Field.Description.get()));
            currentIncident.setShortDescription(util.parseJson(json, levelOne, Field.ShortDescription.get()));
            currentIncident.setState(util.parseJson(json, levelOne, Field.State.get()));
            currentIncident.setIncidentState(util.parseJson(json, levelOne, Field.IncidentState.get()));
            currentIncident.setReasonPending(util.parseJson(json, levelOne, Field.ReasonPending.get()));
            currentIncident.setImpact(util.parseJson(json, levelOne,Field.Impact.get()));
            currentIncident.setUrgency(util.parseJson(json, levelOne,Field.Urgency.get()));
            currentIncident.setPriority(util.parseJson(json, levelOne, Field.Priority.get()));
            String knowledge = util.parseJson(json, levelOne, Field.Knowledge.get());
            currentIncident.setKnowledge(Boolean.parseBoolean(knowledge != null && knowledge != "" ? knowledge : "false"));
            incidentUpdated = incidentService.save(currentIncident);
            Incident incidentServiceNow = rest.putIncident(endPointSN.PutIncident(), currentIncident);
            //currentIncident.setState(addIncident.getState());
        } catch (DataAccessException e) {
            response.put("mensaje", Errors.dataAccessExceptionUpdate.get());
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", Messages.UpdateOK.get());
        response.put("incident", currentIncident);

        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }

    /*
        @PutMapping("/incidentSN")
        public ResponseEntity<?> update(@RequestBody String json) {
            Map<String, Object> response = new HashMap<>();
            Incident incident = new Incident();
            try {
                Gson gson = new Gson();
                IncidentRequest incidentRequest = gson.fromJson(json, IncidentRequest.class);

                String tagAction = app.CreateConsole();
                String tag = "[Incident] ";

                Incident exists = incidentService.findByIntegrationId(incidentRequest.getSys_id());
                if (exists != null) {
                    incident.setId(exists.getId());
                    incident.setScRequestParent(exists.getScRequestParent());
                    incident.setMaster(exists.getMaster());
                    tagAction = app.UpdateConsole();
                }


                incident.setSysUpdatedOn(incidentRequest.getSys_updated_on());
                incident.setUpdatedOn(util.getLocalDateTime(util.isNull(incidentRequest.getSys_updated_on())));
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
                incident.setCreatedOn(util.getLocalDateTime(util.isNull(incidentRequest.getSys_created_on())));
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





                if (util.hasData(incidentRequest.getCaused_by())) {
                    incident.setCausedBy(sysUserService.findByIntegrationId(incidentRequest.getCaused_by()));
                } else {
                    incident.setClosedBy(null);
                }

                if (util.hasData(incidentRequest.getClosed_by())) {
                    incident.setClosedBy(sysUserService.findByIntegrationId(incidentRequest.getClosed_by()));
                } else {
                    incident.setClosedBy(null);
                }


                if (util.hasData(incidentRequest.getReopened_by())) {
                    incident.setReopenedBy(sysUserService.findByIntegrationId(incidentRequest.getReopened_by()));
                } else {
                    incident.setReopenedBy(null);
                }

                if (util.hasData(incidentRequest.getAssigned_to())) {
                    incident.setAssignedTo(sysUserService.findByIntegrationId(incidentRequest.getAssigned_to()));
                } else {
                    incident.setAssignedTo(null);
                }


                if (util.hasData(incidentRequest.getU_solver_assigned_to())) {
                    incident.setSolverAssignedTo(sysUserService.findByIntegrationId(incidentRequest.getU_solver_assigned_to()));
                } else {
                    incident.setSolverAssignedTo(null);
                }


                if (util.hasData(incidentRequest.getU_solver_resolved_by())) {
                    incident.setSolverResolvedBy(sysUserService.findByIntegrationId(incidentRequest.getU_solver_resolved_by()));
                } else {
                    incident.setSolverResolvedBy(null);
                }


                if (util.hasData(incidentRequest.getOpened_by())) {
                    incident.setOpenedBy(sysUserService.findByIntegrationId(incidentRequest.getOpened_by()));
                } else {
                    incident.setOpenedBy(null);
                }


                if (util.hasData(incidentRequest.getTask_for())) {
                    incident.setTaskFor(sysUserService.findByIntegrationId(incidentRequest.getTask_for()));
                } else {
                    incident.setTaskFor(null);
                }


                if (util.hasData(incidentRequest.getBusiness_service())) {
                    incident.setBusinessService(ciServiceService.findByIntegrationId(incidentRequest.getBusiness_service()));
                } else {
                    incident.setBusinessService(null);
                }

                if (util.hasData(incidentRequest.getCaller_id())) {
                    incident.setCaller(sysUserService.findByIntegrationId(incidentRequest.getCaller_id()));
                } else {
                    incident.setCaller(null);
                }

                if (util.hasData(incidentRequest.getConfiguration_item())) {
                    incident.setConfigurationItem(configurationItemService.findByIntegrationId(incidentRequest.getConfiguration_item()));
                } else {
                    incident.setConfigurationItem(null);
                }

                if (util.hasData(incidentRequest.getAssignment_group())) {
                    incident.setAssignmentGroup(sysGroupService.findByIntegrationId(incidentRequest.getAssignment_group()));
                } else {
                    incident.setAssignmentGroup(null);
                }

                if (util.hasData(incidentRequest.getLocation())) {
                    incident.setLocation(locationService.findByIntegrationId(incidentRequest.getLocation()));
                } else {
                    incident.setLocation(null);
                }

                incidentService.save(incident);
                util.printData(tag, tagAction.concat(util.getFieldDisplay(incident)), util.getFieldDisplay(incident.getCompany()), util.getFieldDisplay(incident.getDomain()));

                if (util.hasData(incidentRequest.getIncident_parent()) || util.hasData(incidentRequest.getParent_incident())) {
                    String incidentParent = util.hasData(incidentRequest.getIncident_parent()) ? incidentRequest.getIncident_parent() : incidentRequest.getParent_incident();
                    Incident master = incidentService.findByIntegrationId(incidentParent);

                    if (!master.getMaster()) {
                        master.setMaster(true);
                        incidentService.save(master);
                    }
                }


            } catch (DataAccessException e) {
                System.out.println("error " + e.getMessage());
                response.put("mensaje", Errors.dataAccessExceptionUpdate.get());
                response.put("error", e.getMessage());
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            response.put("mensaje", Messages.UpdateOK.get());
            response.put("incident", incident);

            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
        }

        @Async*/
    @PutMapping("/incidentSN")
    public ResponseEntity<?> update(@RequestBody IncidentRequest incidentRequest) {
        Incident incident = new Incident();
        Map<String, Object> response = new HashMap<>();

        try {
            String tagAction = app.CreateConsole();
            String tag = "[Incident] ";

            Incident exists = incidentService.findByIntegrationId(incidentRequest.getSys_id());
            if (exists != null) {
                incident.setId(exists.getId());
                incident.setScRequestParent(exists.getScRequestParent());
                incident.setMaster(exists.getMaster());
                tagAction = app.UpdateConsole();
            }


            incident.setSysUpdatedOn(incidentRequest.getSys_updated_on());
            incident.setUpdatedOn(util.getLocalDateTime(util.isNull(incidentRequest.getSys_updated_on())));
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
            incident.setCreatedOn(util.getLocalDateTime(util.isNull(incidentRequest.getSys_created_on())));
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


            if (util.hasData(incidentRequest.getIncident_parent()) || util.hasData(incidentRequest.getParent_incident())) {
                String incidentParent = util.hasData(incidentRequest.getIncident_parent()) ? incidentRequest.getIncident_parent() : incidentRequest.getParent_incident();
                Incident master = incidentService.findByIntegrationId(incidentParent);

                if (!master.getMaster()) {
                    master.setMaster(true);
                    incidentService.save(master);
                }
            }


            if (util.hasData(incidentRequest.getCaused_by())) {
                incident.setCausedBy(sysUserService.findByIntegrationId(incidentRequest.getCaused_by()));
            } else {
                incident.setClosedBy(null);
            }

            if (util.hasData(incidentRequest.getClosed_by())) {
                incident.setClosedBy(sysUserService.findByIntegrationId(incidentRequest.getClosed_by()));
            } else {
                incident.setClosedBy(null);
            }


            if (util.hasData(incidentRequest.getReopened_by())) {
                incident.setReopenedBy(sysUserService.findByIntegrationId(incidentRequest.getReopened_by()));
            } else {
                incident.setReopenedBy(null);
            }

            if (util.hasData(incidentRequest.getAssigned_to())) {
                incident.setAssignedTo(sysUserService.findByIntegrationId(incidentRequest.getAssigned_to()));
            } else {
                incident.setAssignedTo(null);
            }


            if (util.hasData(incidentRequest.getU_solver_assigned_to())) {
                incident.setSolverAssignedTo(sysUserService.findByIntegrationId(incidentRequest.getU_solver_assigned_to()));
            } else {
                incident.setSolverAssignedTo(null);
            }


            if (util.hasData(incidentRequest.getU_solver_resolved_by())) {
                incident.setSolverResolvedBy(sysUserService.findByIntegrationId(incidentRequest.getU_solver_resolved_by()));
            } else {
                incident.setSolverResolvedBy(null);
            }


            if (util.hasData(incidentRequest.getOpened_by())) {
                incident.setOpenedBy(sysUserService.findByIntegrationId(incidentRequest.getOpened_by()));
            } else {
                incident.setOpenedBy(null);
            }


            if (util.hasData(incidentRequest.getTask_for())) {
                incident.setTaskFor(sysUserService.findByIntegrationId(incidentRequest.getTask_for()));
            } else {
                incident.setTaskFor(null);
            }


            if (util.hasData(incidentRequest.getBusiness_service())) {
                incident.setBusinessService(ciServiceService.findByIntegrationId(incidentRequest.getBusiness_service()));
            } else {
                incident.setBusinessService(null);
            }

            if (util.hasData(incidentRequest.getCaller_id())) {
                incident.setCaller(sysUserService.findByIntegrationId(incidentRequest.getCaller_id()));
            } else {
                incident.setCaller(null);
            }

            if (util.hasData(incidentRequest.getConfiguration_item())) {
                incident.setConfigurationItem(configurationItemService.findByIntegrationId(incidentRequest.getConfiguration_item()));
            } else {
                incident.setConfigurationItem(null);
            }


            if (util.hasData(incidentRequest.getAssignment_group())) {
                incident.setAssignmentGroup(sysGroupService.findByIntegrationId(incidentRequest.getAssignment_group()));
            } else {
                incident.setAssignmentGroup(null);
            }

            if (util.hasData(incidentRequest.getLocation())) {
                incident.setLocation(locationService.findByIntegrationId(incidentRequest.getLocation()));
            } else {
                incident.setLocation(null);
            }


            incidentService.save(incident);

            util.printData(tag, tagAction.concat(util.getFieldDisplay(incident)), util.getFieldDisplay(incident.getCompany()), util.getFieldDisplay(incident.getDomain()));


        } catch (DataAccessException e) {
            System.out.println("error " + e.getMessage());
            response.put("mensaje", Errors.dataAccessExceptionUpdate.get());
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", Messages.UpdateOK.get());
        response.put("incident", incident);

        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }

    /*
        @PutMapping("/incidentDescriptionSN")
        public ResponseEntity<?> update(@RequestBody String json) {
            System.out.println("incidentDescriptionSN");
            System.out.println(json);
            Map<String, Object> response = new HashMap<>();
            Incident incident = new Incident();
            try {

                incident = incidentService.findByIntegrationId(util.parseJson(json, "sys_id"));
                incident.setDescription(util.parseJson(json, "description"));
                incident.setShortDescription(util.parseJson(json, "short_description"));
                Incident currentIncident = incidentService.save(incident);
                String tag = "[Incident]";
                String tagAction = "Update";
                util.printData(tag, tagAction);

            } catch (DataAccessException e) {
                response.put("mensaje", Errors.dataAccessExceptionUpdate.get());
                response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            response.put("mensaje", Messages.UpdateOK.get());
            response.put("incident", incident);

            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
        }
    */
    @PutMapping("/incidentDelete")
    public ResponseEntity<?> delete(String element, String integrationId) {
        System.out.println("element: ".concat(util.isNull(element)));
        System.out.println("integrationId: ".concat(util.isNull(integrationId)));
        Map<String, Object> response = new HashMap<>();


        try {

            if (SnTable.TaskSla.get().equals(element)) {
                TaskSla taskSla = taskSlaService.findByIntegrationId(integrationId);
                if (taskSla != null) {
                    taskSla.setActive(false);
                    taskSlaService.save(taskSla);
                    System.out.println("[TaskSla] (Disabled) ".concat(taskSla.getIntegrationId()));
                } else
                    System.out.println("[TaskSla] (Not exist) ".concat(integrationId));
            }
           /* Incident incident = incidentService.findByIntegrationId(integrationId);
            journalService.findByIncident(incident).forEach(journal -> {
                journal.setActive(false);
                journalService.save(journal);
                System.out.println("[Journal] (Delete) ".concat(journal.getName()));
            });
            attachmentService.findAttachmentsByIncident(incident.getId()).forEach(attachment -> {
                attachmentService.delete(attachment.getId());
                System.out.println("[Attachment] (Delete) ".concat(attachment.getFileName()));
            });
            incidentService.delete(incident.getId());
            System.out.println("[Incident] (Delete) ".concat(incident.getNumber()));*/
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
        System.out.println(app.Start());
        APIResponse apiResponse = null;
        List<Incident> incidents = new ArrayList<>();
        String[] sparmOffSets = util.offSets500000();
        long startTime = 0;
        long endTime = 0;
        String tag = "[Incident] ";
        try {
            startTime = System.currentTimeMillis();
            final int[] count = {1};
            for (String sparmOffSet : sparmOffSets) {
                String result = rest.responseByEndPoint(endPointSN.IncidentByCompany().concat(sparmOffSet));
                System.out.println(tag.concat("(".concat(endPointSN.IncidentByCompany().concat(sparmOffSet)).concat(")")));
                ObjectMapper mapper = new ObjectMapper();
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                JSONParser parser = new JSONParser();
                JSONObject resultJson = new JSONObject();
                JSONArray ListSnIncidentJson = new JSONArray();

                resultJson = (JSONObject) parser.parse(result);
                if (resultJson.get("result") != null)
                    ListSnIncidentJson = (JSONArray) parser.parse(resultJson.get("result").toString());

                ListSnIncidentJson.stream().forEach(snIncidentJson -> {
                    String tagAction = app.CreateConsole();
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

                    //objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    IncidentSolver incidentSolver = new IncidentSolver();
                    try {
                        // if (count[0] == 13330) {
                        incidentSolver = objectMapper.readValue(snIncidentJson.toString(), IncidentSolver.class);

                        Incident incident = new Incident();
                        incident.setSysUpdatedOn(util.isNull(incidentSolver.getSys_updated_on()));
                        incident.setUpdatedOn(util.getLocalDateTime(util.isNull(incidentSolver.getSys_updated_on())));
                        incident.setNumber(util.isNull(incidentSolver.getNumber()));
                        incident.setState(util.isNull(incidentSolver.getState()));
                        incident.setSysCreatedBy(util.isNull(incidentSolver.getSys_created_by()));
                        incident.setImpact(util.isNull(incidentSolver.getImpact()));
                        incident.setReasonPending(util.isNull(incidentSolver.getU_sbk_pendiente()));
                        incident.setActive(incidentSolver.isActive());
                        incident.setKnowledge(incidentSolver.getKnowledge());
                        incident.setPriority(util.isNull(incidentSolver.getPriority()));
                        incident.setBusinessDuration(util.isNull(incidentSolver.getBusiness_duration()));
                        incident.setShortDescription(util.isNull(incidentSolver.getShort_description()));
                        incident.setCorrelationDisplay(util.isNull(incidentSolver.getCorrelation_display()));
                        incident.setNotify(util.isNull(incidentSolver.getNotify()));
                        incident.setReassignmentCount(util.isNull(incidentSolver.getReassignment_count()));
                        incident.setUponApproval(util.isNull(incidentSolver.getUpon_approval()));
                        incident.setCorrelationId(util.isNull(incidentSolver.getCorrelation_id()));
                        incident.setMadeSla(incidentSolver.getMade_sla());
                        incident.setSysUpdatedBy(util.isNull(incidentSolver.getSys_updated_by()));
                        incident.setSysCreatedOn(util.isNull(incidentSolver.getSys_created_on()));
                        incident.setCreatedOn(util.getLocalDateTime(util.isNull(incidentSolver.getSys_created_on())));
                        incident.setClosedAt(util.isNull(incidentSolver.getClosed_at()));
                        incident.setClosed(util.getLocalDateTime(util.isNull(incidentSolver.getClosed_at())));
                        incident.setOpenedAt(util.isNull(incidentSolver.getOpened_at()));
                        incident.setOpened(util.getLocalDateTime(util.isNull(incidentSolver.getOpened_at())));
                        incident.setReopenedTime(util.isNull(incidentSolver.getReopened_time()));
                        incident.setResolvedAt(util.isNull(incidentSolver.getResolved_at()));
                        incident.setResolved(util.getLocalDateTime(util.isNull(incidentSolver.getResolved_at())));
                        incident.setSubcategory(util.isNull(incidentSolver.getSubcategory()));
                        incident.setCloseCode(util.isNull(incidentSolver.getClose_code()));
                        incident.setDescription(util.isNull(incidentSolver.getDescription()));
                        incident.setCalendarDuration(util.isNull(incidentSolver.getCalendar_duration()));
                        incident.setCloseNotes(util.isNull(incidentSolver.getClose_notes()));
                        incident.setIntegrationId(util.isNull(incidentSolver.getSys_id()));
                        incident.setContactType(util.isNull(incidentSolver.getContact_type()));
                        incident.setIncidentState(util.isNull(incidentSolver.getIncident_state()));
                        incident.setUrgency(util.isNull(incidentSolver.getUrgency()));
                        incident.setSeverity(util.isNull(incidentSolver.getSeverity()));
                        incident.setApproval(util.isNull(incidentSolver.getApproval()));
                        incident.setSysModCount(util.isNull(incidentSolver.getSys_mod_count()));
                        incident.setReopenCount(util.isNull(incidentSolver.getReopen_count()));
                        incident.setCategory(util.isNull(incidentSolver.getCategory()));
                        Choice category = choiceService.findByValueAndElementAndName(incident.getCategory(), Field.Category.get(), SnTable.Incident.get());
                        if (category != null) {
                            incident.setCategory(category.getLabel());
                        }
                        incident.setServiceLevel(util.isNull(incidentSolver.getU_service()));
                        incident.setAmbiteLevel(util.isNull(incidentSolver.getU_ambite()));
                        incident.setPlatformLevel(util.isNull(incidentSolver.getU_platform()));
                        incident.setSpecificationLevel(util.isNull(incidentSolver.getU_specification()));
                        incident.setIncidentParent(util.getIdByJson((JSONObject) snIncidentJson, "parent_incident", app.Value()));

                        incident.setSolverFlagAssignedTo(incidentSolver.getU_solver_flag_assigned_to());
                        incident.setSolverFlagResolvedBy(incidentSolver.getU_solver_flag_resolved_by());
                        incident.setDelete(false);

                        if (util.hasData(incident.getIncidentParent())) {
                            Incident master = incidentService.findByIntegrationId(incident.getIncidentParent());
                            if (!master.getMaster()) {
                                master.setMaster(true);
                                incidentService.save(master);
                            }
                        }
                        String element = util.getIdByJson((JSONObject) snIncidentJson, "parent", app.Value());
                        if (element != "") {
                            ScRequest scRequest = scRequestService.findByIntegrationId(element);
                            if (scRequest != null)
                                incident.setScRequestParent(element);
                            else {
                                ScRequestItem scRequestItem = scRequestItemService.findByIntegrationId(element);
                                if (scRequestItem != null)
                                    incident.setScRequestItemParent(element);
                            }
                        }

                        Domain domain = getDomainByIntegrationId((JSONObject) snIncidentJson, SnTable.Domain.get(), app.Value());
                        //if (domain != null)
                        incident.setDomain(domain);

                        Company company = getCompanyByIntegrationId((JSONObject) snIncidentJson, SnTable.Company.get(), app.Value());
                        //if (company != null)
                        incident.setCompany(company);

                        SysUser causedBy = getSysUserByIntegrationId((JSONObject) snIncidentJson, "caused_by", app.Value());
                        //if (causedBy != null)
                        incident.setCausedBy(causedBy);

                        SysUser closedBy = getSysUserByIntegrationId((JSONObject) snIncidentJson, "closed_by", app.Value());
                        // if (closedBy != null)
                        incident.setClosedBy(closedBy);

                        SysUser reopenedBy = getSysUserByIntegrationId((JSONObject) snIncidentJson, "reopened_by", app.Value());
                        // if (reopenedBy != null)
                        incident.setReopenedBy(reopenedBy);

                        SysUser assignedTo = getSysUserByIntegrationId((JSONObject) snIncidentJson, "u_solver_assigned_to", app.Value());
                        //if (assignedTo != null)
                        incident.setAssignedTo(assignedTo);

                        SysUser solverAssignedTo = getSysUserByIntegrationId((JSONObject) snIncidentJson, "u_solver_assigned_to", app.Value());
                        //if (solverAssignedTo != null)
                        incident.setSolverAssignedTo(solverAssignedTo);

                        SysUser resolvedBy = getSysUserByIntegrationId((JSONObject) snIncidentJson, "resolved_by", app.Value());
                        //if (resolvedBy != null)
                        incident.setResolvedBy(resolvedBy);

                        SysUser solverResolvedBy = getSysUserByIntegrationId((JSONObject) snIncidentJson, "u_solver_resolved_by", app.Value());
                        //if (solverResolvedBy != null)
                        incident.setSolverResolvedBy(solverResolvedBy);

                        SysUser openedBy = getSysUserByIntegrationId((JSONObject) snIncidentJson, "opened_by", app.Value());
                        //if (openedBy != null)
                        incident.setOpenedBy(openedBy);

                        SysUser taskFor = getSysUserByIntegrationId((JSONObject) snIncidentJson, "task_for", app.Value());
                        //if (taskFor != null)
                        incident.setTaskFor(taskFor);

                        CiService businessService = getCiServiceByIntegrationId((JSONObject) snIncidentJson, "business_service", app.Value());
                        //if (businessService != null)
                        incident.setBusinessService(businessService);

                        SysUser caller = getSysUserByIntegrationId((JSONObject) snIncidentJson, "caller_id", app.Value());
                        //if (caller != null)
                        incident.setCaller(caller);

                        ConfigurationItem configurationItem = getConfigurationItemByIntegrationId((JSONObject) snIncidentJson, "cmdb_ci", app.Value());
                        //if (configurationItem != null)
                        incident.setConfigurationItem(configurationItem);

                        SysGroup sysGroup = getSysGroupByIntegrationId((JSONObject) snIncidentJson, "assignment_group", app.Value());
                        //if (sysGroup != null)
                        incident.setAssignmentGroup(sysGroup);


                        Location location = getLocationByIntegrationId((JSONObject) snIncidentJson, "location", app.Value());
                        incident.setLocation(location);

                        Incident exists = incidentService.findByIntegrationId(incident.getIntegrationId());
                        if (exists != null) {
                            incident.setId(exists.getId());
                            incident.setScRequestParent(exists.getScRequestParent());
                            incident.setMaster(exists.getMaster());
                            tagAction = app.UpdateConsole();
                        }


                        incidents.add(incidentService.save(incident));
                        util.printData(tag, count[0], tagAction.concat(util.getFieldDisplay(incident)), util.getFieldDisplay(company), util.getFieldDisplay(domain));
                        //  }

                        count[0] = count[0] + 1;
                    } catch (Exception e) {
                        System.out.println(tag.concat("Exception (I) (").concat(String.valueOf(count[0])).concat(") ").concat(String.valueOf(e)));
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
        return incidents;
    }

    @GetMapping("/incidenBySolverAndQuery")
    public List<Incident> findByCompany(String query) {
        //String pass = passwordEncoder.encode("Josemora#01");
        System.out.println(app.Start());
        APIResponse apiResponse = null;
        List<Incident> incidents = new ArrayList<>();
        String[] sparmOffSets = util.offSets50000();
        long startTime = 0;
        long endTime = 0;
        String tag = "[Incident] ";
        // ObjectMapper objectMapper = new ObjectMapper();
        // objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        try {
            startTime = System.currentTimeMillis();
            final int[] count = {1};
            for (String sparmOffSet : sparmOffSets) {
                String result = rest.responseByEndPoint(endPointSN.IncidentByQuery().replace("QUERY", query).concat(sparmOffSet));
                System.out.println(tag.concat("(".concat(endPointSN.IncidentByQuery().replace("QUERY", query).concat(sparmOffSet)).concat(")")));

                JSONParser parser = new JSONParser();
                JSONObject resultJson = new JSONObject();
                JSONArray ListSnIncidentJson = new JSONArray();

                resultJson = (JSONObject) parser.parse(result);
                if (resultJson.get("result") != null)
                    ListSnIncidentJson = (JSONArray) parser.parse(resultJson.get("result").toString());

                ListSnIncidentJson.stream().forEach(snIncidentJson -> {
                    String tagAction = app.CreateConsole();

                    //objectMapper = objectMapper.configure(JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER, true);

                    //objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    IncidentSolver incidentSolver = new IncidentSolver();
                    try {
                        Gson gson = new Gson();
                        incidentSolver = gson.fromJson(snIncidentJson.toString(), IncidentSolver.class);


                        //JsonObject convertedObject = new Gson().fromJson(snIncidentJson.toString(), JsonObject.class);
                        // System.out.println(convertedObject.toString());
                        //incidentSolver = objectMapper.readValue(snIncidentJson.toString(), IncidentSolver.class);
                        //incidentSolver = mapper.reader().forType(IncidentSolver.class).readValue(snIncidentJson.toString());
                        Incident incident = new Incident();
                        incident.setSysUpdatedOn(util.isNull(incidentSolver.getSys_updated_on()));
                        incident.setUpdatedOn(util.getLocalDateTime(util.isNull(incidentSolver.getSys_updated_on())));
                        incident.setNumber(util.isNull(incidentSolver.getNumber()));
                        incident.setState(util.isNull(incidentSolver.getState()));
                        incident.setSysCreatedBy(util.isNull(incidentSolver.getSys_created_by()));
                        incident.setImpact(util.isNull(incidentSolver.getImpact()));
                        incident.setActive(incidentSolver.isActive());
                        incident.setKnowledge(incidentSolver.getKnowledge());
                        incident.setPriority(util.isNull(incidentSolver.getPriority()));
                        incident.setBusinessDuration(util.isNull(incidentSolver.getBusiness_duration()));
                        incident.setShortDescription(util.isNull(incidentSolver.getShort_description()));
                        incident.setCorrelationDisplay(util.isNull(incidentSolver.getCorrelation_display()));
                        incident.setNotify(util.isNull(incidentSolver.getNotify()));
                        incident.setReassignmentCount(util.isNull(incidentSolver.getReassignment_count()));
                        incident.setUponApproval(util.isNull(incidentSolver.getUpon_approval()));
                        incident.setCorrelationId(util.isNull(incidentSolver.getCorrelation_id()));
                        incident.setMadeSla(incidentSolver.getMade_sla());
                        incident.setSysUpdatedBy(util.isNull(incidentSolver.getSys_updated_by()));
                        incident.setSysCreatedOn(util.isNull(incidentSolver.getSys_created_on()));
                        if (!util.isNull(incidentSolver.getSys_created_on()).equals(""))
                            incident.setCreatedOn(util.getLocalDateTime(util.isNull(incidentSolver.getSys_created_on())));
                        incident.setClosedAt(util.isNull(incidentSolver.getClosed_at()));
                        if (!util.isNull(incidentSolver.getClosed_at()).equals(""))
                            incident.setClosed(util.getLocalDateTime(util.isNull(incidentSolver.getClosed_at())));
                        incident.setOpenedAt(util.isNull(incidentSolver.getOpened_at()));
                        if (!util.isNull(incidentSolver.getOpened_at()).equals(""))
                            incident.setOpened(util.getLocalDateTime(util.isNull(incidentSolver.getOpened_at())));
                        incident.setReopenedTime(util.isNull(incidentSolver.getReopened_time()));
                        incident.setResolvedAt(util.isNull(incidentSolver.getResolved_at()));
                        if (!util.isNull(incidentSolver.getResolved_at()).equals(""))
                            incident.setResolved(util.getLocalDateTime(util.isNull(incidentSolver.getResolved_at())));
                        incident.setSubcategory(util.isNull(incidentSolver.getSubcategory()));
                        incident.setCloseCode(util.isNull(incidentSolver.getClose_code()));
                        incident.setDescription(util.isNull(incidentSolver.getDescription()));
                        incident.setCalendarDuration(util.isNull(incidentSolver.getCalendar_duration()));
                        incident.setCloseNotes(util.isNull(incidentSolver.getClose_notes()));
                        incident.setIntegrationId(util.isNull(incidentSolver.getSys_id()));
                        incident.setContactType(util.isNull(incidentSolver.getContact_type()));
                        incident.setIncidentState(util.isNull(incidentSolver.getIncident_state()));
                        incident.setUrgency(util.isNull(incidentSolver.getUrgency()));
                        incident.setSeverity(util.isNull(incidentSolver.getSeverity()));
                        incident.setApproval(util.isNull(incidentSolver.getApproval()));
                        incident.setSysModCount(util.isNull(incidentSolver.getSys_mod_count()));
                        incident.setReopenCount(util.isNull(incidentSolver.getReopen_count()));
                        incident.setCategory(util.isNull(incidentSolver.getCategory()));
                        Choice category = choiceService.findByValueAndElementAndName(incident.getCategory(), Field.Category.get(), SnTable.Incident.get());
                        if (category != null) {
                            incident.setCategory(category.getLabel());
                        }
                        incident.setServiceLevel(util.isNull(incidentSolver.getU_service()));
                        incident.setAmbiteLevel(util.isNull(incidentSolver.getU_ambite()));
                        incident.setPlatformLevel(util.isNull(incidentSolver.getU_platform()));
                        incident.setSpecificationLevel(util.isNull(incidentSolver.getU_specification()));
                        incident.setIncidentParent(util.getIdByJson((JSONObject) snIncidentJson, "parent_incident", app.Value()));

                        incident.setSolverFlagAssignedTo(incidentSolver.getU_solver_flag_assigned_to());
                        incident.setSolverFlagResolvedBy(incidentSolver.getU_solver_flag_resolved_by());
                        incident.setReasonPending(util.isNull(incidentSolver.getU_sbk_pendiente()));
                        incident.setDelete(false);

                        if (util.hasData(incident.getIncidentParent())) {
                            Incident master = incidentService.findByIntegrationId(incident.getIncidentParent());
                            if (!master.getMaster()) {
                                master.setMaster(true);
                                incidentService.save(master);
                            }
                        }
                        String element = util.getIdByJson((JSONObject) snIncidentJson, "parent", app.Value());
                        if (element != "") {
                            ScRequest scRequest = scRequestService.findByIntegrationId(element);
                            if (scRequest != null)
                                incident.setScRequestParent(element);
                            else {
                                ScRequestItem scRequestItem = scRequestItemService.findByIntegrationId(element);
                                if (scRequestItem != null)
                                    incident.setScRequestItemParent(element);
                            }
                        }

                        Domain domain = getDomainByIntegrationId((JSONObject) snIncidentJson, SnTable.Domain.get(), app.Value());
                        //if (domain != null)
                        incident.setDomain(domain);

                        Company company = getCompanyByIntegrationId((JSONObject) snIncidentJson, SnTable.Company.get(), app.Value());
                        //if (company != null)
                        incident.setCompany(company);

                        SysUser causedBy = getSysUserByIntegrationId((JSONObject) snIncidentJson, "caused_by", app.Value());
                        //if (causedBy != null)
                        incident.setCausedBy(causedBy);

                        SysUser closedBy = getSysUserByIntegrationId((JSONObject) snIncidentJson, "closed_by", app.Value());
                        // if (closedBy != null)
                        incident.setClosedBy(closedBy);

                        SysUser reopenedBy = getSysUserByIntegrationId((JSONObject) snIncidentJson, "reopened_by", app.Value());
                        // if (reopenedBy != null)
                        incident.setReopenedBy(reopenedBy);

                        SysUser assignedTo = getSysUserByIntegrationId((JSONObject) snIncidentJson, "u_solver_assigned_to", app.Value());
                        //if (assignedTo != null)
                        incident.setAssignedTo(assignedTo);

                        SysUser solverAssignedTo = getSysUserByIntegrationId((JSONObject) snIncidentJson, "u_solver_assigned_to", app.Value());
                        //if (solverAssignedTo != null)
                        incident.setSolverAssignedTo(solverAssignedTo);

                        SysUser resolvedBy = getSysUserByIntegrationId((JSONObject) snIncidentJson, "resolved_by", app.Value());
                        //if (resolvedBy != null)
                        incident.setResolvedBy(resolvedBy);

                        SysUser solverResolvedBy = getSysUserByIntegrationId((JSONObject) snIncidentJson, "u_solver_resolved_by", app.Value());
                        //if (solverResolvedBy != null)
                        incident.setSolverResolvedBy(solverResolvedBy);

                        SysUser openedBy = getSysUserByIntegrationId((JSONObject) snIncidentJson, "opened_by", app.Value());
                        //if (openedBy != null)
                        incident.setOpenedBy(openedBy);

                        SysUser taskFor = getSysUserByIntegrationId((JSONObject) snIncidentJson, "task_for", app.Value());
                        //if (taskFor != null)
                        incident.setTaskFor(taskFor);

                        CiService businessService = getCiServiceByIntegrationId((JSONObject) snIncidentJson, "business_service", app.Value());
                        //if (businessService != null)
                        incident.setBusinessService(businessService);

                        SysUser caller = getSysUserByIntegrationId((JSONObject) snIncidentJson, "caller_id", app.Value());
                        //if (caller != null)
                        incident.setCaller(caller);

                        ConfigurationItem configurationItem = getConfigurationItemByIntegrationId((JSONObject) snIncidentJson, "cmdb_ci", app.Value());
                        //if (configurationItem != null)
                        incident.setConfigurationItem(configurationItem);

                        SysGroup sysGroup = getSysGroupByIntegrationId((JSONObject) snIncidentJson, "assignment_group", app.Value());
                        //if (sysGroup != null)
                        incident.setAssignmentGroup(sysGroup);


                        Location location = getLocationByIntegrationId((JSONObject) snIncidentJson, "location", app.Value());
                        incident.setLocation(location);

                        Incident exists = incidentService.findByIntegrationId(incident.getIntegrationId());
                        if (exists != null) {
                            incident.setId(exists.getId());
                            incident.setScRequestParent(exists.getScRequestParent());
                            incident.setMaster(exists.getMaster());
                            tagAction = app.UpdateConsole();
                        }


                        incidents.add(incidentService.save(incident));
                        util.printData(tag, count[0], tagAction.concat(util.getFieldDisplay(incident)), util.getFieldDisplay(company), util.getFieldDisplay(domain));
                        //  }

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
        return incidents;
    }

    @GetMapping("/incidenBySolverAndQueryCreate")
    public List<Incident> findByCompany(String query, boolean flagCreate) {
        System.out.println(app.Start());
        APIResponse apiResponse = null;
        List<Incident> incidents = new ArrayList<>();
        String[] sparmOffSets = util.offSets50000();
        long startTime = 0;
        long endTime = 0;
        String tag = "[Incident] ";

        try {
            startTime = System.currentTimeMillis();
            final int[] count = {1};
            for (String sparmOffSet : sparmOffSets) {
                String result = rest.responseByEndPoint(endPointSN.IncidentByQuery().replace("QUERY", query).concat(sparmOffSet));
                System.out.println(tag.concat("(".concat(endPointSN.IncidentByQuery().replace("QUERY", query).concat(sparmOffSet)).concat(")")));

                JSONParser parser = new JSONParser();
                JSONObject resultJson;
                JSONArray ListSnIncidentJson = new JSONArray();

                resultJson = (JSONObject) parser.parse(result);
                if (resultJson.get("result") != null)
                    ListSnIncidentJson = (JSONArray) parser.parse(resultJson.get("result").toString());

                ListSnIncidentJson.stream().forEach(snIncidentJson -> {
                    String tagAction = app.CreateConsole();
                    IncidentSolver incidentSolver;
                    try {
                        Gson gson = new Gson();
                        incidentSolver = gson.fromJson(snIncidentJson.toString(), IncidentSolver.class);
                        Incident incident = new Incident();
                        Incident exists = incidentService.findByIntegrationId(incident.getIntegrationId());

                        if (exists != null) {
                            incident.setId(exists.getId());
                            incident.setScRequestParent(exists.getScRequestParent());
                            incident.setMaster(exists.getMaster());
                            tagAction = app.UpdateConsole();
                        } else {


                            incident.setSysUpdatedOn(util.isNull(incidentSolver.getSys_updated_on()));
                            incident.setUpdatedOn(util.getLocalDateTime(util.isNull(incidentSolver.getSys_updated_on())));
                            incident.setNumber(util.isNull(incidentSolver.getNumber()));
                            incident.setState(util.isNull(incidentSolver.getState()));
                            incident.setSysCreatedBy(util.isNull(incidentSolver.getSys_created_by()));
                            incident.setImpact(util.isNull(incidentSolver.getImpact()));
                            incident.setActive(incidentSolver.isActive());
                            incident.setKnowledge(incidentSolver.getKnowledge());
                            incident.setPriority(util.isNull(incidentSolver.getPriority()));
                            incident.setBusinessDuration(util.isNull(incidentSolver.getBusiness_duration()));
                            incident.setShortDescription(util.isNull(incidentSolver.getShort_description()));
                            incident.setCorrelationDisplay(util.isNull(incidentSolver.getCorrelation_display()));
                            incident.setNotify(util.isNull(incidentSolver.getNotify()));
                            incident.setReassignmentCount(util.isNull(incidentSolver.getReassignment_count()));
                            incident.setUponApproval(util.isNull(incidentSolver.getUpon_approval()));
                            incident.setCorrelationId(util.isNull(incidentSolver.getCorrelation_id()));
                            incident.setMadeSla(incidentSolver.getMade_sla());
                            incident.setSysUpdatedBy(util.isNull(incidentSolver.getSys_updated_by()));
                            incident.setSysCreatedOn(util.isNull(incidentSolver.getSys_created_on()));
                            if (!util.isNull(incidentSolver.getSys_created_on()).equals(""))
                                incident.setCreatedOn(util.getLocalDateTime(util.isNull(incidentSolver.getSys_created_on())));
                            incident.setClosedAt(util.isNull(incidentSolver.getClosed_at()));
                            if (!util.isNull(incidentSolver.getClosed_at()).equals(""))
                                incident.setClosed(util.getLocalDateTime(util.isNull(incidentSolver.getClosed_at())));
                            incident.setOpenedAt(util.isNull(incidentSolver.getOpened_at()));
                            if (!util.isNull(incidentSolver.getOpened_at()).equals(""))
                                incident.setOpened(util.getLocalDateTime(util.isNull(incidentSolver.getOpened_at())));
                            incident.setReopenedTime(util.isNull(incidentSolver.getReopened_time()));
                            incident.setResolvedAt(util.isNull(incidentSolver.getResolved_at()));
                            if (!util.isNull(incidentSolver.getResolved_at()).equals(""))
                                incident.setResolved(util.getLocalDateTime(util.isNull(incidentSolver.getResolved_at())));
                            incident.setSubcategory(util.isNull(incidentSolver.getSubcategory()));
                            incident.setCloseCode(util.isNull(incidentSolver.getClose_code()));
                            incident.setDescription(util.isNull(incidentSolver.getDescription()));
                            incident.setCalendarDuration(util.isNull(incidentSolver.getCalendar_duration()));
                            incident.setCloseNotes(util.isNull(incidentSolver.getClose_notes()));
                            incident.setIntegrationId(util.isNull(incidentSolver.getSys_id()));
                            incident.setContactType(util.isNull(incidentSolver.getContact_type()));
                            incident.setIncidentState(util.isNull(incidentSolver.getIncident_state()));
                            incident.setUrgency(util.isNull(incidentSolver.getUrgency()));
                            incident.setSeverity(util.isNull(incidentSolver.getSeverity()));
                            incident.setApproval(util.isNull(incidentSolver.getApproval()));
                            incident.setSysModCount(util.isNull(incidentSolver.getSys_mod_count()));
                            incident.setReopenCount(util.isNull(incidentSolver.getReopen_count()));
                            incident.setCategory(util.isNull(incidentSolver.getCategory()));
                            Choice category = choiceService.findByValueAndElementAndName(incident.getCategory(), Field.Category.get(), SnTable.Incident.get());
                            if (category != null) {
                                incident.setCategory(category.getLabel());
                            }
                            incident.setServiceLevel(util.isNull(incidentSolver.getU_service()));
                            incident.setAmbiteLevel(util.isNull(incidentSolver.getU_ambite()));
                            incident.setPlatformLevel(util.isNull(incidentSolver.getU_platform()));
                            incident.setSpecificationLevel(util.isNull(incidentSolver.getU_specification()));
                            incident.setIncidentParent(util.getIdByJson((JSONObject) snIncidentJson, "parent_incident", app.Value()));

                            incident.setSolverFlagAssignedTo(incidentSolver.getU_solver_flag_assigned_to());
                            incident.setSolverFlagResolvedBy(incidentSolver.getU_solver_flag_resolved_by());
                            incident.setReasonPending(util.isNull(incidentSolver.getU_sbk_pendiente()));
                            incident.setDelete(false);

                            if (util.hasData(incident.getIncidentParent())) {
                                Incident master = incidentService.findByIntegrationId(incident.getIncidentParent());
                                if (!master.getMaster()) {
                                    master.setMaster(true);
                                    incidentService.save(master);
                                }
                            }
                            String element = util.getIdByJson((JSONObject) snIncidentJson, "parent", app.Value());
                            if (element != "") {
                                ScRequest scRequest = scRequestService.findByIntegrationId(element);
                                if (scRequest != null)
                                    incident.setScRequestParent(element);
                                else {
                                    ScRequestItem scRequestItem = scRequestItemService.findByIntegrationId(element);
                                    if (scRequestItem != null)
                                        incident.setScRequestItemParent(element);
                                }
                            }

                            Domain domain = getDomainByIntegrationId((JSONObject) snIncidentJson, SnTable.Domain.get(), app.Value());

                            incident.setDomain(domain);

                            Company company = getCompanyByIntegrationId((JSONObject) snIncidentJson, SnTable.Company.get(), app.Value());

                            incident.setCompany(company);

                            SysUser causedBy = getSysUserByIntegrationId((JSONObject) snIncidentJson, "caused_by", app.Value());

                            incident.setCausedBy(causedBy);

                            SysUser closedBy = getSysUserByIntegrationId((JSONObject) snIncidentJson, "closed_by", app.Value());

                            incident.setClosedBy(closedBy);

                            SysUser reopenedBy = getSysUserByIntegrationId((JSONObject) snIncidentJson, "reopened_by", app.Value());

                            incident.setReopenedBy(reopenedBy);

                            SysUser assignedTo = getSysUserByIntegrationId((JSONObject) snIncidentJson, "u_solver_assigned_to", app.Value());

                            incident.setAssignedTo(assignedTo);

                            SysUser solverAssignedTo = getSysUserByIntegrationId((JSONObject) snIncidentJson, "u_solver_assigned_to", app.Value());
                            incident.setSolverAssignedTo(solverAssignedTo);

                            SysUser resolvedBy = getSysUserByIntegrationId((JSONObject) snIncidentJson, "resolved_by", app.Value());

                            incident.setResolvedBy(resolvedBy);

                            SysUser solverResolvedBy = getSysUserByIntegrationId((JSONObject) snIncidentJson, "u_solver_resolved_by", app.Value());

                            incident.setSolverResolvedBy(solverResolvedBy);

                            SysUser openedBy = getSysUserByIntegrationId((JSONObject) snIncidentJson, "opened_by", app.Value());

                            incident.setOpenedBy(openedBy);

                            SysUser taskFor = getSysUserByIntegrationId((JSONObject) snIncidentJson, "task_for", app.Value());

                            incident.setTaskFor(taskFor);

                            CiService businessService = getCiServiceByIntegrationId((JSONObject) snIncidentJson, "business_service", app.Value());

                            incident.setBusinessService(businessService);

                            SysUser caller = getSysUserByIntegrationId((JSONObject) snIncidentJson, "caller_id", app.Value());

                            incident.setCaller(caller);

                            ConfigurationItem configurationItem = getConfigurationItemByIntegrationId((JSONObject) snIncidentJson, "cmdb_ci", app.Value());

                            incident.setConfigurationItem(configurationItem);

                            SysGroup sysGroup = getSysGroupByIntegrationId((JSONObject) snIncidentJson, "assignment_group", app.Value());

                            incident.setAssignmentGroup(sysGroup);


                            Location location = getLocationByIntegrationId((JSONObject) snIncidentJson, "location", app.Value());
                            incident.setLocation(location);


                            incidents.add(incidentService.save(incident));
                            util.printData(tag, count[0], tagAction.concat(util.getFieldDisplay(incident)), util.getFieldDisplay(company), util.getFieldDisplay(domain));
                        }

                        count[0] = count[0] + 1;
                    } catch (Exception e) {
                        //        System.out.println(tag.concat("Exception (I) (").concat(String.valueOf(count[0])).concat(") ").concat(String.valueOf(e)));
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
            //     System.out.println(tag.concat("Exception (II) : ").concat(String.valueOf(e)));
        }
        System.out.println(app.End());
        return incidents;
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

}


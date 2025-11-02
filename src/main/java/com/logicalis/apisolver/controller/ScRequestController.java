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
import com.logicalis.apisolver.view.ScRequestRequest;
import com.logicalis.apisolver.view.ScRequestSolver;
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
public class ScRequestController {
    @Autowired
    private IScRequestService scRequestService;
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
                log.info("Request existe en la BD: {}",  exists.getNumber());
                scRequest.setId(exists.getId());
                tagAction = App.UpdateConsole();
                log.info("Estableciendo atributos al Objeto scRequest: {}", exists.getNumber());
            }else{
                log.info("Request no existe en la BD: {}",  scRequestRequest.getNumber());
                log.info("Estableciendo atributos al Objeto scRequest: {}", scRequestRequest.getNumber());
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

            log.info("Guardando ScRequest en la Base de Datos: {}", scRequest.getNumber());
            scRequestService.save(scRequest);
            Util.printData(tag, tagAction.concat(Util.getFieldDisplay(scRequest)), Util.getFieldDisplay(scRequest.getCompany()), Util.getFieldDisplay(scRequest.getDomain()));
            log.info("ScRequest Actualizado correctamente: {}", scRequest.getNumber());
        } catch (DataAccessException e) {
            log.error("Error al actualizar ScRequest: {}. Error:", scRequest.getNumber(), e.getMessage());
            response.put("mensaje", Errors.dataAccessExceptionUpdate.get());
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", Messages.UpdateOK.get());
        response.put("scRequest", scRequest);
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }

    @GetMapping("/scRequestBySolverQueryCreate")
    public List<ScRequest> show(String query, boolean flagCreate) {
        log.info(App.Start());
        APIResponse apiResponse = null;
        List<ScRequest> scRequests = new ArrayList<>();
        String[] sparmOffSets = Util.offSets99000();
        long startTime = 0;
        long endTime = 0;
        String tag = "[ScRequest] ";
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        JSONParser parser = new JSONParser();
        JSONObject resultJson;
        JSONArray ListSnScRequestJson = new JSONArray();
        String result;
        final ScRequest[] exists = new ScRequest[1];
        final Domain[] domain = new Domain[1];
        final Company[] company = new Company[1];
        final SysUser[] requestedFor = new SysUser[1];
        APIExecutionStatus status = new APIExecutionStatus();
        final ScRequest[] scRequest = {new ScRequest()};
        final String[] tagAction = new String[1];
        final ScRequestSolver[] scRequestSolver = new ScRequestSolver[1];
        final SysUser[] openedBy = new SysUser[1];
        final SysUser[] assignedTo = new SysUser[1];
        final SysUser[] taskFor = new SysUser[1];
        final Location[] location = new Location[1];
        final SysGroup[] sysGroup = new SysGroup[1];
        final ScRequest[] _scRequest = new ScRequest[1];
        try {
            startTime = System.currentTimeMillis();
            final int[] count = {1};
            for (String sparmOffSet : sparmOffSets) {
                result = rest.responseByEndPoint(EndPointSN.ScRequestByQuery().replace("QUERY", query).concat(sparmOffSet));
                log.info(tag.concat("(".concat(EndPointSN.ScRequestByQuery().replace("QUERY", query).concat(sparmOffSet)).concat(")")));
                resultJson = (JSONObject) parser.parse(result);
                ListSnScRequestJson.clear();
                if (resultJson.get("result") != null)
                    ListSnScRequestJson = (JSONArray) parser.parse(resultJson.get("result").toString());
                ListSnScRequestJson.stream().forEach(snScRequestJson -> {
                    try {
                        scRequestSolver[0] = mapper.readValue(snScRequestJson.toString(), ScRequestSolver.class);
                        scRequest[0] = new ScRequest();
                        exists[0] = scRequestService.findByIntegrationId(scRequest[0].getIntegrationId());
                        tagAction[0] = App.CreateConsole();
                        if (exists[0] != null) {
                            scRequest[0].setId(exists[0].getId());
                        } else {
                            scRequest[0].setSysUpdatedOn(scRequestSolver[0].getSys_updated_on());
                            scRequest[0].setNumber(scRequestSolver[0].getNumber());
                            scRequest[0].setState(scRequestSolver[0].getState());
                            scRequest[0].setSysCreatedBy(scRequestSolver[0].getSys_created_by());
                            scRequest[0].setEscalation(scRequestSolver[0].getEscalation());
                            scRequest[0].setExpectedStart(scRequestSolver[0].getExpected_start());
                            scRequest[0].setStage(scRequestSolver[0].getStage());
                            scRequest[0].setActive(scRequestSolver[0].getActive());
                            scRequest[0].setShortDescription(scRequestSolver[0].getShort_description());
                            scRequest[0].setCorrelationDisplay(scRequestSolver[0].getCorrelation_display());
                            scRequest[0].setCorrelationId(scRequestSolver[0].getCorrelation_id());
                            scRequest[0].setSysUpdatedBy(scRequestSolver[0].getSys_updated_by());
                            scRequest[0].setSysCreatedOn(scRequestSolver[0].getSys_created_on());
                            scRequest[0].setOpenedAt(scRequestSolver[0].getOpened_at());
                            scRequest[0].setDescription(scRequestSolver[0].getDescription());
                            scRequest[0].setIntegrationId(scRequestSolver[0].getSys_id());
                            scRequest[0].setDueDate(scRequestSolver[0].getDue_date());
                            scRequest[0].setContactType(scRequestSolver[0].getContact_type());
                            scRequest[0].setApproval(scRequestSolver[0].getApproval());

                            domain[0] = getDomainByIntegrationId((JSONObject) snScRequestJson, SnTable.Domain.get(), App.Value());
                            if (domain[0] != null)
                                scRequest[0].setDomain(domain[0]);
                            company[0] = getCompanyByIntegrationId((JSONObject) snScRequestJson, SnTable.Company.get(), App.Value());
                            if (company[0] != null)
                                scRequest[0].setCompany(company[0]);
                            requestedFor[0] = getSysUserByIntegrationId((JSONObject) snScRequestJson, "requested_for", App.Value());
                            if (requestedFor[0] != null)
                                scRequest[0].setRequestedFor(requestedFor[0]);
                            openedBy[0] = getSysUserByIntegrationId((JSONObject) snScRequestJson, "opened_by", App.Value());
                            if (openedBy[0] != null)
                                scRequest[0].setOpenedBy(openedBy[0]);
                            assignedTo[0] = getSysUserByIntegrationId((JSONObject) snScRequestJson, "assigned_to", App.Value());
                            if (assignedTo[0] != null)
                                scRequest[0].setAssignedTo(assignedTo[0]);
                            taskFor[0] = getSysUserByIntegrationId((JSONObject) snScRequestJson, "task_for", App.Value());
                            if (taskFor[0] != null)
                                scRequest[0].setTaskFor(taskFor[0]);

                            location[0] = getLocationByIntegrationId((JSONObject) snScRequestJson, "location", App.Value());
                            if (location[0] != null)
                                scRequest[0].setLocation(location[0]);

                            sysGroup[0] = getSysGroupByIntegrationId((JSONObject) snScRequestJson, "assignment_group", App.Value());
                            if (sysGroup[0] != null)
                                scRequest[0].setAssignmentGroup(sysGroup[0]);

                            _scRequest[0] = scRequestService.save(scRequest[0]);
                            scRequests.add(_scRequest[0]);
                            Util.printData(tag, count[0], tagAction[0].concat(Util.getFieldDisplay(scRequest[0])), Util.getFieldDisplay(company[0]), Util.getFieldDisplay(domain[0]));
                            count[0] = count[0] + 1;
                        }
                    } catch (Exception e) {
                        //  log.error(tag.concat("Exception (I) : ").concat(String.valueOf(e)));
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
            //  log.error(tag.concat("Exception (II) : ").concat(String.valueOf(e)));
        }
        log.info(App.End());
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



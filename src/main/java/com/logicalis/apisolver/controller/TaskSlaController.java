package com.logicalis.apisolver.controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.logicalis.apisolver.model.*;
import com.logicalis.apisolver.model.enums.*;
import com.logicalis.apisolver.services.*;
import com.logicalis.apisolver.util.Rest;
import com.logicalis.apisolver.util.Util;
import com.logicalis.apisolver.view.TaskSlaRequest;
import com.logicalis.apisolver.view.TaskSlaSolver;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class TaskSlaController {
    @Autowired
    private ITaskSlaService taskSlaService;
    @Autowired
    private IAPIExecutionStatusService statusService;
    @Autowired
    private IDomainService domainService;
    @Autowired
    private IIncidentService incidentService;
    @Autowired
    private ISysGroupService sysGroupService;
    @Autowired
    private IScRequestItemService scRequestItemService;
    @Autowired
    private IScTaskService scTaskService;
    @Autowired
    private IContractSlaService contractSlaService;
    @Autowired
    private ICmnScheduleService cmnScheduleService;
    @Autowired
    private Rest rest;

    @GetMapping("/taskSlas")
    public List<TaskSla> index() {
        return taskSlaService.findAll();
    }

    @GetMapping("/taskSla/{id}")
    public ResponseEntity<?> show(@PathVariable Long id) {
        TaskSla taskSla = null;
        Map<String, Object> response = new HashMap<>();
        try {
            taskSla = taskSlaService.findById(id);
        } catch (DataAccessException e) {
            response.put("mensaje", Errors.dataAccessExceptionQuery.get());
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (taskSla == null) {
            response.put("mensaje", Messages.notExist.get(id.toString()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<TaskSla>(taskSla, HttpStatus.OK);
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/taskSla")
    public ResponseEntity<?> create(@RequestBody TaskSla taskSla) {
        TaskSla newTaskSla = null;
        Map<String, Object> response = new HashMap<>();
        try {
            newTaskSla = taskSlaService.save(taskSla);
        } catch (DataAccessException e) {
            response.put("mensaje", Errors.dataAccessExceptionInsert.get());
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", Messages.createOK.get());
        response.put("taskSla", newTaskSla);
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }

    @Secured("ROLE_ADMIN")
    @PutMapping("/taskSla/{id}")
    public ResponseEntity<?> update(@RequestBody TaskSla taskSla, @PathVariable Long id) {
        TaskSla currentTaskSla = taskSlaService.findById(id);
        TaskSla taskSlaUpdated = null;
        Map<String, Object> response = new HashMap<>();
        if (currentTaskSla == null) {
            response.put("mensaje", Errors.dataAccessExceptionUpdate.get(id.toString()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }
        try {
            currentTaskSla.setStage(taskSla.getStage());
            taskSlaUpdated = taskSlaService.save(currentTaskSla);

        } catch (DataAccessException e) {
            response.put("mensaje", Errors.dataAccessExceptionUpdate.get());
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", Messages.UpdateOK.get());
        response.put("taskSla", taskSlaUpdated);
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/taskSla/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            taskSlaService.delete(id);
        } catch (DataAccessException e) {
            response.put("mensaje", Errors.dataAccessExceptionDelete.get());
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", Messages.DeleteOK.get());
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
    }

    @GetMapping("/taskSla/{integrationId}")
    public ResponseEntity<?> findByIntegrationId(@PathVariable String integrationId) {
        TaskSla taskSla = null;
        Map<String, Object> response = new HashMap<>();
        try {
            taskSla = taskSlaService.findByIntegrationId(integrationId);
        } catch (DataAccessException e) {
            response.put("mensaje", Errors.dataAccessExceptionQuery.get());
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (taskSla == null) {
            response.put("mensaje", Messages.notExist.get(integrationId.toString()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<TaskSla>(taskSla, HttpStatus.OK);
    }

    @GetMapping("/taskSlasBySolver")
    public List<TaskSlaSolver> show() {
        log.info(App.Start());
        String[] sparmOffSets = Util.offSets99000();
        APIResponse apiResponse = null;
        List<TaskSlaSolver> snTaskSlasSolver = new ArrayList<>();
        long startTime = 0;
        long endTime = 0;
        String tag = "[TaskSla] ";
        String result;
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        JSONParser parser = new JSONParser();
        JSONObject resultJson = new JSONObject();
        JSONArray ListTaskSlasJson = new JSONArray();
        final String[] element = new String[1];
        final Incident[] incident = {new Incident()};
        final ScRequestItem[] scRequestItem = {new ScRequestItem()};
        final ScTask[] scTask = {new ScTask()};
        final TaskSlaSolver[] taskSlaSolver = new TaskSlaSolver[1];
        final TaskSla[] taskSla = {new TaskSla()};
        final String[] table = new String[1];
        final String[] tagAction = new String[1];
        final Domain[] domain = new Domain[1];
        final ContractSla[] sla = new ContractSla[1];
        final CmnSchedule[] schedule = new CmnSchedule[1];
        final TaskSla[] exists = new TaskSla[1];
        APIExecutionStatus status = new APIExecutionStatus();
        try {
            startTime = System.currentTimeMillis();
            final int[] count = {1};
            for (String sparmOffSet : sparmOffSets) {
                result = rest.responseByEndPoint(EndPointSN.TaskSlaByCompany().concat(sparmOffSet));
                log.info(tag.concat("(".concat(EndPointSN.TaskSlaByCompany().concat(sparmOffSet)).concat(")")));
                resultJson = (JSONObject) parser.parse(result);
                ListTaskSlasJson.clear();
                if (resultJson.get("result") != null)
                    ListTaskSlasJson = (JSONArray) parser.parse(resultJson.get("result").toString());
                ListTaskSlasJson.stream().forEach(taskSlaJson -> {
                    try {
                        element[0] = Util.getIdByJson((JSONObject) taskSlaJson, "task", App.Value());
                        scRequestItem[0] = new ScRequestItem();
                        scTask[0] = new ScTask();
                        incident[0] = getIncidentByIntegrationId(element[0]);
                        if (incident[0] == null)
                            scRequestItem[0] = getScRequestItemByIntegrationId(element[0]);
                        if (incident[0] == null && scRequestItem[0] == null)
                            scTask[0] = getScTaskByIntegrationId(element[0]);
                        taskSlaSolver[0] = mapper.readValue(taskSlaJson.toString(), TaskSlaSolver.class);
                        taskSla[0] = new TaskSla();
                        table[0] = "";
                        if (incident[0] != null) {
                            taskSla[0].setIncident(incident[0]);
                            table[0] = Util.getFieldDisplay(incident[0]);
                        } else if (scRequestItem[0] != null) {
                            taskSla[0].setScRequestItem(scRequestItem[0]);
                            table[0] = Util.getFieldDisplay(scRequestItem[0]);
                        } else if (scTask[0] != null) {
                            taskSla[0].setScTask(scTask[0]);
                            table[0] = Util.getFieldDisplay(scTask[0]);
                        }
                        tagAction[0] = "(Not Exist) ";
                        if (!table[0].equals("")) {
                            snTaskSlasSolver.add(taskSlaSolver[0]);
                            taskSla[0].setPauseDuration(Util.isNull(taskSlaSolver[0].getPause_duration()));
                            taskSla[0].setPauseTime(Util.isNull(taskSlaSolver[0].getPause_time()));
                            taskSla[0].setTimezone(Util.isNull(taskSlaSolver[0].getTimezone()));
                            taskSla[0].setSysUpdatedOn(Util.isNull(taskSlaSolver[0].getSys_updated_on()));
                            taskSla[0].setBusinessTimeLeft(Util.isNull(taskSlaSolver[0].getBusiness_time_left()));
                            taskSla[0].setDuration(Util.isNull(taskSlaSolver[0].getDuration()));
                            taskSla[0].setTimeLeft(Util.isNull(taskSlaSolver[0].getTime_left()));
                            taskSla[0].setSysUpdatedBy(Util.isNull(taskSlaSolver[0].getSys_updated_by()));
                            taskSla[0].setSysCreatedOn(Util.isNull(taskSlaSolver[0].getSys_created_on()));
                            taskSla[0].setPercentage(Util.isNull(taskSlaSolver[0].getPercentage()));
                            taskSla[0].setOriginalBreachTime(Util.isNull(taskSlaSolver[0].getOriginal_breach_time()));
                            taskSla[0].setSysCreatedBy(Util.isNull(taskSlaSolver[0].getSys_created_by()));
                            taskSla[0].setBusinessPercentage(Util.isNull(taskSlaSolver[0].getBusiness_percentage()));
                            taskSla[0].setEndTime(Util.isNull(taskSlaSolver[0].getEnd_time()));
                            taskSla[0].setSysModCount(Util.isNull(taskSlaSolver[0].getSys_mod_count()));
                            taskSla[0].setActive(true);
                            taskSla[0].setBusinessPauseDuration(Util.isNull(taskSlaSolver[0].getBusiness_pause_duration()));
                            taskSla[0].setStartTime(Util.isNull(taskSlaSolver[0].getStart_time()));
                            taskSla[0].setBusinessDuration(Util.isNull(taskSlaSolver[0].getBusiness_duration()));
                            taskSla[0].setStage(Util.isNull(taskSlaSolver[0].getStage()));
                            taskSla[0].setPlannedEndTime(Util.isNull(taskSlaSolver[0].getPlanned_end_time()));
                            if (!Util.isNullBool(taskSlaSolver[0].getU_trigger_group()))
                                taskSla[0].setAssignmentGroup(sysGroupService.findByActiveAndName(true, taskSlaSolver[0].getU_trigger_group()));
                            taskSla[0].setIntegrationId(taskSlaSolver[0].getSys_id());
                            domain[0] = getDomainByIntegrationId((JSONObject) taskSlaJson, SnTable.Domain.get(), App.Value());
                            if (domain[0] != null)
                                taskSla[0].setDomain(domain[0]);
                            sla[0] = getContractSlaByIntegrationId((JSONObject) taskSlaJson, "sla", App.Value());
                            if (sla[0] != null) {
                                taskSla[0].setSla(sla[0]);
                            }
                            schedule[0] = getCmnScheduleByIntegrationId((JSONObject) taskSlaJson, "schedule", App.Value());
                            if (schedule[0] != null) {
                                taskSla[0].setSchedule(schedule[0]);
                            }
                            exists[0] = taskSlaService.findByIntegrationId(taskSla[0].getIntegrationId());
                            tagAction[0] = App.CreateConsole();
                            if (exists[0] != null) {
                                taskSla[0].setId(exists[0].getId());
                                tagAction[0] = App.UpdateConsole();
                            }
                            Util.printData(tag, count[0], tagAction[0].concat(table[0]), Util.getFieldDisplay(taskSla[0]));
                            taskSlaService.save(taskSla[0]);
                        } else {
                            Util.printData(tag, count[0], tagAction[0].concat(element[0]));
                        }
                        count[0] = count[0] + 1;
                    } catch (Exception e) {
                        log.error(tag.concat("Exception (I) : ").concat(String.valueOf(e)));
                    }
                });
                apiResponse = mapper.readValue(result, APIResponse.class);
                status.setUri(EndPointSN.Catalog());
                status.setUserAPI(App.SNUser());
                status.setPasswordAPI(App.SNPassword());
                status.setError(apiResponse.getError());
                status.setMessage(apiResponse.getMessage());
                status.setExecutionTime(endTime);
                statusService.save(status);
            }
        } catch (Exception e) {
            log.error(tag.concat("Exception (II) : ").concat(String.valueOf(e)));
        }
        log.info(App.End());
        return snTaskSlasSolver;
    }

    @GetMapping("/taskSlasBySolverByQuery")
    public List<TaskSlaSolver> show(String query) {
        log.info(App.Start());
        String[] sparmOffSets = Util.offSets99000();
        APIResponse apiResponse = null;
        List<TaskSlaSolver> snTaskSlasSolver = new ArrayList<>();
        long startTime = 0;
        long endTime = 0;
        String tag = "[TaskSla] ";
        String result;
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        JSONParser parser = new JSONParser();
        JSONObject resultJson = new JSONObject();
        JSONArray ListTaskSlasJson = new JSONArray();
        final String[] element = new String[1];
        final Incident[] incident = {new Incident()};
        final ScRequestItem[] scRequestItem = {new ScRequestItem()};
        final ScTask[] scTask = {new ScTask()};
        Gson gson = new Gson();
        final TaskSlaSolver[] taskSlaSolver = new TaskSlaSolver[1];
        final TaskSla[] taskSla = {new TaskSla()};
        final String[] table = new String[1];
        final String[] tagAction = new String[1];
        final Domain[] domain = new Domain[1];
        final ContractSla[] sla = new ContractSla[1];
        final CmnSchedule[] schedule = new CmnSchedule[1];
        final TaskSla[] exists = new TaskSla[1];
        APIExecutionStatus status = new APIExecutionStatus();
        try {
            startTime = System.currentTimeMillis();
            final int[] count = {1};
            for (String sparmOffSet : sparmOffSets) {
                result = rest.responseByEndPoint(EndPointSN.TaskSlaByQuery().replace("QUERY", query).concat(sparmOffSet));
                log.info(tag.concat("(".concat(EndPointSN.TaskSlaByQuery().concat(sparmOffSet)).concat(")")));
                resultJson = (JSONObject) parser.parse(result);
                ListTaskSlasJson.clear();
                if (resultJson.get("result") != null)
                    ListTaskSlasJson = (JSONArray) parser.parse(resultJson.get("result").toString());
                ListTaskSlasJson.stream().forEach(taskSlaJson -> {
                    try {
                        element[0] = Util.getIdByJson((JSONObject) taskSlaJson, "task", App.Value());
                        incident[0] = getIncidentByIntegrationId(element[0]);
                        scRequestItem[0] = new ScRequestItem();
                        scTask[0] = new ScTask();
                        if (incident[0] == null)
                            scRequestItem[0] = getScRequestItemByIntegrationId(element[0]);
                        if (incident[0] == null && scRequestItem[0] == null)
                            scTask[0] = getScTaskByIntegrationId(element[0]);
                        taskSlaSolver[0] = gson.fromJson(taskSlaJson.toString(), TaskSlaSolver.class);
                        taskSla[0] = new TaskSla();
                        table[0] = "";
                        if (incident[0] != null) {
                            taskSla[0].setIncident(incident[0]);
                            table[0] = Util.getFieldDisplay(incident[0]);
                        } else if (scRequestItem[0] != null) {
                            taskSla[0].setScRequestItem(scRequestItem[0]);
                            table[0] = Util.getFieldDisplay(scRequestItem[0]);
                        } else if (scTask[0] != null) {
                            taskSla[0].setScTask(scTask[0]);
                            table[0] = Util.getFieldDisplay(scTask[0]);
                        }
                        tagAction[0] = "(Not Exist) ";
                        if (!table[0].equals("")) {
                            snTaskSlasSolver.add(taskSlaSolver[0]);
                            taskSla[0].setPauseDuration(Util.isNull(taskSlaSolver[0].getPause_duration()));
                            taskSla[0].setPauseTime(Util.isNull(taskSlaSolver[0].getPause_time()));
                            taskSla[0].setTimezone(Util.isNull(taskSlaSolver[0].getTimezone()));
                            taskSla[0].setSysUpdatedOn(Util.isNull(taskSlaSolver[0].getSys_updated_on()));
                            taskSla[0].setBusinessTimeLeft(Util.isNull(taskSlaSolver[0].getBusiness_time_left()));
                            taskSla[0].setDuration(Util.isNull(taskSlaSolver[0].getDuration()));
                            taskSla[0].setTimeLeft(Util.isNull(taskSlaSolver[0].getTime_left()));
                            taskSla[0].setSysUpdatedBy(Util.isNull(taskSlaSolver[0].getSys_updated_by()));
                            taskSla[0].setSysCreatedOn(Util.isNull(taskSlaSolver[0].getSys_created_on()));
                            taskSla[0].setPercentage(Util.isNull(taskSlaSolver[0].getPercentage()));
                            taskSla[0].setOriginalBreachTime(Util.isNull(taskSlaSolver[0].getOriginal_breach_time()));
                            taskSla[0].setSysCreatedBy(Util.isNull(taskSlaSolver[0].getSys_created_by()));
                            taskSla[0].setBusinessPercentage(Util.isNull(taskSlaSolver[0].getBusiness_percentage()));
                            taskSla[0].setEndTime(Util.isNull(taskSlaSolver[0].getEnd_time()));
                            taskSla[0].setSysModCount(Util.isNull(taskSlaSolver[0].getSys_mod_count()));
                            taskSla[0].setActive(true);
                            taskSla[0].setBusinessPauseDuration(Util.isNull(taskSlaSolver[0].getBusiness_pause_duration()));
                            taskSla[0].setStartTime(Util.isNull(taskSlaSolver[0].getStart_time()));
                            taskSla[0].setBusinessDuration(Util.isNull(taskSlaSolver[0].getBusiness_duration()));
                            taskSla[0].setStage(Util.isNull(taskSlaSolver[0].getStage()));
                            taskSla[0].setPlannedEndTime(Util.isNull(taskSlaSolver[0].getPlanned_end_time()));
                            taskSla[0].setIntegrationId(taskSlaSolver[0].getSys_id());
                            taskSla[0].setuTriggerGroup(Util.isNull(taskSlaSolver[0].getU_trigger_group()));
                            domain[0] = getDomainByIntegrationId((JSONObject) taskSlaJson, SnTable.Domain.get(), App.Value());
                            if (domain[0] != null)
                                taskSla[0].setDomain(domain[0]);
                            sla[0] = getContractSlaByIntegrationId((JSONObject) taskSlaJson, "sla", App.Value());
                            if (sla[0] != null) {
                                taskSla[0].setSla(sla[0]);
                            }
                            schedule[0] = getCmnScheduleByIntegrationId((JSONObject) taskSlaJson, "schedule", App.Value());
                            if (schedule[0] != null) {
                                taskSla[0].setSchedule(schedule[0]);
                            }
                            exists[0] = taskSlaService.findByIntegrationId(taskSla[0].getIntegrationId());
                            tagAction[0] = App.CreateConsole();
                            if (exists[0] != null) {
                                taskSla[0].setId(exists[0].getId());
                                if (taskSla[0].getIncident() == null)
                                    taskSla[0].setIncident(exists[0].getIncident());
                                if (taskSla[0].getScRequestItem() == null)
                                    taskSla[0].setScRequestItem(exists[0].getScRequestItem());
                                if (taskSla[0].getScTask() == null)
                                    taskSla[0].setScTask(exists[0].getScTask());
                                tagAction[0] = App.UpdateConsole();
                            }
                            Util.printData(tag, count[0], tagAction[0].concat(table[0]), Util.getFieldDisplay(taskSla[0]));
                            taskSlaService.save(taskSla[0]);
                        } else {
                            Util.printData(tag, count[0], tagAction[0].concat(element[0]));
                        }
                        count[0] = count[0] + 1;
                    } catch (Exception e) {
                        log.error(tag.concat("Exception (I) : ").concat(String.valueOf(e)));
                    }
                });
                apiResponse = mapper.readValue(result, APIResponse.class);
                status.setUri(EndPointSN.Catalog());
                status.setUserAPI(App.SNUser());
                status.setPasswordAPI(App.SNPassword());
                status.setError(apiResponse.getError());
                status.setMessage(apiResponse.getMessage());
                status.setExecutionTime(endTime);
                statusService.save(status);
            }
        } catch (Exception e) {
            log.error(tag.concat("Exception (II) : ").concat(String.valueOf(e)));
        }
        log.info(App.End());
        return snTaskSlasSolver;
    }

    @GetMapping("/byIncident/{id}")
    public List<TaskSlaInfo> findByIncident(@PathVariable Long id) {
        return taskSlaService.findByIncident(id);
    }

    @GetMapping("/byScRequestItem/{id}")
    public List<TaskSlaInfo> findByScRequestItem(@PathVariable Long id) {
        return taskSlaService.findByScRequestItem(id);
    }

    @GetMapping("/byScTask/{id}")
    public List<TaskSlaInfo> findByScTask(@PathVariable Long id) {
        return taskSlaService.findByScTask(id);
    }

    @PutMapping("/taskSlaSN")
    public ResponseEntity<?> update(@RequestBody TaskSlaRequest taskSlaRequest) {
        TaskSla currentTaskSla = new TaskSla();
        TaskSla taskSlaUpdated = null;
        Map<String, Object> response = new HashMap<>();
        log.info("Solicitud PUT recibida para actualizar Task SLA. sys_id Task SLA: {}", taskSlaRequest.getSys_id());
        if (currentTaskSla == null) {
            log.warn("Task SLA no encontrada para sys_id: {}", taskSlaRequest.getSys_id());
            response.put("mensaje", Errors.dataAccessExceptionUpdate.get(taskSlaRequest.getSys_id()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }
        try {
            log.debug("Actualizando Task SLA. sys_id Task SLA: {}", taskSlaRequest.getSys_id());
            TaskSla taskSla = new TaskSla();
            log.info("Estableciendo nuevos atributos al Objeto taskSla. sys_id Task SLA: {}", taskSlaRequest.getSys_id());
            taskSla.setActive(taskSlaRequest.getActive());
            taskSla.setBusinessDuration(taskSlaRequest.getBusiness_duration());
            taskSla.setBusinessPauseDuration(taskSlaRequest.getBusiness_pause_duration());
            taskSla.setBusinessPercentage(taskSlaRequest.getBusiness_percentage());
            taskSla.setBusinessTimeLeft(taskSlaRequest.getBusiness_time_left());
            taskSla.setDuration(taskSlaRequest.getDuration());
            taskSla.setEndTime(taskSlaRequest.getEnd_time());
            taskSla.setOriginalBreachTime(taskSlaRequest.getOriginal_breach_time());
            taskSla.setPauseDuration(taskSlaRequest.getPause_duration());
            taskSla.setPauseTime(taskSlaRequest.getPause_time());
            taskSla.setPercentage(taskSlaRequest.getPercentage());
            taskSla.setPlannedEndTime(taskSlaRequest.getPlanned_end_time());
            taskSla.setStage(taskSlaRequest.getStage());
            taskSla.setStartTime(taskSlaRequest.getStart_time());
            taskSla.setSysCreatedBy(taskSlaRequest.getSys_created_by());
            taskSla.setSysCreatedOn(taskSlaRequest.getSys_created_on());
            taskSla.setIntegrationId(taskSlaRequest.getSys_id());
            taskSla.setSysModCount(taskSlaRequest.getSys_mod_count());
            taskSla.setSysUpdatedBy(taskSlaRequest.getSys_updated_by());
            taskSla.setSysUpdatedOn(taskSlaRequest.getSys_updated_on());
            taskSla.setTimeLeft(taskSlaRequest.getTime_left());
            taskSla.setTimezone(taskSlaRequest.getTimezone());
            taskSla.setuTriggerGroup(taskSlaRequest.getU_trigger_group());
            log.info("Buscando y estableciendo relacion de Dominio. sys_id Task Dominio: {}", taskSlaRequest.getSys_domain());
            if (Util.hasData(taskSlaRequest.getSys_domain())) {
                Domain domain = domainService.findByIntegrationId(taskSlaRequest.getSys_domain());
                if (domain != null)
                    taskSla.setDomain(domain);
            }
            log.info("Determinando relacion entre objeto Incident, ScRequestItem o ScTask. sys_id Task SLA: {}", taskSlaRequest.getSys_id());
            if (taskSlaRequest.getElement().equals(SnTable.Incident.get())) {
                Incident incident = incidentService.findByIntegrationId(taskSlaRequest.getTask());
                if (incident != null) {
                    log.info("Relacion establecida con Incident: {}", incident.getNumber());
                    taskSla.setIncident(incident);
                }
            } else if (taskSlaRequest.getElement().equals(SnTable.ScRequestItem.get())) {
                ScRequestItem scRequestItem = scRequestItemService.findByIntegrationId(taskSlaRequest.getTask());
                if (scRequestItem != null) {
                    log.info("Relacion establecida con scRequest: {}", scRequestItem.getNumber());
                    taskSla.setScRequestItem(scRequestItem);
                }
            } else if (taskSlaRequest.getElement().equals(SnTable.ScTask.get())) {
                ScTask scTask = scTaskService.findByIntegrationId(taskSlaRequest.getTask());
                if (scTask != null) {
                    log.info("Relacion establecida con TASK: {}", scTask.getNumber());
                    taskSla.setScTask(scTask);
                }
            }
            if (Util.hasData(taskSlaRequest.getSla())) {
                ContractSla sla = contractSlaService.findByIntegrationId(taskSlaRequest.getSla());
                if (sla != null) {
                    log.info("Relacion establecida con contractSlaService: {}", taskSlaRequest.getSla());
                    taskSla.setSla(sla);
                }
            }
            if (Util.hasData(taskSlaRequest.getSchedule())) {
                CmnSchedule schedule = cmnScheduleService.findByIntegrationId(taskSlaRequest.getSchedule());
                if (schedule != null) {
                    log.info("Relacion establecida con cmnScheduleService: {}", taskSlaRequest.getSchedule());
                    taskSla.setSchedule(schedule);
                }
            }
            TaskSla exists = taskSlaService.findByIntegrationId(taskSla.getIntegrationId());
            if (exists != null) {
                taskSla.setId(exists.getId());
            }
            log.info("Guardando Task SLA en la Base de Datos: {}", taskSla.getId());
            taskSlaUpdated = taskSlaService.save(taskSla);
            log.info("Task SLA Actualizado correctamente: sys_id Task SLA: {}", taskSlaRequest.getSys_id());
        } catch (DataAccessException e) {
            log.error("Error al actualizar la Task SLA: {}. Error: {}", taskSlaRequest.getSys_id(), e.getMessage());
            response.put("mensaje", Errors.dataAccessExceptionUpdate.get());
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", Messages.UpdateOK.get());
        response.put("taskSla", taskSlaUpdated);
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }

    public Domain getDomainByIntegrationId(JSONObject jsonObject, String levelOne, String levelTwo) {
        String integrationId = Util.getIdByJson(jsonObject, levelOne, levelTwo);
        if (Util.hasData(integrationId)) {
            return domainService.findByIntegrationId(integrationId);
        } else
            return null;
    }

    public Incident getIncidentByIntegrationId(String element) {
        if (Util.hasData(element)) {
            return incidentService.findByIntegrationId(element);
        } else
            return null;
    }

    public ScTask getScTaskByIntegrationId(String element) {
        if (Util.hasData(element)) {
            return scTaskService.findByIntegrationId(element);
        } else
            return null;
    }

    public ScRequestItem getScRequestItemByIntegrationId(String element) {
        if (Util.hasData(element)) {
            return scRequestItemService.findByIntegrationId(element);
        } else
            return null;
    }

    public ContractSla getContractSlaByIntegrationId(JSONObject jsonObject, String levelOne, String levelTwo) {
        String integrationId = Util.getIdByJson(jsonObject, levelOne, levelTwo);
        if (Util.hasData(integrationId)) {
            return contractSlaService.findByIntegrationId(integrationId);
        } else
            return null;
    }

    public CmnSchedule getCmnScheduleByIntegrationId(JSONObject jsonObject, String levelOne, String levelTwo) {
        String integrationId = Util.getIdByJson(jsonObject, levelOne, levelTwo);
        if (Util.hasData(integrationId)) {
            return cmnScheduleService.findByIntegrationId(integrationId);
        } else
            return null;
    }
}


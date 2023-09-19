
package com.logicalis.apisolver.controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.logicalis.apisolver.model.*;
import com.logicalis.apisolver.model.enums.*;
import com.logicalis.apisolver.services.*;
import com.logicalis.apisolver.util.Rest;
import com.logicalis.apisolver.util.Util;
import com.logicalis.apisolver.view.ScTaskRequest;
import com.logicalis.apisolver.view.TaskSlaRequest;
import com.logicalis.apisolver.view.TaskSlaSolver;
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
        System.out.println(App.Start());
        String[] sparmOffSets = Util.offSets99000();
        APIResponse apiResponse = null;
        List<TaskSlaSolver> snTaskSlasSolver = new ArrayList<>();
        long startTime = 0;
        long endTime = 0;
        String tag = "[TaskSla] ";
        try {
            startTime = System.currentTimeMillis();
            final int[] count = {1};
            for (String sparmOffSet : sparmOffSets) {
                String result = rest.responseByEndPoint(EndPointSN.TaskSlaByCompany().concat(sparmOffSet));
                System.out.println(tag.concat("(".concat(EndPointSN.TaskSlaByCompany().concat(sparmOffSet)).concat(")")));
                ObjectMapper mapper = new ObjectMapper();
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                JSONParser parser = new JSONParser();
                JSONObject resultJson = new JSONObject();
                JSONArray ListTaskSlasJson = new JSONArray();

                resultJson = (JSONObject) parser.parse(result);
                if (resultJson.get("result") != null)
                    ListTaskSlasJson = (JSONArray) parser.parse(resultJson.get("result").toString());

                ListTaskSlasJson.stream().forEach(taskSlaJson -> {

                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    try {
                        String element = Util.getIdByJson((JSONObject) taskSlaJson, "task", App.Value());
                        Incident incident = new Incident();
                        ScRequestItem scRequestItem = new ScRequestItem();
                        ScTask scTask = new ScTask();

                        incident = getIncidentByIntegrationId(element);
                        if (incident == null)
                            scRequestItem = getScRequestItemByIntegrationId(element);
                        if (incident == null && scRequestItem == null)
                            scTask = getScTaskByIntegrationId(element);

                        TaskSlaSolver taskSlaSolver = objectMapper.readValue(taskSlaJson.toString(), TaskSlaSolver.class);
                        TaskSla taskSla = new TaskSla();
                        String table = "";


                        if (incident != null) {
                            taskSla.setIncident(incident);
                            table = Util.getFieldDisplay(incident);
                        } else if (scRequestItem != null) {
                            taskSla.setScRequestItem(scRequestItem);
                            table = Util.getFieldDisplay(scRequestItem);
                        } else if (scTask != null) {
                            taskSla.setScTask(scTask);
                            table = Util.getFieldDisplay(scTask);
                        }
                        String tagAction = "(Not Exist) ";
                        if (table != "") {
                            snTaskSlasSolver.add(taskSlaSolver);
                            /*System.out.println("taskSlaSolver.getPause_duration: ".concat(String.valueOf(taskSlaSolver.getPause_duration().length())));
                            System.out.println("taskSlaSolver.getPause_time: ".concat(String.valueOf(taskSlaSolver.getPause_time().length())));
                            System.out.println("taskSlaSolver.getTimezone: ".concat(String.valueOf(taskSlaSolver.getTimezone().length())));
                            System.out.println("taskSlaSolver.getSys_updated_on: ".concat(String.valueOf(taskSlaSolver.getSys_updated_on().length())));
                            System.out.println("taskSlaSolver.getBusiness_time_left: ".concat(String.valueOf(taskSlaSolver.getBusiness_time_left().length())));
                            System.out.println("taskSlaSolver.getDuration: ".concat(String.valueOf(taskSlaSolver.getDuration().length())));
                            System.out.println("taskSlaSolver.getTime_left: ".concat(String.valueOf(taskSlaSolver.getTime_left().length())));
                            System.out.println("taskSlaSolver.getSys_updated_by: ".concat(String.valueOf(taskSlaSolver.getSys_updated_by().length())));
                            System.out.println("taskSlaSolver.getSys_created_on: ".concat(String.valueOf(taskSlaSolver.getSys_created_on().length())));
                            System.out.println("taskSlaSolver.getPercentage: ".concat(String.valueOf(taskSlaSolver.getPercentage().length())));
                            System.out.println("taskSlaSolver.getOriginal_breach_time: ".concat(String.valueOf(taskSlaSolver.getOriginal_breach_time().length())));
                            System.out.println("taskSlaSolver.getSys_created_by: ".concat(String.valueOf(taskSlaSolver.getSys_created_by().length())));
                            System.out.println("taskSlaSolver.getBusiness_percentage: ".concat(String.valueOf(taskSlaSolver.getBusiness_percentage().length())));
                            System.out.println("taskSlaSolver.getEnd_time: ".concat(String.valueOf(taskSlaSolver.getEnd_time().length())));
                            System.out.println("taskSlaSolver.getSys_mod_count: ".concat(String.valueOf(taskSlaSolver.getSys_mod_count().length())));
                            System.out.println("taskSlaSolver.getBusiness_pause_duration: ".concat(String.valueOf(taskSlaSolver.getBusiness_pause_duration().length())));
                            System.out.println("taskSlaSolver.getStart_time: ".concat(String.valueOf(taskSlaSolver.getStart_time().length())));
                            System.out.println("taskSlaSolver.getBusiness_duration: ".concat(String.valueOf(taskSlaSolver.getBusiness_duration().length())));
                            System.out.println("taskSlaSolver.getStage: ".concat(String.valueOf(taskSlaSolver.getStage().length())));
                            System.out.println("taskSlaSolver.getPlanned_end_time: ".concat(String.valueOf(taskSlaSolver.getPlanned_end_time().length())));
                            System.out.println("taskSlaSolver.getHas_breached: ".concat(String.valueOf(taskSlaSolver.getHas_breached().length())));*/
                            taskSla.setPauseDuration(Util.isNull(taskSlaSolver.getPause_duration()));
                            taskSla.setPauseTime(Util.isNull(taskSlaSolver.getPause_time()));
                            taskSla.setTimezone(Util.isNull(taskSlaSolver.getTimezone()));
                            taskSla.setSysUpdatedOn(Util.isNull(taskSlaSolver.getSys_updated_on()));
                            taskSla.setBusinessTimeLeft(Util.isNull(taskSlaSolver.getBusiness_time_left()));
                            taskSla.setDuration(Util.isNull(taskSlaSolver.getDuration()));
                            taskSla.setTimeLeft(Util.isNull(taskSlaSolver.getTime_left()));
                            taskSla.setSysUpdatedBy(Util.isNull(taskSlaSolver.getSys_updated_by()));
                            taskSla.setSysCreatedOn(Util.isNull(taskSlaSolver.getSys_created_on()));
                            taskSla.setPercentage(Util.isNull(taskSlaSolver.getPercentage()));
                            taskSla.setOriginalBreachTime(Util.isNull(taskSlaSolver.getOriginal_breach_time()));
                            taskSla.setSysCreatedBy(Util.isNull(taskSlaSolver.getSys_created_by()));
                            taskSla.setBusinessPercentage(Util.isNull(taskSlaSolver.getBusiness_percentage()));
                            taskSla.setEndTime(Util.isNull(taskSlaSolver.getEnd_time()));
                            taskSla.setSysModCount(Util.isNull(taskSlaSolver.getSys_mod_count()));
                            taskSla.setActive(true);
                            taskSla.setBusinessPauseDuration(Util.isNull(taskSlaSolver.getBusiness_pause_duration()));
                            taskSla.setStartTime(Util.isNull(taskSlaSolver.getStart_time()));
                            taskSla.setBusinessDuration(Util.isNull(taskSlaSolver.getBusiness_duration()));
                            taskSla.setStage(Util.isNull(taskSlaSolver.getStage()));
                            taskSla.setPlannedEndTime(Util.isNull(taskSlaSolver.getPlanned_end_time()));
                            //taskSla.setHasBreached(Util.isNull(taskSlaSolver.getHas_breached()));
                            //taskSla.setTimezone(taskSlaSolver.getTimezone_source());
                            //taskSla.setType(taskSlaSolver.getType());
                            //taskSla.setVendor(taskSlaSolver.getVendor());
//sysGroupService.fin

                            if (!Util.isNullBool(taskSlaSolver.getU_trigger_group()))
                                taskSla.setAssignmentGroup(sysGroupService.findByActiveAndName(true, taskSlaSolver.getU_trigger_group()));

                            taskSla.setIntegrationId(taskSlaSolver.getSys_id());

                            Domain domain = getDomainByIntegrationId((JSONObject) taskSlaJson, SnTable.Domain.get(), App.Value());
                            if (domain != null)
                                taskSla.setDomain(domain);

                            ContractSla sla = getContractSlaByIntegrationId((JSONObject) taskSlaJson, "sla", App.Value());
                            if (sla != null) {
                                taskSla.setSla(sla);
                            }

                            CmnSchedule schedule = getCmnScheduleByIntegrationId((JSONObject) taskSlaJson, "schedule", App.Value());
                            if (schedule != null) {
                                taskSla.setSchedule(schedule);
                            }

                            TaskSla exists = taskSlaService.findByIntegrationId(taskSla.getIntegrationId());
                            tagAction = App.CreateConsole();
                            if (exists != null) {
                                taskSla.setId(exists.getId());
                                tagAction = App.UpdateConsole();
                            }
                            Util.printData(tag, count[0], tagAction.concat(table), Util.getFieldDisplay(taskSla));

                            taskSlaService.save(taskSla);
                        } else {
                            Util.printData(tag, count[0], tagAction.concat(element));
                        }
                        count[0] = count[0] + 1;
                    } catch (Exception e) {
                        System.out.println(tag.concat("Exception (I) : ").concat(String.valueOf(e)));
                    }
                });

                apiResponse = mapper.readValue(result, APIResponse.class);

                APIExecutionStatus status = new APIExecutionStatus();
                status.setUri(EndPointSN.Catalog());
                status.setUserAPI(App.SNUser());
                status.setPasswordAPI(App.SNPassword());
                status.setError(apiResponse.getError());
                status.setMessage(apiResponse.getMessage());
                status.setExecutionTime(endTime);
                statusService.save(status);
            }


        } catch (Exception e) {
            System.out.println(tag.concat("Exception (II) : ").concat(String.valueOf(e)));
        }
        System.out.println(App.End());
        return snTaskSlasSolver;
    }

    @GetMapping("/taskSlasBySolverByQuery")
    public List<TaskSlaSolver> show(String query) {
        System.out.println(App.Start());
        String[] sparmOffSets = Util.offSets99000();
        APIResponse apiResponse = null;
        List<TaskSlaSolver> snTaskSlasSolver = new ArrayList<>();
        long startTime = 0;
        long endTime = 0;
        String tag = "[TaskSla] ";
        try {
            startTime = System.currentTimeMillis();
            final int[] count = {1};
            for (String sparmOffSet : sparmOffSets) {
                String result = rest.responseByEndPoint(EndPointSN.TaskSlaByQuery().replace("QUERY", query).concat(sparmOffSet));
                System.out.println(tag.concat("(".concat(EndPointSN.TaskSlaByQuery().concat(sparmOffSet)).concat(")")));
                ObjectMapper mapper = new ObjectMapper();
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                JSONParser parser = new JSONParser();
                JSONObject resultJson = new JSONObject();
                JSONArray ListTaskSlasJson = new JSONArray();

                resultJson = (JSONObject) parser.parse(result);
                if (resultJson.get("result") != null)
                    ListTaskSlasJson = (JSONArray) parser.parse(resultJson.get("result").toString());

                ListTaskSlasJson.stream().forEach(taskSlaJson -> {

                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    try {
                        String element = Util.getIdByJson((JSONObject) taskSlaJson, "task", App.Value());
                        Incident incident = new Incident();
                        ScRequestItem scRequestItem = new ScRequestItem();
                        ScTask scTask = new ScTask();

                        incident = getIncidentByIntegrationId(element);
                        if (incident == null)
                            scRequestItem = getScRequestItemByIntegrationId(element);
                        if (incident == null && scRequestItem == null)
                            scTask = getScTaskByIntegrationId(element);

                        //TaskSlaSolver taskSlaSolver = objectMapper.readValue(taskSlaJson.toString(), TaskSlaSolver.class);

                        Gson gson = new Gson();
                        TaskSlaSolver taskSlaSolver = gson.fromJson(taskSlaJson.toString(), TaskSlaSolver.class);

                        TaskSla taskSla = new TaskSla();
                        String table = "";
                        if (incident != null) {
                            taskSla.setIncident(incident);
                            table = Util.getFieldDisplay(incident);
                        } else if (scRequestItem != null) {
                            taskSla.setScRequestItem(scRequestItem);
                            table = Util.getFieldDisplay(scRequestItem);
                        } else if (scTask != null) {
                            taskSla.setScTask(scTask);
                            table = Util.getFieldDisplay(scTask);
                        }
                        String tagAction = "(Not Exist) ";
                        if (table != "") {
                            snTaskSlasSolver.add(taskSlaSolver);
                            /*System.out.println("taskSlaSolver.getPause_duration: ".concat(String.valueOf(taskSlaSolver.getPause_duration().length())));
                            System.out.println("taskSlaSolver.getPause_time: ".concat(String.valueOf(taskSlaSolver.getPause_time().length())));
                            System.out.println("taskSlaSolver.getTimezone: ".concat(String.valueOf(taskSlaSolver.getTimezone().length())));
                            System.out.println("taskSlaSolver.getSys_updated_on: ".concat(String.valueOf(taskSlaSolver.getSys_updated_on().length())));
                            System.out.println("taskSlaSolver.getBusiness_time_left: ".concat(String.valueOf(taskSlaSolver.getBusiness_time_left().length())));
                            System.out.println("taskSlaSolver.getDuration: ".concat(String.valueOf(taskSlaSolver.getDuration().length())));
                            System.out.println("taskSlaSolver.getTime_left: ".concat(String.valueOf(taskSlaSolver.getTime_left().length())));
                            System.out.println("taskSlaSolver.getSys_updated_by: ".concat(String.valueOf(taskSlaSolver.getSys_updated_by().length())));
                            System.out.println("taskSlaSolver.getSys_created_on: ".concat(String.valueOf(taskSlaSolver.getSys_created_on().length())));
                            System.out.println("taskSlaSolver.getPercentage: ".concat(String.valueOf(taskSlaSolver.getPercentage().length())));
                            System.out.println("taskSlaSolver.getOriginal_breach_time: ".concat(String.valueOf(taskSlaSolver.getOriginal_breach_time().length())));
                            System.out.println("taskSlaSolver.getSys_created_by: ".concat(String.valueOf(taskSlaSolver.getSys_created_by().length())));
                            System.out.println("taskSlaSolver.getBusiness_percentage: ".concat(String.valueOf(taskSlaSolver.getBusiness_percentage().length())));
                            System.out.println("taskSlaSolver.getEnd_time: ".concat(String.valueOf(taskSlaSolver.getEnd_time().length())));
                            System.out.println("taskSlaSolver.getSys_mod_count: ".concat(String.valueOf(taskSlaSolver.getSys_mod_count().length())));
                            System.out.println("taskSlaSolver.getBusiness_pause_duration: ".concat(String.valueOf(taskSlaSolver.getBusiness_pause_duration().length())));
                            System.out.println("taskSlaSolver.getStart_time: ".concat(String.valueOf(taskSlaSolver.getStart_time().length())));
                            System.out.println("taskSlaSolver.getBusiness_duration: ".concat(String.valueOf(taskSlaSolver.getBusiness_duration().length())));
                            System.out.println("taskSlaSolver.getStage: ".concat(String.valueOf(taskSlaSolver.getStage().length())));
                            System.out.println("taskSlaSolver.getPlanned_end_time: ".concat(String.valueOf(taskSlaSolver.getPlanned_end_time().length())));
                            System.out.println("taskSlaSolver.getHas_breached: ".concat(String.valueOf(taskSlaSolver.getHas_breached().length())));*/
                            taskSla.setPauseDuration(Util.isNull(taskSlaSolver.getPause_duration()));
                            taskSla.setPauseTime(Util.isNull(taskSlaSolver.getPause_time()));
                            taskSla.setTimezone(Util.isNull(taskSlaSolver.getTimezone()));
                            taskSla.setSysUpdatedOn(Util.isNull(taskSlaSolver.getSys_updated_on()));
                            taskSla.setBusinessTimeLeft(Util.isNull(taskSlaSolver.getBusiness_time_left()));
                            taskSla.setDuration(Util.isNull(taskSlaSolver.getDuration()));
                            taskSla.setTimeLeft(Util.isNull(taskSlaSolver.getTime_left()));
                            taskSla.setSysUpdatedBy(Util.isNull(taskSlaSolver.getSys_updated_by()));
                            taskSla.setSysCreatedOn(Util.isNull(taskSlaSolver.getSys_created_on()));
                            taskSla.setPercentage(Util.isNull(taskSlaSolver.getPercentage()));
                            taskSla.setOriginalBreachTime(Util.isNull(taskSlaSolver.getOriginal_breach_time()));
                            taskSla.setSysCreatedBy(Util.isNull(taskSlaSolver.getSys_created_by()));
                            taskSla.setBusinessPercentage(Util.isNull(taskSlaSolver.getBusiness_percentage()));
                            taskSla.setEndTime(Util.isNull(taskSlaSolver.getEnd_time()));
                            taskSla.setSysModCount(Util.isNull(taskSlaSolver.getSys_mod_count()));
                            taskSla.setActive(true);
                            taskSla.setBusinessPauseDuration(Util.isNull(taskSlaSolver.getBusiness_pause_duration()));
                            taskSla.setStartTime(Util.isNull(taskSlaSolver.getStart_time()));
                            taskSla.setBusinessDuration(Util.isNull(taskSlaSolver.getBusiness_duration()));
                            taskSla.setStage(Util.isNull(taskSlaSolver.getStage()));
                            taskSla.setPlannedEndTime(Util.isNull(taskSlaSolver.getPlanned_end_time()));
                            //taskSla.setHasBreached(Util.isNull(taskSlaSolver.getHas_breached()));
                            //taskSla.setTimezone(taskSlaSolver.getTimezone_source());
                            //taskSla.setType(taskSlaSolver.getType());
                            //taskSla.setVendor(taskSlaSolver.getVendor());
                            taskSla.setIntegrationId(taskSlaSolver.getSys_id());

                            Domain domain = getDomainByIntegrationId((JSONObject) taskSlaJson, SnTable.Domain.get(), App.Value());
                            if (domain != null)
                                taskSla.setDomain(domain);

                            ContractSla sla = getContractSlaByIntegrationId((JSONObject) taskSlaJson, "sla", App.Value());
                            if (sla != null) {
                                taskSla.setSla(sla);
                            }

                            CmnSchedule schedule = getCmnScheduleByIntegrationId((JSONObject) taskSlaJson, "schedule", App.Value());
                            if (schedule != null) {
                                taskSla.setSchedule(schedule);
                            }

                            TaskSla exists = taskSlaService.findByIntegrationId(taskSla.getIntegrationId());
                            tagAction = App.CreateConsole();
                            if (exists != null) {
                                taskSla.setId(exists.getId());
                                if (taskSla.getIncident() == null)
                                    taskSla.setIncident(exists.getIncident());
                                if (taskSla.getScRequestItem() == null)
                                    taskSla.setScRequestItem(exists.getScRequestItem());
                                if (taskSla.getScTask() == null)
                                    taskSla.setScTask(exists.getScTask());
                                tagAction = App.UpdateConsole();
                            }
                            Util.printData(tag, count[0], tagAction.concat(table), Util.getFieldDisplay(taskSla));

                            taskSlaService.save(taskSla);
                        } else {
                            Util.printData(tag, count[0], tagAction.concat(element));
                        }
                        count[0] = count[0] + 1;
                    } catch (Exception e) {
                        System.out.println(tag.concat("Exception (I) : ").concat(String.valueOf(e)));
                    }
                });

                apiResponse = mapper.readValue(result, APIResponse.class);

                APIExecutionStatus status = new APIExecutionStatus();
                status.setUri(EndPointSN.Catalog());
                status.setUserAPI(App.SNUser());
                status.setPasswordAPI(App.SNPassword());
                status.setError(apiResponse.getError());
                status.setMessage(apiResponse.getMessage());
                status.setExecutionTime(endTime);
                statusService.save(status);
            }


        } catch (Exception e) {
            System.out.println(tag.concat("Exception (II) : ").concat(String.valueOf(e)));
        }
        System.out.println(App.End());
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

        String tagAction = "Create";
        String tag = "[TaskSla] ";

        if (currentTaskSla == null) {
            response.put("mensaje", Errors.dataAccessExceptionUpdate.get(taskSlaRequest.getSys_id()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }

        try {
           /* System.out.println("taskSlaRequest.getActive()) : ".concat(Util.isNull(String.valueOf(taskSlaRequest.getActive()))));
            System.out.println("taskSlaRequest.getBusiness_duration()) : ".concat(Util.isNull(taskSlaRequest.getBusiness_duration())));
            System.out.println("taskSlaRequest.getBusiness_pause_duration()) : ".concat(Util.isNull(taskSlaRequest.getBusiness_pause_duration())));
            System.out.println("taskSlaRequest.getBusiness_percentage()) : ".concat(Util.isNull(taskSlaRequest.getBusiness_percentage())));
            System.out.println("taskSlaRequest.getBusiness_time_left()) : ".concat(Util.isNull(taskSlaRequest.getBusiness_time_left())));
            System.out.println("taskSlaRequest.getDuration()) : ".concat(Util.isNull(taskSlaRequest.getDuration())));
            System.out.println("taskSlaRequest.getEnd_time()) : ".concat(Util.isNull(taskSlaRequest.getEnd_time())));
            System.out.println("taskSlaRequest.getHas_breached()) : ".concat(Util.isNull(taskSlaRequest.getHas_breached())));
            System.out.println("taskSlaRequest.getOriginal_breach_time()) : ".concat(Util.isNull(taskSlaRequest.getOriginal_breach_time())));
            System.out.println("taskSlaRequest.getPause_duration()) : ".concat(Util.isNull(taskSlaRequest.getPause_duration())));
            System.out.println("taskSlaRequest.getPause_time()) : ".concat(Util.isNull(taskSlaRequest.getPause_time())));
            System.out.println("taskSlaRequest.getPercentage()) : ".concat(Util.isNull(taskSlaRequest.getPercentage())));
            System.out.println("taskSlaRequest.getPlanned_end_time()) : ".concat(Util.isNull(taskSlaRequest.getPlanned_end_time())));
            System.out.println("taskSlaRequest.getSchedule()) : ".concat(Util.isNull(taskSlaRequest.getSchedule())));
            System.out.println("taskSlaRequest.getSla()) : ".concat(Util.isNull(taskSlaRequest.getSla())));
            System.out.println("taskSlaRequest.getStage()) : ".concat(Util.isNull(taskSlaRequest.getStage())));
            System.out.println("taskSlaRequest.getStart_time()) : ".concat(Util.isNull(taskSlaRequest.getStart_time())));
            System.out.println("taskSlaRequest.getSys_created_by()) : ".concat(Util.isNull(taskSlaRequest.getSys_created_by())));
            System.out.println("taskSlaRequest.getSys_created_on()) : ".concat(Util.isNull(taskSlaRequest.getSys_created_on())));
            System.out.println("taskSlaRequest.getSys_domain()) : ".concat(Util.isNull(taskSlaRequest.getSys_domain())));
            System.out.println("taskSlaRequest.getSys_domain_path()) : ".concat(Util.isNull(taskSlaRequest.getSys_domain_path())));
            System.out.println("taskSlaRequest.getSys_id()) : ".concat(Util.isNull(taskSlaRequest.getSys_id())));
            System.out.println("taskSlaRequest.getSys_mod_count()) : ".concat(Util.isNull(taskSlaRequest.getSys_mod_count())));
            System.out.println("taskSlaRequest.getSys_tags()) : ".concat(Util.isNull(taskSlaRequest.getSys_tags())));
            System.out.println("taskSlaRequest.getSys_updated_by()) : ".concat(Util.isNull(taskSlaRequest.getSys_updated_by())));
            System.out.println("taskSlaRequest.getSys_updated_on()) : ".concat(Util.isNull(taskSlaRequest.getSys_updated_on())));
            System.out.println("taskSlaRequest.getTask()) : ".concat(Util.isNull(taskSlaRequest.getTask())));
            System.out.println("taskSlaRequest.getTime_left()) : ".concat(Util.isNull(taskSlaRequest.getTime_left())));
            System.out.println("taskSlaRequest.getTimezone()) : ".concat(Util.isNull(taskSlaRequest.getTimezone())));
            System.out.println("taskSlaRequest.getU_invalid_breach()) : ".concat(Util.isNull(taskSlaRequest.getU_invalid_breach())));
            System.out.println("taskSlaRequest.getU_invalid_code()) : ".concat(Util.isNull(taskSlaRequest.getU_invalid_code())));
            System.out.println("taskSlaRequest.getU_invalid_reason()) : ".concat(Util.isNull(taskSlaRequest.getU_invalid_reason())));
            System.out.println("taskSlaRequest.getU_measurable()) : ".concat(Util.isNull(taskSlaRequest.getU_measurable())));
            System.out.println("taskSlaRequest.getU_trigger_group()) : ".concat(Util.isNull(taskSlaRequest.getU_trigger_group())));*/
            TaskSla taskSla = new TaskSla();
            taskSla.setActive(taskSlaRequest.getActive());
            taskSla.setBusinessDuration(taskSlaRequest.getBusiness_duration());
            taskSla.setBusinessPauseDuration(taskSlaRequest.getBusiness_pause_duration());
            taskSla.setBusinessPercentage(taskSlaRequest.getBusiness_percentage());
            taskSla.setBusinessTimeLeft(taskSlaRequest.getBusiness_time_left());
            taskSla.setDuration(taskSlaRequest.getDuration());
            taskSla.setEndTime(taskSlaRequest.getEnd_time());
            // taskSla.setHasBreached(taskSlaRequest.getHas_breached());
            taskSla.setOriginalBreachTime(taskSlaRequest.getOriginal_breach_time());
            taskSla.setPauseDuration(taskSlaRequest.getPause_duration());
            taskSla.setPauseTime(taskSlaRequest.getPause_time());
            taskSla.setPercentage(taskSlaRequest.getPercentage());
            taskSla.setPlannedEndTime(taskSlaRequest.getPlanned_end_time());
            //taskSla.setSchedule(taskSlaRequest.getSchedule());
            //taskSla.setSla(taskSlaRequest.getSla());
            taskSla.setStage(taskSlaRequest.getStage());
            taskSla.setStartTime(taskSlaRequest.getStart_time());
            taskSla.setSysCreatedBy(taskSlaRequest.getSys_created_by());
            taskSla.setSysCreatedOn(taskSlaRequest.getSys_created_on());
            // taskSla.setDomain(taskSlaRequest.getSys_domain());
            taskSla.setIntegrationId(taskSlaRequest.getSys_id());
            taskSla.setSysModCount(taskSlaRequest.getSys_mod_count());
            taskSla.setSysUpdatedBy(taskSlaRequest.getSys_updated_by());
            taskSla.setSysUpdatedOn(taskSlaRequest.getSys_updated_on());
            //taskSla.setTask(taskSlaRequest.getTask());
            taskSla.setTimeLeft(taskSlaRequest.getTime_left());
            taskSla.setTimezone(taskSlaRequest.getTimezone());
            //taskSla.setUInvalidBreach(taskSlaRequest.getU_invalid_breach());
            //taskSla.setUInvalidCode(taskSlaRequest.getU_invalid_code());
            //taskSla.setUInvalidReason(taskSlaRequest.getU_invalid_reason());
            //taskSla.setUMeasurable(taskSlaRequest.getU_measurable());
            //taskSla.setUTriggerGroup(taskSlaRequest.getU_trigger_group());
            if (Util.hasData(taskSlaRequest.getSys_domain())) {
                Domain domain = domainService.findByIntegrationId(taskSlaRequest.getSys_domain());
                if (domain != null)
                    taskSla.setDomain(domain);
            }
            String element = "";
            if (taskSlaRequest.getElement().equals(SnTable.Incident.get())) {
                Incident incident = incidentService.findByIntegrationId(taskSlaRequest.getTask());
                if (incident != null) {
                    taskSla.setIncident(incident);
                    element = Util.getFieldDisplay(incident);
                }
            } else if (taskSlaRequest.getElement().equals(SnTable.ScRequestItem.get())) {
                ScRequestItem scRequestItem = scRequestItemService.findByIntegrationId(taskSlaRequest.getTask());
                if (scRequestItem != null) {
                    taskSla.setScRequestItem(scRequestItem);
                    element = Util.getFieldDisplay(scRequestItem);
                }
            } else if (taskSlaRequest.getElement().equals(SnTable.ScTask.get())) {
                ScTask scTask = scTaskService.findByIntegrationId(taskSlaRequest.getTask());
                if (scTask != null) {
                    taskSla.setScTask(scTask);
                    element = Util.getFieldDisplay(scTask);
                }
            }
            if (Util.hasData(taskSlaRequest.getSla())) {
                ContractSla sla = contractSlaService.findByIntegrationId(taskSlaRequest.getSla());
                if (sla != null) {
                    taskSla.setSla(sla);
                }
            }

            if (Util.hasData(taskSlaRequest.getSchedule())) {
                CmnSchedule schedule = cmnScheduleService.findByIntegrationId(taskSlaRequest.getSchedule());
                if (schedule != null) {
                    taskSla.setSchedule(schedule);
                }
            }

            TaskSla exists = taskSlaService.findByIntegrationId(taskSla.getIntegrationId());
            if (exists != null) {
                taskSla.setId(exists.getId());
                tagAction = "Update";
            }
            //Util.printData(tag, tagAction, Util.getFieldDisplay(taskSla), element);
            taskSlaUpdated = taskSlaService.save(taskSla);
        } catch (DataAccessException e) {
            System.out.println("error " + e.getMessage());
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


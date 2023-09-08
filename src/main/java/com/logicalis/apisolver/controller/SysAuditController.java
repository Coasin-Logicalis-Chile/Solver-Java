
package com.logicalis.apisolver.controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.logicalis.apisolver.model.*;
import com.logicalis.apisolver.model.enums.*;
import com.logicalis.apisolver.services.*;
import com.logicalis.apisolver.util.Rest;
import com.logicalis.apisolver.util.Util;
import com.logicalis.apisolver.view.SysAuditRequest;
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
import java.util.stream.Collectors;

@CrossOrigin(origins = {"${app.api.settings.cross-origin.urls}", "*"})
@RestController
@RequestMapping("/api/v1")
public class SysAuditController {

    @Autowired
    private ISysAuditService sysAuditService;
    @Autowired
    private IScRequestItemService scRequestItemService;
    @Autowired
    private IScRequestService scRequestService;
    @Autowired
    private IScTaskService scTaskService;
    @Autowired
    private ISysGroupService sysGroupService;
    @Autowired
    private ICiServiceService ciServiceService;
    @Autowired
    private IConfigurationItemService configurationItemService;
    @Autowired
    private ISysDocumentationService sysDocumentationService;
    @Autowired
    private IChoiceService choiceService;
    @Autowired
    private IAPIExecutionStatusService statusService;
    @Autowired
    private IJournalService journalService;
    @Autowired
    private IIncidentService incidentService;
    @Autowired
    private ISysUserService sysUserService;

    Util util = new Util();
    App app = new App();
    EndPointSN endPointSN = new EndPointSN();
    @GetMapping("/sysAudits")
    public List<SysAudit> index() {
        return sysAuditService.findAll();
    }

    @GetMapping("/sysAudit/{id}")
    public ResponseEntity<?> show(@PathVariable Long id) {

        SysAudit sysAudit = null;
        Map<String, Object> response = new HashMap<>();

        try {
            sysAudit = sysAuditService.findById(id);
        } catch (DataAccessException e) {
            response.put("mensaje", Errors.dataAccessExceptionQuery.get());
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (sysAudit == null) {
            response.put("mensaje", Messages.notExist.get(id.toString()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<SysAudit>(sysAudit, HttpStatus.OK);
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/sysAudit")
    public ResponseEntity<?> create(@RequestBody SysAudit sysAudit) {
        SysAudit newSysAudit = null;

        Map<String, Object> response = new HashMap<>();
        try {
            newSysAudit = sysAuditService.save(sysAudit);
        } catch (DataAccessException e) {
            response.put("mensaje", Errors.dataAccessExceptionInsert.get());
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", Messages.createOK.get());
        response.put("sysAudit", newSysAudit);

        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }

    @Secured("ROLE_ADMIN")
    @PutMapping("/sysAudit/{id}")
    public ResponseEntity<?> update(@RequestBody SysAudit sysAudit, @PathVariable Long id) {

        SysAudit currentSysAudit = sysAuditService.findById(id);
        SysAudit sysAuditUpdated = null;

        Map<String, Object> response = new HashMap<>();

        if (currentSysAudit == null) {
            response.put("mensaje", Errors.dataAccessExceptionUpdate.get(id.toString()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }

        try {
            currentSysAudit.setDocumentkey(sysAudit.getDocumentkey());
            sysAuditUpdated = sysAuditService.save(currentSysAudit);

        } catch (DataAccessException e) {
            response.put("mensaje", Errors.dataAccessExceptionUpdate.get());
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", Messages.UpdateOK.get());
        response.put("sysAudit", sysAuditUpdated);

        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/sysAudit/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {

        Map<String, Object> response = new HashMap<>();
        try {
            sysAuditService.delete(id);
        } catch (DataAccessException e) {
            response.put("mensaje", Errors.dataAccessExceptionDelete.get());
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", Messages.DeleteOK.get());
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
    }

    @GetMapping("/sysAudit/{integrationId}")
    public ResponseEntity<?> show(@PathVariable String integrationId) {

        SysAudit sysAudit = null;
        Map<String, Object> response = new HashMap<>();

        try {
            sysAudit = sysAuditService.findByIntegrationId(integrationId);
        } catch (DataAccessException e) {
            response.put("mensaje", Errors.dataAccessExceptionQuery.get());
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (sysAudit == null) {
            response.put("mensaje", Messages.notExist.get(integrationId));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<SysAudit>(sysAudit, HttpStatus.OK);
    }

    @GetMapping("/byInternalCheckpoint/{internalCheckpoint}")
    public ResponseEntity<?> findByInternalCheckpoint(@PathVariable String internalCheckpoint) {

        List<SysAudit> sysAudits = null;
        Map<String, Object> response = new HashMap<>();

        try {
            sysAudits = sysAuditService.findByInternalCheckpoint(internalCheckpoint);
        } catch (DataAccessException e) {
            response.put("mensaje", Errors.dataAccessExceptionQuery.get());
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (sysAudits == null) {
            response.put("mensaje", Messages.notExist.get(internalCheckpoint));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<List<SysAudit>>(sysAudits, HttpStatus.OK);
    }

    @GetMapping("/sysAuditsBySolver")
    public List<SysAuditRequest> show() {
        System.out.println(app.Start());
        APIResponse apiResponse = null;
        List<SysAuditRequest> sysAuditRequests = new ArrayList<>();
        List<SysDocumentation> sysDocumentations = sysDocumentationService.findAll();
        List<ChoiceFields> choicesIncident = choiceService.choicesByIncident();
        List<ChoiceFields> choicesScTask = choiceService.choicesByScTask();

        String[] sparmOffSets = util.offSets7500000();
        long startTime = 0;
        long endTime = 0;
        String tag = "[SysAudit] ";
        try {
            Rest rest = new Rest();
            startTime = System.currentTimeMillis();
            final int[] count = {1};
            for (String sparmOffSet : sparmOffSets) {

                String result = rest.responseByEndPoint(endPointSN.SysAudit().concat(sparmOffSet));
                System.out.println(tag.concat("(".concat(endPointSN.SysAudit().concat(sparmOffSet)).concat(")")));
                ObjectMapper mapper = new ObjectMapper();
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

                JSONParser parser = new JSONParser();
                JSONObject resultJson = new JSONObject();
                JSONArray ListSnSysAuditJson = new JSONArray();
                resultJson = (JSONObject) parser.parse(result);
                if (resultJson.get("result") != null)
                    ListSnSysAuditJson = (JSONArray) parser.parse(resultJson.get("result").toString());

                ListSnSysAuditJson.stream().forEach(snSysAuditJson -> {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    SysAuditRequest sysAuditRequest = new SysAuditRequest();

                    try {
                        sysAuditRequest = objectMapper.readValue(snSysAuditJson.toString(), SysAuditRequest.class);
                        SysAudit sysAudit = new SysAudit();
                        sysAuditRequests.add(sysAuditRequest);
                        sysAudit.setFieldname(sysAuditRequest.getFieldname());
                        sysAudit.setReason(sysAuditRequest.getReason());
                        sysAudit.setIntegrationId(sysAuditRequest.getSys_id());
                        sysAudit.setNewvalue(sysAuditRequest.getNewvalue());
                        sysAudit.setSysCreatedOn(sysAuditRequest.getSys_created_on());
                        sysAudit.setDocumentkey(sysAuditRequest.getDocumentkey());
                        sysAudit.setInternalCheckpoint(sysAuditRequest.getInternal_checkpoint());
                        sysAudit.setRecordCheckpoint(sysAuditRequest.getRecord_checkpoint());
                        sysAudit.setTablename(sysAuditRequest.getTablename());
                        sysAudit.setUserName(sysAuditRequest.getUser());
                        sysAudit.setOldvalue(sysAuditRequest.getOldvalue());
                        sysAudit.setSysCreatedBy(sysAuditRequest.getSys_created_by());
                        sysAudit.setActive(true);
                        String tagAction = app.CreateConsole();
                        SysAudit exists = sysAuditService.findByIntegrationId(sysAudit.getIntegrationId());
                        String element = "";
                        String company = "";
                        if (exists != null) {
                            sysAudit.setId(exists.getId());
                            tagAction = app.UpdateConsole();

                        }
                        util.printData(tag, count[0], tagAction.concat(util.getFieldDisplay(sysAudit)));
                        sysAuditService.save(sysAudit);

                        List<SysAudit> sysAudits = sysAuditService.findByInternalCheckpoint(sysAudit.getInternalCheckpoint()).stream().
                                filter(p -> !p.getFieldname().equals(Field.businessDuration.get()) &&
                                        !p.getFieldname().equals(Field.calendarDuration.get()) &&
                                        !p.getFieldname().equals(Field.CalendarStc.get()) &&
                                        !p.getFieldname().equals(Field.TimeWorked.get()) &&
                                        !p.getFieldname().equals(Field.BusinessStc.get())).
                                collect(Collectors.toList());
                        String template = app.TableData();
                        final String[] templateDetail = {""};
                        Journal journal = new Journal();

                        journal.setOrigin(app.OriginFieldChanges());
                        journal.setElement(sysAudit.getDocumentkey());
                        journal.setReviewed(true);
                        journal.setCreatedOn(sysAudit.getSysCreatedOn());
                        journal.setIntegrationId(sysAudit.getInternalCheckpoint());
                        journal.setInternalCheckpoint(sysAudit.getInternalCheckpoint());
                        journal.setActive(true);

                        if (sysAuditRequest.getTablename().equals(SnTable.Incident.get())) {
                            Incident incident = incidentService.findByIntegrationId(sysAudit.getDocumentkey());
                            if (incident != null) {
                                journal.setIncident(incident);
                                company = util.getFieldDisplay(incident.getCompany());
                                element = util.getFieldDisplay(incident);
                            }
                        } else if (sysAuditRequest.getTablename().equals(SnTable.ScRequestItem.get())) {
                            ScRequestItem scRequestItem = scRequestItemService.findByIntegrationId(sysAudit.getDocumentkey());
                            if (scRequestItem != null) {
                                journal.setScRequestItem(scRequestItem);
                                company = util.getFieldDisplay(scRequestItem.getCompany());
                                element = util.getFieldDisplay(scRequestItem);
                            }
                        } else if (sysAuditRequest.getTablename().equals(SnTable.ScRequest.get())) {
                            ScRequest scRequest = scRequestService.findByIntegrationId(sysAudit.getDocumentkey());
                            if (scRequest != null) {
                                journal.setScRequest(scRequest);
                                company = util.getFieldDisplay(scRequest.getCompany());
                                element = util.getFieldDisplay(scRequest);
                            }
                        } else if (sysAuditRequest.getTablename().equals(SnTable.ScTask.get())) {
                            ScTask scTask = scTaskService.findByIntegrationId(sysAudit.getDocumentkey());
                            ScRequestItem scRequestItem = scTask.getScRequestItem();
                            if (scRequestItem != null) {
                                journal.setScRequestItem(scRequestItem);
                                company = util.getFieldDisplay(scRequestItem.getCompany());
                                element = util.getFieldDisplay(scRequestItem);
                            }
                        }
                        if (util.hasData(sysAuditRequest.getUser())) {
                            SysUser createdBy = sysUserService.findByUserName(sysAuditRequest.getUser());
                            if (createdBy != null)
                                journal.setCreateBy(createdBy);
                        }
                        sysAudits.forEach(audit -> {
                            String field = "";
                            String newValue = audit.getNewvalue().trim().equals("") ? "[Vacío]" : audit.getNewvalue().trim();
                            String oldValue = audit.getOldvalue().trim().equals("") ? "[Vacío]" : audit.getOldvalue().trim();

                            List<SysDocumentation> documentations = sysDocumentations.stream().
                                    filter(p -> p.getElement().equals(audit.getFieldname())).
                                    collect(Collectors.toList());

                            if (documentations.size() > 0) {
                                field = documentations.get(0).getEs();
                            } else {
                                field = audit.getFieldname();
                            }

                            if ((audit.getFieldname().endsWith("_to") ||
                                    audit.getFieldname().endsWith("_by") ||
                                    audit.getFieldname().equals(Field.TaskFor.get()) ||
                                    audit.getFieldname().equals(Field.CallerId.get())
                            ) && !audit.getFieldname().contains("flag")) {
                                SysUser sysUser = sysUserService.findByIntegrationId(newValue);
                                if (sysUser != null) {
                                    newValue = sysUser.getName();
                                }
                                sysUser = sysUserService.findByIntegrationId(oldValue);
                                if (sysUser != null) {
                                    oldValue = sysUser.getName();
                                }

                            } else if ((audit.getFieldname().equals(Field.State.get()) ||
                                    audit.getFieldname().equals(Field.Priority.get()) ||
                                    audit.getFieldname().equals(Field.IncidentState.get()) ||
                                    audit.getFieldname().equals(Field.CloseCode.get()) ||
                                    audit.getFieldname().equals(Field.Impact.get()) ||
                                    audit.getFieldname().equals(Field.Urgency.get())) &&
                                    journal.getIncident() != null) {
                                List<ChoiceFields> choices = choicesIncident.stream().
                                        filter(p -> p.getElement().equals(audit.getFieldname()) && p.getValue().equals(audit.getNewvalue())).
                                        collect(Collectors.toList());
                                if (choices.size() > 0) {
                                    newValue = choices.get(0).getLabel();
                                }
                                choices = choicesIncident.stream().
                                        filter(p -> p.getElement().equals(audit.getFieldname()) && p.getValue().equals(audit.getOldvalue())).
                                        collect(Collectors.toList());
                                if (choices.size() > 0) {
                                    oldValue = choices.get(0).getLabel();
                                }

                            } else if ((audit.getFieldname().equals(Field.State.get()) ||
                                    audit.getFieldname().equals(Field.Priority.get()) ||
                                    audit.getFieldname().equals(Field.Stage.get())||
                                    audit.getFieldname().equals(Field.Impact.get()) ||
                                    audit.getFieldname().equals(Field.Urgency.get())) &&
                                    journal.getScRequestItem() != null) {
                                List<ChoiceFields> choices = choicesScTask.stream().
                                        filter(p -> p.getElement().equals(audit.getFieldname()) && p.getValue().equals(audit.getNewvalue())).
                                        collect(Collectors.toList());
                                if (choices.size() > 0) {
                                    newValue = choices.get(0).getLabel();
                                }

                                choices = choicesScTask.stream().
                                        filter(p -> p.getElement().equals(audit.getFieldname()) && p.getValue().equals(audit.getOldvalue())).
                                        collect(Collectors.toList());
                                if (choices.size() > 0) {
                                    oldValue = choices.get(0).getLabel();
                                }
                            } else if (audit.getFieldname().equals(Field.AssignmentGroup.get()) ||
                                    audit.getFieldname().equals(Field.ScalingGroup.get())) {
                                SysGroup sysGroup = sysGroupService.findByIntegrationId(audit.getNewvalue());
                                if (sysGroup != null) {
                                    newValue = sysGroup.getName();
                                }
                                sysGroup = sysGroupService.findByIntegrationId(audit.getOldvalue());
                                if (sysGroup != null) {
                                    oldValue = sysGroup.getName();
                                }
                            } else if (audit.getFieldname().equals(Field.SolverFlagResolvedBy.get()) ||
                                    audit.getFieldname().equals(Field.SolverFlagAssignedTo.get()) ||
                                    audit.getFieldname().equals(Field.SolverFlagClosedBy.get()) ||
                                    audit.getFieldname().equals(Field.PendingOtherGroup.get()) ||
                                    audit.getFieldname().equals(Field.NeedsAttention.get()) ||
                                    audit.getFieldname().equals(Field.MadeSla.get()) ||
                                    audit.getFieldname().equals(Field.Knowledge.get()) ||
                                    audit.getFieldname().equals(Field.Active.get())) {
                                if (newValue.trim().equals("1")) {
                                    newValue = "Verdadero";
                                } else if (newValue.trim().equals("0")) {
                                    newValue = "Falso";
                                }

                                if (oldValue.trim().equals("1")) {
                                    oldValue = "Verdadero";
                                } else if (oldValue.trim().equals("0")) {
                                    oldValue = "Falso";
                                }
                            } else if (audit.getFieldname().equals(Field.CmdbCi.get())) {
                                ConfigurationItem configurationItem = configurationItemService.findByIntegrationId(audit.getNewvalue());
                                if (configurationItem != null) {
                                    newValue = configurationItem.getName();
                                }
                                configurationItem = configurationItemService.findByIntegrationId(audit.getOldvalue());
                                if (configurationItem != null) {
                                    oldValue = configurationItem.getName();
                                }
                            } else if (audit.getFieldname().equals(Field.BusinessService.get())) {
                                CiService ciService = ciServiceService.findByIntegrationId(audit.getNewvalue());
                                if (ciService != null) {
                                    newValue = ciService.getName();
                                }
                                ciService = ciServiceService.findByIntegrationId(audit.getOldvalue());
                                if (ciService != null) {
                                    oldValue = ciService.getName();
                                }
                            } else if (util.dateValidator(audit.getNewvalue()) || util.dateValidator(audit.getOldvalue())) {
                                if (util.dateValidator(audit.getNewvalue())) {
                                    newValue = util.LocalDateTimeToString(util.getLocalDateTime(util.isNull(audit.getNewvalue())));
                                }
                                if (util.dateValidator(audit.getOldvalue())) {
                                    oldValue = util.LocalDateTimeToString(util.getLocalDateTime(util.isNull(audit.getOldvalue())));
                                }
                            }


                            templateDetail[0] = templateDetail[0].concat(app.TRData().
                                    replace("FIELD", field).
                                    replace("VALUE", newValue.concat("  \uD835\uDE5A\uD835\uDE67\uD835\uDE56  ".concat(oldValue))));
                        });

                        template = template.replace("TBODY", templateDetail[0]);
                        journal.setValue(template);
                        tagAction = app.CreateConsole();
                        Journal journalExists = journalService.findByIntegrationId(journal.getIntegrationId());
                        if (journalExists != null) {
                            journal.setId(journalExists.getId());
                            tagAction = app.UpdateConsole();
                        }

                        util.printData(tag, count[0], tagAction.concat(util.getFieldDisplay(journal)), element, company);
                        journalService.save(journal);
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
        return sysAuditRequests;
    }



    @PutMapping("/sysAuditSN")
    public ResponseEntity<?> update(@RequestBody SysAuditRequest sysAuditRequest) {
        SysAudit currentSysAudit = new SysAudit();
        List<SysDocumentation> sysDocumentations = sysDocumentationService.findAll();
        List<ChoiceFields> choicesIncident = choiceService.choicesByIncident();
        List<ChoiceFields> choicesScTask = choiceService.choicesByScTask();
        SysAudit sysAuditUpdated = null;
        Map<String, Object> response = new HashMap<>();
        Rest rest = new Rest();
        final int[] count = {1};

        if (currentSysAudit == null) {
            response.put("mensaje", Errors.dataAccessExceptionUpdate.get(sysAuditRequest.getSys_id()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }

        try {

            SysAudit sysAudit = new SysAudit();
            String tagAction = app.CreateConsole();
            String tag = "[SysAudit] ";
            sysAudit.setFieldname(sysAuditRequest.getFieldname());
            sysAudit.setReason(sysAuditRequest.getReason());
            sysAudit.setIntegrationId(sysAuditRequest.getSys_id());
            sysAudit.setNewvalue(sysAuditRequest.getNewvalue());
            sysAudit.setSysCreatedOn(sysAuditRequest.getSys_created_on());
            sysAudit.setDocumentkey(sysAuditRequest.getDocumentkey());
            sysAudit.setInternalCheckpoint(sysAuditRequest.getInternal_checkpoint());
            sysAudit.setRecordCheckpoint(sysAuditRequest.getRecord_checkpoint());
            sysAudit.setTablename(sysAuditRequest.getTablename());
            sysAudit.setUserName(sysAuditRequest.getUser());
            sysAudit.setOldvalue(sysAuditRequest.getOldvalue());
            sysAudit.setSysCreatedBy(sysAuditRequest.getSys_created_by());
            sysAudit.setActive(true);
            SysAudit exists = sysAuditService.findByIntegrationId(sysAudit.getIntegrationId());
            String element = "";
            String company = "";
            if (exists != null) {
                sysAudit.setId(exists.getId());
                tagAction = app.UpdateConsole();

            }
            util.printData(tag, count[0], tagAction.concat(util.getFieldDisplay(sysAudit)));
            sysAuditService.save(sysAudit);

            List<SysAudit> sysAudits = sysAuditService.findByInternalCheckpoint(sysAudit.getInternalCheckpoint()).stream().
                    filter(p -> !p.getFieldname().equals(Field.businessDuration.get()) &&
                            !p.getFieldname().equals(Field.calendarDuration.get()) &&
                            !p.getFieldname().equals(Field.CalendarStc.get()) &&
                            !p.getFieldname().equals(Field.TimeWorked.get()) &&
                            !p.getFieldname().equals(Field.BusinessStc.get())).
                    collect(Collectors.toList());
            String template = app.TableData();
            final String[] templateDetail = {""};
            Journal journal = new Journal();

            journal.setOrigin(app.OriginFieldChanges());
            journal.setElement(sysAudit.getDocumentkey());
            journal.setReviewed(true);
            journal.setCreatedOn(sysAudit.getSysCreatedOn());
            journal.setIntegrationId(sysAudit.getInternalCheckpoint());
            journal.setInternalCheckpoint(sysAudit.getInternalCheckpoint());
            journal.setActive(true);

            if (sysAuditRequest.getTablename().equals(SnTable.Incident.get())) {
                Incident incident = incidentService.findByIntegrationId(sysAudit.getDocumentkey());
                if (incident != null) {
                    journal.setIncident(incident);
                    company = util.getFieldDisplay(incident.getCompany());
                    element = util.getFieldDisplay(incident);
                }
            } else if (sysAuditRequest.getTablename().equals(SnTable.ScRequestItem.get())) {
                ScRequestItem scRequestItem = scRequestItemService.findByIntegrationId(sysAudit.getDocumentkey());
                if (scRequestItem != null) {
                    journal.setScRequestItem(scRequestItem);
                    company = util.getFieldDisplay(scRequestItem.getCompany());
                    element = util.getFieldDisplay(scRequestItem);
                }
            } else if (sysAuditRequest.getTablename().equals(SnTable.ScRequest.get())) {
                ScRequest scRequest = scRequestService.findByIntegrationId(sysAudit.getDocumentkey());
                if (scRequest != null) {
                    journal.setScRequest(scRequest);
                    company = util.getFieldDisplay(scRequest.getCompany());
                    element = util.getFieldDisplay(scRequest);
                }
            } else if (sysAuditRequest.getTablename().equals(SnTable.ScTask.get())) {
                ScTask scTask = scTaskService.findByIntegrationId(sysAudit.getDocumentkey());
                ScRequestItem scRequestItem = scTask.getScRequestItem();
                if (scRequestItem != null) {
                    journal.setScRequestItem(scRequestItem);
                    company = util.getFieldDisplay(scRequestItem.getCompany());
                    element = util.getFieldDisplay(scRequestItem);
                }
            }
            if (util.hasData(sysAuditRequest.getUser())) {
                SysUser createdBy = sysUserService.findByUserName(sysAuditRequest.getUser());
                if (createdBy != null)
                    journal.setCreateBy(createdBy);
            }
            sysAudits.forEach(audit -> {
                String field = "";
                String newValue = audit.getNewvalue().trim().equals("") ? "[Vacío]" : audit.getNewvalue().trim();
                String oldValue = audit.getOldvalue().trim().equals("") ? "[Vacío]" : audit.getOldvalue().trim();

                List<SysDocumentation> documentations = sysDocumentations.stream().
                        filter(p -> p.getElement().equals(audit.getFieldname())).
                        collect(Collectors.toList());

                if (documentations.size() > 0) {
                    field = documentations.get(0).getEs();
                } else {
                    field = audit.getFieldname();
                }

                if ((audit.getFieldname().endsWith("_to") ||
                        audit.getFieldname().endsWith("_by") ||
                        audit.getFieldname().equals(Field.TaskFor.get()) ||
                        audit.getFieldname().equals(Field.CallerId.get())
                ) && !audit.getFieldname().contains("flag")) {
                    SysUser sysUser = sysUserService.findByIntegrationId(newValue);
                    if (sysUser != null) {
                        newValue = sysUser.getName();
                    }
                    sysUser = sysUserService.findByIntegrationId(oldValue);
                    if (sysUser != null) {
                        oldValue = sysUser.getName();
                    }

                } else if ((audit.getFieldname().equals(Field.State.get()) ||
                        audit.getFieldname().equals(Field.Priority.get()) ||
                        audit.getFieldname().equals(Field.IncidentState.get()) ||
                        audit.getFieldname().equals(Field.CloseCode.get()) ||
                        audit.getFieldname().equals(Field.Impact.get()) ||
                        audit.getFieldname().equals(Field.Urgency.get())) &&
                        journal.getIncident() != null) {
                    List<ChoiceFields> choices = choicesIncident.stream().
                            filter(p -> p.getElement().equals(audit.getFieldname()) && p.getValue().equals(audit.getNewvalue())).
                            collect(Collectors.toList());
                    if (choices.size() > 0) {
                        newValue = choices.get(0).getLabel();
                    }
                    choices = choicesIncident.stream().
                            filter(p -> p.getElement().equals(audit.getFieldname()) && p.getValue().equals(audit.getOldvalue())).
                            collect(Collectors.toList());
                    if (choices.size() > 0) {
                        oldValue = choices.get(0).getLabel();
                    }

                } else if ((audit.getFieldname().equals(Field.State.get()) ||
                        audit.getFieldname().equals(Field.Priority.get()) ||
                        audit.getFieldname().equals(Field.Stage.get())||
                        audit.getFieldname().equals(Field.Impact.get()) ||
                        audit.getFieldname().equals(Field.Urgency.get())) &&
                        journal.getScRequestItem() != null) {
                    List<ChoiceFields> choices = choicesScTask.stream().
                            filter(p -> p.getElement().equals(audit.getFieldname()) && p.getValue().equals(audit.getNewvalue())).
                            collect(Collectors.toList());
                    if (choices.size() > 0) {
                        newValue = choices.get(0).getLabel();
                    }

                    choices = choicesScTask.stream().
                            filter(p -> p.getElement().equals(audit.getFieldname()) && p.getValue().equals(audit.getOldvalue())).
                            collect(Collectors.toList());
                    if (choices.size() > 0) {
                        oldValue = choices.get(0).getLabel();
                    }
                } else if (audit.getFieldname().equals(Field.AssignmentGroup.get()) ||
                        audit.getFieldname().equals(Field.ScalingGroup.get())) {
                    SysGroup sysGroup = sysGroupService.findByIntegrationId(audit.getNewvalue());
                    if (sysGroup != null) {
                        newValue = sysGroup.getName();
                    }
                    sysGroup = sysGroupService.findByIntegrationId(audit.getOldvalue());
                    if (sysGroup != null) {
                        oldValue = sysGroup.getName();
                    }
                } else if (audit.getFieldname().equals(Field.SolverFlagResolvedBy.get()) ||
                        audit.getFieldname().equals(Field.SolverFlagAssignedTo.get()) ||
                        audit.getFieldname().equals(Field.SolverFlagClosedBy.get()) ||
                        audit.getFieldname().equals(Field.PendingOtherGroup.get()) ||
                        audit.getFieldname().equals(Field.NeedsAttention.get()) ||
                        audit.getFieldname().equals(Field.MadeSla.get()) ||
                        audit.getFieldname().equals(Field.Knowledge.get()) ||
                        audit.getFieldname().equals(Field.Active.get())) {
                    if (newValue.trim().equals("1")) {
                        newValue = "Verdadero";
                    } else if (newValue.trim().equals("0")) {
                        newValue = "Falso";
                    }

                    if (oldValue.trim().equals("1")) {
                        oldValue = "Verdadero";
                    } else if (oldValue.trim().equals("0")) {
                        oldValue = "Falso";
                    }
                } else if (audit.getFieldname().equals(Field.CmdbCi.get())) {
                    ConfigurationItem configurationItem = configurationItemService.findByIntegrationId(audit.getNewvalue());
                    if (configurationItem != null) {
                        newValue = configurationItem.getName();
                    }
                    configurationItem = configurationItemService.findByIntegrationId(audit.getOldvalue());
                    if (configurationItem != null) {
                        oldValue = configurationItem.getName();
                    }
                } else if (audit.getFieldname().equals(Field.BusinessService.get())) {
                    CiService ciService = ciServiceService.findByIntegrationId(audit.getNewvalue());
                    if (ciService != null) {
                        newValue = ciService.getName();
                    }
                    ciService = ciServiceService.findByIntegrationId(audit.getOldvalue());
                    if (ciService != null) {
                        oldValue = ciService.getName();
                    }
                } else if (util.dateValidator(audit.getNewvalue()) || util.dateValidator(audit.getOldvalue())) {
                    if (util.dateValidator(audit.getNewvalue())) {
                        newValue = util.LocalDateTimeToString(util.getLocalDateTime(util.isNull(audit.getNewvalue())));
                    }
                    if (util.dateValidator(audit.getOldvalue())) {
                        oldValue = util.LocalDateTimeToString(util.getLocalDateTime(util.isNull(audit.getOldvalue())));
                    }
                }


                templateDetail[0] = templateDetail[0].concat(app.TRData().
                        replace("FIELD", field).
                        replace("VALUE", newValue.concat("  \uD835\uDE5A\uD835\uDE67\uD835\uDE56  ".concat(oldValue))));
            });

            template = template.replace("TBODY", templateDetail[0]);
            journal.setValue(template);
            tagAction = app.CreateConsole();
            Journal journalExists = journalService.findByIntegrationId(journal.getIntegrationId());
            if (journalExists != null) {
                journal.setId(journalExists.getId());
                tagAction = app.UpdateConsole();
            }

            util.printData(tag, count[0], tagAction.concat(util.getFieldDisplay(journal)), element, company);
            journalService.save(journal);
            count[0] = count[0] + 1;


        } catch (DataAccessException e) {
            System.out.println("error " + e.getMessage());
            response.put("mensaje", Errors.dataAccessExceptionUpdate.get());
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", Messages.UpdateOK.get());
        response.put("sysAudit", sysAuditUpdated);

        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }
}


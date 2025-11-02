package com.logicalis.apisolver.controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.logicalis.apisolver.model.*;
import com.logicalis.apisolver.model.enums.*;
import com.logicalis.apisolver.services.*;
import com.logicalis.apisolver.util.Rest;
import com.logicalis.apisolver.util.Util;
import com.logicalis.apisolver.view.SysAuditRequest;
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

import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin(origins = {"${app.api.settings.cross-origin.urls}", "*"})
@RestController
@RequestMapping("/api/v1")
@Slf4j
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
    @Autowired
    private Rest rest;

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
        log.info(App.Start());
        APIResponse apiResponse = null;
        List<SysAuditRequest> sysAuditRequests = new ArrayList<>();
        List<SysDocumentation> sysDocumentations = sysDocumentationService.findAll();
        List<ChoiceFields> choicesIncident = choiceService.choicesByIncident();
        List<ChoiceFields> choicesScTask = choiceService.choicesByScTask();
        String[] sparmOffSets = Util.offSets7500000();
        long startTime = 0;
        long endTime = 0;
        String tag = "[SysAudit] ";
        String result;
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        JSONParser parser = new JSONParser();
        JSONObject resultJson = new JSONObject();
        JSONArray ListSnSysAuditJson = new JSONArray();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        final SysAuditRequest[] sysAuditRequest = {new SysAuditRequest()};
        final SysAudit[] sysAudit = {new SysAudit()};
        final String[] tagAction = new String[1];
        final SysAudit[] exists = new SysAudit[1];
        final String[] element = new String[1];
        final String[] company = new String[1];
        final String[] template = new String[1];
        final Journal[] journal = {new Journal()};
        final String[] templateDetail = {""};
        final Incident[] incident = new Incident[1];
        final ScRequestItem[] scRequestItem = new ScRequestItem[1];
        final ScRequest[] scRequest = new ScRequest[1];
        final ScTask[] scTask = new ScTask[1];
        final SysUser[] createdBy = new SysUser[1];
        final String[] field = new String[1];
        final String[] newValue = new String[1];
        final String[] oldValue = new String[1];
        final List<SysAudit>[] sysAudits = new List[1];
        final List<SysDocumentation>[] documentations = new List[1];
        final List<ChoiceFields>[] choices = new List[1];
        final SysUser[] sysUser = new SysUser[1];
        final SysGroup[] sysGroup = new SysGroup[1];
        final ConfigurationItem[] configurationItem = new ConfigurationItem[1];
        final CiService[] ciService = new CiService[1];
        final Journal[] journalExists = new Journal[1];
        APIExecutionStatus status = new APIExecutionStatus();
        try {
            startTime = System.currentTimeMillis();
            final int[] count = {1};
            for (String sparmOffSet : sparmOffSets) {
                result = rest.responseByEndPoint(EndPointSN.SysAudit().concat(sparmOffSet));
                log.info(tag.concat("(".concat(EndPointSN.SysAudit().concat(sparmOffSet)).concat(")")));
                resultJson = (JSONObject) parser.parse(result);
                ListSnSysAuditJson.clear();
                if (resultJson.get("result") != null)
                    ListSnSysAuditJson = (JSONArray) parser.parse(resultJson.get("result").toString());
                ListSnSysAuditJson.stream().forEach(snSysAuditJson -> {
                    try {
                        sysAuditRequest[0] = objectMapper.readValue(snSysAuditJson.toString(), SysAuditRequest.class);
                        sysAuditRequests.add(sysAuditRequest[0]);
                        sysAudit[0] = new SysAudit();
                        sysAudit[0].setFieldname(sysAuditRequest[0].getFieldname());
                        sysAudit[0].setReason(sysAuditRequest[0].getReason());
                        sysAudit[0].setIntegrationId(sysAuditRequest[0].getSys_id());
                        sysAudit[0].setNewvalue(sysAuditRequest[0].getNewvalue());
                        sysAudit[0].setSysCreatedOn(sysAuditRequest[0].getSys_created_on());
                        sysAudit[0].setDocumentkey(sysAuditRequest[0].getDocumentkey());
                        sysAudit[0].setInternalCheckpoint(sysAuditRequest[0].getInternal_checkpoint());
                        sysAudit[0].setRecordCheckpoint(sysAuditRequest[0].getRecord_checkpoint());
                        sysAudit[0].setTablename(sysAuditRequest[0].getTablename());
                        sysAudit[0].setUserName(sysAuditRequest[0].getUser());
                        sysAudit[0].setOldvalue(sysAuditRequest[0].getOldvalue());
                        sysAudit[0].setSysCreatedBy(sysAuditRequest[0].getSys_created_by());
                        sysAudit[0].setActive(true);
                        tagAction[0] = App.CreateConsole();
                        exists[0] = sysAuditService.findByIntegrationId(sysAudit[0].getIntegrationId());
                        element[0] = "";
                        company[0] = "";
                        if (exists[0] != null) {
                            sysAudit[0].setId(exists[0].getId());
                            tagAction[0] = App.UpdateConsole();

                        }
                        Util.printData(tag, count[0], tagAction[0].concat(Util.getFieldDisplay(sysAudit[0])));
                        sysAuditService.save(sysAudit[0]);
                        sysAudits[0] = sysAuditService.findByInternalCheckpoint(sysAudit[0].getInternalCheckpoint()).stream().
                                filter(p -> !p.getFieldname().equals(Field.businessDuration.get()) &&
                                        !p.getFieldname().equals(Field.calendarDuration.get()) &&
                                        !p.getFieldname().equals(Field.CalendarStc.get()) &&
                                        !p.getFieldname().equals(Field.TimeWorked.get()) &&
                                        !p.getFieldname().equals(Field.BusinessStc.get())).
                                collect(Collectors.toList());
                        template[0] = App.TableData();
                        templateDetail[0] = "";
                        journal[0] = new Journal();
                        journal[0].setOrigin(App.OriginFieldChanges());
                        journal[0].setElement(sysAudit[0].getDocumentkey());
                        journal[0].setReviewed(true);
                        journal[0].setCreatedOn(sysAudit[0].getSysCreatedOn());
                        journal[0].setIntegrationId(sysAudit[0].getInternalCheckpoint());
                        journal[0].setInternalCheckpoint(sysAudit[0].getInternalCheckpoint());
                        journal[0].setActive(true);
                        if (sysAuditRequest[0].getTablename().equals(SnTable.Incident.get())) {
                            incident[0] = incidentService.findByIntegrationId(sysAudit[0].getDocumentkey());
                            if (incident[0] != null) {
                                journal[0].setIncident(incident[0]);
                                company[0] = Util.getFieldDisplay(incident[0].getCompany());
                                element[0] = Util.getFieldDisplay(incident[0]);
                            }
                        } else if (sysAuditRequest[0].getTablename().equals(SnTable.ScRequestItem.get())) {
                            scRequestItem[0] = scRequestItemService.findByIntegrationId(sysAudit[0].getDocumentkey());
                            if (scRequestItem[0] != null) {
                                journal[0].setScRequestItem(scRequestItem[0]);
                                company[0] = Util.getFieldDisplay(scRequestItem[0].getCompany());
                                element[0] = Util.getFieldDisplay(scRequestItem[0]);
                            }
                        } else if (sysAuditRequest[0].getTablename().equals(SnTable.ScRequest.get())) {
                            scRequest[0] = scRequestService.findByIntegrationId(sysAudit[0].getDocumentkey());
                            if (scRequest[0] != null) {
                                journal[0].setScRequest(scRequest[0]);
                                company[0] = Util.getFieldDisplay(scRequest[0].getCompany());
                                element[0] = Util.getFieldDisplay(scRequest[0]);
                            }
                        } else if (sysAuditRequest[0].getTablename().equals(SnTable.ScTask.get())) {
                            scTask[0] = scTaskService.findByIntegrationId(sysAudit[0].getDocumentkey());
                            scRequestItem[0] = scTask[0].getScRequestItem();
                            if (scRequestItem[0] != null) {
                                journal[0].setScRequestItem(scRequestItem[0]);
                                company[0] = Util.getFieldDisplay(scRequestItem[0].getCompany());
                                element[0] = Util.getFieldDisplay(scRequestItem[0]);
                            }
                        }
                        if (Util.hasData(sysAuditRequest[0].getUser())) {
                            createdBy[0] = sysUserService.findByUserName(sysAuditRequest[0].getUser());
                            if (createdBy[0] != null)
                                journal[0].setCreateBy(createdBy[0]);
                        }
                        sysAudits[0].forEach(audit -> {
                            field[0] = "";
                            newValue[0] = audit.getNewvalue().trim().equals("") ? "[Vacío]" : audit.getNewvalue().trim();
                            oldValue[0] = audit.getOldvalue().trim().equals("") ? "[Vacío]" : audit.getOldvalue().trim();

                            documentations[0] = sysDocumentations.stream().
                                    filter(p -> p.getElement().equals(audit.getFieldname())).
                                    collect(Collectors.toList());

                            if (documentations[0].size() > 0) {
                                field[0] = documentations[0].get(0).getEs();
                            } else {
                                field[0] = audit.getFieldname();
                            }

                            if ((audit.getFieldname().endsWith("_to") ||
                                    audit.getFieldname().endsWith("_by") ||
                                    audit.getFieldname().equals(Field.TaskFor.get()) ||
                                    audit.getFieldname().equals(Field.CallerId.get())
                            ) && !audit.getFieldname().contains("flag")) {
                                sysUser[0] = sysUserService.findByIntegrationId(newValue[0]);
                                if (sysUser[0] != null) {
                                    newValue[0] = sysUser[0].getName();
                                }
                                sysUser[0] = sysUserService.findByIntegrationId(oldValue[0]);
                                if (sysUser[0] != null) {
                                    oldValue[0] = sysUser[0].getName();
                                }

                            } else if ((audit.getFieldname().equals(Field.State.get()) ||
                                    audit.getFieldname().equals(Field.Priority.get()) ||
                                    audit.getFieldname().equals(Field.IncidentState.get()) ||
                                    audit.getFieldname().equals(Field.CloseCode.get()) ||
                                    audit.getFieldname().equals(Field.Impact.get()) ||
                                    audit.getFieldname().equals(Field.Urgency.get())) &&
                                    journal[0].getIncident() != null) {
                                choices[0] = choicesIncident.stream().
                                        filter(p -> p.getElement().equals(audit.getFieldname()) && p.getValue().equals(audit.getNewvalue())).
                                        collect(Collectors.toList());
                                if (choices[0].size() > 0) {
                                    newValue[0] = choices[0].get(0).getLabel();
                                }
                                choices[0] = choicesIncident.stream().
                                        filter(p -> p.getElement().equals(audit.getFieldname()) && p.getValue().equals(audit.getOldvalue())).
                                        collect(Collectors.toList());
                                if (choices[0].size() > 0) {
                                    oldValue[0] = choices[0].get(0).getLabel();
                                }

                            } else if ((audit.getFieldname().equals(Field.State.get()) ||
                                    audit.getFieldname().equals(Field.Priority.get()) ||
                                    audit.getFieldname().equals(Field.Stage.get())||
                                    audit.getFieldname().equals(Field.Impact.get()) ||
                                    audit.getFieldname().equals(Field.Urgency.get())) &&
                                    journal[0].getScRequestItem() != null) {
                                choices[0] = choicesScTask.stream().
                                        filter(p -> p.getElement().equals(audit.getFieldname()) && p.getValue().equals(audit.getNewvalue())).
                                        collect(Collectors.toList());
                                if (choices[0].size() > 0) {
                                    newValue[0] = choices[0].get(0).getLabel();
                                }

                                choices[0] = choicesScTask.stream().
                                        filter(p -> p.getElement().equals(audit.getFieldname()) && p.getValue().equals(audit.getOldvalue())).
                                        collect(Collectors.toList());
                                if (choices[0].size() > 0) {
                                    oldValue[0] = choices[0].get(0).getLabel();
                                }
                            } else if (audit.getFieldname().equals(Field.AssignmentGroup.get()) ||
                                    audit.getFieldname().equals(Field.ScalingGroup.get())) {
                                sysGroup[0] = sysGroupService.findByIntegrationId(audit.getNewvalue());
                                if (sysGroup[0] != null) {
                                    newValue[0] = sysGroup[0].getName();
                                }
                                sysGroup[0] = sysGroupService.findByIntegrationId(audit.getOldvalue());
                                if (sysGroup[0] != null) {
                                    oldValue[0] = sysGroup[0].getName();
                                }
                            } else if (audit.getFieldname().equals(Field.SolverFlagResolvedBy.get()) ||
                                    audit.getFieldname().equals(Field.SolverFlagAssignedTo.get()) ||
                                    audit.getFieldname().equals(Field.SolverFlagClosedBy.get()) ||
                                    audit.getFieldname().equals(Field.PendingOtherGroup.get()) ||
                                    audit.getFieldname().equals(Field.NeedsAttention.get()) ||
                                    audit.getFieldname().equals(Field.MadeSla.get()) ||
                                    audit.getFieldname().equals(Field.Knowledge.get()) ||
                                    audit.getFieldname().equals(Field.Active.get())) {
                                if (newValue[0].trim().equals("1")) {
                                    newValue[0] = "Verdadero";
                                } else if (newValue[0].trim().equals("0")) {
                                    newValue[0] = "Falso";
                                }
                                if (oldValue[0].trim().equals("1")) {
                                    oldValue[0] = "Verdadero";
                                } else if (oldValue[0].trim().equals("0")) {
                                    oldValue[0] = "Falso";
                                }
                            } else if (audit.getFieldname().equals(Field.CmdbCi.get())) {
                                configurationItem[0] = configurationItemService.findByIntegrationId(audit.getNewvalue());
                                if (configurationItem[0] != null) {
                                    newValue[0] = configurationItem[0].getName();
                                }
                                configurationItem[0] = configurationItemService.findByIntegrationId(audit.getOldvalue());
                                if (configurationItem[0] != null) {
                                    oldValue[0] = configurationItem[0].getName();
                                }
                            } else if (audit.getFieldname().equals(Field.BusinessService.get())) {
                                ciService[0] = ciServiceService.findByIntegrationId(audit.getNewvalue());
                                if (ciService[0] != null) {
                                    newValue[0] = ciService[0].getName();
                                }
                                ciService[0] = ciServiceService.findByIntegrationId(audit.getOldvalue());
                                if (ciService[0] != null) {
                                    oldValue[0] = ciService[0].getName();
                                }
                            } else if (Util.dateValidator(audit.getNewvalue()) || Util.dateValidator(audit.getOldvalue())) {
                                if (Util.dateValidator(audit.getNewvalue())) {
                                    newValue[0] = Util.LocalDateTimeToString(Util.getLocalDateTime(Util.isNull(audit.getNewvalue())));
                                }
                                if (Util.dateValidator(audit.getOldvalue())) {
                                    oldValue[0] = Util.LocalDateTimeToString(Util.getLocalDateTime(Util.isNull(audit.getOldvalue())));
                                }
                            }
                            templateDetail[0] = "";
                            templateDetail[0] = templateDetail[0].concat(App.TRData().
                                    replace("FIELD", field[0]).
                                    replace("VALUE", newValue[0].concat("  \uD835\uDE5A\uD835\uDE67\uD835\uDE56  ".concat(oldValue[0]))));
                        });

                        template[0] = template[0].replace("TBODY", templateDetail[0]);
                        journal[0].setValue(template[0]);
                        tagAction[0] = App.CreateConsole();
                        journalExists[0] = journalService.findByIntegrationId(journal[0].getIntegrationId());
                        if (journalExists[0] != null) {
                            journal[0].setId(journalExists[0].getId());
                            tagAction[0] = App.UpdateConsole();
                        }
                        Util.printData(tag, count[0], tagAction[0].concat(Util.getFieldDisplay(journal[0])), element[0], company[0]);
                        journalService.save(journal[0]);
                        count[0] = count[0] + 1;
                    } catch (Exception e) {
                        log.error(tag.concat("Exception (I) : ").concat(String.valueOf(e)));
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
            log.error(tag.concat("Exception (II) : ").concat(String.valueOf(e)));
        }
        log.info(App.End());
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
        final int[] count = {1};
        if (currentSysAudit == null) {
            response.put("mensaje", Errors.dataAccessExceptionUpdate.get(sysAuditRequest.getSys_id()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }
        try {
            SysAudit sysAudit = new SysAudit();
            String tagAction = App.CreateConsole();
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
                tagAction = App.UpdateConsole();
            }
            Util.printData(tag, count[0], tagAction.concat(Util.getFieldDisplay(sysAudit)));
            sysAuditService.save(sysAudit);

            List<SysAudit> sysAudits = sysAuditService.findByInternalCheckpoint(sysAudit.getInternalCheckpoint()).stream().
                    filter(p -> !p.getFieldname().equals(Field.businessDuration.get()) &&
                            !p.getFieldname().equals(Field.calendarDuration.get()) &&
                            !p.getFieldname().equals(Field.CalendarStc.get()) &&
                            !p.getFieldname().equals(Field.TimeWorked.get()) &&
                            !p.getFieldname().equals(Field.BusinessStc.get())).
                    collect(Collectors.toList());
            String template = App.TableData();
            final String[] templateDetail = {""};
            Journal journal = new Journal();

            journal.setOrigin(App.OriginFieldChanges());
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
                    company = Util.getFieldDisplay(incident.getCompany());
                    element = Util.getFieldDisplay(incident);
                }
            } else if (sysAuditRequest.getTablename().equals(SnTable.ScRequestItem.get())) {
                ScRequestItem scRequestItem = scRequestItemService.findByIntegrationId(sysAudit.getDocumentkey());
                if (scRequestItem != null) {
                    journal.setScRequestItem(scRequestItem);
                    company = Util.getFieldDisplay(scRequestItem.getCompany());
                    element = Util.getFieldDisplay(scRequestItem);
                }
            } else if (sysAuditRequest.getTablename().equals(SnTable.ScRequest.get())) {
                ScRequest scRequest = scRequestService.findByIntegrationId(sysAudit.getDocumentkey());
                if (scRequest != null) {
                    journal.setScRequest(scRequest);
                    company = Util.getFieldDisplay(scRequest.getCompany());
                    element = Util.getFieldDisplay(scRequest);
                }
            } else if (sysAuditRequest.getTablename().equals(SnTable.ScTask.get())) {
                ScTask scTask = scTaskService.findByIntegrationId(sysAudit.getDocumentkey());
                ScRequestItem scRequestItem = scTask.getScRequestItem();
                if (scRequestItem != null) {
                    journal.setScRequestItem(scRequestItem);
                    company = Util.getFieldDisplay(scRequestItem.getCompany());
                    element = Util.getFieldDisplay(scRequestItem);
                }
            }
            if (Util.hasData(sysAuditRequest.getUser())) {
                SysUser createdBy = sysUserService.findByUserName(sysAuditRequest.getUser());
                if (createdBy != null)
                    journal.setCreateBy(createdBy);
            }
            final String[] field = new String[1];
            final String[] newValue = new String[1];
            final String[] oldValue = new String[1];
            final List<SysDocumentation>[] documentations = new List[1];
            final SysUser[] sysUser = new SysUser[1];
            final List<ChoiceFields>[] choices = new List[1];
            final SysGroup[] sysGroup = new SysGroup[1];
            final ConfigurationItem[] configurationItem = new ConfigurationItem[1];
            final CiService[] ciService = new CiService[1];
            Journal journalExists;
            sysAudits.forEach(audit -> {
                field[0] = "";
                newValue[0] = audit.getNewvalue().trim().equals("") ? "[Vacío]" : audit.getNewvalue().trim();
                oldValue[0] = audit.getOldvalue().trim().equals("") ? "[Vacío]" : audit.getOldvalue().trim();

                documentations[0] = sysDocumentations.stream().
                        filter(p -> p.getElement().equals(audit.getFieldname())).
                        collect(Collectors.toList());
                if (documentations[0].size() > 0) {
                    field[0] = documentations[0].get(0).getEs();
                } else {
                    field[0] = audit.getFieldname();
                }
                if ((audit.getFieldname().endsWith("_to") ||
                        audit.getFieldname().endsWith("_by") ||
                        audit.getFieldname().equals(Field.TaskFor.get()) ||
                        audit.getFieldname().equals(Field.CallerId.get())
                ) && !audit.getFieldname().contains("flag")) {
                    sysUser[0] = sysUserService.findByIntegrationId(newValue[0]);
                    if (sysUser[0] != null) {
                        newValue[0] = sysUser[0].getName();
                    }
                    sysUser[0] = sysUserService.findByIntegrationId(oldValue[0]);
                    if (sysUser[0] != null) {
                        oldValue[0] = sysUser[0].getName();
                    }
                } else if ((audit.getFieldname().equals(Field.State.get()) ||
                        audit.getFieldname().equals(Field.Priority.get()) ||
                        audit.getFieldname().equals(Field.IncidentState.get()) ||
                        audit.getFieldname().equals(Field.CloseCode.get()) ||
                        audit.getFieldname().equals(Field.Impact.get()) ||
                        audit.getFieldname().equals(Field.Urgency.get())) &&
                        journal.getIncident() != null) {
                    choices[0] = choicesIncident.stream().
                            filter(p -> p.getElement().equals(audit.getFieldname()) && p.getValue().equals(audit.getNewvalue())).
                            collect(Collectors.toList());
                    if (choices[0].size() > 0) {
                        newValue[0] = choices[0].get(0).getLabel();
                    }
                    choices[0] = choicesIncident.stream().
                            filter(p -> p.getElement().equals(audit.getFieldname()) && p.getValue().equals(audit.getOldvalue())).
                            collect(Collectors.toList());
                    if (choices[0].size() > 0) {
                        oldValue[0] = choices[0].get(0).getLabel();
                    }
                } else if ((audit.getFieldname().equals(Field.State.get()) ||
                        audit.getFieldname().equals(Field.Priority.get()) ||
                        audit.getFieldname().equals(Field.Stage.get())||
                        audit.getFieldname().equals(Field.Impact.get()) ||
                        audit.getFieldname().equals(Field.Urgency.get())) &&
                        journal.getScRequestItem() != null) {
                    choices[0] = choicesScTask.stream().
                            filter(p -> p.getElement().equals(audit.getFieldname()) && p.getValue().equals(audit.getNewvalue())).
                            collect(Collectors.toList());
                    if (choices[0].size() > 0) {
                        newValue[0] = choices[0].get(0).getLabel();
                    }
                    choices[0] = choicesScTask.stream().
                            filter(p -> p.getElement().equals(audit.getFieldname()) && p.getValue().equals(audit.getOldvalue())).
                            collect(Collectors.toList());
                    if (choices[0].size() > 0) {
                        oldValue[0] = choices[0].get(0).getLabel();
                    }
                } else if (audit.getFieldname().equals(Field.AssignmentGroup.get()) ||
                        audit.getFieldname().equals(Field.ScalingGroup.get())) {
                    sysGroup[0] = sysGroupService.findByIntegrationId(audit.getNewvalue());
                    if (sysGroup[0] != null) {
                        newValue[0] = sysGroup[0].getName();
                    }
                    sysGroup[0] = sysGroupService.findByIntegrationId(audit.getOldvalue());
                    if (sysGroup[0] != null) {
                        oldValue[0] = sysGroup[0].getName();
                    }
                } else if (audit.getFieldname().equals(Field.SolverFlagResolvedBy.get()) ||
                        audit.getFieldname().equals(Field.SolverFlagAssignedTo.get()) ||
                        audit.getFieldname().equals(Field.SolverFlagClosedBy.get()) ||
                        audit.getFieldname().equals(Field.PendingOtherGroup.get()) ||
                        audit.getFieldname().equals(Field.NeedsAttention.get()) ||
                        audit.getFieldname().equals(Field.MadeSla.get()) ||
                        audit.getFieldname().equals(Field.Knowledge.get()) ||
                        audit.getFieldname().equals(Field.Active.get())) {
                    if (newValue[0].trim().equals("1")) {
                        newValue[0] = "Verdadero";
                    } else if (newValue[0].trim().equals("0")) {
                        newValue[0] = "Falso";
                    }
                    if (oldValue[0].trim().equals("1")) {
                        oldValue[0] = "Verdadero";
                    } else if (oldValue[0].trim().equals("0")) {
                        oldValue[0] = "Falso";
                    }
                } else if (audit.getFieldname().equals(Field.CmdbCi.get())) {
                    configurationItem[0] = configurationItemService.findByIntegrationId(audit.getNewvalue());
                    if (configurationItem[0] != null) {
                        newValue[0] = configurationItem[0].getName();
                    }
                    configurationItem[0] = configurationItemService.findByIntegrationId(audit.getOldvalue());
                    if (configurationItem[0] != null) {
                        oldValue[0] = configurationItem[0].getName();
                    }
                } else if (audit.getFieldname().equals(Field.BusinessService.get())) {
                    ciService[0] = ciServiceService.findByIntegrationId(audit.getNewvalue());
                    if (ciService[0] != null) {
                        newValue[0] = ciService[0].getName();
                    }
                    ciService[0] = ciServiceService.findByIntegrationId(audit.getOldvalue());
                    if (ciService[0] != null) {
                        oldValue[0] = ciService[0].getName();
                    }
                } else if (Util.dateValidator(audit.getNewvalue()) || Util.dateValidator(audit.getOldvalue())) {
                    if (Util.dateValidator(audit.getNewvalue())) {
                        newValue[0] = Util.LocalDateTimeToString(Util.getLocalDateTime(Util.isNull(audit.getNewvalue())));
                    }
                    if (Util.dateValidator(audit.getOldvalue())) {
                        oldValue[0] = Util.LocalDateTimeToString(Util.getLocalDateTime(Util.isNull(audit.getOldvalue())));
                    }
                }
                templateDetail[0] = templateDetail[0].concat(App.TRData().
                        replace("FIELD", field[0]).
                        replace("VALUE", newValue[0].concat("  \uD835\uDE5A\uD835\uDE67\uD835\uDE56  ".concat(oldValue[0]))));
            });
            template = template.replace("TBODY", templateDetail[0]);
            journal.setValue(template);
            tagAction = App.CreateConsole();
            journalExists = journalService.findByIntegrationId(journal.getIntegrationId());
            if (journalExists != null) {
                journal.setId(journalExists.getId());
                tagAction = App.UpdateConsole();
            }
            Util.printData(tag, count[0], tagAction.concat(Util.getFieldDisplay(journal)), element, company);
            journalService.save(journal);
            count[0] = count[0] + 1;
        } catch (DataAccessException e) {
            log.error("error " + e.getMessage());
            response.put("mensaje", Errors.dataAccessExceptionUpdate.get());
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", Messages.UpdateOK.get());
        response.put("sysAudit", sysAuditUpdated);
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }
}


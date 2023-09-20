package com.logicalis.apisolver.controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.logicalis.apisolver.model.*;
import com.logicalis.apisolver.model.enums.*;
import com.logicalis.apisolver.model.utilities.AttachmentInfo;
import com.logicalis.apisolver.services.*;
import com.logicalis.apisolver.util.Rest;
import com.logicalis.apisolver.util.Util;
import com.logicalis.apisolver.view.JournalRequest;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@CrossOrigin(origins = {"${app.api.settings.cross-origin.urls}", "*"})
@RestController
@RequestMapping("/api/v1")
public class JournalController {
    @Autowired
    private IJournalService journalService;
    @Autowired
    private IIncidentService incidentService;
    @Autowired
    private IAttachmentService attachmentService;
    @Autowired
    private IScRequestItemService scRequestItemService;
    @Autowired
    private IScRequestService scRequestService;
    @Autowired
    private ISysUserService sysUserService;
    @Autowired
    private IAPIExecutionStatusService statusService;
    @Autowired
    private Rest rest;
    @Autowired
    private Environment environment;
    @Async
    @PutMapping("/journalSN")
    public ResponseEntity<?> update(@RequestBody JournalRequest journalRequest) {
        Journal currentJournal = new Journal();
        Journal journalUpdated = null;
        Map<String, Object> response = new HashMap<>();
        if (currentJournal == null) {
            response.put("mensaje", Errors.dataAccessExceptionUpdate.get(journalRequest.getSys_id()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }
        try {
            Journal journal = new Journal();
            journal.setOrigin(journalRequest.getElement());
            journal.setElement(journalRequest.getElement_id());
            journal.setCreatedOn(journalRequest.getSys_created_on());
            journal.setIntegrationId(journalRequest.getSys_id());
            if (journalRequest.getName().equals(SnTable.Incident.get())) {
                Incident incident = incidentService.findByIntegrationId(journal.getElement());
                if (incident != null) {
                    journal.setIncident(incident);
                }
            } else if (journalRequest.getName().equals(SnTable.ScRequestItem.get())) {
                ScRequestItem scRequestItem = scRequestItemService.findByIntegrationId(journal.getElement());
                if (scRequestItem != null) {
                    journal.setScRequestItem(scRequestItem);
                }
            }

            if (Util.hasData(journalRequest.getSys_created_by())) {
                SysUser openedBy = sysUserService.findByUserName(journalRequest.getSys_created_by());
                if (openedBy != null)
                    journal.setCreateBy(openedBy);
            }
            Journal exists = journalService.findByIntegrationId(journal.getIntegrationId());
            if (exists != null) {
                journal.setId(exists.getId());
            }
            journal.setValue(Util.reeplaceImg(journalRequest.getValue()));
            journalService.save(journal);
        } catch (DataAccessException e) {
            System.out.println("error " + e.getMessage());
            response.put("mensaje", Errors.dataAccessExceptionUpdate.get());
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", Messages.UpdateOK.get());
        response.put("journal", journalUpdated);
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }

    @GetMapping("/journals")
    public List<Journal> index() {
        return journalService.findAll();
    }

    @GetMapping("/journalsInfoByIncident/{id}")
    public List<Journal> findInfoByIncident(@PathVariable Long id) {
        List<Journal> journals = journalService.findByIncident(incidentService.findById(id));
        List<AttachmentInfo> attachments = attachmentService.findAttachmentsByIncident(id);
        final String[] master = {""};
        final String[] target = {""};
        final String[] replacement = {""};
        final StringBuilder[] builder = {new StringBuilder()};
        final int[] startIndex = new int[0];
        final int[] stopIndex = new int[1];
        journals.forEach(journal -> {
            Pattern p = Pattern.compile(".*\\<[^>]+>.*");
            Matcher m = p.matcher(journal.getValue());
            if (m.find() && !journal.getReviewed()) {
                attachments.forEach(attachment -> {
                    journal.setParse(true);
                    journal.setReviewed(true);
                    master[0] = journal.getValue();
                    target[0] = EndPointSN.SrcAttachment().concat(attachment.getIntegrationId());
                    replacement[0] = environment.getProperty("setting.url.api.img").concat(":").concat(environment.getProperty("server.port")).concat(environment.getProperty("setting.api.version")).concat("/").concat("snAttachment/").concat(attachment.getId().toString());
                    startIndex[0] = master[0].indexOf(target[0]);
                    stopIndex[0] = startIndex[0] + target[0].length();
                    builder[0] = new StringBuilder(master[0]);
                    if (startIndex[0] != -1) {
                        builder[0].replace(startIndex[0], stopIndex[0], replacement[0]);

                        System.out.println("IMG VALUE READ");
                        System.out.println(builder[0].toString());
                        System.out.println("IMG VALUE PROCESS");
                        System.out.println(Util.reeplaceImg(builder[0].toString()));

                        journal.setValue(Util.reeplaceImg(builder[0].toString()));
                        journal.setValue(builder[0].toString());
                    }
                });
                journalService.save(journal);
            }
        });
        return journals;
    }

    @GetMapping("/journalsInfoIncidentOrScRequestItem")
    public List<Journal> findInfoByIncidentOrScRequestItem(@NotNull @RequestParam(value = "incident", required = true, defaultValue = "") String incident,
                                                           @NotNull @RequestParam(value = "scRequestItem", required = true, defaultValue = "") String scRequestItem) {
        List<JournalInfo> journalsInfo = journalService.findInfoByIncidentOrScRequestItem(incident, scRequestItem);
        List<Journal> journals = new ArrayList<>();
        List<AttachmentInfo> attachments = attachmentService.findAttachmentsByIncidentOrScRequestItem(incident, scRequestItem);
        final String[] master = {""};
        final String[] target = new String[1];
        final String[] replacement = new String[1];
        final int[] startIndex = new int[1];
        final int[] stopIndex = new int[1];
        journalsInfo.forEach(journalInfo -> {
            Journal journal = journalService.findById(journalInfo.getId());
            Pattern p = Pattern.compile(".*\\<[^>]+>.*");
            Matcher m = p.matcher(journal.getValue());
            if (m.find() && !journal.getReviewed()) {
                attachments.forEach(attachment -> {
                    journal.setParse(true);
                    journal.setReviewed(true);
                    master[0] = journal.getValue();
                    target[0] = EndPointSN.SrcAttachment().concat(attachment.getIntegrationId());
                    replacement[0] = environment.getProperty("setting.url.api.img").concat(":").concat(environment.getProperty("server.port")).concat(environment.getProperty("setting.api.version")).concat("/").concat("snAttachment/").concat(attachment.getId().toString());
                    startIndex[0] = master[0].indexOf(target[0]);
                    stopIndex[0] = startIndex[0] + target[0].length();
                    StringBuilder builder = new StringBuilder(master[0]);
                    if (startIndex[0] != -1) {
                        builder.replace(startIndex[0], stopIndex[0], replacement[0]);
                        journal.setValue(Util.reeplaceImg(builder.toString()));
                    }
                });
                journalService.save(journal);
            }
            journals.add(journal);
        });
        return journals;
    }

    @GetMapping("/journalsByScRequestItem/{id}")
    public List<Journal> findJournalsByScRequestItem(@PathVariable Long id) {
        List<Journal> journals = journalService.findJournalsByScRequestItem(scRequestItemService.findById(id));
        List<AttachmentInfo> attachments = attachmentService.findAttachmentsByScRequestItem(id);
        final Pattern[] p = new Pattern[1];
        final Matcher[] m = new Matcher[1];
        final String[] master = new String[1];
        final String[] target = new String[1];
        final String[] replacement = new String[1];
        final int[] startIndex = new int[1];
        final int[] stopIndex = new int[1];
        final StringBuilder[] builder = new StringBuilder[1];
        journals.forEach(journal -> {
            p[0] = Pattern.compile(".*\\<[^>]+>.*");
            m[0] = p[0].matcher(journal.getValue());
            if (m[0].find() && !journal.getReviewed()) {
                attachments.forEach(attachment -> {
                    journal.setParse(true);
                    journal.setReviewed(true);
                    master[0] = journal.getValue();
                    target[0] = EndPointSN.SrcAttachment().concat(attachment.getIntegrationId());
                    replacement[0] = environment.getProperty("setting.url.api.img").concat(":").concat(environment.getProperty("server.port")).concat(environment.getProperty("setting.api.version")).concat("/").concat("snAttachment/").concat(attachment.getId().toString());
                    startIndex[0] = master[0].indexOf(target[0]);
                    stopIndex[0] = startIndex[0] + target[0].length();
                    builder[0] = new StringBuilder(master[0]);
                    if (startIndex[0] != -1) {
                        builder[0].replace(startIndex[0], stopIndex[0], replacement[0]);
                        journal.setValue(builder[0].toString());
                    }
                });
                journalService.save(journal);
            }
        });
        return journals;
    }

    @GetMapping("/journalsByScTask/{id}")
    public List<Journal> findJournalsByScTask(@PathVariable Long id) {
        List<Journal> journals = journalService.findJournalsByScRequestItem(scRequestItemService.findById(id));
        List<AttachmentInfo> attachments = attachmentService.findAttachmentsByScRequestItem(id);
        final String[] master = new String[1];
        final String[] target = new String[1];
        final String[] replacement = new String[1];
        final int[] startIndex = new int[1];
        final int[] stopIndex = new int[1];
        final StringBuilder[] builder = new StringBuilder[1];
        final Pattern[] p = new Pattern[1];
        final Matcher[] m = new Matcher[1];
        journals.forEach(journal -> {
            p[0] = Pattern.compile(".*\\<[^>]+>.*");
            m[0] = p[0].matcher(journal.getValue());
            if (m[0].find() && !journal.getReviewed()) {
                attachments.forEach(attachment -> {
                    journal.setParse(true);
                    journal.setReviewed(true);
                    master[0] = journal.getValue();
                    target[0] = EndPointSN.SrcAttachment().concat(attachment.getIntegrationId());
                    replacement[0] = environment.getProperty("setting.url.api.img").concat(":").concat(environment.getProperty("server.port")).concat(environment.getProperty("setting.api.version")).concat("/").concat("snAttachment/").concat(attachment.getId().toString());
                    startIndex[0] = master[0].indexOf(target[0]);
                    stopIndex[0] = startIndex[0] + target[0].length();
                    builder[0] = new StringBuilder(master[0]);
                    if (startIndex[0] != -1) {
                        builder[0].replace(startIndex[0], stopIndex[0], replacement[0]);
                        journal.setValue(builder[0].toString());
                    }
                });
                journalService.save(journal);
            }
        });
        return journals;
    }

    @GetMapping("/journalsByIncident/{id}")
    public List<Journal> index(@PathVariable Long id) {
        return journalService.findByIncident(incidentService.findById(id));
    }

    @GetMapping("/journal/{id}")
    public ResponseEntity<?> show(@PathVariable Long id) {
        Journal journal = null;
        Map<String, Object> response = new HashMap<>();
        try {
            journal = journalService.findById(id);
        } catch (DataAccessException e) {
            response.put("mensaje", Errors.dataAccessExceptionQuery.get());
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (journal == null) {
            response.put("mensaje", Messages.notExist.get(id.toString()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Journal>(journal, HttpStatus.OK);
    }

    @PostMapping("/journal")
    public ResponseEntity<?> create(@RequestBody String json) {
        Map<String, Object> response = new HashMap<>();
        Journal addJournal = new Journal();
        try {
            Journal journal = new Journal();
            journal.setValue(Util.parseJson(json, "journal", "value"));
            journal.setOrigin(Util.parseJson(json, "journal", "origin"));
            journal.setName(Util.parseJson(json, "journal", "name"));
            journal.setElement(Util.parseJson(json, "journal", "element"));
            if (journal.getName().equals(SnTable.Incident.get())) {
                Incident incident = incidentService.findByIntegrationId(journal.getElement());
                if (incident != null)
                    journal.setIncident(incident);
            } else if (journal.getName().equals(SnTable.ScRequestItem.get())) {
                ScRequestItem scRequestItem = scRequestItemService.findByIntegrationId(journal.getElement());
                if (scRequestItem != null)
                    journal.setScRequestItem(scRequestItem);
            }
            journal.setCreateBy(sysUserService.findById(Util.parseIdJson(json, "journal", "createBy")));
            addJournal = rest.addJournal(EndPointSN.PostJournal(), journal);
            journalService.save(addJournal);
        } catch (DataAccessException e) {
            response.put("mensaje", Errors.dataAccessExceptionInsert.get());
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", Messages.createOK.get());
        response.put("journal", addJournal);
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }

    @Secured("ROLE_ADMIN")
    @PutMapping("/journal/{id}")
    public ResponseEntity<?> update(@RequestBody Journal journal, @PathVariable Long id) {
        Journal currentJournal = journalService.findById(id);
        Journal journalUpdated = null;
        Map<String, Object> response = new HashMap<>();
        if (currentJournal == null) {
            response.put("mensaje", Errors.dataAccessExceptionUpdate.get(id.toString()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }
        try {
            currentJournal.setElement(journal.getElement());
            journalUpdated = journalService.save(currentJournal);

        } catch (DataAccessException e) {
            response.put("mensaje", Errors.dataAccessExceptionUpdate.get());
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", Messages.UpdateOK.get());
        response.put("journal", journalUpdated);
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/journal/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            journalService.delete(id);
        } catch (DataAccessException e) {
            response.put("mensaje", Errors.dataAccessExceptionDelete.get());
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", Messages.DeleteOK.get());
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
    }

    @GetMapping("/journal/{integration_id}")
    public ResponseEntity<?> show(@PathVariable String integration_id) {
        Journal journal = null;
        Map<String, Object> response = new HashMap<>();
        try {
            journal = journalService.findByIntegrationId(integration_id);
        } catch (DataAccessException e) {
            response.put("mensaje", Errors.dataAccessExceptionQuery.get());
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (journal == null) {
            response.put("mensaje", "El registro ".concat(integration_id.concat(" no existe en la base de datos!")));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Journal>(journal, HttpStatus.OK);
    }

    @GetMapping("/journalsBySolver")
    public List<JournalRequest> show() {
        System.out.println(App.Start());
        APIResponse apiResponse = null;
        List<JournalRequest> journalRequests = new ArrayList<>();
        String[] sparmOffSets = Util.offSets1500000();
        long startTime = 0;
        long endTime = 0;
        String tag = "[Journal] ";
        try {
            startTime = System.currentTimeMillis();
            final int[] count = {1};
            String result;
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            final Journal[] journal = {new Journal()};
            final Journal[] exists = new Journal[1];
            final ScRequest[] scRequest = new ScRequest[1];
            final Incident[] incident = new Incident[1];
            JSONParser parser = new JSONParser();
            JSONObject resultJson = new JSONObject();
            JSONArray ListSnJournalJson = new JSONArray();
            final JournalRequest[] journalRequest = {new JournalRequest()};
            final String[] element = new String[1];
            final String[] company = new String[1];
            final ScRequestItem[] scRequestItem = new ScRequestItem[1];
            final String[] tagAction = new String[1];
            final boolean[] flag = new boolean[1];
            final SysUser[] openedBy = new SysUser[1];
            APIExecutionStatus status = new APIExecutionStatus();
            for (String sparmOffSet : sparmOffSets) {
                result = rest.responseByEndPoint(EndPointSN.Journal().concat(sparmOffSet));
                System.out.println(tag.concat("(".concat(EndPointSN.Journal().concat(sparmOffSet)).concat(")")));
                resultJson = (JSONObject) parser.parse(result);
                if (resultJson.get("result") != null)
                    ListSnJournalJson = (JSONArray) parser.parse(resultJson.get("result").toString());

                ListSnJournalJson.stream().forEach(snJournalJson -> {
                    flag[0] = false;
                    try {
                        journalRequest[0] = objectMapper.readValue(snJournalJson.toString(), JournalRequest.class);
                        if (journalRequest[0].getName().equals(SnTable.Incident.get())) {
                            incident[0] = incidentService.findByIntegrationId(journalRequest[0].getElement_id());
                            if (incident[0] != null) {
                                journal[0].setIncident(incident[0]);
                                flag[0] = incident[0].getCompany().getSolver();
                                company[0] = Util.getFieldDisplay(incident[0].getCompany());
                                element[0] = Util.getFieldDisplay(incident[0]);
                            }
                        } else if (journalRequest[0].getName().equals(SnTable.ScRequestItem.get())) {
                            scRequestItem[0] = scRequestItemService.findByIntegrationId(journalRequest[0].getElement_id());
                            if (scRequestItem[0] != null) {
                                journal[0].setScRequestItem(scRequestItem[0]);
                                flag[0] = scRequestItem[0].getCompany().getSolver();
                                company[0] = Util.getFieldDisplay(scRequestItem[0].getCompany());
                                element[0] = Util.getFieldDisplay(scRequestItem[0]);
                            }
                        } else if (journalRequest[0].getName().equals(SnTable.ScRequest.get())) {
                            scRequest[0] = scRequestService.findByIntegrationId(journalRequest[0].getElement_id());
                            if (scRequest[0] != null) {
                                journal[0].setScRequest(scRequest[0]);
                                flag[0] = scRequest[0].getCompany().getSolver();
                                company[0] = Util.getFieldDisplay(scRequest[0].getCompany());
                                element[0] = Util.getFieldDisplay(scRequest[0]);
                            }
                        }

                        if (flag[0]) {
                            journalRequests.add(journalRequest[0]);
                            journal[0].setOrigin(journalRequest[0].getElement());
                            journal[0].setElement(journalRequest[0].getElement_id());
                            journal[0].setValue(Util.reeplaceImg(journalRequest[0].getValue().trim()));
                            journal[0].setCreatedOn(journalRequest[0].getSys_created_on());
                            journal[0].setIntegrationId(journalRequest[0].getSys_id());
                            journal[0].setActive(true);
                            if (Util.hasData(journalRequest[0].getSys_created_by())) {
                                openedBy[0] = sysUserService.findByUserName(journalRequest[0].getSys_created_by());
                                if (openedBy[0] != null)
                                    journal[0].setCreateBy(openedBy[0]);
                            }
                            tagAction[0] = App.CreateConsole();
                            exists[0] = journalService.findByIntegrationId(journal[0].getIntegrationId());
                            if (exists[0] != null) {
                                journal[0].setId(exists[0].getId());
                                tagAction[0] = App.UpdateConsole();
                            }
                            Util.printData(tag, count[0], tagAction[0].concat(Util.getFieldDisplay(journal[0])), element[0], company[0]);
                            journalService.save(journal[0]);
                            count[0] = count[0] + 1;
                        }
                    } catch (Exception e) {
                        System.out.println(tag.concat("Exception (I) : ").concat(String.valueOf(e)));
                    }
                });
                apiResponse = objectMapper.readValue(result, APIResponse.class);
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
        return journalRequests;
    }

    @GetMapping("/journalsBySolverByQuery")
    public List<JournalRequest> show(String query, boolean flagQuery) {
        System.out.println(App.Start());
        APIResponse apiResponse = null;
        List<JournalRequest> journalRequests = new ArrayList<>();
        String[] sparmOffSets = Util.offSets1500000();
        long startTime = 0;
        long endTime = 0;
        String tag = "[Journal] ";
        String result;
        APIExecutionStatus status = new APIExecutionStatus();
        JSONParser parser = new JSONParser();
        JSONObject resultJson = new JSONObject();
        JSONArray ListSnJournalJson = new JSONArray();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        Journal journal = new Journal();
        final String[] element = {""};
        final String[] company = {""};
        final JournalRequest[] journalRequest = {new JournalRequest()};
        final boolean[] flag = new boolean[1];
        final Incident[] incident = new Incident[1];
        final ScRequestItem[] scRequestItem = new ScRequestItem[1];
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        final ScRequest[] scRequest = new ScRequest[1];
        final SysUser[] openedBy = new SysUser[1];
        final String[] tagAction = new String[1];
        final Journal[] exists = new Journal[1];
        try {
            startTime = System.currentTimeMillis();
            final int[] count = {1};
            for (String sparmOffSet : sparmOffSets) {
                result = rest.responseByEndPoint(EndPointSN.JournalByQuery().replace("QUERY", query).concat(sparmOffSet));
                System.out.println(tag.concat("(".concat(EndPointSN.JournalByQuery().replace("QUERY", query).concat(sparmOffSet))));
                resultJson = (JSONObject) parser.parse(result);
                if (resultJson.get("result") != null)
                    ListSnJournalJson = (JSONArray) parser.parse(resultJson.get("result").toString());

                ListSnJournalJson.stream().forEach(snJournalJson -> {
                    flag[0] = false;
                    try {
                        journalRequest[0] = objectMapper.readValue(snJournalJson.toString(), JournalRequest.class);
                        element[0] = "";
                        company[0] = "";
                        if (journalRequest[0].getName().equals(SnTable.Incident.get())) {
                            incident[0] = incidentService.findByIntegrationId(journalRequest[0].getElement_id());
                            if (incident[0] != null) {
                                journal.setIncident(incident[0]);
                                flag[0] = incident[0].getCompany().getSolver();
                                company[0] = Util.getFieldDisplay(incident[0].getCompany());
                                element[0] = Util.getFieldDisplay(incident[0]);
                            }
                        } else if (journalRequest[0].getName().equals(SnTable.ScRequestItem.get())) {
                            scRequestItem[0] = scRequestItemService.findByIntegrationId(journalRequest[0].getElement_id());
                            if (scRequestItem[0] != null) {
                                journal.setScRequestItem(scRequestItem[0]);
                                flag[0] = scRequestItem[0].getCompany().getSolver();
                                company[0] = Util.getFieldDisplay(scRequestItem[0].getCompany());
                                element[0] = Util.getFieldDisplay(scRequestItem[0]);
                            }
                        } else if (journalRequest[0].getName().equals(SnTable.ScRequest.get())) {
                            scRequest[0] = scRequestService.findByIntegrationId(journalRequest[0].getElement_id());
                            if (scRequest[0] != null) {
                                journal.setScRequest(scRequest[0]);
                                flag[0] = scRequest[0].getCompany().getSolver();
                                company[0] = Util.getFieldDisplay(scRequest[0].getCompany());
                                element[0] = Util.getFieldDisplay(scRequest[0]);
                            }
                        }

                        if (flag[0]) {
                            journalRequests.add(journalRequest[0]);
                            journal.setOrigin(journalRequest[0].getElement());
                            journal.setElement(journalRequest[0].getElement_id());
                            journal.setValue(Util.reeplaceImg(journalRequest[0].getValue().trim()));
                            journal.setCreatedOn(journalRequest[0].getSys_created_on());
                            journal.setIntegrationId(journalRequest[0].getSys_id());
                            journal.setActive(true);
                            if (Util.hasData(journalRequest[0].getSys_created_by())) {
                                openedBy[0] = sysUserService.findByUserName(journalRequest[0].getSys_created_by());
                                if (openedBy[0] != null)
                                    journal.setCreateBy(openedBy[0]);
                            }
                            tagAction[0] = App.CreateConsole();
                            exists[0] = journalService.findByIntegrationId(journal.getIntegrationId());
                            if (exists[0] != null) {
                                journal.setId(exists[0].getId());
                                tagAction[0] = App.UpdateConsole();
                            }
                            Util.printData(tag, count[0], tagAction[0].concat(Util.getFieldDisplay(journal)), element[0], company[0]);
                            journalService.save(journal);
                            count[0] = count[0] + 1;
                        }
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
        return journalRequests;
    }

    @GetMapping("/journalsBySolverAndQueryCreate")
    public List<JournalRequest> show(String query, boolean flagQuery, boolean flagA) {
        System.out.println(App.Start());
        APIResponse apiResponse = null;
        List<JournalRequest> journalRequests = new ArrayList<>();
        String[] sparmOffSets = Util.offSets1500000();
        long startTime = 0;
        long endTime = 0;
        final String[] element = {""};
        final String[] company = {""};
        String tag = "[Journal] ";
        final boolean[] flag = new boolean[1];
        JSONParser parser = new JSONParser();
        JSONObject resultJson = new JSONObject();
        JSONArray ListSnJournalJson = new JSONArray();
        APIExecutionStatus status = new APIExecutionStatus();
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        final JournalRequest[] journalRequest = {new JournalRequest()};
        final Journal[] exists = new Journal[1];
        final ScRequestItem[] scRequestItem = new ScRequestItem[1];
        Journal journal = new Journal();
        final ScRequest[] scRequest = new ScRequest[1];
        final SysUser[] openedBy = new SysUser[1];
        final String[] tagAction = new String[1];
        final Incident[] incident = new Incident[1];
        try {
            startTime = System.currentTimeMillis();
            final int[] count = {1};
            for (String sparmOffSet : sparmOffSets) {
                String result = rest.responseByEndPoint(EndPointSN.JournalByQuery().replace("QUERY", query).concat(sparmOffSet));
                System.out.println(tag.concat("(".concat(EndPointSN.JournalByQuery().replace("QUERY", query).concat(sparmOffSet))));
                resultJson = (JSONObject) parser.parse(result);
                if (resultJson.get("result") != null)
                    ListSnJournalJson = (JSONArray) parser.parse(resultJson.get("result").toString());
                ListSnJournalJson.stream().forEach(snJournalJson -> {
                    flag[0] = false;
                    try {
                        tagAction[0] = App.CreateConsole();
                        journalRequest[0] = mapper.readValue(snJournalJson.toString(), JournalRequest.class);
                        exists[0] = journalService.findByIntegrationId(journalRequest[0].getSys_id());
                        if (exists[0] != null) {
                            journal.setId(exists[0].getId());
                        } else {
                            element[0] = "";
                            company[0] = "";
                            if (journalRequest[0].getName().equals(SnTable.Incident.get())) {
                                incident[0] = incidentService.findByIntegrationId(journalRequest[0].getElement_id());
                                if (incident[0] != null) {
                                    journal.setIncident(incident[0]);
                                    flag[0] = incident[0].getCompany().getSolver();
                                    company[0] = Util.getFieldDisplay(incident[0].getCompany());
                                    element[0] = Util.getFieldDisplay(incident[0]);
                                }
                            } else if (journalRequest[0].getName().equals(SnTable.ScRequestItem.get())) {
                                scRequestItem[0] = scRequestItemService.findByIntegrationId(journalRequest[0].getElement_id());
                                if (scRequestItem[0] != null) {
                                    journal.setScRequestItem(scRequestItem[0]);
                                    flag[0] = scRequestItem[0].getCompany().getSolver();
                                    company[0] = Util.getFieldDisplay(scRequestItem[0].getCompany());
                                    element[0] = Util.getFieldDisplay(scRequestItem[0]);
                                }
                            } else if (journalRequest[0].getName().equals(SnTable.ScRequest.get())) {
                                scRequest[0] = scRequestService.findByIntegrationId(journalRequest[0].getElement_id());
                                if (scRequest[0] != null) {
                                    journal.setScRequest(scRequest[0]);
                                    flag[0] = scRequest[0].getCompany().getSolver();
                                    company[0] = Util.getFieldDisplay(scRequest[0].getCompany());
                                    element[0] = Util.getFieldDisplay(scRequest[0]);
                                }
                            }

                            if (flag[0]) {
                                journalRequests.add(journalRequest[0]);
                                journal.setOrigin(journalRequest[0].getElement());
                                journal.setElement(journalRequest[0].getElement_id());
                                journal.setValue(Util.reeplaceImg(journalRequest[0].getValue().trim()));
                                journal.setCreatedOn(journalRequest[0].getSys_created_on());
                                journal.setIntegrationId(journalRequest[0].getSys_id());
                                journal.setActive(true);
                                if (Util.hasData(journalRequest[0].getSys_created_by())) {
                                    openedBy[0] = sysUserService.findByUserName(journalRequest[0].getSys_created_by());
                                    if (openedBy[0] != null)
                                        journal.setCreateBy(openedBy[0]);
                                }
                                Util.printData(tag, count[0], tagAction[0].concat(Util.getFieldDisplay(journal)), element[0], company[0]);
                                journalService.save(journal);
                                count[0] = count[0] + 1;
                            }
                        }
                    } catch (Exception e) {
                      // System.out.println(tag.concat("Exception (I) : ").concat(String.valueOf(e)));
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
        return journalRequests;
    }

    @GetMapping("/journalsSNByElement")
    public List<JournalRequest> findJournalsSNByElement(String elementId) {
        System.out.println(App.Start());
        APIResponse apiResponse = null;
        List<JournalRequest> journalRequests = new ArrayList<>();
        long startTime = 0;
        long endTime = 0;
        String tag = "[Journal] ";
        APIExecutionStatus status = new APIExecutionStatus();
        final String[] element = {""};
        final String[] company = {""};
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //int startIndex;
        final int[] count = {1};
        String result;
        final Boolean[] flag = new Boolean[1];
        final int[] startIndex = new int[1];
        final int[] stopIndex = new int[1];
        final Pattern[] p = new Pattern[1];
        final Matcher[] m = new Matcher[1];
        final SysUser[] openedBy = new SysUser[1];
        final String[] tagAction = new String[1];
        final Journal[] exists = new Journal[1];
        final Incident[] incident = new Incident[1];
        final String[] target = new String[1];
        final String[] master = new String[1];
        final String[] replacement = new String[1];
        try {
            startTime = System.currentTimeMillis();
            result = rest.responseByEndPoint(EndPointSN.JournalByElement().concat(elementId));
            System.out.println(tag.concat("(".concat(EndPointSN.JournalByElement().concat(elementId)).concat(")")));
            List<Attachment> attachments = attachmentService.findByElement(elementId);
            JSONParser parser = new JSONParser();
            JSONObject resultJson = new JSONObject();
            JSONArray ListSnJournalJson = new JSONArray();
            resultJson = (JSONObject) parser.parse(result);
            final StringBuilder[] builder = new StringBuilder[1];
            final JournalRequest[] journalRequest = {new JournalRequest()};
            final ScRequest[] scRequest = new ScRequest[1];
            final ScRequestItem[] scRequestItem = new ScRequestItem[1];
            Journal journal = new Journal();
            if (resultJson.get("result") != null)
                ListSnJournalJson = (JSONArray) parser.parse(resultJson.get("result").toString());
            ListSnJournalJson.stream().forEach(snJournalJson -> {
                flag[0] = false;
                try {
                    journalRequest[0] = objectMapper.readValue(snJournalJson.toString(), JournalRequest.class);
                    if (journalRequest[0].getName().equals(SnTable.Incident.get())) {
                        incident[0] = incidentService.findByIntegrationId(journalRequest[0].getElement_id());
                        if (incident[0] != null) {
                            journal.setIncident(incident[0]);
                            flag[0] = incident[0].getCompany().getSolver();
                            company[0] = Util.getFieldDisplay(incident[0].getCompany());
                            element[0] = Util.getFieldDisplay(incident[0]);
                        }
                    } else if (journalRequest[0].getName().equals(SnTable.ScRequestItem.get())) {
                        scRequestItem[0] = scRequestItemService.findByIntegrationId(journalRequest[0].getElement_id());
                        if (scRequestItem[0] != null) {
                            journal.setScRequestItem(scRequestItem[0]);
                            flag[0] = scRequestItem[0].getCompany().getSolver();
                            company[0] = Util.getFieldDisplay(scRequestItem[0].getCompany());
                            element[0] = Util.getFieldDisplay(scRequestItem[0]);
                        }
                    } else if (journalRequest[0].getName().equals(SnTable.ScRequest.get())) {
                        scRequest[0] = scRequestService.findByIntegrationId(journalRequest[0].getElement_id());
                        if (scRequest[0] != null) {
                            journal.setScRequest(scRequest[0]);
                            flag[0] = scRequest[0].getCompany().getSolver();
                            company[0] = Util.getFieldDisplay(scRequest[0].getCompany());
                            element[0] = Util.getFieldDisplay(scRequest[0]);
                        }
                    }
                    if (flag[0]) {
                        journalRequests.add(journalRequest[0]);
                        journal.setOrigin(journalRequest[0].getElement());
                        journal.setElement(journalRequest[0].getElement_id());
                        journal.setValue(Util.reeplaceImg(journalRequest[0].getValue().trim()));
                        p[0] = Pattern.compile(".*\\<[^>]+>.*");
                        m[0] = p[0].matcher(journal.getValue());
                        if (m[0].find() && !journal.getReviewed()) {
                            attachments.forEach(attachment -> {
                                journal.setParse(true);
                                journal.setReviewed(true);
                                master[0] = journal.getValue();
                                target[0] = EndPointSN.SrcAttachment().concat(attachment.getIntegrationId());
                                replacement[0] = environment.getProperty("setting.url.api.img").concat(":").concat(environment.getProperty("server.port")).concat(environment.getProperty("setting.api.version")).concat("/").concat("snAttachment/").concat(attachment.getId().toString());
                                startIndex[0] = master[0].indexOf(target[0]);
                                stopIndex[0] = startIndex[0] + target[0].length();
                                builder[0] = new StringBuilder(master[0]);
                                if (startIndex[0] != -1) {
                                    builder[0].replace(startIndex[0], stopIndex[0], replacement[0]);
                                    journal.setValue(builder[0].toString());
                                }
                            });
                        }
                        journal.setCreatedOn(journalRequest[0].getSys_created_on());
                        journal.setIntegrationId(journalRequest[0].getSys_id());
                        journal.setActive(true);
                        if (Util.hasData(journalRequest[0].getSys_created_by())) {
                            openedBy[0] = sysUserService.findByUserName(journalRequest[0].getSys_created_by());
                            if (openedBy[0] != null)
                                journal.setCreateBy(openedBy[0]);
                        }
                        tagAction[0] = App.CreateConsole();
                        exists[0] = journalService.findByIntegrationId(journal.getIntegrationId());
                        if (exists[0] != null) {
                            journal.setId(exists[0].getId());
                            tagAction[0] = App.UpdateConsole();
                        }
                        Util.printData(tag, count[0], tagAction[0].concat(Util.getFieldDisplay(journal)), element[0], company[0]);
                        journalService.save(journal);
                        count[0] = count[0] + 1;
                    }
                } catch (Exception e) {
                    System.out.println(tag.concat("Exception (I) : ").concat(String.valueOf(e)));
                }
            });

            apiResponse = objectMapper.readValue(result, APIResponse.class);
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
        return journalRequests;
    }

    @GetMapping("/journal/{internalCheckpoint}")
    public ResponseEntity<?> findByInternalCheckpoint(@PathVariable String internalCheckpoint) {
        Journal journal = null;
        Map<String, Object> response = new HashMap<>();
        try {
            journal = journalService.findByInternalCheckpoint(internalCheckpoint);
        } catch (DataAccessException e) {
            response.put("mensaje", Errors.dataAccessExceptionQuery.get());
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (journal == null) {
            response.put("mensaje", "El registro ".concat(internalCheckpoint.concat(" no existe en la base de datos!")));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Journal>(journal, HttpStatus.OK);
    }
}


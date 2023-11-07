package com.logicalis.apisolver.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.logicalis.apisolver.model.*;
import com.logicalis.apisolver.model.enums.*;
import com.logicalis.apisolver.services.IAPIExecutionStatusService;
import com.logicalis.apisolver.services.IContractSlaService;
import com.logicalis.apisolver.services.IDomainService;
import com.logicalis.apisolver.util.Rest;
import com.logicalis.apisolver.util.Util;
import com.logicalis.apisolver.view.ContractSlaSolver;
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
public class ContractSlaController {
    @Autowired
    private IContractSlaService contractSlaService;
    @Autowired
    private IAPIExecutionStatusService statusService;
    @Autowired
    private IDomainService domainService;
    @Autowired
    private Rest rest;

    @GetMapping("/contractSlas")
    public List<ContractSla> index() {
        return contractSlaService.findAll();
    }

    @GetMapping("/contractSla/{id}")
    public ResponseEntity<?> show(@PathVariable Long id) {
        ContractSla contractSla = null;
        Map<String, Object> response = new HashMap<>();

        try {
            contractSla = contractSlaService.findById(id);
        } catch (DataAccessException e) {
            response.put("mensaje", Errors.dataAccessExceptionQuery.get());
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (contractSla == null) {
            response.put("mensaje", Messages.notExist.get(id.toString()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<ContractSla>(contractSla, HttpStatus.OK);
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/contractSla")
    public ResponseEntity<?> create(@RequestBody ContractSla contractSla) {
        ContractSla newContractSla = null;
        Map<String, Object> response = new HashMap<>();
        try {
            newContractSla = contractSlaService.save(contractSla);
        } catch (DataAccessException e) {
            response.put("mensaje", Errors.dataAccessExceptionInsert.get());
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", Messages.createOK.get());
        response.put("contractSla", newContractSla);
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }

    @Secured("ROLE_ADMIN")
    @PutMapping("/contractSla/{id}")
    public ResponseEntity<?> update(@RequestBody ContractSla contractSla, @PathVariable Long id) {
        ContractSla currentContractSla = contractSlaService.findById(id);
        ContractSla contractSlaUpdated = null;
        Map<String, Object> response = new HashMap<>();
        if (currentContractSla == null) {
            response.put("mensaje", Errors.dataAccessExceptionUpdate.get(id.toString()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }
        try {
            currentContractSla.setName(contractSla.getName());
            contractSlaUpdated = contractSlaService.save(currentContractSla);
        } catch (DataAccessException e) {
            response.put("mensaje", Errors.dataAccessExceptionUpdate.get());
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", Messages.UpdateOK.get());
        response.put("contractSla", contractSlaUpdated);
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/contractSla/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            contractSlaService.delete(id);
        } catch (DataAccessException e) {
            response.put("mensaje", Errors.dataAccessExceptionDelete.get());
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", Messages.DeleteOK.get());
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
    }

    @GetMapping("/contractSla/{integrationId}")
    public ResponseEntity<?> findByIntegrationId(@PathVariable String integrationId) {
        ContractSla contractSla = null;
        Map<String, Object> response = new HashMap<>();
        try {
            contractSla = contractSlaService.findByIntegrationId(integrationId);
        } catch (DataAccessException e) {
            response.put("mensaje", Errors.dataAccessExceptionQuery.get());
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (contractSla == null) {
            response.put("mensaje", Messages.notExist.get(integrationId.toString()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<ContractSla>(contractSla, HttpStatus.OK);
    }

    @GetMapping("/contractsSlaSN")
    public List<ContractSlaSolver> show() {
        log.info(App.Start());
        APIResponse apiResponse = null;
        List<ContractSlaSolver> snContractsSlaSolver = new ArrayList<>();
        long startTime = 0;
        long endTime = 0;
        String tag = "[ContractSla] ";
        final Domain[] domain = new Domain[1];
        final ContractSla[] exists = new ContractSla[1];
        final String[] tagAction = new String[1];
        APIExecutionStatus status = new APIExecutionStatus();
        final ContractSlaSolver[] contractSlaSolver = new ContractSlaSolver[1];
        try {
            startTime = System.currentTimeMillis();
            String result = rest.responseByEndPoint(EndPointSN.ContractSla());
            endTime = (System.currentTimeMillis() - startTime);
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JSONParser parser = new JSONParser();
            JSONObject resultJson = (JSONObject) parser.parse(result);
            JSONArray ListContractsSlaJson = new JSONArray();
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            final ContractSla[] contractSla = {new ContractSla()};
            if (resultJson.get("result") != null)
                ListContractsSlaJson = (JSONArray) parser.parse(resultJson.get("result").toString());
            final int[] count = {1};
            ListContractsSlaJson.forEach(contractSlaJson -> {
                try {
                    contractSlaSolver[0] = objectMapper.readValue(contractSlaJson.toString(), ContractSlaSolver.class);
                    snContractsSlaSolver.add(contractSlaSolver[0]);
                    contractSla[0] = new ContractSla();
                    contractSla[0].setActive(Boolean.parseBoolean(contractSlaSolver[0].getActive()));
                    contractSla[0].setCollection(contractSlaSolver[0].getCollection());
                    contractSla[0].setDuration(contractSlaSolver[0].getDuration());
                    contractSla[0].setDurationType(contractSlaSolver[0].getDuration_type());
                    contractSla[0].setEnableLogging(contractSlaSolver[0].getEnable_logging());
                    contractSla[0].setName(contractSlaSolver[0].getName());
                    contractSla[0].setRelativeDurationWorksOn(contractSlaSolver[0].getRelative_duration_works_on());
                    contractSla[0].setResetAction(contractSlaSolver[0].getReset_action());
                    contractSla[0].setRetroactive(contractSlaSolver[0].getRetroactive());
                    contractSla[0].setRetroactivePause(contractSlaSolver[0].getRetroactive_pause());
                    contractSla[0].setScheduleSource(contractSlaSolver[0].getSchedule_source());
                    contractSla[0].setScheduleSourceField(contractSlaSolver[0].getSchedule_source_field());
                    contractSla[0].setSysClassName(contractSlaSolver[0].getSys_class_name());
                    contractSla[0].setSysCreatedBy(contractSlaSolver[0].getSys_created_by());
                    contractSla[0].setSysCreatedOn(contractSlaSolver[0].getSys_created_on());
                    contractSla[0].setSysModCount(contractSlaSolver[0].getSys_mod_count());
                    contractSla[0].setSysName(contractSlaSolver[0].getSys_name());
                    contractSla[0].setSysPolicy(contractSlaSolver[0].getSys_policy());
                    contractSla[0].setSysUpdateName(contractSlaSolver[0].getSys_update_name());
                    contractSla[0].setSysUpdatedBy(contractSlaSolver[0].getSys_updated_by());
                    contractSla[0].setSysUpdatedOn(contractSlaSolver[0].getSys_updated_on());
                    contractSla[0].setTarget(contractSlaSolver[0].getTarget());
                    contractSla[0].setTimezone(contractSlaSolver[0].getTimezone());
                    contractSla[0].setTimezoneSource(contractSlaSolver[0].getTimezone_source());
                    contractSla[0].setType(contractSlaSolver[0].getType());
                    contractSla[0].setVendor(contractSlaSolver[0].getVendor());
                    contractSla[0].setWhenToCancel(contractSlaSolver[0].getWhen_to_cancel());
                    contractSla[0].setWhenToResume(contractSlaSolver[0].getWhen_to_resume());
                    contractSla[0].setIntegrationId(contractSlaSolver[0].getSys_id());
                    domain[0] = getDomainByIntegrationId((JSONObject) contractSlaJson, SnTable.Domain.get(), App.Value());

                    if (domain[0] != null)
                        contractSla[0].setDomain(domain[0]);
                    exists[0] = contractSlaService.findByIntegrationId(contractSla[0].getIntegrationId());
                    tagAction[0] = App.CreateConsole();

                    if (exists[0] != null) {
                        contractSla[0].setId(exists[0].getId());
                        tagAction[0] = App.UpdateConsole();
                    }

                    Util.printData(tag, count[0], tagAction[0].concat(Util.getFieldDisplay(contractSla[0])), Util.getFieldDisplay(contractSla[0].getDomain()));
                    contractSlaService.save(contractSla[0]);
                    count[0] = count[0] + 1;
                } catch (JsonProcessingException e) {
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
        } catch (Exception e) {
            log.error(tag.concat("Exception (II) : ").concat(String.valueOf(e)));
        }
        log.info(App.End());
        return snContractsSlaSolver;
    }

    @GetMapping("/contractsSlaSolver")
    public List<ContractSlaSolver> show(String query) {
        log.info(App.Start());
        APIResponse apiResponse = null;
        List<ContractSlaSolver> snContractsSlaSolver = new ArrayList<>();
        long startTime = 0;
        long endTime = 0;
        String tag = "[ContractSla] ";
        String[] sparmOffSets = Util.offSets50000();
        String result;
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        JSONParser parser = new JSONParser();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        JSONArray ListContractsSlaJson = new JSONArray();
        final ContractSlaSolver[] contractSlaSolver = new ContractSlaSolver[1];
        final int[] count = {1};
        JSONObject resultJson;
        final ContractSla[] contractSla = {new ContractSla()};
        final Domain[] domain = new Domain[1];
        final ContractSla[] exists = new ContractSla[1];
        final String[] tagAction = new String[1];
        APIExecutionStatus status = new APIExecutionStatus();
        for (String sparmOffSet : sparmOffSets) {
            try {
                startTime = System.currentTimeMillis();
                result = rest.responseByEndPoint(EndPointSN.ContractSlaByQuery().replace("QUERY", query).concat(sparmOffSet));
                endTime = (System.currentTimeMillis() - startTime);
                resultJson = (JSONObject) parser.parse(result);
                ListContractsSlaJson.clear();
                if (resultJson.get("result") != null)
                    ListContractsSlaJson = (JSONArray) parser.parse(resultJson.get("result").toString());

                ListContractsSlaJson.forEach(contractSlaJson -> {
                    try {
                        contractSlaSolver[0] = objectMapper.readValue(contractSlaJson.toString(), ContractSlaSolver.class);
                        snContractsSlaSolver.add(contractSlaSolver[0]);
                        contractSla[0] = new ContractSla();
                        contractSla[0].setActive(Boolean.parseBoolean(contractSlaSolver[0].getActive()));
                        contractSla[0].setCollection(contractSlaSolver[0].getCollection());
                        contractSla[0].setDuration(contractSlaSolver[0].getDuration());
                        contractSla[0].setDurationType(contractSlaSolver[0].getDuration_type());
                        contractSla[0].setEnableLogging(contractSlaSolver[0].getEnable_logging());
                        contractSla[0].setName(contractSlaSolver[0].getName());
                        contractSla[0].setRelativeDurationWorksOn(contractSlaSolver[0].getRelative_duration_works_on());
                        contractSla[0].setResetAction(contractSlaSolver[0].getReset_action());
                        contractSla[0].setRetroactive(contractSlaSolver[0].getRetroactive());
                        contractSla[0].setRetroactivePause(contractSlaSolver[0].getRetroactive_pause());
                        contractSla[0].setScheduleSource(contractSlaSolver[0].getSchedule_source());
                        contractSla[0].setScheduleSourceField(contractSlaSolver[0].getSchedule_source_field());
                        contractSla[0].setSysClassName(contractSlaSolver[0].getSys_class_name());
                        contractSla[0].setSysCreatedBy(contractSlaSolver[0].getSys_created_by());
                        contractSla[0].setSysCreatedOn(contractSlaSolver[0].getSys_created_on());
                        contractSla[0].setSysModCount(contractSlaSolver[0].getSys_mod_count());
                        contractSla[0].setSysName(contractSlaSolver[0].getSys_name());
                        contractSla[0].setSysPolicy(contractSlaSolver[0].getSys_policy());
                        contractSla[0].setSysUpdateName(contractSlaSolver[0].getSys_update_name());
                        contractSla[0].setSysUpdatedBy(contractSlaSolver[0].getSys_updated_by());
                        contractSla[0].setSysUpdatedOn(contractSlaSolver[0].getSys_updated_on());
                        contractSla[0].setTarget(contractSlaSolver[0].getTarget());
                        contractSla[0].setTimezone(contractSlaSolver[0].getTimezone());
                        contractSla[0].setTimezoneSource(contractSlaSolver[0].getTimezone_source());
                        contractSla[0].setType(contractSlaSolver[0].getType());
                        contractSla[0].setVendor(contractSlaSolver[0].getVendor());
                        contractSla[0].setWhenToCancel(contractSlaSolver[0].getWhen_to_cancel());
                        contractSla[0].setWhenToResume(contractSlaSolver[0].getWhen_to_resume());
                        contractSla[0].setIntegrationId(contractSlaSolver[0].getSys_id());
                        domain[0] = getDomainByIntegrationId((JSONObject) contractSlaJson, SnTable.Domain.get(), App.Value());
                        if (domain[0] != null)
                            contractSla[0].setDomain(domain[0]);
                        exists[0] = contractSlaService.findByIntegrationId(contractSla[0].getIntegrationId());
                        tagAction[0] = App.CreateConsole();
                        if (exists[0] != null) {
                            contractSla[0].setId(exists[0].getId());
                            tagAction[0] = App.UpdateConsole();
                        }

                        Util.printData(tag, count[0], tagAction[0].concat(Util.getFieldDisplay(contractSla[0])), Util.getFieldDisplay(contractSla[0].getDomain()));
                        contractSlaService.save(contractSla[0]);
                        count[0] = count[0] + 1;
                    } catch (JsonProcessingException e) {
                        log.error(tag.concat("Exception (I) : ").concat(String.valueOf(e)));
                    }
                });

                apiResponse = mapper.readValue(result, APIResponse.class);
                status.setUri(EndPointSN.ContractSla());
                status.setUserAPI(App.SNUser());
                status.setPasswordAPI(App.SNPassword());
                status.setError(apiResponse.getError());
                status.setMessage(apiResponse.getMessage());
                status.setExecutionTime(endTime);
                statusService.save(status);
            } catch (Exception e) {
                log.error(tag.concat("Exception (II) : ").concat(String.valueOf(e)));
            }
            log.info(App.End());
        }
        return snContractsSlaSolver;
    }

    public Domain getDomainByIntegrationId(JSONObject jsonObject, String levelOne, String levelTwo) {
        String integrationId = Util.getIdByJson(jsonObject, levelOne, levelTwo);
        if (Util.hasData(integrationId)) {
            return domainService.findByIntegrationId(integrationId);
        } else
            return null;
    }
}


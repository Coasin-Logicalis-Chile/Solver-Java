
package com.logicalis.apisolver.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.logicalis.apisolver.model.*;
import com.logicalis.apisolver.model.enums.*;
import com.logicalis.apisolver.model.servicenow.SnDepartment;
import com.logicalis.apisolver.services.IAPIExecutionStatusService;
import com.logicalis.apisolver.services.IContractSlaService;
import com.logicalis.apisolver.services.IDomainService;
import com.logicalis.apisolver.util.Rest;
import com.logicalis.apisolver.util.Util;
import com.logicalis.apisolver.view.ContractSlaSolver;
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
public class ContractSlaController {

    @Autowired
    private IContractSlaService contractSlaService;

    @Autowired
    private IAPIExecutionStatusService statusService;
    @Autowired
    private IDomainService domainService;
    private Util util = new Util();
    App app = new App();
    EndPointSN endPointSN = new EndPointSN();
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
        System.out.println(app.Start());
        APIResponse apiResponse = null;
        List<ContractSlaSolver> snContractsSlaSolver = new ArrayList<>();
        Util util = new Util();
        long startTime = 0;
        long endTime = 0;
        String tag = "[ContractSla] ";
        try {
            Rest rest = new Rest();
            startTime = System.currentTimeMillis();
            String result = rest.responseByEndPoint(endPointSN.ContractSla());
            endTime = (System.currentTimeMillis() - startTime);
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JSONParser parser = new JSONParser();
            JSONObject resultJson = (JSONObject) parser.parse(result);
            JSONArray ListContractsSlaJson = new JSONArray();
            if (resultJson.get("result") != null)
                ListContractsSlaJson = (JSONArray) parser.parse(resultJson.get("result").toString());
            final int[] count = {1};
            ListContractsSlaJson.forEach(contractSlaJson -> {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                try {
                    ContractSlaSolver contractSlaSolver = objectMapper.readValue(contractSlaJson.toString(), ContractSlaSolver.class);
                    snContractsSlaSolver.add(contractSlaSolver);
                    ContractSla contractSla = new ContractSla();
                    contractSla.setActive(Boolean.parseBoolean(contractSlaSolver.getActive()));
                    contractSla.setCollection(contractSlaSolver.getCollection());
                    contractSla.setDuration(contractSlaSolver.getDuration());
                    contractSla.setDurationType(contractSlaSolver.getDuration_type());
                    contractSla.setEnableLogging(contractSlaSolver.getEnable_logging());
                    contractSla.setName(contractSlaSolver.getName());
                    contractSla.setRelativeDurationWorksOn(contractSlaSolver.getRelative_duration_works_on());
                    contractSla.setResetAction(contractSlaSolver.getReset_action());
                    contractSla.setRetroactive(contractSlaSolver.getRetroactive());
                    contractSla.setRetroactivePause(contractSlaSolver.getRetroactive_pause());
                    contractSla.setScheduleSource(contractSlaSolver.getSchedule_source());
                    contractSla.setScheduleSourceField(contractSlaSolver.getSchedule_source_field());
                    contractSla.setSysClassName(contractSlaSolver.getSys_class_name());
                    contractSla.setSysCreatedBy(contractSlaSolver.getSys_created_by());
                    contractSla.setSysCreatedOn(contractSlaSolver.getSys_created_on());
                    contractSla.setSysModCount(contractSlaSolver.getSys_mod_count());
                    contractSla.setSysName(contractSlaSolver.getSys_name());
                    contractSla.setSysPolicy(contractSlaSolver.getSys_policy());
                    contractSla.setSysUpdateName(contractSlaSolver.getSys_update_name());
                    contractSla.setSysUpdatedBy(contractSlaSolver.getSys_updated_by());
                    contractSla.setSysUpdatedOn(contractSlaSolver.getSys_updated_on());
                    contractSla.setTarget(contractSlaSolver.getTarget());
                    contractSla.setTimezone(contractSlaSolver.getTimezone());
                    contractSla.setTimezoneSource(contractSlaSolver.getTimezone_source());
                    contractSla.setType(contractSlaSolver.getType());
                    contractSla.setVendor(contractSlaSolver.getVendor());
                    contractSla.setWhenToCancel(contractSlaSolver.getWhen_to_cancel());
                    contractSla.setWhenToResume(contractSlaSolver.getWhen_to_resume());
                    contractSla.setIntegrationId(contractSlaSolver.getSys_id());
                    Domain domain = getDomainByIntegrationId((JSONObject) contractSlaJson, SnTable.Domain.get(), app.Value());

                    if (domain != null)
                        contractSla.setDomain(domain);
                    ContractSla exists = contractSlaService.findByIntegrationId(contractSla.getIntegrationId());
                    String tagAction = app.CreateConsole();

                    if (exists != null) {
                        contractSla.setId(exists.getId());
                        tagAction = app.UpdateConsole();
                    }

                    util.printData(tag, count[0], tagAction.concat(util.getFieldDisplay(contractSla)), util.getFieldDisplay(contractSla.getDomain()));
                    contractSlaService.save(contractSla);
                    count[0] = count[0] + 1;
                } catch (JsonProcessingException e) {
                    System.out.println(tag.concat("Exception (I) : ").concat(String.valueOf(e)));
                }
            });

            apiResponse = mapper.readValue(result, APIResponse.class);

            APIExecutionStatus status = new APIExecutionStatus();
            status.setUri(endPointSN.Catalog());
            status.setUserAPI(app.SNUser());
            status.setPasswordAPI(app.SNPassword());
            status.setError(apiResponse.getError());
            status.setMessage(apiResponse.getMessage());
            status.setExecutionTime(endTime);
            statusService.save(status);


        } catch (Exception e) {
            System.out.println(tag.concat("Exception (II) : ").concat(String.valueOf(e)));
        }
        System.out.println(app.End());
        return snContractsSlaSolver;
    }


    @GetMapping("/contractsSlaSolver")
    public List<ContractSlaSolver> show(String query) {
        System.out.println(app.Start());
        APIResponse apiResponse = null;
        List<ContractSlaSolver> snContractsSlaSolver = new ArrayList<>();
        Util util = new Util();
        long startTime = 0;
        long endTime = 0;
        String tag = "[ContractSla] ";
        String[] sparmOffSets = util.offSets50000();

        for (String sparmOffSet : sparmOffSets) {

            try {
                Rest rest = new Rest();
                startTime = System.currentTimeMillis();
                String result = rest.responseByEndPoint(endPointSN.ContractSlaByQuery().replace("QUERY", query).concat(sparmOffSet));
                endTime = (System.currentTimeMillis() - startTime);
                ObjectMapper mapper = new ObjectMapper();
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                JSONParser parser = new JSONParser();
                JSONObject resultJson = (JSONObject) parser.parse(result);
                JSONArray ListContractsSlaJson = new JSONArray();
                if (resultJson.get("result") != null)
                    ListContractsSlaJson = (JSONArray) parser.parse(resultJson.get("result").toString());
                final int[] count = {1};
                ListContractsSlaJson.forEach(contractSlaJson -> {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    try {
                        ContractSlaSolver contractSlaSolver = objectMapper.readValue(contractSlaJson.toString(), ContractSlaSolver.class);
                        snContractsSlaSolver.add(contractSlaSolver);
                        ContractSla contractSla = new ContractSla();
                        contractSla.setActive(Boolean.parseBoolean(contractSlaSolver.getActive()));
                        contractSla.setCollection(contractSlaSolver.getCollection());
                        contractSla.setDuration(contractSlaSolver.getDuration());
                        contractSla.setDurationType(contractSlaSolver.getDuration_type());
                        contractSla.setEnableLogging(contractSlaSolver.getEnable_logging());
                        contractSla.setName(contractSlaSolver.getName());
                        contractSla.setRelativeDurationWorksOn(contractSlaSolver.getRelative_duration_works_on());
                        contractSla.setResetAction(contractSlaSolver.getReset_action());
                        contractSla.setRetroactive(contractSlaSolver.getRetroactive());
                        contractSla.setRetroactivePause(contractSlaSolver.getRetroactive_pause());
                        contractSla.setScheduleSource(contractSlaSolver.getSchedule_source());
                        contractSla.setScheduleSourceField(contractSlaSolver.getSchedule_source_field());
                        contractSla.setSysClassName(contractSlaSolver.getSys_class_name());
                        contractSla.setSysCreatedBy(contractSlaSolver.getSys_created_by());
                        contractSla.setSysCreatedOn(contractSlaSolver.getSys_created_on());
                        contractSla.setSysModCount(contractSlaSolver.getSys_mod_count());
                        contractSla.setSysName(contractSlaSolver.getSys_name());
                        contractSla.setSysPolicy(contractSlaSolver.getSys_policy());
                        contractSla.setSysUpdateName(contractSlaSolver.getSys_update_name());
                        contractSla.setSysUpdatedBy(contractSlaSolver.getSys_updated_by());
                        contractSla.setSysUpdatedOn(contractSlaSolver.getSys_updated_on());
                        contractSla.setTarget(contractSlaSolver.getTarget());
                        contractSla.setTimezone(contractSlaSolver.getTimezone());
                        contractSla.setTimezoneSource(contractSlaSolver.getTimezone_source());
                        contractSla.setType(contractSlaSolver.getType());
                        contractSla.setVendor(contractSlaSolver.getVendor());
                        contractSla.setWhenToCancel(contractSlaSolver.getWhen_to_cancel());
                        contractSla.setWhenToResume(contractSlaSolver.getWhen_to_resume());
                        contractSla.setIntegrationId(contractSlaSolver.getSys_id());
                        Domain domain = getDomainByIntegrationId((JSONObject) contractSlaJson, SnTable.Domain.get(), app.Value());
                        if (domain != null)
                            contractSla.setDomain(domain);
                        ContractSla exists = contractSlaService.findByIntegrationId(contractSla.getIntegrationId());
                        String tagAction = app.CreateConsole();
                        if (exists != null) {
                            contractSla.setId(exists.getId());
                            tagAction = app.UpdateConsole();
                        }

                        util.printData(tag, count[0], tagAction.concat(util.getFieldDisplay(contractSla)), util.getFieldDisplay(contractSla.getDomain()));
                        contractSlaService.save(contractSla);
                        count[0] = count[0] + 1;
                    } catch (JsonProcessingException e) {
                        System.out.println(tag.concat("Exception (I) : ").concat(String.valueOf(e)));
                    }
                });

                apiResponse = mapper.readValue(result, APIResponse.class);

                APIExecutionStatus status = new APIExecutionStatus();
                status.setUri(endPointSN.ContractSla());
                status.setUserAPI(app.SNUser());
                status.setPasswordAPI(app.SNPassword());
                status.setError(apiResponse.getError());
                status.setMessage(apiResponse.getMessage());
                status.setExecutionTime(endTime);
                statusService.save(status);


            } catch (Exception e) {
                System.out.println(tag.concat("Exception (II) : ").concat(String.valueOf(e)));
            }
            System.out.println(app.End());
        }
        return snContractsSlaSolver;
    }





    public Domain getDomainByIntegrationId(JSONObject jsonObject, String levelOne, String levelTwo) {
        String integrationId = util.getIdByJson(jsonObject, levelOne, levelTwo);
        if (util.hasData(integrationId)) {
            return domainService.findByIntegrationId(integrationId);
        } else
            return null;
    }
}


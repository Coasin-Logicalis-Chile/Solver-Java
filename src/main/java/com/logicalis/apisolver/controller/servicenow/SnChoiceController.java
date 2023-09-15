
package com.logicalis.apisolver.controller.servicenow;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.logicalis.apisolver.model.APIExecutionStatus;
import com.logicalis.apisolver.model.APIResponse;
import com.logicalis.apisolver.model.Choice;
import com.logicalis.apisolver.model.Domain;
import com.logicalis.apisolver.model.enums.App;
import com.logicalis.apisolver.model.enums.EndPointSN;
import com.logicalis.apisolver.model.enums.SnTable;
import com.logicalis.apisolver.model.servicenow.SnChoice;
import com.logicalis.apisolver.services.IAPIExecutionStatusService;
import com.logicalis.apisolver.services.IChoiceService;
import com.logicalis.apisolver.services.IDomainService;
import com.logicalis.apisolver.services.servicenow.ISnChoiceService;
import com.logicalis.apisolver.util.Rest;
import com.logicalis.apisolver.util.Util;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = {"${app.api.settings.cross-origin.urls}", "*"})
@RestController
@RequestMapping("/api/v1")
public class SnChoiceController {

    @Autowired
    private ISnChoiceService snChoiceService;
    @Autowired
    private IChoiceService choiceService;
    @Autowired
    private IDomainService domainService;
    @Autowired
    private IAPIExecutionStatusService statusService;
    @Autowired
    private Rest rest;

    Util util = new Util();
    App app = new App();
    EndPointSN endPointSN = new EndPointSN();

    @GetMapping("/sn_choices_incidents")
    public List<SnChoice> show() {
        System.out.println(app.Start());
        APIResponse apiResponse = null;
        List<SnChoice> snChoices = new ArrayList<>();
        // String[] sparmOffSets = new String[]{"0", "5000", "10000", "15000", "20000", "25000", "30000", "35000", "40000", "45000", "50000", "55000", "60000", "65000"};
        Util util = new Util();
        long startTime = 0;
        long endTime = 0;
        String tag = "[Choice] ";
        try {
            List<Domain> domains = domainService.findAll();
            System.out.println(tag.concat("(Get All Domains)"));
            startTime = System.currentTimeMillis();
            final int[] count = {1};
            //for (String sparmOffSet : sparmOffSets) {
            // String result = rest.responseByEndPoint(endPointSN.Choice().concat(sparmOffSet));
            // System.out.println(tag.concat("(".concat(endPointSN.Choice().concat(sparmOffSet)).concat(")")));
            String result = rest.responseByEndPoint(endPointSN.ChoiceIncident());
            System.out.println(tag.concat("(".concat(endPointSN.ChoiceIncident())));
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JSONParser parser = new JSONParser();
            JSONObject resultJson = new JSONObject();
            JSONArray ListSnChoiceJson = new JSONArray();

            resultJson = (JSONObject) parser.parse(result);
            if (resultJson.get("result") != null)
                ListSnChoiceJson = (JSONArray) parser.parse(resultJson.get("result").toString());

            ListSnChoiceJson.stream().forEach(snChoiceJson -> {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                SnChoice snChoice = new SnChoice();
                try {
                    snChoice = objectMapper.readValue(snChoiceJson.toString(), SnChoice.class);
                    snChoices.add(snChoice);
                    Choice choice = new Choice();
                    choice.setElement(snChoice.getElement());
                    choice.setLanguage(snChoice.getLanguage());
                    choice.setName(snChoice.getName());
                    choice.setLabel(snChoice.getLabel());
                    choice.setIntegrationId(snChoice.getSys_id());
                    choice.setValue(snChoice.getValue());
                    choice.setInactive(snChoice.isInactive());
                    choice.setName(snChoice.getName());
                    choice.setSolver(snChoice.getU_Solver());
                    choice.setSolverDependentValue(snChoice.getU_solver_dependent_value());

                    String domainSysId = util.getIdByJson((JSONObject) snChoiceJson, SnTable.Domain.get(), app.Value());
                    Domain domain = util.filterDomain(domains, domainSysId);
                    if (domain != null)
                        choice.setDomain(domain);

                    String tagAction = app.CreateConsole();
                    Choice exists = choiceService.findByIntegrationId(choice.getIntegrationId());
                    if (exists != null) {
                        choice.setId(exists.getId());
                        tagAction = app.UpdateConsole();
                    }
                    choiceService.save(choice);
                    util.printData(tag, count[0], tagAction.concat(util.getFieldDisplay(choice)), util.getFieldDisplay(domain));
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
            // }

        } catch (Exception e) {
            System.out.println(tag.concat("Exception (II) : ").concat(String.valueOf(e)));
        }
        System.out.println(app.End());
        return snChoices;
    }

    @GetMapping("/sn_choices_by_filter")
    public List<SnChoice> show(String query) {
        System.out.println(app.Start());
        APIResponse apiResponse = null;
        List<SnChoice> snChoices = new ArrayList<>();
        String[] sparmOffSets = util.offSets50000();
        // String[] sparmOffSets = new String[]{"0", "5000", "10000", "15000", "20000", "25000", "30000", "35000", "40000", "45000", "50000", "55000", "60000", "65000"};
        Util util = new Util();
        long startTime = 0;
        long endTime = 0;
        String tag = "[Choice] ";
        String result = "";
        try {
            List<Domain> domains = domainService.findAll();
            System.out.println(tag.concat("(Get All Domains)"));
            startTime = System.currentTimeMillis();
            final int[] count = {1};
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            //for (String sparmOffSet : sparmOffSets) {
            // String result = rest.responseByEndPoint(endPointSN.Choice().concat(sparmOffSet));
            // System.out.println(tag.concat("(".concat(endPointSN.Choice().concat(sparmOffSet)).concat(")")));
            //String result = rest.responseByEndPoint(endPointSN.ChoiceIncident.get());
            //System.out.println(tag.concat("(".concat(endPointSN.ChoiceIncident.get())));
            for (String sparmOffSet : sparmOffSets) {
              result = rest.responseByEndPoint(endPointSN.ChoiceByQuery().replace("QUERY", query).concat(sparmOffSet));
            System.out.println(tag.concat("(".concat(endPointSN.ChoiceByQuery().replace("QUERY", query).concat(sparmOffSet)).concat(")")));

            JSONParser parser = new JSONParser();
            JSONObject resultJson = new JSONObject();
            JSONArray ListSnChoiceJson = new JSONArray();

            resultJson = (JSONObject) parser.parse(result);
            if (resultJson.get("result") != null)
                ListSnChoiceJson = (JSONArray) parser.parse(resultJson.get("result").toString());

            ListSnChoiceJson.stream().forEach(snChoiceJson -> {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                SnChoice snChoice = new SnChoice();
                try {
                    snChoice = objectMapper.readValue(snChoiceJson.toString(), SnChoice.class);
                    snChoices.add(snChoice);
                    Choice choice = new Choice();
                    choice.setElement(snChoice.getElement());
                    choice.setLanguage(snChoice.getLanguage());
                    choice.setName(snChoice.getName());
                    choice.setLabel(snChoice.getLabel());
                    choice.setIntegrationId(snChoice.getSys_id());
                    choice.setValue(snChoice.getValue());
                    choice.setInactive(snChoice.isInactive());
                    choice.setName(snChoice.getName());
                    choice.setSolver(snChoice.getU_Solver());
                    choice.setSolverDependentValue(snChoice.getU_solver_dependent_value());

                    String domainSysId = util.getIdByJson((JSONObject) snChoiceJson, SnTable.Domain.get(), app.Value());
                    Domain domain = util.filterDomain(domains, domainSysId);
                    if (domain != null)
                        choice.setDomain(domain);

                    String tagAction = app.CreateConsole();
                    Choice exists = choiceService.findByIntegrationId(choice.getIntegrationId());
                    if (exists != null) {
                        choice.setId(exists.getId());
                        tagAction = app.UpdateConsole();
                    }
                    choiceService.save(choice);
                    util.printData(tag, count[0], tagAction.concat(util.getFieldDisplay(choice)), util.getFieldDisplay(domain));
                    count[0] = count[0] + 1;
                } catch (Exception e) {
                    System.out.println(tag.concat("Exception (I) : ").concat(String.valueOf(e)));
                }
            });
            };
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
            // }

        } catch (Exception e) {
            System.out.println(tag.concat("Exception (II) : ").concat(String.valueOf(e)));
        }
        System.out.println(app.End());
        return snChoices;
    }
    /*@GetMapping("/sn_choices_by_filter")
    public List<SnChoice> show(@NotNull @RequestParam(value = "table", required = true, defaultValue = "incident") String table) {
        System.out.println(app.Start());
        APIResponse apiResponse = null;
        List<SnChoice> snChoices = new ArrayList<>();
        Util util = new Util();
        long startTime = 0;
        long endTime = 0;
        String tag = "[Choice] ";
        try {
            List<Domain> domains = domainService.findAll();
            System.out.println(tag.concat("(Get All Domains)"));
            startTime = System.currentTimeMillis();
            final int[] count = {1};
            String result = "";
            switch (table) {
                case "incident":
                    result = rest.responseByEndPoint(endPointSN.ChoiceIncident.get());
                    System.out.println(tag.concat("(".concat(endPointSN.ChoiceIncident.get())));
                case "request":
                    result = rest.responseByEndPoint(endPointSN.ChoiceRequest.get());
                    System.out.println(tag.concat("(".concat(endPointSN.ChoiceRequest.get())));
                case "sc_req_item":
                    result = rest.responseByEndPoint(endPointSN.ChoiceRequest.get());
                    System.out.println(tag.concat("(".concat(endPointSN.ChoiceRequest.get())));
                case "task_sla":
                    result = rest.responseByEndPoint(endPointSN.ChoiceTaskSla.get());
                    System.out.println(tag.concat("(".concat(endPointSN.ChoiceTaskSla.get())));
                case "task":
                    result = rest.responseByEndPoint(endPointSN.ChoiceTask.get());
                    System.out.println(tag.concat("(".concat(endPointSN.ChoiceTaskSla.get())));
            }

            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JSONParser parser = new JSONParser();
            JSONObject resultJson = new JSONObject();
            JSONArray ListSnChoiceJson = new JSONArray();

            resultJson = (JSONObject) parser.parse(result);
            if (resultJson.get("result") != null)
                ListSnChoiceJson = (JSONArray) parser.parse(resultJson.get("result").toString());

            ListSnChoiceJson.stream().forEach(snChoiceJson -> {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                SnChoice snChoice = new SnChoice();
                try {
                    snChoice = objectMapper.readValue(snChoiceJson.toString(), SnChoice.class);
                    snChoices.add(snChoice);
                    Choice choice = new Choice();
                    choice.setElement(snChoice.getElement());
                    choice.setLanguage(snChoice.getLanguage());
                    choice.setName(snChoice.getName());
                    choice.setLabel(snChoice.getLabel());
                    choice.setIntegrationId(snChoice.getSys_id());
                    choice.setValue(snChoice.getValue());
                    choice.setInactive(snChoice.isInactive());
                    choice.setName(snChoice.getName());
                    choice.setSolver(snChoice.getU_Solver());
                    choice.setSolverDependentValue(snChoice.getU_solver_dependent_value());
                    String domainSysId = util.getIdByJson((JSONObject) snChoiceJson, SnTable.Domain.get(), app.Value());
                    Domain domain = util.filterDomain(domains, domainSysId);
                    if (domain != null)
                        choice.setDomain(domain);

                    String tagAction = app.CreateConsole();
                    Choice exists = choiceService.findByIntegrationId(choice.getIntegrationId());
                    if (exists != null) {
                        choice.setId(exists.getId());
                        tagAction = app.UpdateConsole();
                    }
                    choiceService.save(choice);
                    util.printData(tag, count[0], tagAction.concat(util.getFieldDisplay(choice)), util.getFieldDisplay(domain));

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
            // }

        } catch (Exception e) {
            System.out.println(tag.concat("Exception (II) : ").concat(String.valueOf(e)));
        }
        System.out.println(app.End());
        return snChoices;
    }*/

}

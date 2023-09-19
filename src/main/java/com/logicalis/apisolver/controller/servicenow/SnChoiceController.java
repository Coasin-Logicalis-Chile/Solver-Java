
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

    @GetMapping("/sn_choices_incidents")
    public List<SnChoice> show() {
        System.out.println(App.Start());
        APIResponse apiResponse = null;
        List<SnChoice> snChoices = new ArrayList<>();
        // String[] sparmOffSets = new String[]{"0", "5000", "10000", "15000", "20000", "25000", "30000", "35000", "40000", "45000", "50000", "55000", "60000", "65000"};
        long startTime = 0;
        long endTime = 0;
        String tag = "[Choice] ";
        try {
            List<Domain> domains = domainService.findAll();
            System.out.println(tag.concat("(Get All Domains)"));
            startTime = System.currentTimeMillis();
            final int[] count = {1};
            //for (String sparmOffSet : sparmOffSets) {
            // String result = rest.responseByEndPoint(EndPointSN.Choice().concat(sparmOffSet));
            // System.out.println(tag.concat("(".concat(EndPointSN.Choice().concat(sparmOffSet)).concat(")")));
            String result = rest.responseByEndPoint(EndPointSN.ChoiceIncident());
            System.out.println(tag.concat("(".concat(EndPointSN.ChoiceIncident())));
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

                    String domainSysId = Util.getIdByJson((JSONObject) snChoiceJson, SnTable.Domain.get(), App.Value());
                    Domain domain = Util.filterDomain(domains, domainSysId);
                    if (domain != null)
                        choice.setDomain(domain);

                    String tagAction = App.CreateConsole();
                    Choice exists = choiceService.findByIntegrationId(choice.getIntegrationId());
                    if (exists != null) {
                        choice.setId(exists.getId());
                        tagAction = App.UpdateConsole();
                    }
                    choiceService.save(choice);
                    Util.printData(tag, count[0], tagAction.concat(Util.getFieldDisplay(choice)), Util.getFieldDisplay(domain));
                    count[0] = count[0] + 1;
                } catch (Exception e) {
                    System.out.println(tag.concat("Exception (I) : ").concat(String.valueOf(e)));
                }
            });

            apiResponse = mapper.readValue(result, APIResponse.class);
            APIExecutionStatus status = new APIExecutionStatus();
            status.setUri(EndPointSN.Location());
            status.setUserAPI(App.SNUser());
            status.setPasswordAPI(App.SNPassword());
            status.setError(apiResponse.getError());
            status.setMessage(apiResponse.getMessage());
            endTime = (System.currentTimeMillis() - startTime);
            status.setExecutionTime(endTime);
            statusService.save(status);
            // }

        } catch (Exception e) {
            System.out.println(tag.concat("Exception (II) : ").concat(String.valueOf(e)));
        }
        System.out.println(App.End());
        return snChoices;
    }

    @GetMapping("/sn_choices_by_filter")
    public List<SnChoice> show(String query) {
        System.out.println(App.Start());
        APIResponse apiResponse = null;
        List<SnChoice> snChoices = new ArrayList<>();
        String[] sparmOffSets = Util.offSets50000();
        // String[] sparmOffSets = new String[]{"0", "5000", "10000", "15000", "20000", "25000", "30000", "35000", "40000", "45000", "50000", "55000", "60000", "65000"};
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
            // String result = rest.responseByEndPoint(EndPointSN.Choice().concat(sparmOffSet));
            // System.out.println(tag.concat("(".concat(EndPointSN.Choice().concat(sparmOffSet)).concat(")")));
            //String result = rest.responseByEndPoint(EndPointSN.ChoiceIncident.get());
            //System.out.println(tag.concat("(".concat(EndPointSN.ChoiceIncident.get())));
            for (String sparmOffSet : sparmOffSets) {
              result = rest.responseByEndPoint(EndPointSN.ChoiceByQuery().replace("QUERY", query).concat(sparmOffSet));
            System.out.println(tag.concat("(".concat(EndPointSN.ChoiceByQuery().replace("QUERY", query).concat(sparmOffSet)).concat(")")));

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

                    String domainSysId = Util.getIdByJson((JSONObject) snChoiceJson, SnTable.Domain.get(), App.Value());
                    Domain domain = Util.filterDomain(domains, domainSysId);
                    if (domain != null)
                        choice.setDomain(domain);

                    String tagAction = App.CreateConsole();
                    Choice exists = choiceService.findByIntegrationId(choice.getIntegrationId());
                    if (exists != null) {
                        choice.setId(exists.getId());
                        tagAction = App.UpdateConsole();
                    }
                    choiceService.save(choice);
                    Util.printData(tag, count[0], tagAction.concat(Util.getFieldDisplay(choice)), Util.getFieldDisplay(domain));
                    count[0] = count[0] + 1;
                } catch (Exception e) {
                    System.out.println(tag.concat("Exception (I) : ").concat(String.valueOf(e)));
                }
            });
            };
            apiResponse = mapper.readValue(result, APIResponse.class);
            APIExecutionStatus status = new APIExecutionStatus();
            status.setUri(EndPointSN.Location());
            status.setUserAPI(App.SNUser());
            status.setPasswordAPI(App.SNPassword());
            status.setError(apiResponse.getError());
            status.setMessage(apiResponse.getMessage());
            endTime = (System.currentTimeMillis() - startTime);
            status.setExecutionTime(endTime);
            statusService.save(status);
            // }

        } catch (Exception e) {
            System.out.println(tag.concat("Exception (II) : ").concat(String.valueOf(e)));
        }
        System.out.println(App.End());
        return snChoices;
    }
    /*@GetMapping("/sn_choices_by_filter")
    public List<SnChoice> show(@NotNull @RequestParam(value = "table", required = true, defaultValue = "incident") String table) {
        System.out.println(App.Start());
        APIResponse apiResponse = null;
        List<SnChoice> snChoices = new ArrayList<>();
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
                    result = rest.responseByEndPoint(EndPointSN.ChoiceIncident.get());
                    System.out.println(tag.concat("(".concat(EndPointSN.ChoiceIncident.get())));
                case "request":
                    result = rest.responseByEndPoint(EndPointSN.ChoiceRequest.get());
                    System.out.println(tag.concat("(".concat(EndPointSN.ChoiceRequest.get())));
                case "sc_req_item":
                    result = rest.responseByEndPoint(EndPointSN.ChoiceRequest.get());
                    System.out.println(tag.concat("(".concat(EndPointSN.ChoiceRequest.get())));
                case "task_sla":
                    result = rest.responseByEndPoint(EndPointSN.ChoiceTaskSla.get());
                    System.out.println(tag.concat("(".concat(EndPointSN.ChoiceTaskSla.get())));
                case "task":
                    result = rest.responseByEndPoint(EndPointSN.ChoiceTask.get());
                    System.out.println(tag.concat("(".concat(EndPointSN.ChoiceTaskSla.get())));
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
                    String domainSysId = Util.getIdByJson((JSONObject) snChoiceJson, SnTable.Domain.get(), App.Value());
                    Domain domain = Util.filterDomain(domains, domainSysId);
                    if (domain != null)
                        choice.setDomain(domain);

                    String tagAction = App.CreateConsole();
                    Choice exists = choiceService.findByIntegrationId(choice.getIntegrationId());
                    if (exists != null) {
                        choice.setId(exists.getId());
                        tagAction = App.UpdateConsole();
                    }
                    choiceService.save(choice);
                    Util.printData(tag, count[0], tagAction.concat(Util.getFieldDisplay(choice)), Util.getFieldDisplay(domain));

                    count[0] = count[0] + 1;
                } catch (Exception e) {
                    System.out.println(tag.concat("Exception (I) : ").concat(String.valueOf(e)));
                }
            });

            apiResponse = mapper.readValue(result, APIResponse.class);
            APIExecutionStatus status = new APIExecutionStatus();
            status.setUri(EndPointSN.Location());
            status.setUserAPI(App.SNUser());
            status.setPasswordAPI(App.SNPassword());
            status.setError(apiResponse.getError());
            status.setMessage(apiResponse.getMessage());
            endTime = (System.currentTimeMillis() - startTime);
            status.setExecutionTime(endTime);
            statusService.save(status);
            // }

        } catch (Exception e) {
            System.out.println(tag.concat("Exception (II) : ").concat(String.valueOf(e)));
        }
        System.out.println(App.End());
        return snChoices;
    }*/

}

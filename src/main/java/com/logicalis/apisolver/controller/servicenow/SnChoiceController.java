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
import com.logicalis.apisolver.util.Rest;
import com.logicalis.apisolver.util.Util;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = {"${app.api.settings.cross-origin.urls}", "*"})
@RestController
@RequestMapping("/api/v1")
public class SnChoiceController {
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
        long startTime = 0;
        long endTime = 0;
        String tag = "[Choice] ";
        try {
            List<Domain> domains = domainService.findAll();
            System.out.println(tag.concat("(Get All Domains)"));
            startTime = System.currentTimeMillis();
            final int[] count = {1};
            String result = rest.responseByEndPoint(EndPointSN.ChoiceIncident());
            System.out.println(tag.concat("(".concat(EndPointSN.ChoiceIncident())));
            JSONParser parser = new JSONParser();
            JSONObject resultJson = new JSONObject();
            JSONArray ListSnChoiceJson = new JSONArray();
            resultJson = (JSONObject) parser.parse(result);
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            final Choice[] choice = {new Choice()};
            final String[] domainSysId = new String[1];
            final Domain[] domain = new Domain[1];
            final String[] tagAction = new String[1];
            final Choice[] exists = new Choice[1];
            APIExecutionStatus status = new APIExecutionStatus();
            final SnChoice[] snChoice = {new SnChoice()};
            if (resultJson.get("result") != null)
                ListSnChoiceJson = (JSONArray) parser.parse(resultJson.get("result").toString());
            ListSnChoiceJson.stream().forEach(snChoiceJson -> {
                try {
                    snChoice[0] = objectMapper.readValue(snChoiceJson.toString(), SnChoice.class);
                    snChoices.add(snChoice[0]);
                    choice[0] = new Choice();
                    choice[0].setElement(snChoice[0].getElement());
                    choice[0].setLanguage(snChoice[0].getLanguage());
                    choice[0].setName(snChoice[0].getName());
                    choice[0].setLabel(snChoice[0].getLabel());
                    choice[0].setIntegrationId(snChoice[0].getSys_id());
                    choice[0].setValue(snChoice[0].getValue());
                    choice[0].setInactive(snChoice[0].isInactive());
                    choice[0].setName(snChoice[0].getName());
                    choice[0].setSolver(snChoice[0].getU_Solver());
                    choice[0].setSolverDependentValue(snChoice[0].getU_solver_dependent_value());

                    domainSysId[0] = Util.getIdByJson((JSONObject) snChoiceJson, SnTable.Domain.get(), App.Value());
                    domain[0] = Util.filterDomain(domains, domainSysId[0]);
                    if (domain[0] != null)
                        choice[0].setDomain(domain[0]);

                    tagAction[0] = App.CreateConsole();
                    exists[0] = choiceService.findByIntegrationId(choice[0].getIntegrationId());
                    if (exists[0] != null) {
                        choice[0].setId(exists[0].getId());
                        tagAction[0] = App.UpdateConsole();
                    }
                    choiceService.save(choice[0]);
                    Util.printData(tag, count[0], tagAction[0].concat(Util.getFieldDisplay(choice[0])), Util.getFieldDisplay(domain[0]));
                    count[0] = count[0] + 1;
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
        return snChoices;
    }

    @GetMapping("/sn_choices_by_filter")
    public List<SnChoice> show(String query) {
        System.out.println(App.Start());
        APIResponse apiResponse = null;
        List<SnChoice> snChoices = new ArrayList<>();
        String[] sparmOffSets = Util.offSets50000();
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
            JSONParser parser = new JSONParser();
            JSONObject resultJson = new JSONObject();
            JSONArray ListSnChoiceJson = new JSONArray();
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            final SnChoice[] snChoice = {new SnChoice()};
            final Choice[] choice = {new Choice()};
            final String[] domainSysId = new String[1];
            final Domain[] domain = new Domain[1];
            final String[] tagAction = new String[1];
            final Choice[] exists = new Choice[1];
            APIExecutionStatus status = new APIExecutionStatus();
            for (String sparmOffSet : sparmOffSets) {
                result = rest.responseByEndPoint(EndPointSN.ChoiceByQuery().replace("QUERY", query).concat(sparmOffSet));
                System.out.println(tag.concat("(".concat(EndPointSN.ChoiceByQuery().replace("QUERY", query).concat(sparmOffSet)).concat(")")));
                ListSnChoiceJson.clear();
                resultJson = (JSONObject) parser.parse(result);
                if (resultJson.get("result") != null)
                    ListSnChoiceJson = (JSONArray) parser.parse(resultJson.get("result").toString());
                ListSnChoiceJson.stream().forEach(snChoiceJson -> {
                    try {
                        snChoice[0] = objectMapper.readValue(snChoiceJson.toString(), SnChoice.class);
                        snChoices.add(snChoice[0]);
                        choice[0] = new Choice();
                        choice[0].setElement(snChoice[0].getElement());
                        choice[0].setLanguage(snChoice[0].getLanguage());
                        choice[0].setName(snChoice[0].getName());
                        choice[0].setLabel(snChoice[0].getLabel());
                        choice[0].setIntegrationId(snChoice[0].getSys_id());
                        choice[0].setValue(snChoice[0].getValue());
                        choice[0].setInactive(snChoice[0].isInactive());
                        choice[0].setName(snChoice[0].getName());
                        choice[0].setSolver(snChoice[0].getU_Solver());
                        choice[0].setSolverDependentValue(snChoice[0].getU_solver_dependent_value());

                        domainSysId[0] = Util.getIdByJson((JSONObject) snChoiceJson, SnTable.Domain.get(), App.Value());
                        domain[0] = Util.filterDomain(domains, domainSysId[0]);
                        if (domain[0] != null)
                            choice[0].setDomain(domain[0]);

                        tagAction[0] = App.CreateConsole();
                        exists[0] = choiceService.findByIntegrationId(choice[0].getIntegrationId());
                        if (exists[0] != null) {
                            choice[0].setId(exists[0].getId());
                            tagAction[0] = App.UpdateConsole();
                        }
                        choiceService.save(choice[0]);
                        Util.printData(tag, count[0], tagAction[0].concat(Util.getFieldDisplay(choice[0])), Util.getFieldDisplay(domain[0]));
                        count[0] = count[0] + 1;
                    } catch (Exception e) {
                        System.out.println(tag.concat("Exception (I) : ").concat(String.valueOf(e)));
                    }
                });
            }
            apiResponse = mapper.readValue(result, APIResponse.class);
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
        return snChoices;
    }
}

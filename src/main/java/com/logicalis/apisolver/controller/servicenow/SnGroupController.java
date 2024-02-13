package com.logicalis.apisolver.controller.servicenow;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.logicalis.apisolver.model.*;
import com.logicalis.apisolver.model.enums.App;
import com.logicalis.apisolver.model.enums.EndPointSN;
import com.logicalis.apisolver.model.enums.SnTable;
import com.logicalis.apisolver.model.servicenow.SnGroup;
import com.logicalis.apisolver.services.IAPIExecutionStatusService;
import com.logicalis.apisolver.services.ICompanyService;
import com.logicalis.apisolver.services.IDomainService;
import com.logicalis.apisolver.services.ISysGroupService;
import com.logicalis.apisolver.services.servicenow.ISnGroupService;
import com.logicalis.apisolver.util.Rest;
import com.logicalis.apisolver.util.Util;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = {"${app.api.settings.cross-origin.urls}", "*"})
@RestController
@RequestMapping("/api/v1")
@Slf4j
public class SnGroupController {
    @Autowired
    private ISysGroupService groupService;
    @Autowired
    private IAPIExecutionStatusService statusService;
    @Autowired
    private IDomainService domainService;
    @Autowired
    private ICompanyService companyService;
    @Autowired
    private Rest rest;

    @GetMapping("/sn_groups")
    public List<SnGroup> show() {
        log.info(App.Start());
        APIResponse apiResponse = null;
        List<SnGroup> snGroups = new ArrayList<>();
        long startTime = 0;
        long endTime = 0;
        String tag = "[SysGroup] ";
        try {
            startTime = System.currentTimeMillis();
            String result = rest.responseByEndPoint(EndPointSN.Group());
            endTime = (System.currentTimeMillis() - startTime);
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JSONParser parser = new JSONParser();
            JSONObject resultJson = (JSONObject) parser.parse(result);
            JSONArray ListSnGroupJson = new JSONArray();
            final SysGroup[] group = {new SysGroup()};
            final Domain[] domain = new Domain[1];
            final Company[] company = new Company[1];
            final String[] tagAction = new String[1];
            final SysGroup[] exists = new SysGroup[1];
            final SysGroup[] current = new SysGroup[1];
            APIExecutionStatus status = new APIExecutionStatus();
            final SnGroup[] snGroup = new SnGroup[1];
            if (resultJson.get("result") != null)
                ListSnGroupJson = (JSONArray) parser.parse(resultJson.get("result").toString());
            final int[] count = {1};
            ListSnGroupJson.forEach(snGroupJson -> {
                try {
                    snGroup[0] = mapper.readValue(snGroupJson.toString(), SnGroup.class);
                    snGroups.add(snGroup[0]);
                    group[0] = new SysGroup();
                    group[0].setActive(snGroup[0].isActive());
                    group[0].setName(snGroup[0].getName());
                    group[0].setIntegrationId(snGroup[0].getSys_id());
                    group[0].setSolver(snGroup[0].getU_solver());
                    domain[0] = getDomainByIntegrationId((JSONObject) snGroupJson, SnTable.Domain.get(), App.Value());
                    if (domain[0] != null)
                        group[0].setDomain(domain[0]);
                    company[0] = getCompanyByIntegrationId((JSONObject) snGroupJson, SnTable.Company.get(), App.Value());
                    if (company[0] != null)
                        group[0].setCompany(company[0]);
                    tagAction[0] = App.CreateConsole();
                    exists[0] = groupService.findByIntegrationId(group[0].getIntegrationId());
                    if (exists[0] != null) {
                        group[0].setId(exists[0].getId());
                        if(exists[0].getActive() == false){
                            group[0].setActive(false);
                        }
                        tagAction[0] = App.UpdateConsole();
                    }
                    current[0] = groupService.save(group[0]);
                    Util.printData(tag, count[0], tagAction[0].concat(Util.getFieldDisplay(current[0])), Util.getFieldDisplay(company[0]), Util.getFieldDisplay(domain[0]));
                    count[0] = count[0] + 1;
                } catch (JsonProcessingException e) {
                    log.info(tag.concat("Exception (I) : ").concat(String.valueOf(e)));
                }
            });
            apiResponse = mapper.readValue(result, APIResponse.class);
            status.setUri(EndPointSN.Group());
            status.setUserAPI(App.SNUser());
            status.setPasswordAPI(App.SNPassword());
            status.setError(apiResponse.getError());
            status.setMessage(apiResponse.getMessage());
            status.setExecutionTime(endTime);
            statusService.save(status);
        } catch (Exception e) {
            log.info(tag.concat("Exception (II) : ").concat(String.valueOf(e)));
        }
        log.info(App.End());
        return snGroups;
    }

    public Domain getDomainByIntegrationId(JSONObject jsonObject, String levelOne, String levelTwo) {
        String integrationId = Util.getIdByJson(jsonObject, levelOne, levelTwo);
        if (Util.hasData(integrationId)) {
            return domainService.findByIntegrationId(integrationId);
        } else
            return null;
    }

    public Company getCompanyByIntegrationId(JSONObject jsonObject, String levelOne, String levelTwo) {
        String integrationId = Util.getIdByJson(jsonObject, levelOne, levelTwo);
        if (Util.hasData(integrationId)) {
            return companyService.findByIntegrationId(integrationId);
        } else
            return null;
    }

}


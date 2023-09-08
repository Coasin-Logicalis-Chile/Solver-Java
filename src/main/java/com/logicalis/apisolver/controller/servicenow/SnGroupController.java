
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
public class SnGroupController {

    @Autowired
    private ISnGroupService snGroupService;
    @Autowired
    private ISysGroupService groupService;
    @Autowired
    private IAPIExecutionStatusService statusService;
    @Autowired
    private IDomainService domainService;
    @Autowired
    private ICompanyService companyService;
    private Util util = new Util();
    App app = new App();
    EndPointSN endPointSN = new EndPointSN();

    @GetMapping("/sn_groups")
    public List<SnGroup> show() {
        System.out.println(app.Start());
        APIResponse apiResponse = null;
        List<SnGroup> snGroups = new ArrayList<>();
        long startTime = 0;
        long endTime = 0;
        Util util = new Util();
        String tag = "[SysGroup] ";
        try {
            Rest rest = new Rest();
            startTime = System.currentTimeMillis();
            String result = rest.responseByEndPoint(endPointSN.Group());
            endTime = (System.currentTimeMillis() - startTime);
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JSONParser parser = new JSONParser();
            JSONObject resultJson = (JSONObject) parser.parse(result);
            JSONArray ListSnGroupJson = new JSONArray();
            if (resultJson.get("result") != null)
                ListSnGroupJson = (JSONArray) parser.parse(resultJson.get("result").toString());
            final int[] count = {1};
            ListSnGroupJson.forEach(snGroupJson -> {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                try {
                    SnGroup snGroup = objectMapper.readValue(snGroupJson.toString(), SnGroup.class);
                    snGroups.add(snGroup);
                    SysGroup group = new SysGroup();
                    group.setActive(snGroup.isActive());
                    group.setName(snGroup.getName());
                    group.setIntegrationId(snGroup.getSys_id());
                    group.setSolver(snGroup.getU_solver());
                    Domain domain = getDomainByIntegrationId((JSONObject) snGroupJson, SnTable.Domain.get(), app.Value());
                    if (domain != null)
                        group.setDomain(domain);

                    Company company = getCompanyByIntegrationId((JSONObject) snGroupJson, SnTable.Company.get(), app.Value());

                    if (company != null)
                        group.setCompany(company);

                    String tagAction = app.CreateConsole();
                    SysGroup exists = groupService.findByIntegrationId(group.getIntegrationId());
                    if (exists != null) {
                        group.setId(exists.getId());
                        tagAction = app.UpdateConsole();
                    }
                    SysGroup current = groupService.save(group);
                    util.printData(tag, count[0], tagAction.concat(util.getFieldDisplay(current)), util.getFieldDisplay(company), util.getFieldDisplay(domain));

                    count[0] = count[0] + 1;
                } catch (JsonProcessingException e) {
                    System.out.println(tag.concat("Exception (I) : ").concat(String.valueOf(e)));
                }
            });

            apiResponse = mapper.readValue(result, APIResponse.class);

            APIExecutionStatus status = new APIExecutionStatus();
            status.setUri(endPointSN.Group());
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
        return snGroups;
    }

    public Domain getDomainByIntegrationId(JSONObject jsonObject, String levelOne, String levelTwo) {
        String integrationId = util.getIdByJson(jsonObject, levelOne, levelTwo);
        if (util.hasData(integrationId)) {
            return domainService.findByIntegrationId(integrationId);
        } else
            return null;
    }

    public Company getCompanyByIntegrationId(JSONObject jsonObject, String levelOne, String levelTwo) {
        String integrationId = util.getIdByJson(jsonObject, levelOne, levelTwo);
        if (util.hasData(integrationId)) {
            return companyService.findByIntegrationId(integrationId);
        } else
            return null;
    }

}


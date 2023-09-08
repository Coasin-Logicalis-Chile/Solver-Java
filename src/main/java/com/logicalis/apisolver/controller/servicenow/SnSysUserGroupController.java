
package com.logicalis.apisolver.controller.servicenow;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.logicalis.apisolver.model.*;
import com.logicalis.apisolver.model.enums.App;
import com.logicalis.apisolver.model.enums.EndPointSN;
import com.logicalis.apisolver.model.servicenow.SnSysUser;
import com.logicalis.apisolver.model.servicenow.SnSysUserGroup;
import com.logicalis.apisolver.services.*;
import com.logicalis.apisolver.services.servicenow.ISnSysUserGroupService;
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
public class SnSysUserGroupController {

    @Autowired
    private ISnSysUserGroupService snSysUserGroupService;
    @Autowired
    private ISysUserGroupService sysUserGroupService;
    @Autowired
    private ISysUserService sysUserService;
    @Autowired
    private IDomainService domainService;
    @Autowired
    private ISysGroupService sysGroupService;
    @Autowired
    private IAPIExecutionStatusService statusService;

    private Util util = new Util();
    App app = new App();
    EndPointSN endPointSN = new EndPointSN();

    @GetMapping("/sn_sys_user_groups")
    public List<SnSysUserGroup> show() {
        System.out.println(app.Start());
        APIResponse apiResponse = null;
        List<SnSysUserGroup> snSysUserGroups = new ArrayList<>();
        String[] sparmOffSets = util.offSets99000();
        long startTime = 0;
        long endTime = 0;
        String tag = "[SysUserGroup] ";
        try {
            Rest rest = new Rest();
            startTime = System.currentTimeMillis();
            final int[] count = {1};
            for (String sparmOffSet : sparmOffSets) {
                String result = rest.responseByEndPoint(endPointSN.SysUserGroup().concat(sparmOffSet));
                System.out.println(tag.concat("(".concat(endPointSN.SysUserGroup().concat(sparmOffSet)).concat(")")));
                ObjectMapper mapper = new ObjectMapper();
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                JSONParser parser = new JSONParser();
                JSONObject resultJson = new JSONObject();
                JSONArray ListSnSysUserGroupJson = new JSONArray();

                resultJson = (JSONObject) parser.parse(result);
                if (resultJson.get("result") != null)
                    ListSnSysUserGroupJson = (JSONArray) parser.parse(resultJson.get("result").toString());

                ListSnSysUserGroupJson.stream().forEach(snSysUserGroupJson -> {

                    String tagAction = app.CreateConsole();
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    SnSysUserGroup snSysUserGroup = new SnSysUserGroup();
                    try {
                    //    snSysUserGroup = objectMapper.readValue(snSysUserGroupJson.toString(), SnSysUserGroup.class);
                        Gson gson = new Gson();
                        snSysUserGroup = gson.fromJson(snSysUserGroupJson.toString(), SnSysUserGroup.class);

                        snSysUserGroups.add(snSysUserGroup);
                        SysUserGroup sysUserGroup = new SysUserGroup();
                        sysUserGroup.setIntegrationId(snSysUserGroup.getSys_id());
                        sysUserGroup.setActive(snSysUserGroup.isActive());
                        SysUser sysUser = getSysUserByIntegrationId((JSONObject) snSysUserGroupJson, "user", app.Value());
                        if (sysUser != null)
                            sysUserGroup.setSysUser(sysUser);

                        SysGroup sysGroup = getSysGroupByIntegrationId((JSONObject) snSysUserGroupJson, "group", app.Value());
                        if (sysGroup != null)
                            sysUserGroup.setSysGroup(sysGroup);

                        SysUserGroup exists = sysUserGroupService.findByIntegrationId(sysUserGroup.getIntegrationId());

                        if (exists != null) {
                            sysUserGroup.setId(exists.getId());
                            tagAction = app.UpdateConsole();
                        }

                        sysUserGroup.setActive(true);
                        util.printData(tag, count[0], tagAction.concat(util.getFieldDisplay(sysUserGroup)), util.getFieldDisplay(sysUser));
                        sysUserGroupService.save(sysUserGroup);
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
        return snSysUserGroups;
    }

    public SysUser getSysUserByIntegrationId(JSONObject jsonObject, String levelOne, String levelTwo) {
        String integrationId = util.getIdByJson(jsonObject, levelOne, levelTwo);
        if (util.hasData(integrationId)) {
            return sysUserService.findByIntegrationId(integrationId);
        } else
            return null;
    }

    public SysGroup getSysGroupByIntegrationId(JSONObject jsonObject, String levelOne, String levelTwo) {
        String integrationId = util.getIdByJson(jsonObject, levelOne, levelTwo);
        if (util.hasData(integrationId)) {
            return sysGroupService.findByIntegrationId(integrationId);
        } else
            return null;
    }
}


package com.logicalis.apisolver.controller.servicenow;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.logicalis.apisolver.model.*;
import com.logicalis.apisolver.model.enums.App;
import com.logicalis.apisolver.model.enums.EndPointSN;
import com.logicalis.apisolver.model.servicenow.SnSysUserGroup;
import com.logicalis.apisolver.services.*;
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
    private ISysUserGroupService sysUserGroupService;
    @Autowired
    private ISysUserService sysUserService;
    @Autowired
    private ISysGroupService sysGroupService;
    @Autowired
    private IAPIExecutionStatusService statusService;
    @Autowired
    private Rest rest;

    @GetMapping("/sn_sys_user_groups")
    public List<SnSysUserGroup> show() {
        System.out.println(App.Start());
        APIResponse apiResponse = null;
        List<SnSysUserGroup> snSysUserGroups = new ArrayList<>();
        String[] sparmOffSets = Util.offSets99000();
        long startTime = 0;
        long endTime = 0;
        String tag = "[SysUserGroup] ";
        try {
            startTime = System.currentTimeMillis();
            final int[] count = {1};
            String result;
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JSONParser parser = new JSONParser();
            JSONObject resultJson = new JSONObject();
            JSONArray ListSnSysUserGroupJson = new JSONArray();
            final String[] tagAction = new String[1];
            final SnSysUserGroup[] snSysUserGroup = {new SnSysUserGroup()};
            Gson gson = new Gson();
            final SysUserGroup[] sysUserGroup = {new SysUserGroup()};
            final SysUser[] sysUser = new SysUser[1];
            final SysGroup[] sysGroup = new SysGroup[1];
            final SysUserGroup[] exists = new SysUserGroup[1];
            APIExecutionStatus status = new APIExecutionStatus();
            for (String sparmOffSet : sparmOffSets) {
                result = rest.responseByEndPoint(EndPointSN.SysUserGroup().concat(sparmOffSet));
                System.out.println(tag.concat("(".concat(EndPointSN.SysUserGroup().concat(sparmOffSet)).concat(")")));
                resultJson = (JSONObject) parser.parse(result);
                ListSnSysUserGroupJson.clear();
                if (resultJson.get("result") != null)
                    ListSnSysUserGroupJson = (JSONArray) parser.parse(resultJson.get("result").toString());

                ListSnSysUserGroupJson.stream().forEach(snSysUserGroupJson -> {

                    tagAction[0] = App.CreateConsole();
                    try {
                        snSysUserGroup[0] = gson.fromJson(snSysUserGroupJson.toString(), SnSysUserGroup.class);
                        snSysUserGroups.add(snSysUserGroup[0]);
                        sysUserGroup[0] = new SysUserGroup();
                        sysUserGroup[0].setIntegrationId(snSysUserGroup[0].getSys_id());
                        sysUserGroup[0].setActive(snSysUserGroup[0].isActive());
                        sysUser[0] = getSysUserByIntegrationId((JSONObject) snSysUserGroupJson, "user", App.Value());
                        if (sysUser[0] != null)
                            sysUserGroup[0].setSysUser(sysUser[0]);
                        sysGroup[0] = getSysGroupByIntegrationId((JSONObject) snSysUserGroupJson, "group", App.Value());
                        if (sysGroup[0] != null)
                            sysUserGroup[0].setSysGroup(sysGroup[0]);
                        exists[0] = sysUserGroupService.findByIntegrationId(sysUserGroup[0].getIntegrationId());
                        if (exists[0] != null) {
                            sysUserGroup[0].setId(exists[0].getId());
                            tagAction[0] = App.UpdateConsole();
                        }
                        sysUserGroup[0].setActive(true);
                        Util.printData(tag, count[0], tagAction[0].concat(Util.getFieldDisplay(sysUserGroup[0])), Util.getFieldDisplay(sysUser[0]));
                        sysUserGroupService.save(sysUserGroup[0]);
                        count[0] = count[0] + 1;
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
        return snSysUserGroups;
    }

    public SysUser getSysUserByIntegrationId(JSONObject jsonObject, String levelOne, String levelTwo) {
        String integrationId = Util.getIdByJson(jsonObject, levelOne, levelTwo);
        if (Util.hasData(integrationId)) {
            return sysUserService.findByIntegrationId(integrationId);
        } else
            return null;
    }

    public SysGroup getSysGroupByIntegrationId(JSONObject jsonObject, String levelOne, String levelTwo) {
        String integrationId = Util.getIdByJson(jsonObject, levelOne, levelTwo);
        if (Util.hasData(integrationId)) {
            return sysGroupService.findByIntegrationId(integrationId);
        } else
            return null;
    }
}

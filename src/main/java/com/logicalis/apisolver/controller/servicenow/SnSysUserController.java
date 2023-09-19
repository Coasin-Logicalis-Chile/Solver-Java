
package com.logicalis.apisolver.controller.servicenow;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.logicalis.apisolver.model.*;
import com.logicalis.apisolver.model.enums.App;
import com.logicalis.apisolver.model.enums.EndPointSN;
import com.logicalis.apisolver.model.enums.SnTable;
import com.logicalis.apisolver.model.servicenow.SnSysUser;
import com.logicalis.apisolver.services.*;
import com.logicalis.apisolver.services.servicenow.ISnSysUserService;
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
import java.util.Locale;

@CrossOrigin(origins = {"${app.api.settings.cross-origin.urls}", "*"})
@RestController
@RequestMapping("/api/v1")
public class SnSysUserController {

    @Autowired
    private ISnSysUserService snSysUserService;
    @Autowired
    private ISysUserService sysUserService;
    @Autowired
    private IDomainService domainService;
    @Autowired
    private ILocationService locationService;
    @Autowired
    private IAPIExecutionStatusService statusService;
    @Autowired
    private ICompanyService companyService;
    @Autowired
    private IDepartmentService departmentService;
    @Autowired
    private Rest rest;

    @GetMapping("/sn_sys_users")
    public List<SnSysUser> show() {
        System.out.println(App.Start());
        APIResponse apiResponse = null;
        List<SnSysUser> snSysUsers = new ArrayList<>();
        String[] sparmOffSets = Util.offSets500000();
        long startTime = 0;
        long endTime = 0;
        String tag = "[SysUser] ";
        try {
            startTime = System.currentTimeMillis();
            final int[] count = {1};
            for (String sparmOffSet : sparmOffSets) {
                String result = rest.responseByEndPoint(EndPointSN.SysUser().concat(sparmOffSet));
                System.out.println(tag.concat("(".concat(EndPointSN.SysUser().concat(sparmOffSet)).concat(")")));
                ObjectMapper mapper = new ObjectMapper();
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                JSONParser parser = new JSONParser();
                JSONObject resultJson = new JSONObject();
                JSONArray ListSnSysUserJson = new JSONArray();

                resultJson = (JSONObject) parser.parse(result);
                if (resultJson.get("result") != null)
                    ListSnSysUserJson = (JSONArray) parser.parse(resultJson.get("result").toString());

                ListSnSysUserJson.stream().forEach(snSysUserJson -> {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    SnSysUser snSysUser = new SnSysUser();
                    try {
                        snSysUser = objectMapper.readValue(snSysUserJson.toString(), SnSysUser.class);
                        snSysUsers.add(snSysUser);
                        SysUser sysUser = new SysUser();
                        sysUser.setActive(snSysUser.getActive());
                        sysUser.setName(snSysUser.getName());
                        sysUser.setIntegrationId(snSysUser.getSys_id());
                        sysUser.setVip(snSysUser.getVip());
                        sysUser.setEmail(snSysUser.getEmail().toLowerCase(Locale.ROOT));
                        sysUser.setEmployeeNumber(snSysUser.getEmployee_number());
                        sysUser.setFirstName(snSysUser.getFirst_name());
                        sysUser.setUserName(snSysUser.getUser_name());
                        sysUser.setLastName(snSysUser.getLast_name());
                        sysUser.setVip(snSysUser.getVip());
                        sysUser.setSolver(snSysUser.getU_solver());
                        sysUser.setMobilePhone(snSysUser.getMobile_phone());
                        sysUser.setManager(getIntegrationId((JSONObject) snSysUserJson, "manager", App.Value()));
                        Domain domain = getDomainByIntegrationId((JSONObject) snSysUserJson, SnTable.Domain.get(), App.Value());
                        if (domain != null)
                            sysUser.setDomain(domain);

                        Company company = getCompanyByIntegrationId((JSONObject) snSysUserJson, SnTable.Company.get(), App.Value());
                        if (company != null)
                            sysUser.setCompany(company);

                        Location location = getLocationByIntegrationId((JSONObject) snSysUserJson, "location", App.Value());
                        if (location != null)
                            sysUser.setLocation(location);

                        Department department = getDepartmentByIntegrationId((JSONObject) snSysUserJson, "department", App.Value());
                        if (department != null)
                            sysUser.setDepartment(department);

                        //if (sysUser.getSolver())
                        //sysUser.setPassword("$2a$10$zGJsiIy5q/kQhFLWsHDIO.rde0UzUS5oH.I.Z.U7d/gXjieMmPaZm");

                        String tagAction = App.CreateConsole();
                        SysUser exists = sysUserService.findByIntegrationId(sysUser.getIntegrationId());
                        if (exists != null) {
                            sysUser.setId(exists.getId());
                            sysUser.setPassword(exists.getPassword());
                            tagAction = App.UpdateConsole();
                        }
                        sysUserService.save(sysUser);
                        Util.printData(tag, count[0], tagAction.concat(Util.getFieldDisplay(sysUser)), Util.getFieldDisplay(company), Util.getFieldDisplay(location), Util.getFieldDisplay(department));
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
            }

        } catch (Exception e) {
            System.out.println(tag.concat("Exception (II) : ").concat(String.valueOf(e)));
        }
        System.out.println(App.End());
        return snSysUsers;
    }


    @GetMapping("/sn_sys_users_solver")
    public List<SnSysUser> show(boolean query) {


        System.out.println(App.Start());
        APIResponse apiResponse = null;
        List<SnSysUser> snSysUsers = new ArrayList<>();
        String[] sparmOffSets = Util.offSets500000();
        long startTime = 0;
        long endTime = 0;
        String tag = "[SysUser] ";
        try {
            startTime = System.currentTimeMillis();
            final int[] count = {1};
            for (String sparmOffSet : sparmOffSets) {
                String result = rest.responseByEndPoint(EndPointSN.SysUser().concat(sparmOffSet));
                System.out.println(tag.concat("(".concat(EndPointSN.SysUser().concat(sparmOffSet)).concat(")")));
                ObjectMapper mapper = new ObjectMapper();
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                JSONParser parser = new JSONParser();
                JSONObject resultJson = new JSONObject();
                JSONArray ListSnSysUserJson = new JSONArray();

                resultJson = (JSONObject) parser.parse(result);
                if (resultJson.get("result") != null)
                    ListSnSysUserJson = (JSONArray) parser.parse(resultJson.get("result").toString());

                ListSnSysUserJson.stream().forEach(snSysUserJson -> {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    SnSysUser snSysUser = new SnSysUser();
                    try {
                        snSysUser = objectMapper.readValue(snSysUserJson.toString(), SnSysUser.class);
                        snSysUsers.add(snSysUser);
                        SysUser sysUser = new SysUser();
                        sysUser.setActive(snSysUser.getActive());
                        sysUser.setName(snSysUser.getName());
                        sysUser.setIntegrationId(snSysUser.getSys_id());
                        sysUser.setVip(snSysUser.getVip());
                        sysUser.setEmail(snSysUser.getEmail().toLowerCase(Locale.ROOT));
                        sysUser.setEmployeeNumber(snSysUser.getEmployee_number());
                        sysUser.setFirstName(snSysUser.getFirst_name());
                        sysUser.setUserName(snSysUser.getUser_name());
                        sysUser.setLastName(snSysUser.getLast_name());
                        sysUser.setVip(snSysUser.getVip());
                        sysUser.setSolver(snSysUser.getU_solver());
                        sysUser.setMobilePhone(snSysUser.getMobile_phone());
                        sysUser.setUserType(snSysUser.getU_user_type());
                        sysUser.setManager(getIntegrationId((JSONObject) snSysUserJson, "manager", App.Value()));
                        Domain domain = getDomainByIntegrationId((JSONObject) snSysUserJson, SnTable.Domain.get(), App.Value());
                        if (domain != null)
                            sysUser.setDomain(domain);

                        Company company = getCompanyByIntegrationId((JSONObject) snSysUserJson, SnTable.Company.get(), App.Value());
                        if (company != null)
                            sysUser.setCompany(company);

                        Location location = getLocationByIntegrationId((JSONObject) snSysUserJson, "location", App.Value());
                        if (location != null)
                            sysUser.setLocation(location);

                        Department department = getDepartmentByIntegrationId((JSONObject) snSysUserJson, "department", App.Value());
                        if (department != null)
                            sysUser.setDepartment(department);

                        //if (sysUser.getSolver())
                        //sysUser.setPassword("$2a$10$zGJsiIy5q/kQhFLWsHDIO.rde0UzUS5oH.I.Z.U7d/gXjieMmPaZm");

                        String tagAction = App.CreateConsole();
                        SysUser exists = sysUserService.findByIntegrationId(sysUser.getIntegrationId());
                        if (exists != null) {
                            sysUser.setId(exists.getId());
                            sysUser.setPassword(exists.getPassword());
                            tagAction = App.UpdateConsole();
                        }
                        sysUserService.save(sysUser);
                        Util.printData(tag, count[0], tagAction.concat(Util.getFieldDisplay(sysUser)), Util.getFieldDisplay(company), Util.getFieldDisplay(location), Util.getFieldDisplay(department));


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
            }

        } catch (Exception e) {
            System.out.println(tag.concat("Exception (II) : ").concat(String.valueOf(e)));
        }
        System.out.println(App.End());
        return snSysUsers;
    }
    @GetMapping("/snSyUsersSolverByQuery")
    public List<SnSysUser> show(String query) {
        System.out.println(App.Start());
        APIResponse apiResponse = null;
        List<SnSysUser> snSysUsers = new ArrayList<>();
        String[] sparmOffSets = Util.offSets500000();
        long startTime = 0;
        long endTime = 0;
        String tag = "[SysUser] ";
        try {
            startTime = System.currentTimeMillis();
            final int[] count = {1};
            for (String sparmOffSet : sparmOffSets) {
                String  result = rest.responseByEndPoint(EndPointSN.SysUserByQuery().replace("QUERY", query).concat(sparmOffSet));
                System.out.println(tag.concat("(".concat(EndPointSN.SysUserByQuery().replace("QUERY", query).concat(sparmOffSet)).concat(")")));
                ObjectMapper mapper = new ObjectMapper();
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                JSONParser parser = new JSONParser();
                JSONObject resultJson = new JSONObject();
                JSONArray ListSnSysUserJson = new JSONArray();

                resultJson = (JSONObject) parser.parse(result);
                if (resultJson.get("result") != null)
                    ListSnSysUserJson = (JSONArray) parser.parse(resultJson.get("result").toString());

                ListSnSysUserJson.stream().forEach(snSysUserJson -> {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    SnSysUser snSysUser = new SnSysUser();
                    try {
                      //  snSysUser = objectMapper.readValue(snSysUserJson.toString(), SnSysUser.class);
                        Gson gson = new Gson();
                        snSysUser = gson.fromJson(snSysUserJson.toString(), SnSysUser.class);

                        snSysUsers.add(snSysUser);
                        SysUser sysUser = new SysUser();
                        sysUser.setActive(snSysUser.getActive());
                        sysUser.setName(snSysUser.getName());
                        sysUser.setIntegrationId(snSysUser.getSys_id());
                        sysUser.setVip(snSysUser.getVip());
                        sysUser.setEmail(snSysUser.getEmail().toLowerCase(Locale.ROOT));
                        sysUser.setEmployeeNumber(snSysUser.getEmployee_number());
                        sysUser.setFirstName(snSysUser.getFirst_name());
                        sysUser.setUserName(snSysUser.getUser_name());
                        sysUser.setLastName(snSysUser.getLast_name());
                        sysUser.setVip(snSysUser.getVip());
                        sysUser.setSolver(snSysUser.getU_solver());
                        sysUser.setMobilePhone(snSysUser.getMobile_phone());
                        sysUser.setManager(getIntegrationId((JSONObject) snSysUserJson, "manager", App.Value()));
                        Domain domain = getDomainByIntegrationId((JSONObject) snSysUserJson, SnTable.Domain.get(), App.Value());
                        if (domain != null)
                            sysUser.setDomain(domain);

                        Company company = getCompanyByIntegrationId((JSONObject) snSysUserJson, SnTable.Company.get(), App.Value());
                        if (company != null)
                            sysUser.setCompany(company);

                        Location location = getLocationByIntegrationId((JSONObject) snSysUserJson, "location", App.Value());
                        if (location != null)
                            sysUser.setLocation(location);

                        Department department = getDepartmentByIntegrationId((JSONObject) snSysUserJson, "department", App.Value());
                        if (department != null)
                            sysUser.setDepartment(department);

                        //if (sysUser.getSolver())
                        //sysUser.setPassword("$2a$10$zGJsiIy5q/kQhFLWsHDIO.rde0UzUS5oH.I.Z.U7d/gXjieMmPaZm");

                        String tagAction = App.CreateConsole();
                        SysUser exists = sysUserService.findByIntegrationId(sysUser.getIntegrationId());
                        if (exists != null) {
                            sysUser.setId(exists.getId());
                            sysUser.setPassword(exists.getPassword());
                            tagAction = App.UpdateConsole();
                        }
                        sysUserService.save(sysUser);
                        Util.printData(tag, count[0], tagAction.concat(Util.getFieldDisplay(sysUser)), Util.getFieldDisplay(company), Util.getFieldDisplay(location), Util.getFieldDisplay(department));


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
            }

        } catch (Exception e) {
            System.out.println(tag.concat("Exception (II) : ").concat(String.valueOf(e)));
        }
        System.out.println(App.End());
        return snSysUsers;
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

    public Location getLocationByIntegrationId(JSONObject jsonObject, String levelOne, String levelTwo) {
        String integrationId = Util.getIdByJson(jsonObject, levelOne, levelTwo);
        if (Util.hasData(integrationId)) {
            return locationService.findByIntegrationId(integrationId);
        } else
            return null;
    }

    public Department getDepartmentByIntegrationId(JSONObject jsonObject, String levelOne, String levelTwo) {
        String integrationId = Util.getIdByJson(jsonObject, levelOne, levelTwo);
        if (Util.hasData(integrationId)) {
            return departmentService.findByIntegrationId(integrationId);
        } else
            return null;
    }

    public String getIntegrationId(JSONObject jsonObject, String levelOne, String levelTwo) {
        String integrationId = Util.getIdByJson(jsonObject, levelOne, levelTwo);
        if (Util.hasData(integrationId)) {
            return integrationId;
        } else
            return "";
    }
}

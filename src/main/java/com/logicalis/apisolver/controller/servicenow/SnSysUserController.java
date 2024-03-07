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

import com.logicalis.apisolver.model.UserType;
import com.logicalis.apisolver.services.IUserTypeService;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@CrossOrigin(origins = {"${app.api.settings.cross-origin.urls}", "*"})
@RestController
@RequestMapping("/api/v1")
@Slf4j
public class SnSysUserController {
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
    @Autowired
    private  IUserTypeService userTypeService;

    @GetMapping("/sn_sys_users")
    public List<SnSysUser> show() {
        System.out.println("Llego a esta Lista");
        log.info(App.Start());
        APIResponse apiResponse = null;
        List<SnSysUser> snSysUsers = new ArrayList<>();
        String[] sparmOffSets = Util.offSets500000();
        long startTime = 0;
        long endTime = 0;
        String tag = "[SysUser] ";
        try {
            startTime = System.currentTimeMillis();
            final int[] count = {1};
            String result;
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JSONParser parser = new JSONParser();
            JSONObject resultJson = new JSONObject();
            JSONArray ListSnSysUserJson = new JSONArray();
            final SysUser[] sysUser = {new SysUser()};
            final Domain[] domain = new Domain[1];
            final SnSysUser[] snSysUser = {new SnSysUser()};
            final Company[] company = new Company[1];
            final Location[] location = new Location[1];
            final Department[] department = new Department[1];
            final String[] tagAction = new String[1];
            final SysUser[] exists = new SysUser[1];
            APIExecutionStatus status = new APIExecutionStatus();
            for (String sparmOffSet : sparmOffSets) {
                result = rest.responseByEndPoint(EndPointSN.SysUser().concat(sparmOffSet));
                log.info(tag.concat("(".concat(EndPointSN.SysUser().concat(sparmOffSet)).concat(")")));
                resultJson = (JSONObject) parser.parse(result);
                ListSnSysUserJson.clear();
                if (resultJson.get("result") != null)
                    ListSnSysUserJson = (JSONArray) parser.parse(resultJson.get("result").toString());

                ListSnSysUserJson.stream().forEach(snSysUserJson -> {
                    try {
                        snSysUser[0] = mapper.readValue(snSysUserJson.toString(), SnSysUser.class);
                        snSysUsers.add(snSysUser[0]);
                        sysUser[0] = new SysUser();
                        sysUser[0].setActive(snSysUser[0].getActive());
                        sysUser[0].setName(snSysUser[0].getName());
                        sysUser[0].setIntegrationId(snSysUser[0].getSys_id());
                        sysUser[0].setVip(snSysUser[0].getVip());
                        sysUser[0].setEmail(snSysUser[0].getEmail().toLowerCase(Locale.ROOT));
                        sysUser[0].setEmployeeNumber(snSysUser[0].getEmployee_number());
                        sysUser[0].setFirstName(snSysUser[0].getFirst_name());
                        sysUser[0].setUserName(snSysUser[0].getUser_name());
                        sysUser[0].setLastName(snSysUser[0].getLast_name());
                        sysUser[0].setVip(snSysUser[0].getVip());
                        sysUser[0].setSolver(snSysUser[0].getU_solver());
                        sysUser[0].setMobilePhone(snSysUser[0].getMobile_phone());
                        sysUser[0].setManager(getIntegrationId((JSONObject) snSysUserJson, "manager", App.Value()));
                        domain[0] = getDomainByIntegrationId((JSONObject) snSysUserJson, SnTable.Domain.get(), App.Value());
                        if (domain[0] != null)
                            sysUser[0].setDomain(domain[0]);
                        company[0] = getCompanyByIntegrationId((JSONObject) snSysUserJson, SnTable.Company.get(), App.Value());
                        if (company[0] != null)
                            sysUser[0].setCompany(company[0]);
                        location[0] = getLocationByIntegrationId((JSONObject) snSysUserJson, "location", App.Value());
                        if (location[0] != null)
                            sysUser[0].setLocation(location[0]);
                        department[0] = getDepartmentByIntegrationId((JSONObject) snSysUserJson, "department", App.Value());
                        if (department[0] != null)
                            sysUser[0].setDepartment(department[0]);
                        tagAction[0] = App.CreateConsole();
                        exists[0] = sysUserService.findByIntegrationId(sysUser[0].getIntegrationId());
                        if (exists[0] != null) {
                            sysUser[0].setId(exists[0].getId());
                            sysUser[0].setPassword(exists[0].getPassword());
                            tagAction[0] = App.UpdateConsole();
                        }
                        sysUserService.save(sysUser[0]);
                        Util.printData(tag, count[0], tagAction[0].concat(Util.getFieldDisplay(sysUser[0])), Util.getFieldDisplay(company[0]), Util.getFieldDisplay(location[0]), Util.getFieldDisplay(department[0]));
                        count[0] = count[0] + 1;
                    } catch (Exception e) {
                        log.info(tag.concat("Exception (I) : ").concat(String.valueOf(e)));
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
            log.info(tag.concat("Exception (II) : ").concat(String.valueOf(e)));
        }
        log.info(App.End());
        return snSysUsers;
    }

    @GetMapping("/sn_sys_users_solver")
    public List<SnSysUser> show(boolean query) {

        List<UserType> allUserType =userTypeService.findAll();


        log.info(App.Start());
        APIResponse apiResponse = null;
        List<SnSysUser> snSysUsers = new ArrayList<>();
        String[] sparmOffSets = Util.offSets500000();
        long startTime = 0;
        long endTime = 0;
        String tag = "[SysUser] ";
        try {
            startTime = System.currentTimeMillis();
            final int[] count = {1};

            String result;
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JSONParser parser = new JSONParser();
            JSONObject resultJson = new JSONObject();
            JSONArray ListSnSysUserJson = new JSONArray();
            final SysUser[] sysUser = {new SysUser()};
            final Domain[] domain = new Domain[1];
            final Company[] company = new Company[1];
            final SnSysUser[] snSysUser = {new SnSysUser()};
            final Location[] location = new Location[1];
            final Department[] department = new Department[1];
            final String[] tagAction = new String[1];
            final SysUser[] exists = new SysUser[1];
            APIExecutionStatus status = new APIExecutionStatus();
            for (String sparmOffSet : sparmOffSets) {

                result = rest.responseByEndPoint(EndPointSN.SysUser().concat(sparmOffSet));
                log.info(tag.concat("(".concat(EndPointSN.SysUser().concat(sparmOffSet)).concat(")")));
                resultJson = (JSONObject) parser.parse(result);
                ListSnSysUserJson.clear();
                if (resultJson.get("result") != null)
                    ListSnSysUserJson = (JSONArray) parser.parse(resultJson.get("result").toString());

                ListSnSysUserJson.stream().forEach(snSysUserJson -> {
                    try {
                        snSysUser[0] = mapper.readValue(snSysUserJson.toString(), SnSysUser.class);
                        snSysUsers.add(snSysUser[0]);
                        sysUser[0] = new SysUser();
                        sysUser[0].setActive(snSysUser[0].getActive());
                        sysUser[0].setName(snSysUser[0].getName());
                        sysUser[0].setIntegrationId(snSysUser[0].getSys_id());
                        sysUser[0].setVip(snSysUser[0].getVip());
                        sysUser[0].setEmail(snSysUser[0].getEmail().toLowerCase(Locale.ROOT));
                        sysUser[0].setEmployeeNumber(snSysUser[0].getEmployee_number());
                        sysUser[0].setFirstName(snSysUser[0].getFirst_name());
                        sysUser[0].setUserName(snSysUser[0].getUser_name());
                        sysUser[0].setLastName(snSysUser[0].getLast_name());
                        sysUser[0].setVip(snSysUser[0].getVip());
                        sysUser[0].setSolver(snSysUser[0].getU_solver());
                        sysUser[0].setMobilePhone(snSysUser[0].getMobile_phone());

                        boolean condicionCumplidaTipoUsuario = false;

                        for (UserType userT : allUserType){

                            if (snSysUser[0].getU_user_type() == null){
                                sysUser[0].setUserType(0L);
                                condicionCumplidaTipoUsuario = true;
                                break;
                            }

                            if (userT.getName().toLowerCase().equals(snSysUser[0].getU_user_type().toLowerCase())){
                                sysUser[0].setUserType(userT.getId());
                                condicionCumplidaTipoUsuario = true;
                                break;
                            }
                        }
                        // En caso no coincida con ninguno de los tipos de variables que estan en la tabla se asignara "0" - No asignado
                        if (!condicionCumplidaTipoUsuario) {
                            sysUser[0].setUserType(0L);
                        }

                        sysUser[0].setManager(getIntegrationId((JSONObject) snSysUserJson, "manager", App.Value()));
                        domain[0] = getDomainByIntegrationId((JSONObject) snSysUserJson, SnTable.Domain.get(), App.Value());
                        if (domain[0] != null)
                            sysUser[0].setDomain(domain[0]);

                        company[0] = getCompanyByIntegrationId((JSONObject) snSysUserJson, SnTable.Company.get(), App.Value());
                        if (company[0] != null)
                            sysUser[0].setCompany(company[0]);

                        location[0] = getLocationByIntegrationId((JSONObject) snSysUserJson, "location", App.Value());
                        if (location[0] != null)
                            sysUser[0].setLocation(location[0]);

                        department[0] = getDepartmentByIntegrationId((JSONObject) snSysUserJson, "department", App.Value());
                        if (department[0] != null)
                            sysUser[0].setDepartment(department[0]);

                        tagAction[0] = App.CreateConsole();
                        exists[0] = sysUserService.findByIntegrationId(sysUser[0].getIntegrationId());
                        if (exists[0] != null) {
                            sysUser[0].setId(exists[0].getId());
                            sysUser[0].setPassword(exists[0].getPassword());
                            tagAction[0] = App.UpdateConsole();
                        }
                        sysUserService.save(sysUser[0]);
                        Util.printData(tag, count[0], tagAction[0].concat(Util.getFieldDisplay(sysUser[0])), Util.getFieldDisplay(company[0]), Util.getFieldDisplay(location[0]), Util.getFieldDisplay(department[0]));
                        count[0] = count[0] + 1;
                    } catch (Exception e) {
                        log.info(tag.concat("Exception (I) : ").concat(String.valueOf(e)));
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
            log.info(tag.concat("Exception (II) : ").concat(String.valueOf(e)));
        }
        log.info(App.End());
        return snSysUsers;
    }
    @GetMapping("/snSyUsersSolverByQuery")
    public List<SnSysUser> show(String query) {
        System.out.println("llego a esta parte 3");
        log.info(App.Start());
        APIResponse apiResponse = null;
        List<SnSysUser> snSysUsers = new ArrayList<>();
        String[] sparmOffSets = Util.offSets500000();
        long startTime = 0;
        long endTime = 0;
        String tag = "[SysUser] ";
        try {
            startTime = System.currentTimeMillis();
            final int[] count = {1};
            String  result;
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JSONParser parser = new JSONParser();
            JSONObject resultJson = new JSONObject();
            JSONArray ListSnSysUserJson = new JSONArray();
            final Domain[] domain = new Domain[1];
            Gson gson = new Gson();
            final SysUser[] sysUser = {new SysUser()};
            final SnSysUser[] snSysUser = {new SnSysUser()};
            final Company[] company = new Company[1];
            final Location[] location = new Location[1];
            final Department[] department = new Department[1];
            final String[] tagAction = new String[1];
            final SysUser[] exists = new SysUser[1];
            APIExecutionStatus status = new APIExecutionStatus();
            for (String sparmOffSet : sparmOffSets) {
                result = rest.responseByEndPoint(EndPointSN.SysUserByQuery().replace("QUERY", query).concat(sparmOffSet));
                log.info(tag.concat("(".concat(EndPointSN.SysUserByQuery().replace("QUERY", query).concat(sparmOffSet)).concat(")")));
                resultJson = (JSONObject) parser.parse(result);
                ListSnSysUserJson.clear();
                if (resultJson.get("result") != null)
                    ListSnSysUserJson = (JSONArray) parser.parse(resultJson.get("result").toString());

                ListSnSysUserJson.stream().forEach(snSysUserJson -> {
                    snSysUser[0] = new SnSysUser();
                    try {
                        snSysUser[0] = gson.fromJson(snSysUserJson.toString(), SnSysUser.class);
                        snSysUsers.add(snSysUser[0]);
                        sysUser[0] = new SysUser();
                        sysUser[0].setActive(snSysUser[0].getActive());
                        sysUser[0].setName(snSysUser[0].getName());
                        sysUser[0].setIntegrationId(snSysUser[0].getSys_id());
                        sysUser[0].setVip(snSysUser[0].getVip());
                        sysUser[0].setEmail(snSysUser[0].getEmail().toLowerCase(Locale.ROOT));
                        sysUser[0].setEmployeeNumber(snSysUser[0].getEmployee_number());
                        sysUser[0].setFirstName(snSysUser[0].getFirst_name());
                        sysUser[0].setUserName(snSysUser[0].getUser_name());
                        sysUser[0].setLastName(snSysUser[0].getLast_name());
                        sysUser[0].setVip(snSysUser[0].getVip());
                        sysUser[0].setSolver(snSysUser[0].getU_solver());
                        sysUser[0].setMobilePhone(snSysUser[0].getMobile_phone());
                        sysUser[0].setManager(getIntegrationId((JSONObject) snSysUserJson, "manager", App.Value()));
                        domain[0] = getDomainByIntegrationId((JSONObject) snSysUserJson, SnTable.Domain.get(), App.Value());
                        if (domain[0] != null)
                            sysUser[0].setDomain(domain[0]);
                        company[0] = getCompanyByIntegrationId((JSONObject) snSysUserJson, SnTable.Company.get(), App.Value());
                        if (company[0] != null)
                            sysUser[0].setCompany(company[0]);
                        location[0] = getLocationByIntegrationId((JSONObject) snSysUserJson, "location", App.Value());
                        if (location[0] != null)
                            sysUser[0].setLocation(location[0]);
                        department[0] = getDepartmentByIntegrationId((JSONObject) snSysUserJson, "department", App.Value());
                        if (department[0] != null)
                            sysUser[0].setDepartment(department[0]);
                        tagAction[0] = App.CreateConsole();
                        exists[0] = sysUserService.findByIntegrationId(sysUser[0].getIntegrationId());
                        if (exists[0] != null) {
                            sysUser[0].setId(exists[0].getId());
                            sysUser[0].setPassword(exists[0].getPassword());
                            tagAction[0] = App.UpdateConsole();
                        }
                        sysUserService.save(sysUser[0]);
                        Util.printData(tag, count[0], tagAction[0].concat(Util.getFieldDisplay(sysUser[0])), Util.getFieldDisplay(company[0]), Util.getFieldDisplay(location[0]), Util.getFieldDisplay(department[0]));
                        count[0] = count[0] + 1;
                    } catch (Exception e) {
                        log.info(tag.concat("Exception (I) : ").concat(String.valueOf(e)));
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
            log.info(tag.concat("Exception (II) : ").concat(String.valueOf(e)));
        }
        log.info(App.End());
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

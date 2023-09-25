package com.logicalis.apisolver.controller.servicenow;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.logicalis.apisolver.model.*;
import com.logicalis.apisolver.model.enums.App;
import com.logicalis.apisolver.model.enums.EndPointSN;
import com.logicalis.apisolver.model.enums.SnTable;
import com.logicalis.apisolver.model.servicenow.SnScRequest;
import com.logicalis.apisolver.services.*;
import com.logicalis.apisolver.services.servicenow.ISnScRequestService;
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
public class SnScRequestController {
    @Autowired
    private IScRequestService scRequestService;
    @Autowired
    private IDomainService domainService;
    @Autowired
    private IAPIExecutionStatusService statusService;
    @Autowired
    private ICompanyService companyService;
    @Autowired
    private ISysUserService sysUserService;
    @Autowired
    private ILocationService locationService;
    @Autowired
    private ICiServiceService ciServiceService;
    @Autowired
    private ISysGroupService sysGroupService;
    @Autowired
    private IConfigurationItemService configurationItemService;
    @Autowired
    private Rest rest;

    @GetMapping("/sn_requests_by_solver")
    public List<SnScRequest> show() {
        System.out.println(App.Start());
        APIResponse apiResponse = null;
        List<SnScRequest> snScRequests = new ArrayList<>();
        String[] sparmOffSets = Util.offSets99000();
        long startTime = 0;
        long endTime = 0;
        String tag = "[ScRequest] ";
        try {
            startTime = System.currentTimeMillis();
            final int[] count = {1};
            String result;
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JSONParser parser = new JSONParser();
            JSONObject resultJson = new JSONObject();
            JSONArray ListSnScRequestJson = new JSONArray();
            final SnScRequest[] snScRequest = {new SnScRequest()};
            final ScRequest[] scRequest = {new ScRequest()};
            APIExecutionStatus status = new APIExecutionStatus();
            final Domain[] domain = new Domain[1];
            final Company[] company = new Company[1];
            final SysUser[] requestedFor = new SysUser[1];
            final SysUser[] openedBy = new SysUser[1];
            final SysUser[] assignedTo = new SysUser[1];
            final SysUser[] taskFor = new SysUser[1];
            final Location[] location = new Location[1];
            final SysGroup[] sysGroup = new SysGroup[1];
            final ScRequest[] exists = new ScRequest[1];
            final String[] tagAction = new String[1];
            for (String sparmOffSet : sparmOffSets) {
                result = rest.responseByEndPoint(EndPointSN.ScRequest().concat(sparmOffSet));
                System.out.println(tag.concat("(".concat(EndPointSN.ScRequest().concat(sparmOffSet)).concat(")")));
                resultJson = (JSONObject) parser.parse(result);
                ListSnScRequestJson.clear();
                if (resultJson.get("result") != null)
                    ListSnScRequestJson = (JSONArray) parser.parse(resultJson.get("result").toString());

                ListSnScRequestJson.stream().forEach(snScRequestJson -> {
                    snScRequest[0] = new SnScRequest();
                    try {
                        snScRequest[0] = mapper.readValue(snScRequestJson.toString(), SnScRequest.class);
                        snScRequests.add(snScRequest[0]);
                        scRequest[0] = new ScRequest();
                        scRequest[0].setSysUpdatedOn(snScRequest[0].getSys_updated_on());
                        scRequest[0].setNumber(snScRequest[0].getNumber());
                        scRequest[0].setState(snScRequest[0].getState());
                        scRequest[0].setSysCreatedBy(snScRequest[0].getSys_created_by());
                        scRequest[0].setEscalation(snScRequest[0].getEscalation());
                        scRequest[0].setExpectedStart(snScRequest[0].getExpected_start());
                        scRequest[0].setStage(snScRequest[0].getStage());
                        scRequest[0].setActive(snScRequest[0].isActive());
                        scRequest[0].setShortDescription(snScRequest[0].getShort_description());
                        scRequest[0].setCorrelationDisplay(snScRequest[0].getCorrelation_display());
                        scRequest[0].setCorrelationId(snScRequest[0].getCorrelation_id());
                        scRequest[0].setSysUpdatedBy(snScRequest[0].getSys_updated_by());
                        scRequest[0].setSysCreatedOn(snScRequest[0].getSys_created_on());
                        scRequest[0].setOpenedAt(snScRequest[0].getOpened_at());
                        scRequest[0].setDescription(snScRequest[0].getDescription());
                        scRequest[0].setIntegrationId(snScRequest[0].getSys_id());
                        scRequest[0].setDueDate(snScRequest[0].getDue_date());
                        scRequest[0].setContactType(snScRequest[0].getContact_type());
                        scRequest[0].setApproval(snScRequest[0].getApproval());

                        domain[0] = getDomainByIntegrationId((JSONObject) snScRequestJson, SnTable.Domain.get(), App.Value());
                        if (domain[0] != null)
                            scRequest[0].setDomain(domain[0]);

                        company[0] = getCompanyByIntegrationId((JSONObject) snScRequestJson, SnTable.Company.get(), App.Value());
                        if (company[0] != null)
                            scRequest[0].setCompany(company[0]);

                        requestedFor[0] = getSysUserByIntegrationId((JSONObject) snScRequestJson, "requested_for", App.Value());
                        if (requestedFor[0] != null)
                            scRequest[0].setRequestedFor(requestedFor[0]);

                        openedBy[0] = getSysUserByIntegrationId((JSONObject) snScRequestJson, "opened_by", App.Value());
                        if (openedBy[0] != null)
                            scRequest[0].setOpenedBy(openedBy[0]);

                        assignedTo[0] = getSysUserByIntegrationId((JSONObject) snScRequestJson, "assigned_to", App.Value());
                        if (assignedTo[0] != null)
                            scRequest[0].setAssignedTo(assignedTo[0]);

                        taskFor[0] = getSysUserByIntegrationId((JSONObject) snScRequestJson, "task_for", App.Value());
                        if (taskFor[0] != null)
                            scRequest[0].setTaskFor(taskFor[0]);

                        location[0] = getLocationByIntegrationId((JSONObject) snScRequestJson, "location", App.Value());
                        if (location[0] != null)
                            scRequest[0].setLocation(location[0]);

                        sysGroup[0] = getSysGroupByIntegrationId((JSONObject) snScRequestJson, "assignment_group", App.Value());
                        if (sysGroup[0] != null)
                            scRequest[0].setAssignmentGroup(sysGroup[0]);

                        exists[0] = scRequestService.findByIntegrationId(scRequest[0].getIntegrationId());
                        tagAction[0] = App.CreateConsole();
                        if (exists[0] != null) {
                            scRequest[0].setId(exists[0].getId());
                            tagAction[0] = App.UpdateConsole();
                        }

                        Util.printData(tag, count[0], tagAction[0].concat(Util.getFieldDisplay(scRequest[0])), Util.getFieldDisplay(company[0]), Util.getFieldDisplay(domain[0]));
                        scRequestService.save(scRequest[0]);
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
        return snScRequests;
    }

    @GetMapping("/snRequestsBySolverAndQuery")
    public List<SnScRequest> show(String query) {
        System.out.println(App.Start());
        APIResponse apiResponse = null;
        List<SnScRequest> snScRequests = new ArrayList<>();
        String[] sparmOffSets = Util.offSets99000();
        long startTime = 0;
        long endTime = 0;
        String tag = "[ScRequest] ";

        try {
            startTime = System.currentTimeMillis();
            final int[] count = {1};
            String result;
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JSONParser parser = new JSONParser();
            JSONObject resultJson = new JSONObject();
            JSONArray ListSnScRequestJson = new JSONArray();
            final SnScRequest[] snScRequest = {new SnScRequest()};
            Gson gson = new Gson();
            final ScRequest[] scRequest = {new ScRequest()};
            final APIExecutionStatus[] status = {new APIExecutionStatus()};
            final Domain[] domain = new Domain[1];
            final Company[] company = new Company[1];
            final SysUser[] requestedFor = new SysUser[1];
            final SysUser[] openedBy = new SysUser[1];
            final SysUser[] assignedTo = new SysUser[1];
            final SysUser[] taskFor = new SysUser[1];
            final Location[] location = new Location[1];
            final SysGroup[] sysGroup = new SysGroup[1];
            final ScRequest[] exists = new ScRequest[1];
            final String[] tagAction = new String[1];
            for (String sparmOffSet : sparmOffSets) {
                result = rest.responseByEndPoint(EndPointSN.ScRequestByQuery().replace("QUERY", query).concat(sparmOffSet));
                System.out.println(tag.concat("(".concat(EndPointSN.ScRequestByQuery().replace("QUERY", query).concat(sparmOffSet)).concat(")")));

                resultJson = (JSONObject) parser.parse(result);
                ListSnScRequestJson.clear();
                if (resultJson.get("result") != null)
                    ListSnScRequestJson = (JSONArray) parser.parse(resultJson.get("result").toString());

                ListSnScRequestJson.stream().forEach(snScRequestJson -> {
                    snScRequest[0] = new SnScRequest();
                    try {
                        snScRequest[0] = gson.fromJson(snScRequestJson.toString(), SnScRequest.class);
                        snScRequests.add(snScRequest[0]);
                        scRequest[0] = new ScRequest();
                        scRequest[0].setSysUpdatedOn(snScRequest[0].getSys_updated_on());
                        scRequest[0].setNumber(snScRequest[0].getNumber());
                        scRequest[0].setState(snScRequest[0].getState());
                        scRequest[0].setSysCreatedBy(snScRequest[0].getSys_created_by());
                        scRequest[0].setEscalation(snScRequest[0].getEscalation());
                        scRequest[0].setExpectedStart(snScRequest[0].getExpected_start());
                        scRequest[0].setStage(snScRequest[0].getStage());
                        scRequest[0].setActive(snScRequest[0].isActive());
                        scRequest[0].setShortDescription(snScRequest[0].getShort_description());
                        scRequest[0].setCorrelationDisplay(snScRequest[0].getCorrelation_display());
                        scRequest[0].setCorrelationId(snScRequest[0].getCorrelation_id());
                        scRequest[0].setSysUpdatedBy(snScRequest[0].getSys_updated_by());
                        scRequest[0].setSysCreatedOn(snScRequest[0].getSys_created_on());
                        scRequest[0].setOpenedAt(snScRequest[0].getOpened_at());
                        scRequest[0].setDescription(snScRequest[0].getDescription());
                        scRequest[0].setIntegrationId(snScRequest[0].getSys_id());
                        scRequest[0].setDueDate(snScRequest[0].getDue_date());
                        scRequest[0].setContactType(snScRequest[0].getContact_type());
                        scRequest[0].setApproval(snScRequest[0].getApproval());

                        domain[0] = getDomainByIntegrationId((JSONObject) snScRequestJson, SnTable.Domain.get(), App.Value());
                        if (domain[0] != null)
                            scRequest[0].setDomain(domain[0]);

                        company[0] = getCompanyByIntegrationId((JSONObject) snScRequestJson, SnTable.Company.get(), App.Value());
                        if (company[0] != null)
                            scRequest[0].setCompany(company[0]);

                        requestedFor[0] = getSysUserByIntegrationId((JSONObject) snScRequestJson, "requested_for", App.Value());
                        if (requestedFor[0] != null)
                            scRequest[0].setRequestedFor(requestedFor[0]);

                        openedBy[0] = getSysUserByIntegrationId((JSONObject) snScRequestJson, "opened_by", App.Value());
                        if (openedBy[0] != null)
                            scRequest[0].setOpenedBy(openedBy[0]);

                        assignedTo[0] = getSysUserByIntegrationId((JSONObject) snScRequestJson, "assigned_to", App.Value());
                        if (assignedTo[0] != null)
                            scRequest[0].setAssignedTo(assignedTo[0]);

                        taskFor[0] = getSysUserByIntegrationId((JSONObject) snScRequestJson, "task_for", App.Value());
                        if (taskFor[0] != null)
                            scRequest[0].setTaskFor(taskFor[0]);

                        location[0] = getLocationByIntegrationId((JSONObject) snScRequestJson, "location", App.Value());
                        if (location[0] != null)
                            scRequest[0].setLocation(location[0]);

                        sysGroup[0] = getSysGroupByIntegrationId((JSONObject) snScRequestJson, "assignment_group", App.Value());
                        if (sysGroup[0] != null)
                            scRequest[0].setAssignmentGroup(sysGroup[0]);

                        exists[0] = scRequestService.findByIntegrationId(scRequest[0].getIntegrationId());
                        tagAction[0] = App.CreateConsole();
                        if (exists[0] != null) {
                            scRequest[0].setId(exists[0].getId());
                            tagAction[0] = App.UpdateConsole();
                        }
                        Util.printData(tag, count[0], tagAction[0].concat(Util.getFieldDisplay(scRequest[0])), Util.getFieldDisplay(company[0]), Util.getFieldDisplay(domain[0]));
                        scRequestService.save(scRequest[0]);
                        count[0] = count[0] + 1;
                    } catch (Exception e) {
                        System.out.println(tag.concat("Exception (I) : ").concat(String.valueOf(e)));
                    }
                });

                apiResponse = mapper.readValue(result, APIResponse.class);
                status[0].setUri(EndPointSN.Location());
                status[0].setUserAPI(App.SNUser());
                status[0].setPasswordAPI(App.SNPassword());
                status[0].setError(apiResponse.getError());
                status[0].setMessage(apiResponse.getMessage());
                endTime = (System.currentTimeMillis() - startTime);
                status[0].setExecutionTime(endTime);
                statusService.save(status[0]);
            }

        } catch (Exception e) {
            System.out.println(tag.concat("Exception (II) : ").concat(String.valueOf(e)));
        }
        System.out.println(App.End());
        return snScRequests;
    }

    public Domain getDomainByIntegrationId(JSONObject jsonObject, String levelOne, String levelTwo) {
        String integrationId = Util.getIdByJson(jsonObject, levelOne, levelTwo);
        if (Util.hasData(integrationId)) {
            return domainService.findByIntegrationId(integrationId);
        } else
            return null;
    }

    public SysUser getSysUserByIntegrationId(JSONObject jsonObject, String levelOne, String levelTwo) {
        String integrationId = Util.getIdByJson(jsonObject, levelOne, levelTwo);
        if (Util.hasData(integrationId)) {
            return sysUserService.findByIntegrationId(integrationId);
        } else
            return null;
    }

    public CiService getCiServiceByIntegrationId(JSONObject jsonObject, String levelOne, String levelTwo) {
        String integrationId = Util.getIdByJson(jsonObject, levelOne, levelTwo);
        if (Util.hasData(integrationId)) {
            return ciServiceService.findByIntegrationId(integrationId);
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

    public ConfigurationItem getConfigurationItemByIntegrationId(JSONObject jsonObject, String levelOne, String levelTwo) {
        String integrationId = Util.getIdByJson(jsonObject, levelOne, levelTwo);
        if (Util.hasData(integrationId)) {
            return configurationItemService.findByIntegrationId(integrationId);
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

    public Location getLocationByIntegrationId(JSONObject jsonObject, String levelOne, String levelTwo) {
        String integrationId = Util.getIdByJson(jsonObject, levelOne, levelTwo);
        if (Util.hasData(integrationId)) {
            return locationService.findByIntegrationId(integrationId);
        } else
            return null;
    }

}


package com.logicalis.apisolver.controller.servicenow;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.logicalis.apisolver.model.*;
import com.logicalis.apisolver.model.enums.App;
import com.logicalis.apisolver.model.enums.EndPointSN;
import com.logicalis.apisolver.model.enums.SnTable;
import com.logicalis.apisolver.model.servicenow.SnScRequest;
import com.logicalis.apisolver.model.servicenow.SnScRequestItem;
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
    private ISnScRequestService snScRequestService;
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

    private Util util = new Util();
    App app = new App();
    EndPointSN endPointSN = new EndPointSN();

    @GetMapping("/sn_requests_by_solver")
    public List<SnScRequest> show() {
        System.out.println(app.Start());
        APIResponse apiResponse = null;
        List<SnScRequest> snScRequests = new ArrayList<>();
        String[] sparmOffSets = util.offSets99000();
        long startTime = 0;
        long endTime = 0;
        String tag = "[ScRequest] ";
        try {
            startTime = System.currentTimeMillis();
            final int[] count = {1};
            for (String sparmOffSet : sparmOffSets) {
                String result = rest.responseByEndPoint(endPointSN.ScRequest().concat(sparmOffSet));
                System.out.println(tag.concat("(".concat(endPointSN.ScRequest().concat(sparmOffSet)).concat(")")));
                ObjectMapper mapper = new ObjectMapper();
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                JSONParser parser = new JSONParser();
                JSONObject resultJson = new JSONObject();
                JSONArray ListSnScRequestJson = new JSONArray();

                resultJson = (JSONObject) parser.parse(result);
                if (resultJson.get("result") != null)
                    ListSnScRequestJson = (JSONArray) parser.parse(resultJson.get("result").toString());

                ListSnScRequestJson.stream().forEach(snScRequestJson -> {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    SnScRequest snScRequest = new SnScRequest();
                    try {
                        snScRequest = objectMapper.readValue(snScRequestJson.toString(), SnScRequest.class);
                        snScRequests.add(snScRequest);
                        ScRequest scRequest = new ScRequest();
                        scRequest.setSysUpdatedOn(snScRequest.getSys_updated_on());
                        scRequest.setNumber(snScRequest.getNumber());
                        scRequest.setState(snScRequest.getState());
                        scRequest.setSysCreatedBy(snScRequest.getSys_created_by());
                        scRequest.setEscalation(snScRequest.getEscalation());
                        scRequest.setExpectedStart(snScRequest.getExpected_start());
                        scRequest.setStage(snScRequest.getStage());
                        scRequest.setActive(snScRequest.isActive());
                        scRequest.setShortDescription(snScRequest.getShort_description());
                        scRequest.setCorrelationDisplay(snScRequest.getCorrelation_display());
                        scRequest.setCorrelationId(snScRequest.getCorrelation_id());
                        scRequest.setSysUpdatedBy(snScRequest.getSys_updated_by());
                        scRequest.setSysCreatedOn(snScRequest.getSys_created_on());
                        scRequest.setOpenedAt(snScRequest.getOpened_at());
                        scRequest.setDescription(snScRequest.getDescription());
                        scRequest.setIntegrationId(snScRequest.getSys_id());
                        scRequest.setDueDate(snScRequest.getDue_date());
                        scRequest.setContactType(snScRequest.getContact_type());
                        scRequest.setApproval(snScRequest.getApproval());

                        Domain domain = getDomainByIntegrationId((JSONObject) snScRequestJson, SnTable.Domain.get(), app.Value());
                        if (domain != null)
                            scRequest.setDomain(domain);

                        Company company = getCompanyByIntegrationId((JSONObject) snScRequestJson, SnTable.Company.get(), app.Value());
                        if (company != null)
                            scRequest.setCompany(company);

                        SysUser requestedFor = getSysUserByIntegrationId((JSONObject) snScRequestJson, "requested_for", app.Value());
                        if (requestedFor != null)
                            scRequest.setRequestedFor(requestedFor);

                        SysUser openedBy = getSysUserByIntegrationId((JSONObject) snScRequestJson, "opened_by", app.Value());
                        if (openedBy != null)
                            scRequest.setOpenedBy(openedBy);

                        SysUser assignedTo = getSysUserByIntegrationId((JSONObject) snScRequestJson, "assigned_to", app.Value());
                        if (assignedTo != null)
                            scRequest.setAssignedTo(assignedTo);

                        SysUser taskFor = getSysUserByIntegrationId((JSONObject) snScRequestJson, "task_for", app.Value());
                        if (taskFor != null)
                            scRequest.setTaskFor(taskFor);

                        Location location = getLocationByIntegrationId((JSONObject) snScRequestJson, "location", app.Value());
                        if (location != null)
                            scRequest.setLocation(location);

                        SysGroup sysGroup = getSysGroupByIntegrationId((JSONObject) snScRequestJson, "assignment_group", app.Value());
                        if (sysGroup != null)
                            scRequest.setAssignmentGroup(sysGroup);


                        ScRequest exists = scRequestService.findByIntegrationId(scRequest.getIntegrationId());
                        String tagAction = app.CreateConsole();
                        if (exists != null) {
                            scRequest.setId(exists.getId());
                            tagAction = app.UpdateConsole();
                        }

                        util.printData(tag, count[0], tagAction.concat(util.getFieldDisplay(scRequest)), util.getFieldDisplay(company), util.getFieldDisplay(domain));
                        scRequestService.save(scRequest);
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
        return snScRequests;
    }

    @GetMapping("/snRequestsBySolverAndQuery")
    public List<SnScRequest> show(String query) {
        System.out.println(app.Start());
        APIResponse apiResponse = null;
        List<SnScRequest> snScRequests = new ArrayList<>();
        String[] sparmOffSets = util.offSets99000();
        long startTime = 0;
        long endTime = 0;
        String tag = "[ScRequest] ";
        try {
            startTime = System.currentTimeMillis();
            final int[] count = {1};
            for (String sparmOffSet : sparmOffSets) {
                String result = rest.responseByEndPoint(endPointSN.ScRequestByQuery().replace("QUERY", query).concat(sparmOffSet));
                System.out.println(tag.concat("(".concat(endPointSN.ScRequestByQuery().replace("QUERY", query).concat(sparmOffSet)).concat(")")));
                ObjectMapper mapper = new ObjectMapper();
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                JSONParser parser = new JSONParser();
                JSONObject resultJson = new JSONObject();
                JSONArray ListSnScRequestJson = new JSONArray();

                resultJson = (JSONObject) parser.parse(result);
                if (resultJson.get("result") != null)
                    ListSnScRequestJson = (JSONArray) parser.parse(resultJson.get("result").toString());

                ListSnScRequestJson.stream().forEach(snScRequestJson -> {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    SnScRequest snScRequest = new SnScRequest();
                    try {
                        Gson gson = new Gson();
                        snScRequest= gson.fromJson(snScRequestJson.toString(), SnScRequest.class);
                        snScRequests.add(snScRequest);
                        ScRequest scRequest = new ScRequest();
                        scRequest.setSysUpdatedOn(snScRequest.getSys_updated_on());
                        scRequest.setNumber(snScRequest.getNumber());
                        scRequest.setState(snScRequest.getState());
                        scRequest.setSysCreatedBy(snScRequest.getSys_created_by());
                        scRequest.setEscalation(snScRequest.getEscalation());
                        scRequest.setExpectedStart(snScRequest.getExpected_start());
                        scRequest.setStage(snScRequest.getStage());
                        scRequest.setActive(snScRequest.isActive());
                        scRequest.setShortDescription(snScRequest.getShort_description());
                        scRequest.setCorrelationDisplay(snScRequest.getCorrelation_display());
                        scRequest.setCorrelationId(snScRequest.getCorrelation_id());
                        scRequest.setSysUpdatedBy(snScRequest.getSys_updated_by());
                        scRequest.setSysCreatedOn(snScRequest.getSys_created_on());
                        scRequest.setOpenedAt(snScRequest.getOpened_at());
                        scRequest.setDescription(snScRequest.getDescription());
                        scRequest.setIntegrationId(snScRequest.getSys_id());
                        scRequest.setDueDate(snScRequest.getDue_date());
                        scRequest.setContactType(snScRequest.getContact_type());
                        scRequest.setApproval(snScRequest.getApproval());

                        Domain domain = getDomainByIntegrationId((JSONObject) snScRequestJson, SnTable.Domain.get(), app.Value());
                        if (domain != null)
                            scRequest.setDomain(domain);

                        Company company = getCompanyByIntegrationId((JSONObject) snScRequestJson, SnTable.Company.get(), app.Value());
                        if (company != null)
                            scRequest.setCompany(company);

                        SysUser requestedFor = getSysUserByIntegrationId((JSONObject) snScRequestJson, "requested_for", app.Value());
                        if (requestedFor != null)
                            scRequest.setRequestedFor(requestedFor);

                        SysUser openedBy = getSysUserByIntegrationId((JSONObject) snScRequestJson, "opened_by", app.Value());
                        if (openedBy != null)
                            scRequest.setOpenedBy(openedBy);

                        SysUser assignedTo = getSysUserByIntegrationId((JSONObject) snScRequestJson, "assigned_to", app.Value());
                        if (assignedTo != null)
                            scRequest.setAssignedTo(assignedTo);

                        SysUser taskFor = getSysUserByIntegrationId((JSONObject) snScRequestJson, "task_for", app.Value());
                        if (taskFor != null)
                            scRequest.setTaskFor(taskFor);

                        Location location = getLocationByIntegrationId((JSONObject) snScRequestJson, "location", app.Value());
                        if (location != null)
                            scRequest.setLocation(location);

                        SysGroup sysGroup = getSysGroupByIntegrationId((JSONObject) snScRequestJson, "assignment_group", app.Value());
                        if (sysGroup != null)
                            scRequest.setAssignmentGroup(sysGroup);


                        ScRequest exists = scRequestService.findByIntegrationId(scRequest.getIntegrationId());
                        String tagAction = app.CreateConsole();
                        if (exists != null) {
                            scRequest.setId(exists.getId());
                            tagAction = app.UpdateConsole();
                        }

                        util.printData(tag, count[0], tagAction.concat(util.getFieldDisplay(scRequest)), util.getFieldDisplay(company), util.getFieldDisplay(domain));
                        scRequestService.save(scRequest);
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
        return snScRequests;
    }

    public Domain getDomainByIntegrationId(JSONObject jsonObject, String levelOne, String levelTwo) {
        String integrationId = util.getIdByJson(jsonObject, levelOne, levelTwo);
        if (util.hasData(integrationId)) {
            return domainService.findByIntegrationId(integrationId);
        } else
            return null;
    }

    public SysUser getSysUserByIntegrationId(JSONObject jsonObject, String levelOne, String levelTwo) {
        String integrationId = util.getIdByJson(jsonObject, levelOne, levelTwo);
        if (util.hasData(integrationId)) {
            return sysUserService.findByIntegrationId(integrationId);
        } else
            return null;
    }

    public CiService getCiServiceByIntegrationId(JSONObject jsonObject, String levelOne, String levelTwo) {
        String integrationId = util.getIdByJson(jsonObject, levelOne, levelTwo);
        if (util.hasData(integrationId)) {
            return ciServiceService.findByIntegrationId(integrationId);
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

    public ConfigurationItem getConfigurationItemByIntegrationId(JSONObject jsonObject, String levelOne, String levelTwo) {
        String integrationId = util.getIdByJson(jsonObject, levelOne, levelTwo);
        if (util.hasData(integrationId)) {
            return configurationItemService.findByIntegrationId(integrationId);
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

    public Location getLocationByIntegrationId(JSONObject jsonObject, String levelOne, String levelTwo) {
        String integrationId = util.getIdByJson(jsonObject, levelOne, levelTwo);
        if (util.hasData(integrationId)) {
            return locationService.findByIntegrationId(integrationId);
        } else
            return null;
    }

}

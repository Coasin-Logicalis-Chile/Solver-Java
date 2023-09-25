package com.logicalis.apisolver.controller.servicenow;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.logicalis.apisolver.model.*;
import com.logicalis.apisolver.model.enums.App;
import com.logicalis.apisolver.model.enums.EndPointSN;
import com.logicalis.apisolver.model.enums.SnTable;
import com.logicalis.apisolver.model.servicenow.SnScRequestItem;
import com.logicalis.apisolver.services.*;
import com.logicalis.apisolver.services.servicenow.ISnScRequestItemService;
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
public class SnScRequestItemController {

    @Autowired
    private ISnScRequestItemService snScRequestItemService;
    @Autowired
    private IScRequestItemService scRequestItemService;
    @Autowired
    private IScCategoryItemService scCategoryItemService;
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

    @GetMapping("/sn_sc_request_item_by_solver")
    public List<SnScRequestItem> show() {
        System.out.println(App.Start());
        APIResponse apiResponse = null;
        List<SnScRequestItem> snScRequestItems = new ArrayList<>();
        String[] sparmOffSets = Util.offSets99000();
        long startTime = 0;
        long endTime = 0;
        String tag = "[ScRequestItem] ";
        try {
            startTime = System.currentTimeMillis();
            final int[] count = {1};
            String result;
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JSONParser parser = new JSONParser();
            JSONObject resultJson = new JSONObject();
            JSONArray ListSnScRequestItemJson = new JSONArray();
            final ScRequestItem[] scRequestItem = {new ScRequestItem()};
            final SnScRequestItem[] snScRequestItem = {new SnScRequestItem()};
            final Domain[] domain = new Domain[1];
            final Company[] company = new Company[1];
            final SysUser[] requestedFor = new SysUser[1];
            final Location[] location = new Location[1];
            final ScCategoryItem[] scCategoryItem = new ScCategoryItem[1];
            final SysUser[] assignedTo = new SysUser[1];
            final SysUser[] openedBy = new SysUser[1];
            final SysUser[] taskFor = new SysUser[1];
            final CiService[] businessService = new CiService[1];
            final ConfigurationItem[] configurationItem = new ConfigurationItem[1];
            final SysGroup[] sysGroup = new SysGroup[1];
            final ScRequest[] scRequest = new ScRequest[1];
            final ScRequestItem[] exists = new ScRequestItem[1];
            final String[] tagAction = new String[1];
            APIExecutionStatus status = new APIExecutionStatus();
            for (String sparmOffSet : sparmOffSets) {
                result = rest.responseByEndPoint(EndPointSN.ScRequestItem().concat(sparmOffSet));
                System.out.println(tag.concat("(".concat(EndPointSN.ScRequestItem().concat(sparmOffSet)).concat(")")));
                resultJson = (JSONObject) parser.parse(result);
                ListSnScRequestItemJson.clear();
                if (resultJson.get("result") != null)
                    ListSnScRequestItemJson = (JSONArray) parser.parse(resultJson.get("result").toString());

                ListSnScRequestItemJson.stream().forEach(snScRequestItemJson -> {
                    try {
                        snScRequestItem[0] = mapper.readValue(snScRequestItemJson.toString(), SnScRequestItem.class);
                        snScRequestItems.add(snScRequestItem[0]);
                        scRequestItem[0] = new ScRequestItem();
                        scRequestItem[0].setNumber(snScRequestItem[0].getNumber());
                        scRequestItem[0].setActive(snScRequestItem[0].getActive());
                        scRequestItem[0].setState(snScRequestItem[0].getState());
                        scRequestItem[0].setStage(snScRequestItem[0].getStage());
                        scRequestItem[0].setTimeWorked(snScRequestItem[0].getTime_worked());
                        scRequestItem[0].setEscalation(snScRequestItem[0].getEscalation());
                        scRequestItem[0].setExpectedStart(snScRequestItem[0].getExpected_start());
                        scRequestItem[0].setEstimatedDelivery(snScRequestItem[0].getEstimated_delivery());
                        scRequestItem[0].setShortDescription(snScRequestItem[0].getShort_description());
                        scRequestItem[0].setOpenedAt(snScRequestItem[0].getOpened_at());
                        scRequestItem[0].setDescription(snScRequestItem[0].getDescription());
                        scRequestItem[0].setIntegrationId(snScRequestItem[0].getSys_id());
                        scRequestItem[0].setContactType(snScRequestItem[0].getContact_type());
                        scRequestItem[0].setUrgency(snScRequestItem[0].getUrgency());
                        scRequestItem[0].setPriority(snScRequestItem[0].getPriority());
                        scRequestItem[0].setApproval(snScRequestItem[0].getApproval());
                        scRequestItem[0].setSysUpdatedOn(snScRequestItem[0].getSys_updated_on());
                        scRequestItem[0].setSysCreatedBy(snScRequestItem[0].getSys_created_by());
                        scRequestItem[0].setSysUpdatedBy(snScRequestItem[0].getSys_updated_by());
                        scRequestItem[0].setSysCreatedOn(snScRequestItem[0].getSys_created_on());

                        domain[0] = getDomainByIntegrationId((JSONObject) snScRequestItemJson, SnTable.Domain.get(), App.Value());
                        if (domain[0] != null)
                            scRequestItem[0].setDomain(domain[0]);

                        company[0] = getCompanyByIntegrationId((JSONObject) snScRequestItemJson, SnTable.Company.get(), App.Value());
                        if (company[0] != null)
                            scRequestItem[0].setCompany(company[0]);

                        requestedFor[0] = getSysUserByIntegrationId((JSONObject) snScRequestItemJson, "requested_for", App.Value());
                        if (requestedFor[0] != null)
                            scRequestItem[0].setRequestedFor(requestedFor[0]);

                        location[0] = getLocationByIntegrationId((JSONObject) snScRequestItemJson, "location", App.Value());
                        if (requestedFor[0] != null)
                            scRequestItem[0].setLocation(location[0]);

                        scCategoryItem[0] = getScCategoryItemByIntegrationId((JSONObject) snScRequestItemJson, "cat_item", App.Value());
                        if (scCategoryItem[0] != null)
                            scRequestItem[0].setScCategoryItem(scCategoryItem[0]);

                        assignedTo[0] = getSysUserByIntegrationId((JSONObject) snScRequestItemJson, "assigned_to", App.Value());
                        if (assignedTo[0] != null)
                            scRequestItem[0].setAssignedTo(assignedTo[0]);

                        openedBy[0] = getSysUserByIntegrationId((JSONObject) snScRequestItemJson, "opened_by", App.Value());
                        if (openedBy[0] != null)
                            scRequestItem[0].setOpenedBy(openedBy[0]);

                        taskFor[0] = getSysUserByIntegrationId((JSONObject) snScRequestItemJson, "task_for", App.Value());
                        if (taskFor[0] != null)
                            scRequestItem[0].setTaskFor(taskFor[0]);

                        businessService[0] = getCiServiceByIntegrationId((JSONObject) snScRequestItemJson, "business_service", App.Value());
                        if (businessService[0] != null)
                            scRequestItem[0].setBusinessService(businessService[0]);

                        configurationItem[0] = getConfigurationItemByIntegrationId((JSONObject) snScRequestItemJson, "cmdb_ci", App.Value());
                        if (configurationItem[0] != null)
                            scRequestItem[0].setConfigurationItem(configurationItem[0]);

                        sysGroup[0] = getSysGroupByIntegrationId((JSONObject) snScRequestItemJson, "assignment_group", App.Value());
                        if (sysGroup[0] != null)
                            scRequestItem[0].setAssignmentGroup(sysGroup[0]);

                        scRequest[0] = getScRequestByIntegrationId((JSONObject) snScRequestItemJson, "request", App.Value());
                        if (scRequest[0] != null)
                            scRequestItem[0].setScRequest(scRequest[0]);

                        exists[0] = scRequestItemService.findByIntegrationId(scRequestItem[0].getIntegrationId());
                        tagAction[0] = App.CreateConsole();
                        if (exists[0] != null) {
                            scRequestItem[0].setId(exists[0].getId());
                            tagAction[0] = App.UpdateConsole();
                        }
                        Util.printData(tag, count[0], tagAction[0].concat(Util.getFieldDisplay(scRequestItem[0])), Util.getFieldDisplay(company[0]), Util.getFieldDisplay(domain[0]));
                        scRequestItemService.save(scRequestItem[0]);
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
        return snScRequestItems;
    }

    @GetMapping("/snSCRequestItemBySolverAndQuery")
    public List<SnScRequestItem> show(String query) {
        System.out.println(App.Start());
        APIResponse apiResponse = null;
        List<SnScRequestItem> snScRequestItems = new ArrayList<>();
        String[] sparmOffSets = Util.offSets99000();
        long startTime = 0;
        long endTime = 0;
        String tag = "[ScRequestItem] ";
        try {
            startTime = System.currentTimeMillis();
            final int[] count = {1};
            String result;
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JSONParser parser = new JSONParser();
            JSONObject resultJson = new JSONObject();
            JSONArray ListSnScRequestItemJson = new JSONArray();
            final SnScRequestItem[] snScRequestItem = {new SnScRequestItem()};
            Gson gson = new Gson();
            final Domain[] domain = new Domain[1];
            final Company[] company = new Company[1];
            final SysUser[] requestedFor = new SysUser[1];
            final Location[] location = new Location[1];
            final ScCategoryItem[] scCategoryItem = new ScCategoryItem[1];
            final SysUser[] assignedTo = new SysUser[1];
            final SysUser[] openedBy = new SysUser[1];
            final SysUser[] taskFor = new SysUser[1];
            final CiService[] businessService = new CiService[1];
            final ConfigurationItem[] configurationItem = new ConfigurationItem[1];
            final SysGroup[] sysGroup = new SysGroup[1];
            final ScRequest[] scRequest = new ScRequest[1];
            final ScRequestItem[] exists = new ScRequestItem[1];
            APIExecutionStatus status = new APIExecutionStatus();
            for (String sparmOffSet : sparmOffSets) {
                result = rest.responseByEndPoint(EndPointSN.ScRequestItemByQuery().replace("QUERY", query).concat(sparmOffSet));
                System.out.println(tag.concat("(".concat(EndPointSN.ScRequestItemByQuery().replace("QUERY", query).concat(sparmOffSet)).concat(")")));
                resultJson = (JSONObject) parser.parse(result);
                ListSnScRequestItemJson.clear();
                if (resultJson.get("result") != null)
                    ListSnScRequestItemJson = (JSONArray) parser.parse(resultJson.get("result").toString());

                ListSnScRequestItemJson.stream().forEach(snScRequestItemJson -> {
                    snScRequestItem[0] = new SnScRequestItem();
                    try {
                        snScRequestItem[0] = gson.fromJson(snScRequestItemJson.toString(), SnScRequestItem.class);
                        snScRequestItems.add(snScRequestItem[0]);
                        ScRequestItem scRequestItem = new ScRequestItem();
                        scRequestItem.setNumber(snScRequestItem[0].getNumber());
                        scRequestItem.setActive(snScRequestItem[0].getActive());
                        scRequestItem.setState(snScRequestItem[0].getState());
                        scRequestItem.setStage(snScRequestItem[0].getStage());
                        scRequestItem.setTimeWorked(snScRequestItem[0].getTime_worked());
                        scRequestItem.setEscalation(snScRequestItem[0].getEscalation());
                        scRequestItem.setExpectedStart(snScRequestItem[0].getExpected_start());
                        scRequestItem.setEstimatedDelivery(snScRequestItem[0].getEstimated_delivery());
                        scRequestItem.setShortDescription(snScRequestItem[0].getShort_description());
                        scRequestItem.setOpenedAt(snScRequestItem[0].getOpened_at());
                        scRequestItem.setDescription(snScRequestItem[0].getDescription());
                        scRequestItem.setIntegrationId(snScRequestItem[0].getSys_id());
                        scRequestItem.setContactType(snScRequestItem[0].getContact_type());
                        scRequestItem.setUrgency(snScRequestItem[0].getUrgency());
                        scRequestItem.setPriority(snScRequestItem[0].getPriority());
                        scRequestItem.setApproval(snScRequestItem[0].getApproval());
                        scRequestItem.setSysUpdatedOn(snScRequestItem[0].getSys_updated_on());
                        scRequestItem.setSysCreatedBy(snScRequestItem[0].getSys_created_by());
                        scRequestItem.setSysUpdatedBy(snScRequestItem[0].getSys_updated_by());
                        scRequestItem.setSysCreatedOn(snScRequestItem[0].getSys_created_on());
                        domain[0] = getDomainByIntegrationId((JSONObject) snScRequestItemJson, SnTable.Domain.get(), App.Value());
                        if (domain[0] != null)
                            scRequestItem.setDomain(domain[0]);
                        company[0] = getCompanyByIntegrationId((JSONObject) snScRequestItemJson, SnTable.Company.get(), App.Value());
                        if (company[0] != null)
                            scRequestItem.setCompany(company[0]);
                        requestedFor[0] = getSysUserByIntegrationId((JSONObject) snScRequestItemJson, "requested_for", App.Value());
                        if (requestedFor[0] != null)
                            scRequestItem.setRequestedFor(requestedFor[0]);
                        location[0] = getLocationByIntegrationId((JSONObject) snScRequestItemJson, "location", App.Value());
                        if (requestedFor[0] != null)
                            scRequestItem.setLocation(location[0]);
                        scCategoryItem[0] = getScCategoryItemByIntegrationId((JSONObject) snScRequestItemJson, "cat_item", App.Value());
                        if (scCategoryItem[0] != null)
                            scRequestItem.setScCategoryItem(scCategoryItem[0]);
                        assignedTo[0] = getSysUserByIntegrationId((JSONObject) snScRequestItemJson, "assigned_to", App.Value());
                        if (assignedTo[0] != null)
                            scRequestItem.setAssignedTo(assignedTo[0]);
                        openedBy[0] = getSysUserByIntegrationId((JSONObject) snScRequestItemJson, "opened_by", App.Value());
                        if (openedBy[0] != null)
                            scRequestItem.setOpenedBy(openedBy[0]);
                        taskFor[0] = getSysUserByIntegrationId((JSONObject) snScRequestItemJson, "task_for", App.Value());
                        if (taskFor[0] != null)
                            scRequestItem.setTaskFor(taskFor[0]);
                        businessService[0] = getCiServiceByIntegrationId((JSONObject) snScRequestItemJson, "business_service", App.Value());
                        if (businessService[0] != null)
                            scRequestItem.setBusinessService(businessService[0]);
                        configurationItem[0] = getConfigurationItemByIntegrationId((JSONObject) snScRequestItemJson, "cmdb_ci", App.Value());
                        if (configurationItem[0] != null)
                            scRequestItem.setConfigurationItem(configurationItem[0]);
                        sysGroup[0] = getSysGroupByIntegrationId((JSONObject) snScRequestItemJson, "assignment_group", App.Value());
                        if (sysGroup[0] != null)
                            scRequestItem.setAssignmentGroup(sysGroup[0]);
                        scRequest[0] = getScRequestByIntegrationId((JSONObject) snScRequestItemJson, "request", App.Value());
                        if (scRequest[0] != null)
                            scRequestItem.setScRequest(scRequest[0]);
                        exists[0] = scRequestItemService.findByIntegrationId(scRequestItem.getIntegrationId());
                        String tagAction = App.CreateConsole();
                        if (exists[0] != null) {
                            scRequestItem.setId(exists[0].getId());
                            tagAction = App.UpdateConsole();
                        }
                        Util.printData(tag, count[0], tagAction.concat(Util.getFieldDisplay(scRequestItem)), Util.getFieldDisplay(company[0]), Util.getFieldDisplay(domain[0]));
                        scRequestItemService.save(scRequestItem);
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
        return snScRequestItems;
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

    public ScCategoryItem getScCategoryItemByIntegrationId(JSONObject jsonObject, String levelOne, String levelTwo) {
        String integrationId = Util.getIdByJson(jsonObject, levelOne, levelTwo);
        if (Util.hasData(integrationId)) {
            return scCategoryItemService.findByIntegrationId(integrationId);
        } else
            return null;
    }

    public ScRequest getScRequestByIntegrationId(JSONObject jsonObject, String levelOne, String levelTwo) {
        String integrationId = Util.getIdByJson(jsonObject, levelOne, levelTwo);
        if (Util.hasData(integrationId)) {
            return scRequestService.findByIntegrationId(integrationId);
        } else
            return null;
    }
}


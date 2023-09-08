
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
import com.logicalis.apisolver.view.TaskSlaSolver;
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
    private Util util = new Util();
    App app = new App();
    EndPointSN endPointSN = new EndPointSN();
    @GetMapping("/sn_sc_request_item_by_solver")
    public List<SnScRequestItem> show() {
        System.out.println(app.Start());
        APIResponse apiResponse = null;
        List<SnScRequestItem> snScRequestItems = new ArrayList<>();
        String[] sparmOffSets = util.offSets99000();
        long startTime = 0;
        long endTime = 0;
        String tag = "[ScRequestItem] ";
        try {
            Rest rest = new Rest();
            startTime = System.currentTimeMillis();
            final int[] count = {1};
            for (String sparmOffSet : sparmOffSets) {
                String result = rest.responseByEndPoint(endPointSN.ScRequestItem().concat(sparmOffSet));
                System.out.println(tag.concat("(".concat(endPointSN.ScRequestItem().concat(sparmOffSet)).concat(")")));
                ObjectMapper mapper = new ObjectMapper();
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                JSONParser parser = new JSONParser();
                JSONObject resultJson = new JSONObject();
                JSONArray ListSnScRequestItemJson = new JSONArray();

                resultJson = (JSONObject) parser.parse(result);
                if (resultJson.get("result") != null)
                    ListSnScRequestItemJson = (JSONArray) parser.parse(resultJson.get("result").toString());

                ListSnScRequestItemJson.stream().forEach(snScRequestItemJson -> {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    SnScRequestItem snScRequestItem = new SnScRequestItem();
                    try {
                        snScRequestItem = objectMapper.readValue(snScRequestItemJson.toString(), SnScRequestItem.class);
                        snScRequestItems.add(snScRequestItem);
                        ScRequestItem scRequestItem = new ScRequestItem();
                        scRequestItem.setNumber(snScRequestItem.getNumber());
                        scRequestItem.setActive(snScRequestItem.getActive());
                        scRequestItem.setState(snScRequestItem.getState());
                        scRequestItem.setStage(snScRequestItem.getStage());
                        scRequestItem.setTimeWorked(snScRequestItem.getTime_worked());
                        scRequestItem.setEscalation(snScRequestItem.getEscalation());
                        scRequestItem.setExpectedStart(snScRequestItem.getExpected_start());
                        scRequestItem.setEstimatedDelivery(snScRequestItem.getEstimated_delivery());
                        scRequestItem.setShortDescription(snScRequestItem.getShort_description());
                        scRequestItem.setOpenedAt(snScRequestItem.getOpened_at());
                        scRequestItem.setDescription(snScRequestItem.getDescription());
                        scRequestItem.setIntegrationId(snScRequestItem.getSys_id());
                        scRequestItem.setContactType(snScRequestItem.getContact_type());
                        scRequestItem.setUrgency(snScRequestItem.getUrgency());
                        scRequestItem.setPriority(snScRequestItem.getPriority());
                        scRequestItem.setApproval(snScRequestItem.getApproval());
                        scRequestItem.setSysUpdatedOn(snScRequestItem.getSys_updated_on());
                        scRequestItem.setSysCreatedBy(snScRequestItem.getSys_created_by());
                        scRequestItem.setSysUpdatedBy(snScRequestItem.getSys_updated_by());
                        scRequestItem.setSysCreatedOn(snScRequestItem.getSys_created_on());

                        Domain domain = getDomainByIntegrationId((JSONObject) snScRequestItemJson, SnTable.Domain.get(), app.Value());
                        if (domain != null)
                            scRequestItem.setDomain(domain);

                        Company company = getCompanyByIntegrationId((JSONObject) snScRequestItemJson, SnTable.Company.get(), app.Value());
                        if (company != null)
                            scRequestItem.setCompany(company);

                        SysUser requestedFor = getSysUserByIntegrationId((JSONObject) snScRequestItemJson, "requested_for", app.Value());
                        if (requestedFor != null)
                            scRequestItem.setRequestedFor(requestedFor);

                        Location location = getLocationByIntegrationId((JSONObject) snScRequestItemJson, "location", app.Value());
                        if (requestedFor != null)
                            scRequestItem.setLocation(location);

                        ScCategoryItem scCategoryItem = getScCategoryItemByIntegrationId((JSONObject) snScRequestItemJson, "cat_item", app.Value());
                        if (scCategoryItem != null)
                            scRequestItem.setScCategoryItem(scCategoryItem);


                        SysUser assignedTo = getSysUserByIntegrationId((JSONObject) snScRequestItemJson, "assigned_to", app.Value());
                        if (assignedTo != null)
                            scRequestItem.setAssignedTo(assignedTo);

                        SysUser openedBy = getSysUserByIntegrationId((JSONObject) snScRequestItemJson, "opened_by", app.Value());
                        if (openedBy != null)
                            scRequestItem.setOpenedBy(openedBy);

                        SysUser taskFor = getSysUserByIntegrationId((JSONObject) snScRequestItemJson, "task_for", app.Value());
                        if (taskFor != null)
                            scRequestItem.setTaskFor(taskFor);

                        CiService businessService = getCiServiceByIntegrationId((JSONObject) snScRequestItemJson, "business_service", app.Value());
                        if (businessService != null)
                            scRequestItem.setBusinessService(businessService);

                        ConfigurationItem configurationItem = getConfigurationItemByIntegrationId((JSONObject) snScRequestItemJson, "cmdb_ci", app.Value());
                        if (configurationItem != null)
                            scRequestItem.setConfigurationItem(configurationItem);

                        SysGroup sysGroup = getSysGroupByIntegrationId((JSONObject) snScRequestItemJson, "assignment_group", app.Value());
                        if (sysGroup != null)
                            scRequestItem.setAssignmentGroup(sysGroup);

                        ScRequest scRequest = getScRequestByIntegrationId((JSONObject) snScRequestItemJson, "request", app.Value());
                        if (scRequest != null)
                            scRequestItem.setScRequest(scRequest);

                        ScRequestItem exists = scRequestItemService.findByIntegrationId(scRequestItem.getIntegrationId());
                        String tagAction = app.CreateConsole();
                        if (exists != null) {
                            scRequestItem.setId(exists.getId());
                            tagAction = app.UpdateConsole();
                        }

                        util.printData(tag, count[0], tagAction.concat(util.getFieldDisplay(scRequestItem)), util.getFieldDisplay(company), util.getFieldDisplay(domain));

                        scRequestItemService.save(scRequestItem);


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
        return snScRequestItems;
    }

    @GetMapping("/snSCRequestItemBySolverAndQuery")
    public List<SnScRequestItem> show(String query) {
        System.out.println(app.Start());
        APIResponse apiResponse = null;
        List<SnScRequestItem> snScRequestItems = new ArrayList<>();
        String[] sparmOffSets = util.offSets99000();
        long startTime = 0;
        long endTime = 0;
        String tag = "[ScRequestItem] ";
        try {
            Rest rest = new Rest();
            startTime = System.currentTimeMillis();
            final int[] count = {1};
            for (String sparmOffSet : sparmOffSets) {
                String result = rest.responseByEndPoint(endPointSN.ScRequestItemByQuery().replace("QUERY", query).concat(sparmOffSet));
                System.out.println(tag.concat("(".concat(endPointSN.ScRequestItemByQuery().replace("QUERY", query).concat(sparmOffSet)).concat(")")));
                ObjectMapper mapper = new ObjectMapper();
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                JSONParser parser = new JSONParser();
                JSONObject resultJson = new JSONObject();
                JSONArray ListSnScRequestItemJson = new JSONArray();

                resultJson = (JSONObject) parser.parse(result);
                if (resultJson.get("result") != null)
                    ListSnScRequestItemJson = (JSONArray) parser.parse(resultJson.get("result").toString());

                ListSnScRequestItemJson.stream().forEach(snScRequestItemJson -> {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    SnScRequestItem snScRequestItem = new SnScRequestItem();
                    try {
                        Gson gson = new Gson();
                        snScRequestItem = gson.fromJson(snScRequestItemJson.toString(), SnScRequestItem.class);

                        // snScRequestItem = objectMapper.readValue(snScRequestItemJson.toString(), SnScRequestItem.class);
                        snScRequestItems.add(snScRequestItem);
                        ScRequestItem scRequestItem = new ScRequestItem();
                        scRequestItem.setNumber(snScRequestItem.getNumber());
                        scRequestItem.setActive(snScRequestItem.getActive());
                        scRequestItem.setState(snScRequestItem.getState());
                        scRequestItem.setStage(snScRequestItem.getStage());
                        scRequestItem.setTimeWorked(snScRequestItem.getTime_worked());
                        scRequestItem.setEscalation(snScRequestItem.getEscalation());
                        scRequestItem.setExpectedStart(snScRequestItem.getExpected_start());
                        scRequestItem.setEstimatedDelivery(snScRequestItem.getEstimated_delivery());
                        scRequestItem.setShortDescription(snScRequestItem.getShort_description());
                        scRequestItem.setOpenedAt(snScRequestItem.getOpened_at());
                        scRequestItem.setDescription(snScRequestItem.getDescription());
                        scRequestItem.setIntegrationId(snScRequestItem.getSys_id());
                        scRequestItem.setContactType(snScRequestItem.getContact_type());
                        scRequestItem.setUrgency(snScRequestItem.getUrgency());
                        scRequestItem.setPriority(snScRequestItem.getPriority());
                        scRequestItem.setApproval(snScRequestItem.getApproval());
                        scRequestItem.setSysUpdatedOn(snScRequestItem.getSys_updated_on());
                        scRequestItem.setSysCreatedBy(snScRequestItem.getSys_created_by());
                        scRequestItem.setSysUpdatedBy(snScRequestItem.getSys_updated_by());
                        scRequestItem.setSysCreatedOn(snScRequestItem.getSys_created_on());

                        Domain domain = getDomainByIntegrationId((JSONObject) snScRequestItemJson, SnTable.Domain.get(), app.Value());
                        if (domain != null)
                            scRequestItem.setDomain(domain);

                        Company company = getCompanyByIntegrationId((JSONObject) snScRequestItemJson, SnTable.Company.get(), app.Value());
                        if (company != null)
                            scRequestItem.setCompany(company);

                        SysUser requestedFor = getSysUserByIntegrationId((JSONObject) snScRequestItemJson, "requested_for", app.Value());
                        if (requestedFor != null)
                            scRequestItem.setRequestedFor(requestedFor);

                        Location location = getLocationByIntegrationId((JSONObject) snScRequestItemJson, "location", app.Value());
                        if (requestedFor != null)
                            scRequestItem.setLocation(location);

                        ScCategoryItem scCategoryItem = getScCategoryItemByIntegrationId((JSONObject) snScRequestItemJson, "cat_item", app.Value());
                        if (scCategoryItem != null)
                            scRequestItem.setScCategoryItem(scCategoryItem);


                        SysUser assignedTo = getSysUserByIntegrationId((JSONObject) snScRequestItemJson, "assigned_to", app.Value());
                        if (assignedTo != null)
                            scRequestItem.setAssignedTo(assignedTo);

                        SysUser openedBy = getSysUserByIntegrationId((JSONObject) snScRequestItemJson, "opened_by", app.Value());
                        if (openedBy != null)
                            scRequestItem.setOpenedBy(openedBy);

                        SysUser taskFor = getSysUserByIntegrationId((JSONObject) snScRequestItemJson, "task_for", app.Value());
                        if (taskFor != null)
                            scRequestItem.setTaskFor(taskFor);

                        CiService businessService = getCiServiceByIntegrationId((JSONObject) snScRequestItemJson, "business_service", app.Value());
                        if (businessService != null)
                            scRequestItem.setBusinessService(businessService);

                        ConfigurationItem configurationItem = getConfigurationItemByIntegrationId((JSONObject) snScRequestItemJson, "cmdb_ci", app.Value());
                        if (configurationItem != null)
                            scRequestItem.setConfigurationItem(configurationItem);

                        SysGroup sysGroup = getSysGroupByIntegrationId((JSONObject) snScRequestItemJson, "assignment_group", app.Value());
                        if (sysGroup != null)
                            scRequestItem.setAssignmentGroup(sysGroup);

                        ScRequest scRequest = getScRequestByIntegrationId((JSONObject) snScRequestItemJson, "request", app.Value());
                        if (scRequest != null)
                            scRequestItem.setScRequest(scRequest);

                        ScRequestItem exists = scRequestItemService.findByIntegrationId(scRequestItem.getIntegrationId());
                        String tagAction = app.CreateConsole();
                        if (exists != null) {
                            scRequestItem.setId(exists.getId());
                            tagAction = app.UpdateConsole();
                        }

                        util.printData(tag, count[0], tagAction.concat(util.getFieldDisplay(scRequestItem)), util.getFieldDisplay(company), util.getFieldDisplay(domain));

                        scRequestItemService.save(scRequestItem);


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
        return snScRequestItems;
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

    public ScCategoryItem getScCategoryItemByIntegrationId(JSONObject jsonObject, String levelOne, String levelTwo) {
        String integrationId = util.getIdByJson(jsonObject, levelOne, levelTwo);
        if (util.hasData(integrationId)) {
            return scCategoryItemService.findByIntegrationId(integrationId);
        } else
            return null;
    }

    public ScRequest getScRequestByIntegrationId(JSONObject jsonObject, String levelOne, String levelTwo) {
        String integrationId = util.getIdByJson(jsonObject, levelOne, levelTwo);
        if (util.hasData(integrationId)) {
            return scRequestService.findByIntegrationId(integrationId);
        } else
            return null;
    }


}


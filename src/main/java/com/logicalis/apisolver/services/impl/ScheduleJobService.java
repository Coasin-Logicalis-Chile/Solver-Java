package com.logicalis.apisolver.services.impl;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.logicalis.apisolver.model.*;
import com.logicalis.apisolver.model.enums.App;
import com.logicalis.apisolver.model.enums.EndPointSN;
import com.logicalis.apisolver.model.enums.SnTable;
import com.logicalis.apisolver.services.*;
import com.logicalis.apisolver.util.Rest;
import com.logicalis.apisolver.util.Util;
import com.logicalis.apisolver.view.ScRequestItemSolver;
import com.logicalis.apisolver.view.ScRequestSolver;
import com.logicalis.apisolver.view.ScTaskRequest;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ScheduleJobService {

    @Value("${jobs.boot.enable.request}")
    private boolean jobEnableRequest;

    @Autowired
    private IIncidentService incidentService;
    @Autowired
    private IScRequestService scRequestService;
    @Autowired
    private IScRequestItemService scRequestItemService;
    @Autowired
    private IScTaskService scTaskService;
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
    private IScCategoryItemService scCategoryItemService;

    Util util = new Util();
    App app = new App();
    EndPointSN endPointSN = new EndPointSN();
    /*
    @Scheduled(cron = "${jobs.boot.schedule.request}")
    public void scRequestJob() {
        if (jobEnableRequest) {
            System.out.println(app.Start());
            List<ScRequest> scRequests = new ArrayList<>();
            List<ScRequestItem> scRequestItems = new ArrayList<>();
            List<ScTask> scTasks = new ArrayList<>();
            String[] sparmOffSets = util.offSets3000();
            long startTime = 0;
            long endTime = 0;
            String tag = "[ScRequest] ";
            try {
                Rest rest = new Rest();
                startTime = System.currentTimeMillis();
                final int[] count = {1};
                for (String sparmOffSet : sparmOffSets) {

                    String result = rest.responseByEndPoint(endPointSN.GeneralQuery().replace("TABLE", SnTable.ScRequest.get()).concat(sparmOffSet));
                    System.out.println(tag.concat("(".concat(endPointSN.GeneralQuery().replace("TABLE", SnTable.ScRequest.get()).concat(sparmOffSet)).concat(")")));


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
                        ScRequestSolver scRequestSolver;
                        try {
                            scRequestSolver = objectMapper.readValue(snScRequestJson.toString(), ScRequestSolver.class);
                            ScRequest scRequest = new ScRequest();
                            scRequest.setSysUpdatedOn(scRequestSolver.getSys_updated_on());
                            scRequest.setNumber(scRequestSolver.getNumber());
                            scRequest.setState(scRequestSolver.getState());
                            scRequest.setSysCreatedBy(scRequestSolver.getSys_created_by());
                            scRequest.setEscalation(scRequestSolver.getEscalation());
                            scRequest.setExpectedStart(scRequestSolver.getExpected_start());
                            scRequest.setStage(scRequestSolver.getStage());
                            scRequest.setActive(scRequestSolver.getActive());
                            scRequest.setShortDescription(scRequestSolver.getShort_description());
                            scRequest.setCorrelationDisplay(scRequestSolver.getCorrelation_display());
                            scRequest.setCorrelationId(scRequestSolver.getCorrelation_id());
                            scRequest.setSysUpdatedBy(scRequestSolver.getSys_updated_by());
                            scRequest.setSysCreatedOn(scRequestSolver.getSys_created_on());
                            scRequest.setOpenedAt(scRequestSolver.getOpened_at());
                            scRequest.setDescription(scRequestSolver.getDescription());
                            scRequest.setIntegrationId(scRequestSolver.getSys_id());
                            scRequest.setDueDate(scRequestSolver.getDue_date());
                            scRequest.setContactType(scRequestSolver.getContact_type());
                            scRequest.setApproval(scRequestSolver.getApproval());

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
                            scRequests.add(scRequestService.save(scRequest));
                            count[0] = count[0] + 1;
                        } catch (Exception e) {
                            System.out.println(tag.concat("Exception (I) : ").concat(String.valueOf(e)));
                        }
                    });


                }
                for (String sparmOffSet : sparmOffSets) {

                    String result = rest.responseByEndPoint(endPointSN.GeneralQuery().replace("TABLE", SnTable.ScRequestItem.get()).concat(sparmOffSet));
                    System.out.println(tag.concat("(".concat(endPointSN.GeneralQuery().replace("TABLE", SnTable.ScRequestItem.get()).concat(sparmOffSet)).concat(")")));

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
                        ScRequestItemSolver scRequestItemSolver = new ScRequestItemSolver();
                        try {
                            scRequestItemSolver = objectMapper.readValue(snScRequestItemJson.toString(), ScRequestItemSolver.class);

                            ScRequestItem scRequestItem = new ScRequestItem();
                            scRequestItem.setNumber(scRequestItemSolver.getNumber());
                            scRequestItem.setActive(scRequestItemSolver.getActive());
                            scRequestItem.setState(scRequestItemSolver.getState());
                            scRequestItem.setStage(scRequestItemSolver.getStage());
                            scRequestItem.setTimeWorked(scRequestItemSolver.getTime_worked());
                            scRequestItem.setEscalation(scRequestItemSolver.getEscalation());
                            scRequestItem.setExpectedStart(scRequestItemSolver.getExpected_start());
                            scRequestItem.setEstimatedDelivery(scRequestItemSolver.getEstimated_delivery());
                            scRequestItem.setShortDescription(scRequestItemSolver.getShort_description());
                            scRequestItem.setOpenedAt(scRequestItemSolver.getOpened_at());
                            scRequestItem.setDescription(scRequestItemSolver.getDescription());
                            scRequestItem.setIntegrationId(scRequestItemSolver.getSys_id());
                            scRequestItem.setContactType(scRequestItemSolver.getContact_type());
                            scRequestItem.setUrgency(scRequestItemSolver.getUrgency());
                            scRequestItem.setPriority(scRequestItemSolver.getPriority());
                            scRequestItem.setApproval(scRequestItemSolver.getApproval());
                            scRequestItem.setSysUpdatedOn(scRequestItemSolver.getSys_updated_on());
                            scRequestItem.setSysCreatedBy(scRequestItemSolver.getSys_created_by());
                            scRequestItem.setSysUpdatedBy(scRequestItemSolver.getSys_updated_by());
                            scRequestItem.setSysCreatedOn(scRequestItemSolver.getSys_created_on());


                            Domain domain = getDomainByIntegrationId((JSONObject) snScRequestItemJson, SnTable.Domain.get(), app.Value());
                            if (domain != null)
                                scRequestItem.setDomain(domain);

                            Company company = getCompanyByIntegrationId((JSONObject) snScRequestItemJson, SnTable.Company.get(), app.Value());
                            if (company != null)
                                scRequestItem.setCompany(company);

                            Location location = getLocationByIntegrationId((JSONObject) snScRequestItemJson, "location", app.Value());
                            if (location != null)
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
                            if (scRequest != null) {
                                scRequestItem.setScRequest(scRequest);
                                if (scRequest.getRequestedFor() != null)
                                    scRequestItem.setRequestedFor(scRequest.getRequestedFor());
                            }

                            String incidentParent = util.getIdByJson((JSONObject) snScRequestItemJson, "parent", app.Value());
                            if (util.hasData(incidentParent)) {
                                Incident parent = incidentService.findByIntegrationId(incidentParent);
                                if (util.hasData(parent))
                                    scRequestItem.setIncidentParent(incidentParent);

                            }

                            ScRequestItem exists = scRequestItemService.findByIntegrationId(scRequestItem.getIntegrationId());
                            String tagAction = app.CreateConsole();
                            if (exists != null) {
                                scRequestItem.setId(exists.getId());
                                tagAction = app.UpdateConsole();
                            }

                            util.printData(tag, count[0], tagAction.concat(util.getFieldDisplay(scRequestItem)), util.getFieldDisplay(company), util.getFieldDisplay(domain));

                            scRequestItems.add(scRequestItemService.save(scRequestItem));


                            count[0] = count[0] + 1;
                        } catch (Exception e) {
                            System.out.println(tag.concat("Exception (I) : ").concat(String.valueOf(e)));
                        }
                    });
                }
                for (String sparmOffSet : sparmOffSets) {
                    String result = rest.responseByEndPoint(endPointSN.GeneralQuery().replace("TABLE", SnTable.ScTask.get()).concat(sparmOffSet));
                    System.out.println(tag.concat("(".concat(endPointSN.GeneralQuery().replace("TABLE", SnTable.ScTask.get()).concat(sparmOffSet)).concat(")")));
                    ObjectMapper mapper = new ObjectMapper();
                    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    JSONParser parser = new JSONParser();
                    JSONObject resultJson = new JSONObject();
                    JSONArray ListScTaskJson = new JSONArray();

                    resultJson = (JSONObject) parser.parse(result);
                    if (resultJson.get("result") != null)
                        ListScTaskJson = (JSONArray) parser.parse(resultJson.get("result").toString());

                    ListScTaskJson.stream().forEach(scTaskJson -> {
                        ScTaskRequest scTaskSolver;
                        try {
                            Gson gson = new Gson();
                            scTaskSolver = gson.fromJson(scTaskJson.toString(), ScTaskRequest.class);
                            ScTask scTask = new ScTask();
                            scTask.setActionStatus(util.isNull(scTaskSolver.getAction_status()));
                            scTask.setActive(scTaskSolver.getActive());
                            scTask.setActivityDue(util.isNull(scTaskSolver.getActivity_due()));
                            scTask.setAdditionalAssigneeList(util.isNull(scTaskSolver.getAdditional_assignee_list()));
                            scTask.setApproval(util.isNull(scTaskSolver.getApproval()));
                            scTask.setApprovalHistory(util.isNull(scTaskSolver.getApproval_history()));
                            scTask.setApprovalSet(util.isNull(scTaskSolver.getApproval_set()));
                            scTask.setBusinessDuration(util.isNull(scTaskSolver.getBusiness_duration()));
                            scTask.setCalendarDuration(util.isNull(scTaskSolver.getCalendar_duration()));
                            scTask.setCalendarStc(util.isNull(scTaskSolver.getCalendar_stc()));
                            scTask.setCloseNotes(util.isNull(scTaskSolver.getClose_notes()));
                            scTask.setClosedAt(util.isNull(scTaskSolver.getClosed_at()));
                            scTask.setComments(util.isNull(scTaskSolver.getComments()));
                            scTask.setCommentsAndWorkNotes(util.isNull(scTaskSolver.getComments_and_work_notes()));
                            scTask.setContactType(util.isNull(scTaskSolver.getContact_type()));
                            scTask.setCorrelationDisplay(util.isNull(scTaskSolver.getCorrelation_display()));
                            scTask.setCorrelationId(util.isNull(scTaskSolver.getCorrelation_id()));
                            scTask.setDeliveryPlan(util.isNull(scTaskSolver.getDelivery_plan()));
                            scTask.setDeliveryTask(util.isNull(scTaskSolver.getDelivery_task()));
                            scTask.setDescription(util.isNull(scTaskSolver.getDescription()));
                            scTask.setDueDate(util.isNull(scTaskSolver.getDue_date()));
                            scTask.setEscalation(util.isNull(scTaskSolver.getEscalation()));
                            scTask.setExpectedStart(util.isNull(scTaskSolver.getExpected_start()));
                            scTask.setFollowUp(util.isNull(scTaskSolver.getFollow_up()));
                            scTask.setGroupList(util.isNull(scTaskSolver.getGroup_list()));
                            scTask.setImpact(util.isNull(scTaskSolver.getImpact()));
                            scTask.setKnowledge(util.isNull(scTaskSolver.getKnowledge()));
                            scTask.setMadeSla(util.isNull(scTaskSolver.getMade_sla()));
                            scTask.setNeedsAttention(util.isNull(scTaskSolver.getNeeds_attention()));
                            scTask.setNumber(util.isNull(scTaskSolver.getNumber()));
                            scTask.setOpenedAt(util.isNull(scTaskSolver.getOpened_at()));
                            scTask.setPriority(util.isNull(scTaskSolver.getPriority()));
                            scTask.setReassignmentCount(util.isNull(scTaskSolver.getReassignment_count()));
                            scTask.setRouteReason(util.isNull(scTaskSolver.getRoute_reason()));
                            scTask.setServiceOffering(util.isNull(scTaskSolver.getService_offering()));
                            scTask.setShortDescription(util.isNull(scTaskSolver.getShort_description()));
                            scTask.setSkills(util.isNull(scTaskSolver.getSkills()));
                            scTask.setSlaDue(util.isNull(scTaskSolver.getSla_due()));
                            scTask.setState(util.isNull(scTaskSolver.getState()));
                            scTask.setSysClassName(util.isNull(scTaskSolver.getSys_class_name()));
                            scTask.setSysCreatedBy(util.isNull(scTaskSolver.getSys_created_by()));
                            scTask.setSysCreatedOn(util.isNull(scTaskSolver.getSys_created_on()));
                            if (!util.isNull(scTaskSolver.getSys_created_on()).equals(""))
                                scTask.setCreatedOn(util.getLocalDateTime(util.isNull(scTaskSolver.getSys_created_on())));
                            scTask.setIntegrationId(util.isNull(scTaskSolver.getSys_id()));
                            scTask.setSysModCount(util.isNull(scTaskSolver.getSys_mod_count()));
                            scTask.setSysUpdatedBy(util.isNull(scTaskSolver.getSys_updated_by()));
                            scTask.setSysUpdatedOn(util.isNull(scTaskSolver.getSys_updated_on()));
                            if (!util.isNull(scTaskSolver.getClosed_at()).equals(""))
                                scTask.setUpdatedOn(util.getLocalDateTime(util.isNull(scTaskSolver.getClosed_at())));
                            scTask.setTaskEffectiveNumber(util.isNull(scTaskSolver.getTask_effective_number()));
                            scTask.setTimeWorked(util.isNull(scTaskSolver.getTime_worked()));
                            scTask.setUniversalRequest(util.isNull(scTaskSolver.getUniversal_request()));
                            scTask.setUponApproval(util.isNull(scTaskSolver.getUpon_approval()));
                            scTask.setUponReject(util.isNull(scTaskSolver.getUpon_reject()));
                            scTask.setUrgency(util.isNull(scTaskSolver.getUrgency()));
                            scTask.setUserInput(util.isNull(scTaskSolver.getUser_input()));
                            scTask.setWorkEnd(util.isNull(scTaskSolver.getWork_end()));
                            scTask.setWorkStart(util.isNull(scTaskSolver.getWork_start()));
                            scTask.setScaling(scTaskSolver.getU_pending_other_group());

                            Domain domain = getDomainByIntegrationId((JSONObject) scTaskJson, SnTable.Domain.get(), app.Value());
                            if (domain != null)
                                scTask.setDomain(domain);

                            Company company = getCompanyByIntegrationId((JSONObject) scTaskJson, SnTable.Company.get(), app.Value());
                            if (company != null)
                                scTask.setCompany(company);

                            SysUser closedBy = getSysUserByIntegrationId((JSONObject) scTaskJson, "closed_by", app.Value());
                            if (closedBy != null)
                                scTask.setClosedBy(closedBy);

                            SysUser scalingAssignedTo = getSysUserByIntegrationId((JSONObject) scTaskJson, "u_scaling_assigned_to", app.Value());
                            if (scalingAssignedTo != null)
                                scTask.setScalingAssignedTo(scalingAssignedTo);

                            Location location = getLocationByIntegrationId((JSONObject) scTaskJson, "location", app.Value());
                            if (location != null)
                                scTask.setLocation(location);

                            SysUser assignedTo = getSysUserByIntegrationId((JSONObject) scTaskJson, "u_solver_assigned_to", app.Value());
                            if (assignedTo != null)
                                scTask.setAssignedTo(assignedTo);


                            SysUser openedBy = getSysUserByIntegrationId((JSONObject) scTaskJson, "opened_by", app.Value());
                            if (openedBy != null)
                                scTask.setOpenedBy(openedBy);


                            CiService businessService = getCiServiceByIntegrationId((JSONObject) scTaskJson, "business_service", app.Value());
                            if (businessService != null)
                                scTask.setBusinessService(businessService);

                            ConfigurationItem configurationItem = getConfigurationItemByIntegrationId((JSONObject) scTaskJson, "cmdb_ci", app.Value());
                            if (configurationItem != null)
                                scTask.setConfigurationItem(configurationItem);

                            SysGroup sysGroup = getSysGroupByIntegrationId((JSONObject) scTaskJson, "assignment_group", app.Value());
                            if (sysGroup != null)
                                scTask.setAssignmentGroup(sysGroup);

                            SysGroup scalingAssignmentGroup = getSysGroupByIntegrationId((JSONObject) scTaskJson, "u_scaling_group", app.Value());
                            if (scalingAssignmentGroup != null)
                                scTask.setScalingAssignmentGroup(scalingAssignmentGroup);

                            ScRequest scRequest = getScRequestByIntegrationId((JSONObject) scTaskJson, "request", app.Value());
                            if (scRequest != null)
                                scTask.setScRequest(scRequest);

                            ScRequestItem scRequestItem = getScRequestItemByIntegrationId((JSONObject) scTaskJson, "request_item", app.Value());
                            if (scRequestItem != null) {
                                scTask.setScRequestItem(scRequestItem);
                                if (scRequestItem.getRequestedFor() != null)
                                    scTask.setRequestedFor(scRequestItem.getRequestedFor());
                                if (scRequestItem.getTaskFor() != null)
                                    scTask.setTaskFor(scRequestItem.getTaskFor());
                            }

                            ScTask exists = scTaskService.findByIntegrationId(scTask.getIntegrationId());
                            String tagAction = app.CreateConsole();
                            if (exists != null) {
                                scTask.setId(exists.getId());
                                tagAction = app.UpdateConsole();
                            }

                            util.printData(tag, count[0], tagAction.concat(util.getFieldDisplay(scTask)), util.getFieldDisplay(company), util.getFieldDisplay(domain));

                            scTasks.add(scTaskService.save(scTask));


                            count[0] = count[0] + 1;
                        } catch (Exception e) {
                            System.out.println(tag.concat("Exception (I) : ").concat(String.valueOf(e)));
                        }
                    });
                }

                APIExecutionStatus status = new APIExecutionStatus();
                status.setUri(endPointSN.ScRequestScheduleByQuery());
                status.setUserAPI(app.SNUser());
                status.setPasswordAPI(app.SNPassword());
                endTime = (System.currentTimeMillis() - startTime);
                status.setExecutionTime(endTime);
                statusService.save(status);
            } catch (Exception e) {
                System.out.println(tag.concat("Exception (II) : ").concat(String.valueOf(e)));
            }
            System.out.println(app.End());
        }
    }

*/
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

    public ScCategoryItem getScCategoryItemByIntegrationId(JSONObject jsonObject, String levelOne, String levelTwo) {
        String integrationId = util.getIdByJson(jsonObject, levelOne, levelTwo);
        if (util.hasData(integrationId)) {
            return scCategoryItemService.findByIntegrationId(integrationId);
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

    public ScRequest getScRequestByIntegrationId(JSONObject jsonObject, String levelOne, String levelTwo) {
        String integrationId = util.getIdByJson(jsonObject, levelOne, levelTwo);
        if (util.hasData(integrationId)) {
            return scRequestService.findByIntegrationId(integrationId);
        } else
            return null;
    }

    public ScRequestItem getScRequestItemByIntegrationId(JSONObject jsonObject, String levelOne, String levelTwo) {
        String integrationId = util.getIdByJson(jsonObject, levelOne, levelTwo);
        if (util.hasData(integrationId)) {
            return scRequestItemService.findByIntegrationId(integrationId);
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



package com.logicalis.apisolver.controller.servicenow;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.logicalis.apisolver.model.*;
import com.logicalis.apisolver.model.enums.App;
import com.logicalis.apisolver.model.enums.EndPointSN;
import com.logicalis.apisolver.model.enums.SnTable;
import com.logicalis.apisolver.model.servicenow.SnConfigurationItem;
import com.logicalis.apisolver.services.*;
import com.logicalis.apisolver.services.servicenow.ISnConfigurationItemService;
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
public class SnConfigurationItemController {

    @Autowired
    private ISnConfigurationItemService snConfigurationItemService;
    @Autowired
    private IConfigurationItemService configurationItemService;
    @Autowired
    private IDomainService domainService;
    @Autowired
    private ILocationService locationService;
    @Autowired
    private IAPIExecutionStatusService statusService;
    @Autowired
    private ICompanyService companyService;
    @Autowired
    private ISysUserService sysUserService;
    @Autowired
    private ISysGroupService sysGroupService;
    @Autowired
    private ICiServiceService ciServiceService;
    @Autowired
    private Rest rest;

    private Util util = new Util();
    App app = new App();
    EndPointSN endPointSN = new EndPointSN();

    @GetMapping("/sn_configuration_items")
    public List<SnConfigurationItem> show() {
        System.out.println(app.Start());
        APIResponse apiResponse = null;
        List<SnConfigurationItem> snConfigurationItems = new ArrayList<>();
        String[] sparmOffSets = util.offSets99000();
        Util util = new Util();
        long startTime = 0;
        long endTime = 0;
        String tag = "[ConfigurationItem] ";
        try {
            List<Domain> domains = domainService.findAll();
            System.out.println(tag.concat("(Get All Domains)"));
            List<Company> companies = companyService.findAll();
            System.out.println(tag.concat("(Get All Companies)"));
            List<Location> locations = locationService.findAll();
            System.out.println(tag.concat("(Get All Locations)"));
            List<SysUser> sysUsers = sysUserService.findAll();
            System.out.println(tag.concat("(Get All Sys Users)"));
            List<SysGroup> sysGroups = sysGroupService.findAll();
            System.out.println(tag.concat("(Get All Sys Groups)"));
            startTime = System.currentTimeMillis();
            final int[] count = {1};
            for (String sparmOffSet : sparmOffSets) {
                String result = rest.responseByEndPoint(endPointSN.ConfigurationItem().concat(sparmOffSet));
                System.out.println(tag.concat("(".concat(endPointSN.ConfigurationItem().concat(sparmOffSet)).concat(")")));
                ObjectMapper mapper = new ObjectMapper();
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                JSONParser parser = new JSONParser();
                JSONObject resultJson = new JSONObject();
                JSONArray ListSnConfigurationItemJson = new JSONArray();

                resultJson = (JSONObject) parser.parse(result);
                if (resultJson.get("result") != null)
                    ListSnConfigurationItemJson = (JSONArray) parser.parse(resultJson.get("result").toString());

                ListSnConfigurationItemJson.stream().forEach(snConfigurationItemJson -> {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    SnConfigurationItem snConfigurationItem = new SnConfigurationItem();
                    try {
                        snConfigurationItem = objectMapper.readValue(snConfigurationItemJson.toString(), SnConfigurationItem.class);
                        snConfigurationItems.add(snConfigurationItem);
                        ConfigurationItem configurationItem = new ConfigurationItem();
                        configurationItem.setName(snConfigurationItem.getName());
                        configurationItem.setIntegrationId(snConfigurationItem.getSys_id());
                        configurationItem.setDescription(snConfigurationItem.getDescription());
                        configurationItem.setStatus(snConfigurationItem.getStatus());
                        configurationItem.setUpdated(snConfigurationItem.getUpdated());
                        configurationItem.setCreatedBy(snConfigurationItem.getSys_created_by());
                        configurationItem.setAssetTag(snConfigurationItem.getAsset_tag());
                        configurationItem.setAssigned(snConfigurationItem.getAssigned());
                        configurationItem.setCategory(snConfigurationItem.getCategory());
                        configurationItem.setComments(snConfigurationItem.getComments());
                        configurationItem.setCorrelationId(snConfigurationItem.getCorrelation_id());
                        configurationItem.setCostCurrency(snConfigurationItem.getCost_cc());
                        configurationItem.setCreated(snConfigurationItem.getSys_created_on());
                        configurationItem.setHostname(snConfigurationItem.getHostname());
                        configurationItem.setImpact(snConfigurationItem.getImpact());
                        configurationItem.setInstalled(snConfigurationItem.getInstalled());
                        configurationItem.setModelNumber(snConfigurationItem.getModel_number());
                        configurationItem.setMonitor(snConfigurationItem.getMonitor());
                        configurationItem.setOperationalStatus(snConfigurationItem.getOperational_status());
                        configurationItem.setSerialNumber(snConfigurationItem.getSerial_number());
                        configurationItem.setSpecialInstruction(snConfigurationItem.getSpecial_instruction());
                        configurationItem.setSubcategory(snConfigurationItem.getSubcategory());
                        configurationItem.setUpdatedBy(snConfigurationItem.getSys_updated_by());

                        Company company = util.filterCompany(companies, util.getIdByJson((JSONObject) snConfigurationItemJson, SnTable.Company.get(), app.Value()));
                        if (company != null) configurationItem.setCompany(company);

                        Location location = util.filterLocation(locations, util.getIdByJson((JSONObject) snConfigurationItemJson, SnTable.Location.get(), app.Value()));
                        if (location != null) configurationItem.setLocation(location);

                        Company manufacturer = util.filterCompany(companies, util.getIdByJson((JSONObject) snConfigurationItemJson, "manufacturer", app.Value()));
                        if (manufacturer != null) configurationItem.setManufacturer(manufacturer);

                        SysUser assignedTo = util.filterSysUser(sysUsers, util.getIdByJson((JSONObject) snConfigurationItemJson, "assigned_to", app.Value()));
                        if (assignedTo != null) configurationItem.setAssignedTo(assignedTo);

                        SysUser ownedBy = util.filterSysUser(sysUsers, util.getIdByJson((JSONObject) snConfigurationItemJson, "owned_by", app.Value()));
                        if (ownedBy != null) configurationItem.setOwnedBy(ownedBy);

                        Domain domain = util.filterDomain(domains, util.getIdByJson((JSONObject) snConfigurationItemJson, "sys_domain", app.Value()));
                        if (domain != null)
                            configurationItem.setDomain(domain);

                        SysGroup supportGroup = util.filterSysGroup(sysGroups, util.getIdByJson((JSONObject) snConfigurationItemJson, "support_group", app.Value()));
                        if (supportGroup != null)
                            configurationItem.setSupportGroup(supportGroup);
                        String tagAction = app.CreateConsole();
                        ConfigurationItem exists = configurationItemService.findByIntegrationId(configurationItem.getIntegrationId());
                        if (exists != null) {
                            configurationItem.setId(exists.getId());
                            tagAction = app.UpdateConsole();
                        }
                        configurationItemService.save(configurationItem);
                        util.printData(tag, count[0], tagAction.concat(util.getFieldDisplay(configurationItem)), util.getFieldDisplay(company), util.getFieldDisplay(domain));

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
        return snConfigurationItems;
    }

}

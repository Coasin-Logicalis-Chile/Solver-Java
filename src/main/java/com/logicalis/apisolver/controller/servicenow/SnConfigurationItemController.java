package com.logicalis.apisolver.controller.servicenow;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.logicalis.apisolver.model.*;
import com.logicalis.apisolver.model.enums.App;
import com.logicalis.apisolver.model.enums.EndPointSN;
import com.logicalis.apisolver.model.enums.SnTable;
import com.logicalis.apisolver.model.servicenow.SnConfigurationItem;
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

import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = {"${app.api.settings.cross-origin.urls}", "*"})
@RestController
@RequestMapping("/api/v1")
@Slf4j
public class SnConfigurationItemController {
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
    private Rest rest;

    @GetMapping("/sn_configuration_items")
    public List<SnConfigurationItem> show() {
        log.info(App.Start());
        APIResponse apiResponse = null;
        List<SnConfigurationItem> snConfigurationItems = new ArrayList<>();
        String[] sparmOffSets = Util.offSets99000();
        long startTime = 0;
        long endTime = 0;
        String tag = "[ConfigurationItem] ";
        try {
            List<Domain> domains = domainService.findAll();
            log.info(tag.concat("(Get All Domains)"));
            List<Company> companies = companyService.findAll();
            log.info(tag.concat("(Get All Companies)"));
            List<Location> locations = locationService.findAll();
            log.info(tag.concat("(Get All Locations)"));
            List<SysUser> sysUsers = sysUserService.findAll();
            log.info(tag.concat("(Get All Sys Users)"));
            List<SysGroup> sysGroups = sysGroupService.findAll();
            log.info(tag.concat("(Get All Sys Groups)"));
            startTime = System.currentTimeMillis();
            final int[] count = {1};
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JSONParser parser = new JSONParser();
            JSONObject resultJson = new JSONObject();
            JSONArray ListSnConfigurationItemJson = new JSONArray();
            final SnConfigurationItem[] snConfigurationItem = {new SnConfigurationItem()};
            final ConfigurationItem[] configurationItem = {new ConfigurationItem()};
            APIExecutionStatus status = new APIExecutionStatus();
            final Company[] company = new Company[1];
            final Location[] location = new Location[1];
            final Company[] manufacturer = new Company[1];
            final SysUser[] assignedTo = new SysUser[1];
            final SysUser[] ownedBy = new SysUser[1];
            final Domain[] domain = new Domain[1];
            final SysGroup[] supportGroup = new SysGroup[1];
            final String[] tagAction = new String[1];
            final ConfigurationItem[] exists = new ConfigurationItem[1];
            String result;
            for (String sparmOffSet : sparmOffSets) {
                result = rest.responseByEndPoint(EndPointSN.ConfigurationItem().concat(sparmOffSet));
                log.info(tag.concat("(".concat(EndPointSN.ConfigurationItem().concat(sparmOffSet)).concat(")")));
                ListSnConfigurationItemJson.clear();
                resultJson = (JSONObject) parser.parse(result);
                if (resultJson.get("result") != null)
                    ListSnConfigurationItemJson = (JSONArray) parser.parse(resultJson.get("result").toString());

                ListSnConfigurationItemJson.stream().forEach(snConfigurationItemJson -> {
                    snConfigurationItem[0] = new SnConfigurationItem();
                    try {
                        snConfigurationItem[0] = mapper.readValue(snConfigurationItemJson.toString(), SnConfigurationItem.class);
                        snConfigurationItems.add(snConfigurationItem[0]);
                        configurationItem[0] = new ConfigurationItem();
                        configurationItem[0].setName(snConfigurationItem[0].getName());
                        configurationItem[0].setIntegrationId(snConfigurationItem[0].getSys_id());
                        configurationItem[0].setDescription(snConfigurationItem[0].getDescription());
                        configurationItem[0].setStatus(snConfigurationItem[0].getStatus());
                        configurationItem[0].setUpdated(snConfigurationItem[0].getUpdated());
                        configurationItem[0].setCreatedBy(snConfigurationItem[0].getSys_created_by());
                        configurationItem[0].setAssetTag(snConfigurationItem[0].getAsset_tag());
                        configurationItem[0].setAssigned(snConfigurationItem[0].getAssigned());
                        configurationItem[0].setCategory(snConfigurationItem[0].getCategory());
                        configurationItem[0].setComments(snConfigurationItem[0].getComments());
                        configurationItem[0].setCorrelationId(snConfigurationItem[0].getCorrelation_id());
                        configurationItem[0].setCostCurrency(snConfigurationItem[0].getCost_cc());
                        configurationItem[0].setCreated(snConfigurationItem[0].getSys_created_on());
                        configurationItem[0].setHostname(snConfigurationItem[0].getHostname());
                        configurationItem[0].setImpact(snConfigurationItem[0].getImpact());
                        configurationItem[0].setInstalled(snConfigurationItem[0].getInstalled());
                        configurationItem[0].setModelNumber(snConfigurationItem[0].getModel_number());
                        configurationItem[0].setMonitor(snConfigurationItem[0].getMonitor());
                        configurationItem[0].setOperationalStatus(snConfigurationItem[0].getOperational_status());
                        configurationItem[0].setSerialNumber(snConfigurationItem[0].getSerial_number());
                        configurationItem[0].setSpecialInstruction(snConfigurationItem[0].getSpecial_instruction());
                        configurationItem[0].setSubcategory(snConfigurationItem[0].getSubcategory());
                        configurationItem[0].setUpdatedBy(snConfigurationItem[0].getSys_updated_by());

                        company[0] = Util.filterCompany(companies, Util.getIdByJson((JSONObject) snConfigurationItemJson, SnTable.Company.get(), App.Value()));
                        if (company[0] != null) configurationItem[0].setCompany(company[0]);

                        location[0] = Util.filterLocation(locations, Util.getIdByJson((JSONObject) snConfigurationItemJson, SnTable.Location.get(), App.Value()));
                        if (location[0] != null) configurationItem[0].setLocation(location[0]);

                        manufacturer[0] = Util.filterCompany(companies, Util.getIdByJson((JSONObject) snConfigurationItemJson, "manufacturer", App.Value()));
                        if (manufacturer[0] != null) configurationItem[0].setManufacturer(manufacturer[0]);

                        assignedTo[0] = Util.filterSysUser(sysUsers, Util.getIdByJson((JSONObject) snConfigurationItemJson, "assigned_to", App.Value()));
                        if (assignedTo[0] != null) configurationItem[0].setAssignedTo(assignedTo[0]);

                        ownedBy[0] = Util.filterSysUser(sysUsers, Util.getIdByJson((JSONObject) snConfigurationItemJson, "owned_by", App.Value()));
                        if (ownedBy[0] != null) configurationItem[0].setOwnedBy(ownedBy[0]);

                        domain[0] = Util.filterDomain(domains, Util.getIdByJson((JSONObject) snConfigurationItemJson, "sys_domain", App.Value()));
                        if (domain[0] != null)
                            configurationItem[0].setDomain(domain[0]);

                        supportGroup[0] = Util.filterSysGroup(sysGroups, Util.getIdByJson((JSONObject) snConfigurationItemJson, "support_group", App.Value()));
                        if (supportGroup[0] != null)
                            configurationItem[0].setSupportGroup(supportGroup[0]);
                        tagAction[0] = App.CreateConsole();
                        exists[0] = configurationItemService.findByIntegrationId(configurationItem[0].getIntegrationId());
                        if (exists[0] != null) {
                            configurationItem[0].setId(exists[0].getId());
                            tagAction[0] = App.UpdateConsole();
                        }
                        configurationItemService.save(configurationItem[0]);
                        Util.printData(tag, count[0], tagAction[0].concat(Util.getFieldDisplay(configurationItem[0])), Util.getFieldDisplay(company[0]), Util.getFieldDisplay(domain[0]));

                        count[0] = count[0] + 1;
                    } catch (Exception e) {
                        log.error(tag.concat("Exception (I) : ").concat(String.valueOf(e)));
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
            log.error(tag.concat("Exception (II) : ").concat(String.valueOf(e)));
        }
        log.info(App.End());
        return snConfigurationItems;
    }

}

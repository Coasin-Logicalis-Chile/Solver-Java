package com.logicalis.apisolver.controller.servicenow;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.logicalis.apisolver.model.*;
import com.logicalis.apisolver.model.enums.App;
import com.logicalis.apisolver.model.enums.EndPointSN;
import com.logicalis.apisolver.model.servicenow.SnCatalogLine;
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
public class SnCatalogLineController {
    @Autowired
    private ICatalogLineService catalogLineService;
    @Autowired
    private ICompanyService companyService;
    @Autowired
    private ICiServiceService ciServiceService;
    @Autowired
    private ISysGroupService sysGroupService;
    @Autowired
    private IAPIExecutionStatusService statusService;
    @Autowired
    private IConfigurationItemService configurationItemService;
    @Autowired
    private Rest rest;

    @GetMapping("/sn_catalogs_line")
    public List<SnCatalogLine> show() {
        System.out.println(App.Start());
        APIResponse apiResponse = null;
        List<SnCatalogLine> snCatalogsLine = new ArrayList<>();
        long startTime = 0;
        long endTime = 0;
        String tag = "[CatalogLine] ";
        try {
            startTime = System.currentTimeMillis();
            String result = rest.responseByEndPoint(EndPointSN.CatalogLine());
            endTime = (System.currentTimeMillis() - startTime);
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JSONParser parser = new JSONParser();
            JSONObject resultJson = (JSONObject) parser.parse(result);
            JSONArray ListSnCatalogLineJson = new JSONArray();
            if (resultJson.get("result") != null)
                ListSnCatalogLineJson = (JSONArray) parser.parse(resultJson.get("result").toString());
            final int[] count = {1};
            final String[] tagAction = new String[1];
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            final SnCatalogLine[] snCatalogLine = new SnCatalogLine[1];
            final CatalogLine[] catalogLine = new CatalogLine[1];
            final CatalogLine[] exists = new CatalogLine[1];
            APIExecutionStatus status = new APIExecutionStatus();
            final Company[] company = new Company[1];
            final ConfigurationItem[] configurationItem = new ConfigurationItem[1];
            final SysGroup[] sysGroup = new SysGroup[1];
            final SysGroup[] approvalGroup = new SysGroup[1];
            final CiService[] businessService = new CiService[1];
            ListSnCatalogLineJson.forEach(snCatalogLineJson -> {
                tagAction[0] = App.CreateConsole();
                try {
                    snCatalogLine[0] = objectMapper.readValue(snCatalogLineJson.toString(), SnCatalogLine.class);
                    snCatalogsLine.add(snCatalogLine[0]);
                    catalogLine[0] = new CatalogLine();
                    catalogLine[0].setIntegrationId(snCatalogLine[0].getSys_id());
                    catalogLine[0].setNumber(snCatalogLine[0].getU_number());
                    catalogLine[0].setService(snCatalogLine[0].getU_service());
                    catalogLine[0].setAmbite(snCatalogLine[0].getU_ambite());
                    catalogLine[0].setPlatform(snCatalogLine[0].getU_platform());
                    catalogLine[0].setSpecification(snCatalogLine[0].getU_specification());
                    catalogLine[0].setSysCreatedBy(snCatalogLine[0].getSys_created_by());
                    catalogLine[0].setSysCreatedOn(snCatalogLine[0].getSys_created_on());
                    catalogLine[0].setSysUpdatedBy(snCatalogLine[0].getSys_updated_by());
                    catalogLine[0].setSysUpdatedOn(snCatalogLine[0].getSys_updated_on());
                    catalogLine[0].setActive(snCatalogLine[0].isU_active());
                    company[0] = getCompanyByIntegrationId((JSONObject) snCatalogLineJson, "u_company", App.Value());
                    if (company[0] != null)
                        catalogLine[0].setCompany(company[0]);

                    configurationItem[0] = getConfigurationItemByIntegrationId((JSONObject) snCatalogLineJson, "u_configuration_item", App.Value());
                    if (configurationItem[0] != null)
                        catalogLine[0].setConfigurationItem(configurationItem[0]);

                    sysGroup[0] = getSysGroupByIntegrationId((JSONObject) snCatalogLineJson, "u_assignment_group", App.Value());
                    if (sysGroup[0] != null)
                        catalogLine[0].setAssignmentGroup(sysGroup[0]);

                    approvalGroup[0] = getSysGroupByIntegrationId((JSONObject) snCatalogLineJson, "u_approval_group", App.Value());
                    if (approvalGroup[0] != null)
                        catalogLine[0].setApprovalGroup(approvalGroup[0]);

                    businessService[0] = getCiServiceByIntegrationId((JSONObject) snCatalogLineJson, "u_cmdb_ci_service", App.Value());
                    if (businessService[0] != null)
                        catalogLine[0].setBusinessService(businessService[0]);
                    exists[0] = catalogLineService.findByIntegrationId(catalogLine[0].getIntegrationId());
                    if (exists[0] != null) {
                        catalogLine[0].setId(exists[0].getId());
                        tagAction[0] = App.UpdateConsole();
                    }
                    Util.printData(tag, count[0], tagAction[0].concat(Util.getFieldDisplay(catalogLine[0])), Util.getFieldDisplay(company[0]));
                    catalogLineService.save(catalogLine[0]);
                    count[0] = count[0] + 1;
                } catch (JsonProcessingException e) {
                    System.out.println(tag.concat("Exception (I) : ").concat(String.valueOf(e)));
                }
            });
            apiResponse = mapper.readValue(result, APIResponse.class);
            status.setUri(EndPointSN.Catalog());
            status.setUserAPI(App.SNUser());
            status.setPasswordAPI(App.SNPassword());
            status.setError(apiResponse.getError());
            status.setMessage(apiResponse.getMessage());
            status.setExecutionTime(endTime);
            statusService.save(status);
        } catch (Exception e) {
            System.out.println(tag.concat("Exception (II) : ").concat(String.valueOf(e)));
        }
        System.out.println(App.End());
        return snCatalogsLine;
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

    public CiService getCiServiceByIntegrationId(JSONObject jsonObject, String levelOne, String levelTwo) {
        String integrationId = Util.getIdByJson(jsonObject, levelOne, levelTwo);
        if (Util.hasData(integrationId)) {
            return ciServiceService.findByIntegrationId(integrationId);
        } else
            return null;
    }

}


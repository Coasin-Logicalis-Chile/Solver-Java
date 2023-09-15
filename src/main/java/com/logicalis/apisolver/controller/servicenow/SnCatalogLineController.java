
package com.logicalis.apisolver.controller.servicenow;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.logicalis.apisolver.model.*;
import com.logicalis.apisolver.model.enums.App;
import com.logicalis.apisolver.model.enums.EndPointSN;
import com.logicalis.apisolver.model.servicenow.SnCatalogLine;
import com.logicalis.apisolver.services.*;
import com.logicalis.apisolver.services.servicenow.ISnCatalogLineService;
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
    private ISnCatalogLineService snCatalogLineService;
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

    private Util util = new Util();
    App app = new App();
    EndPointSN endPointSN = new EndPointSN();

    @GetMapping("/sn_catalogs_line")
    public List<SnCatalogLine> show() {
        System.out.println(app.Start());
        APIResponse apiResponse = null;
        List<SnCatalogLine> snCatalogsLine = new ArrayList<>();
        Util util = new Util();
        long startTime = 0;
        long endTime = 0;
        String tag = "[CatalogLine] ";
        try {
            startTime = System.currentTimeMillis();
            String result = rest.responseByEndPoint(endPointSN.CatalogLine());
            endTime = (System.currentTimeMillis() - startTime);
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JSONParser parser = new JSONParser();
            JSONObject resultJson = (JSONObject) parser.parse(result);
            JSONArray ListSnCatalogLineJson = new JSONArray();
            if (resultJson.get("result") != null)
                ListSnCatalogLineJson = (JSONArray) parser.parse(resultJson.get("result").toString());
            final int[] count = {1};
            ListSnCatalogLineJson.forEach(snCatalogLineJson -> {
                String tagAction = app.CreateConsole();
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                try {
                    SnCatalogLine snCatalogLine = objectMapper.readValue(snCatalogLineJson.toString(), SnCatalogLine.class);
                    snCatalogsLine.add(snCatalogLine);
                    CatalogLine catalogLine = new CatalogLine();
                    catalogLine.setIntegrationId(snCatalogLine.getSys_id());
                    catalogLine.setNumber(snCatalogLine.getU_number());
                    catalogLine.setService(snCatalogLine.getU_service());
                    catalogLine.setAmbite(snCatalogLine.getU_ambite());
                    catalogLine.setPlatform(snCatalogLine.getU_platform());
                    catalogLine.setSpecification(snCatalogLine.getU_specification());
                    catalogLine.setSysCreatedBy(snCatalogLine.getSys_created_by());
                    catalogLine.setSysCreatedOn(snCatalogLine.getSys_created_on());
                    catalogLine.setSysUpdatedBy(snCatalogLine.getSys_updated_by());
                    catalogLine.setSysUpdatedOn(snCatalogLine.getSys_updated_on());
                    catalogLine.setActive(snCatalogLine.isU_active());

                    Company company = getCompanyByIntegrationId((JSONObject) snCatalogLineJson, "u_company", app.Value());
                    if (company != null)
                        catalogLine.setCompany(company);

                    ConfigurationItem configurationItem = getConfigurationItemByIntegrationId((JSONObject) snCatalogLineJson, "u_configuration_item", app.Value());
                    if (configurationItem != null)
                        catalogLine.setConfigurationItem(configurationItem);

                    SysGroup sysGroup = getSysGroupByIntegrationId((JSONObject) snCatalogLineJson, "u_assignment_group", app.Value());
                    if (sysGroup != null)
                        catalogLine.setAssignmentGroup(sysGroup);

                    SysGroup approvalGroup = getSysGroupByIntegrationId((JSONObject) snCatalogLineJson, "u_approval_group", app.Value());
                    if (approvalGroup != null)
                        catalogLine.setApprovalGroup(approvalGroup);

                    CiService businessService = getCiServiceByIntegrationId((JSONObject) snCatalogLineJson, "u_cmdb_ci_service", app.Value());
                    if (businessService != null)
                        catalogLine.setBusinessService(businessService);

                    //util.printData(tag, count[0], (catalogLine != null ? catalogLine.getNumber() != "" ? catalogLine.getNumber() : app.Title() : app.Title()));

                    CatalogLine exists = catalogLineService.findByIntegrationId(catalogLine.getIntegrationId());
                    if (exists != null) {
                        catalogLine.setId(exists.getId());
                        tagAction = app.UpdateConsole();
                    }
                    util.printData(tag, count[0], tagAction.concat(util.getFieldDisplay(catalogLine)), util.getFieldDisplay(company));


                    catalogLineService.save(catalogLine);
                    count[0] = count[0] + 1;
                } catch (JsonProcessingException e) {
                    System.out.println(tag.concat("Exception (I) : ").concat(String.valueOf(e)));
                }
            });

            apiResponse = mapper.readValue(result, APIResponse.class);

            APIExecutionStatus status = new APIExecutionStatus();
            status.setUri(endPointSN.Catalog());
            status.setUserAPI(app.SNUser());
            status.setPasswordAPI(app.SNPassword());
            status.setError(apiResponse.getError());
            status.setMessage(apiResponse.getMessage());
            status.setExecutionTime(endTime);
            statusService.save(status);


        } catch (Exception e) {
            System.out.println(tag.concat("Exception (II) : ").concat(String.valueOf(e)));
        }
        System.out.println(app.End());
        return snCatalogsLine;
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

    public CiService getCiServiceByIntegrationId(JSONObject jsonObject, String levelOne, String levelTwo) {
        String integrationId = util.getIdByJson(jsonObject, levelOne, levelTwo);
        if (util.hasData(integrationId)) {
            return ciServiceService.findByIntegrationId(integrationId);
        } else
            return null;
    }

}


package com.logicalis.apisolver.controller.servicenow;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.logicalis.apisolver.model.APIExecutionStatus;
import com.logicalis.apisolver.model.APIResponse;
import com.logicalis.apisolver.model.Catalog;
import com.logicalis.apisolver.model.ScCategory;
import com.logicalis.apisolver.model.enums.App;
import com.logicalis.apisolver.model.enums.EndPointSN;
import com.logicalis.apisolver.model.enums.SnTable;
import com.logicalis.apisolver.model.servicenow.SnScCategory;
import com.logicalis.apisolver.services.IAPIExecutionStatusService;
import com.logicalis.apisolver.services.ICatalogService;
import com.logicalis.apisolver.services.IScCategoryService;
import com.logicalis.apisolver.services.servicenow.ISnScCategoryService;
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
public class SnScCategoryController {

    @Autowired
    private ISnScCategoryService snScCategoryService;
    @Autowired
    private IScCategoryService scCategoryService;
    @Autowired
    private IAPIExecutionStatusService statusService;
    @Autowired
    private ICatalogService catalogService;
    @Autowired
    private Rest rest;

    @GetMapping("/sn_sc_categories")
    public List<SnScCategory> show() {
        System.out.println(App.Start());
        APIResponse apiResponse = null;
        List<SnScCategory> snCatalogs = new ArrayList<>();
        long startTime = 0;
        long endTime = 0;
        String tag = "[ScCategory] ";
        try {
            startTime = System.currentTimeMillis();
            String result = rest.responseByEndPoint(EndPointSN.ScCategory());
            endTime = (System.currentTimeMillis() - startTime);
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JSONParser parser = new JSONParser();
            JSONObject resultJson = (JSONObject) parser.parse(result);
            JSONArray ListSnScCategoryJson = new JSONArray();
            if (resultJson.get("result") != null)
                ListSnScCategoryJson = (JSONArray) parser.parse(resultJson.get("result").toString());
            final int[] count = {1};
            ListSnScCategoryJson.forEach(snCatalogJson -> {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                try {
                    SnScCategory snScCategory = objectMapper.readValue(snCatalogJson.toString(), SnScCategory.class);
                    snCatalogs.add(snScCategory);
                    ScCategory scCategory = new ScCategory();
                    scCategory.setActive(snScCategory.isActive());
                    scCategory.setTitle(snScCategory.getTitle());
                    scCategory.setDescription(snScCategory.getDescription());
                    scCategory.setIntegrationId(snScCategory.getSys_id());

                    Catalog catalog = getCatalogByIntegrationId((JSONObject) snCatalogJson, SnTable.Catalog.get(), App.Value());
                    if (catalog != null)
                        scCategory.setCatalog(catalog);

                    ScCategory exists = scCategoryService.findByIntegrationId(scCategory.getIntegrationId());
                    String tagAction = App.CreateConsole();
                    if (exists != null) {
                        scCategory.setId(exists.getId());
                        tagAction = App.UpdateConsole();
                    }
                    scCategoryService.save(scCategory);
                    Util.printData(tag, count[0], tagAction.concat(scCategory != null ? scCategory.getTitle() != "" && scCategory.getTitle() != null ? scCategory.getTitle() : App.Title() : App.Title()));
                    count[0] = count[0] + 1;
                } catch (JsonProcessingException e) {
                    System.out.println(tag.concat("Exception (I) : ").concat(String.valueOf(e)));
                }
            });

            apiResponse = mapper.readValue(result, APIResponse.class);

            APIExecutionStatus status = new APIExecutionStatus();
            status.setUri(EndPointSN.ScCategory());
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
        return snCatalogs;
    }

    public Catalog getCatalogByIntegrationId(JSONObject jsonObject, String levelOne, String levelTwo) {
        String integrationId = Util.getIdByJson(jsonObject, levelOne, levelTwo);
        if (Util.hasData(integrationId)) {
            return catalogService.findByIntegrationId(integrationId);
        } else
            return null;
    }
}

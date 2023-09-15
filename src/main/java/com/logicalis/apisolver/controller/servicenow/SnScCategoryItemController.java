
package com.logicalis.apisolver.controller.servicenow;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.logicalis.apisolver.model.*;
import com.logicalis.apisolver.model.enums.App;
import com.logicalis.apisolver.model.enums.EndPointSN;
import com.logicalis.apisolver.model.servicenow.SnScCategoryItem;
import com.logicalis.apisolver.services.IAPIExecutionStatusService;
import com.logicalis.apisolver.services.ICatalogService;
import com.logicalis.apisolver.services.IScCategoryItemService;
import com.logicalis.apisolver.services.IScCategoryService;
import com.logicalis.apisolver.services.servicenow.ISnScCategoryItemService;
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
public class SnScCategoryItemController {

    @Autowired
    private ISnScCategoryItemService snScCategoryItemService;
    @Autowired
    private IScCategoryItemService scCategoryItemService;
    @Autowired
    private IScCategoryService scCategoryService;
    @Autowired
    private IAPIExecutionStatusService statusService;
    @Autowired
    private ICatalogService catalogService;
    @Autowired
    private Rest rest;

    private Util util = new Util();
    App app = new App();
    EndPointSN endPointSN = new EndPointSN();
    @GetMapping("/sn_sc_category_items")
    public List<SnScCategoryItem> show() {
        System.out.println(app.Start());
        APIResponse apiResponse = null;
        List<SnScCategoryItem> snCatalogs = new ArrayList<>();
        Util util = new Util();
        long startTime = 0;
        long endTime = 0;
        String tag = "[ScCategoryItem] ";
        try {
            startTime = System.currentTimeMillis();
            String result = rest.responseByEndPoint(endPointSN.ScCategoryItem());
            endTime = (System.currentTimeMillis() - startTime);
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JSONParser parser = new JSONParser();
            JSONObject resultJson = (JSONObject) parser.parse(result);
            JSONArray ListSnScCategoryItemJson = new JSONArray();
            if (resultJson.get("result") != null)
                ListSnScCategoryItemJson = (JSONArray) parser.parse(resultJson.get("result").toString());
            final int[] count = {1};
            ListSnScCategoryItemJson.forEach(snCatalogJson -> {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                try {
                    SnScCategoryItem snScCategoryItem = objectMapper.readValue(snCatalogJson.toString(), SnScCategoryItem.class);
                    snCatalogs.add(snScCategoryItem);
                    ScCategoryItem scCategoryItem = new ScCategoryItem();
                    scCategoryItem.setActive(snScCategoryItem.isActive());
                    scCategoryItem.setName(snScCategoryItem.getName());
                    scCategoryItem.setDescription(snScCategoryItem.getDescription());
                    scCategoryItem.setIntegrationId(snScCategoryItem.getSys_id());

                    Catalog catalog = getCatalogByIntegrationId((JSONObject) snCatalogJson, "sc_catalogs");
                    if (catalog != null)
                        scCategoryItem.setCatalog(catalog);

                    ScCategory scCategory = getScCategoryByIntegrationId((JSONObject) snCatalogJson, "category", app.Value());
                    if (scCategory != null)
                        scCategoryItem.setScCategory(scCategory);


                   ScCategoryItem exists = scCategoryItemService.findByIntegrationId(scCategoryItem.getIntegrationId());
                    String tagAction = app.CreateConsole();
                    if (exists != null) {
                        scCategoryItem.setId(exists.getId());
                        tagAction = app.UpdateConsole();
                    }

                    util.printData(tag, count[0],  tagAction.concat(scCategoryItem != null ? scCategoryItem.getName() != "" &&  scCategoryItem.getName() != null  ? scCategoryItem.getName() : app.Title() : app.Title()));

                    scCategoryItemService.save(scCategoryItem);
                    count[0] = count[0] + 1;
                } catch (JsonProcessingException e) {
                    System.out.println(tag.concat("Exception (I) : ").concat(String.valueOf(e)));
                }
            });

            apiResponse = mapper.readValue(result, APIResponse.class);

            APIExecutionStatus status = new APIExecutionStatus();
            status.setUri(endPointSN.ScCategoryItem());
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
        return snCatalogs;
    }

    public Catalog getCatalogByIntegrationId(JSONObject jsonObject, String levelOne) {
        String integrationId = util.getIdByJson(jsonObject, levelOne);
        if (util.hasData(integrationId)) {
            return catalogService.findByIntegrationId(integrationId);
        } else
            return null;
    }
    public ScCategory getScCategoryByIntegrationId(JSONObject jsonObject, String levelOne, String levelTwo) {
        String integrationId = util.getIdByJson(jsonObject, levelOne,levelTwo);
        if (util.hasData(integrationId)) {
            return scCategoryService.findByIntegrationId(integrationId);
        } else
            return null;
    }

}

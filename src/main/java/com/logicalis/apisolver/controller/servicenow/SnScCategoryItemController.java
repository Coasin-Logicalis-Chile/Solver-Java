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
import java.util.Objects;

@CrossOrigin(origins = {"${app.api.settings.cross-origin.urls}", "*"})
@RestController
@RequestMapping("/api/v1")
@Slf4j
public class SnScCategoryItemController {
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

    @GetMapping("/sn_sc_category_items")
    public List<SnScCategoryItem> show() {
        log.info(App.Start());
        APIResponse apiResponse = null;
        List<SnScCategoryItem> snCatalogs = new ArrayList<>();
        long startTime = 0;
        long endTime = 0;
        String tag = "[ScCategoryItem] ";
        try {
            startTime = System.currentTimeMillis();
            String result = rest.responseByEndPoint(EndPointSN.ScCategoryItem());
            endTime = (System.currentTimeMillis() - startTime);
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JSONParser parser = new JSONParser();
            JSONObject resultJson = (JSONObject) parser.parse(result);
            JSONArray ListSnScCategoryItemJson = new JSONArray();
            final SnScCategoryItem[] snScCategoryItem = new SnScCategoryItem[1];
            final ScCategoryItem[] scCategoryItem = {new ScCategoryItem()};
            final Catalog[] catalog = new Catalog[1];
            if (resultJson.get("result") != null)
                ListSnScCategoryItemJson = (JSONArray) parser.parse(resultJson.get("result").toString());
            final int[] count = {1};
            final ScCategory[] scCategory = new ScCategory[1];
            final ScCategoryItem[] exists = new ScCategoryItem[1];
            final String[] tagAction = new String[1];
            APIExecutionStatus status = new APIExecutionStatus();
            ListSnScCategoryItemJson.forEach(snCatalogJson -> {
                try {
                    snScCategoryItem[0] = mapper.readValue(snCatalogJson.toString(), SnScCategoryItem.class);
                    snCatalogs.add(snScCategoryItem[0]);
                    scCategoryItem[0] = new ScCategoryItem();
                    scCategoryItem[0].setActive(snScCategoryItem[0].isActive());
                    scCategoryItem[0].setName(snScCategoryItem[0].getName());
                    scCategoryItem[0].setDescription(snScCategoryItem[0].getDescription());
                    scCategoryItem[0].setIntegrationId(snScCategoryItem[0].getSys_id());

                    catalog[0] = getCatalogByIntegrationId((JSONObject) snCatalogJson, "sc_catalogs");
                    if (catalog[0] != null)
                        scCategoryItem[0].setCatalog(catalog[0]);

                    scCategory[0] = getScCategoryByIntegrationId((JSONObject) snCatalogJson, "category", App.Value());
                    if (scCategory[0] != null)
                        scCategoryItem[0].setScCategory(scCategory[0]);

                    exists[0] = scCategoryItemService.findByIntegrationId(scCategoryItem[0].getIntegrationId());
                    tagAction[0] = App.CreateConsole();
                    if (exists[0] != null) {
                        scCategoryItem[0].setId(exists[0].getId());
                        tagAction[0] = App.UpdateConsole();
                    }

                    Util.printData(tag, count[0],  tagAction[0].concat(!Objects.isNull(scCategoryItem[0]) ? !scCategoryItem[0].getName().equals("") &&  scCategoryItem[0].getName() != null  ? scCategoryItem[0].getName() : App.Title() : App.Title()));
                    scCategoryItemService.save(scCategoryItem[0]);
                    count[0] = count[0] + 1;
                } catch (JsonProcessingException e) {
                    log.error(tag.concat("Exception (I) : ").concat(String.valueOf(e)));
                }
            });

            apiResponse = mapper.readValue(result, APIResponse.class);
            status.setUri(EndPointSN.ScCategoryItem());
            status.setUserAPI(App.SNUser());
            status.setPasswordAPI(App.SNPassword());
            status.setError(apiResponse.getError());
            status.setMessage(apiResponse.getMessage());
            status.setExecutionTime(endTime);
            statusService.save(status);
        } catch (Exception e) {
            log.error(tag.concat("Exception (II) : ").concat(String.valueOf(e)));
        }
        log.info(App.End());
        return snCatalogs;
    }

    public Catalog getCatalogByIntegrationId(JSONObject jsonObject, String levelOne) {
        String integrationId = Util.getIdByJson(jsonObject, levelOne);
        if (Util.hasData(integrationId)) {
            return catalogService.findByIntegrationId(integrationId);
        } else
            return null;
    }
    public ScCategory getScCategoryByIntegrationId(JSONObject jsonObject, String levelOne, String levelTwo) {
        String integrationId = Util.getIdByJson(jsonObject, levelOne,levelTwo);
        if (Util.hasData(integrationId)) {
            return scCategoryService.findByIntegrationId(integrationId);
        } else
            return null;
    }

}

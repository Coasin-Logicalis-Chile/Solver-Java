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
public class SnScCategoryController {
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
        log.info(App.Start());
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
            final ScCategory[] scCategory = {new ScCategory()};
            final Catalog[] catalog = new Catalog[1];
            final ScCategory[] exists = new ScCategory[1];
            final String[] tagAction = new String[1];
            APIExecutionStatus status = new APIExecutionStatus();
            final SnScCategory[] snScCategory = new SnScCategory[1];
            if (resultJson.get("result") != null)
                ListSnScCategoryJson = (JSONArray) parser.parse(resultJson.get("result").toString());
            final int[] count = {1};
            ListSnScCategoryJson.forEach(snCatalogJson -> {
                try {
                    snScCategory[0] = mapper.readValue(snCatalogJson.toString(), SnScCategory.class);
                    snCatalogs.add(snScCategory[0]);
                    scCategory[0] = new ScCategory();
                    scCategory[0].setActive(snScCategory[0].isActive());
                    scCategory[0].setTitle(snScCategory[0].getTitle());
                    scCategory[0].setDescription(snScCategory[0].getDescription());
                    scCategory[0].setIntegrationId(snScCategory[0].getSys_id());

                    catalog[0] = getCatalogByIntegrationId((JSONObject) snCatalogJson, SnTable.Catalog.get(), App.Value());
                    if (!Objects.isNull(catalog[0]))
                        scCategory[0].setCatalog(catalog[0]);

                    exists[0] = scCategoryService.findByIntegrationId(scCategory[0].getIntegrationId());
                    tagAction[0] = App.CreateConsole();
                    if (!Objects.isNull(exists[0])) {
                        scCategory[0].setId(exists[0].getId());
                        tagAction[0] = App.UpdateConsole();
                    }
                    scCategoryService.save(scCategory[0]);
                    Util.printData(tag, count[0], tagAction[0].concat(scCategory[0] != null ? !Objects.isNull(scCategory[0].getTitle()) && !scCategory[0].getTitle().equals("") ? scCategory[0].getTitle() : App.Title() : App.Title()));
                    count[0] = count[0] + 1;
                } catch (JsonProcessingException e) {
                    log.error(tag.concat("Exception (I) : ").concat(String.valueOf(e)));
                }
            });
            apiResponse = mapper.readValue(result, APIResponse.class);
            status.setUri(EndPointSN.ScCategory());
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

    public Catalog getCatalogByIntegrationId(JSONObject jsonObject, String levelOne, String levelTwo) {
        String integrationId = Util.getIdByJson(jsonObject, levelOne, levelTwo);
        if (Util.hasData(integrationId)) {
            return catalogService.findByIntegrationId(integrationId);
        } else
            return null;
    }
}

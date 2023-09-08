
package com.logicalis.apisolver.controller.servicenow;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.logicalis.apisolver.model.APIExecutionStatus;
import com.logicalis.apisolver.model.APIResponse;
import com.logicalis.apisolver.model.Catalog;
import com.logicalis.apisolver.model.enums.App;
import com.logicalis.apisolver.model.enums.EndPointSN;
import com.logicalis.apisolver.model.servicenow.SnCatalog;
import com.logicalis.apisolver.services.IAPIExecutionStatusService;
import com.logicalis.apisolver.services.ICatalogService;
import com.logicalis.apisolver.services.servicenow.ISnCatalogService;
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
public class SnCatalogController {

    @Autowired
    private ISnCatalogService snCatalogService;
    @Autowired
    private ICatalogService catalogService;
    @Autowired
    private IAPIExecutionStatusService statusService;
    Util util = new Util();
    App app = new App();
    EndPointSN endPointSN = new EndPointSN();

    @GetMapping("/sn_catalogs")
    public List<SnCatalog> show() {
        System.out.println(app.Start());
        APIResponse apiResponse = null;
        List<SnCatalog> snCatalogs = new ArrayList<>();
        Util util = new Util();
        long startTime = 0;
        long endTime = 0;
        String tag = "[Catalog] ";
        try {
            Rest rest = new Rest();
            startTime = System.currentTimeMillis();
            String result = rest.responseByEndPoint(endPointSN.Catalog());
            endTime = (System.currentTimeMillis() - startTime);
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JSONParser parser = new JSONParser();
            JSONObject resultJson = (JSONObject) parser.parse(result);
            JSONArray ListSnCatalogJson = new JSONArray();
            if (resultJson.get("result") != null)
                ListSnCatalogJson = (JSONArray) parser.parse(resultJson.get("result").toString());
            final int[] count = {1};
            ListSnCatalogJson.forEach(snCatalogJson -> {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                try {
                    SnCatalog snCatalog = objectMapper.readValue(snCatalogJson.toString(), SnCatalog.class);
                    snCatalogs.add(snCatalog);
                    Catalog catalog = new Catalog();
                    catalog.setActive(snCatalog.isActive());
                    catalog.setTitle(snCatalog.getTitle());
                    catalog.setDescription(snCatalog.getDescription());
                    catalog.setIntegrationId(snCatalog.getSys_id());

                    Catalog exists = catalogService.findByIntegrationId(catalog.getIntegrationId());
                    String tagAction = app.CreateConsole();
                    if (exists != null) {
                        catalog.setId(exists.getId());
                        tagAction = app.UpdateConsole();
                    }
                    util.printData(tag, count[0], tagAction.concat(catalog != null ? catalog.getTitle() != "" ? catalog.getTitle() : app.Title() : app.Title()));

                    catalogService.save(catalog);
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
        return snCatalogs;
    }
}


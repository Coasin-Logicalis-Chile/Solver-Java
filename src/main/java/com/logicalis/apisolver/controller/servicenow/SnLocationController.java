package com.logicalis.apisolver.controller.servicenow;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.logicalis.apisolver.model.*;
import com.logicalis.apisolver.model.enums.App;
import com.logicalis.apisolver.model.enums.EndPointSN;
import com.logicalis.apisolver.model.enums.SnTable;
import com.logicalis.apisolver.model.servicenow.SnLocation;
import com.logicalis.apisolver.services.IAPIExecutionStatusService;
import com.logicalis.apisolver.services.ICompanyService;
import com.logicalis.apisolver.services.IDomainService;
import com.logicalis.apisolver.services.ILocationService;
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
public class SnLocationController {
    @Autowired
    private ILocationService locationService;
    @Autowired
    private IAPIExecutionStatusService statusService;
    @Autowired
    private IDomainService domainService;
    @Autowired
    private ICompanyService companyService;
    @Autowired
    private Rest rest;

    @GetMapping("/sn_locationsSolverByQuery")
    public List<SnLocation> show(String query) {
        log.info(App.Start());
        APIResponse apiResponse = null;
        List<SnLocation> snLocations = new ArrayList<>();
        String[] sparmOffSets = Util.offSets500000();

        long startTime = 0;
        long endTime = 0;
        String tag = "[Location] ";
        try {
            startTime = System.currentTimeMillis();
            final int[] count = {1};
            String result;
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JSONParser parser = new JSONParser();
            JSONArray ListSnLocationJson = new JSONArray();
            JSONObject resultJson;
            final Location[] location = new Location[1];
            final SnLocation[] snLocation = {new SnLocation()};
            final Domain[] domain = new Domain[1];
            final Company[] company = new Company[1];
            final String[] tagAction = new String[1];
            final Location[] exists = new Location[1];
            APIExecutionStatus status = new APIExecutionStatus();
            for (String sparmOffSet : sparmOffSets) {
                result = rest.responseByEndPoint(EndPointSN.LocationByQuery().replace("QUERY", query).concat(sparmOffSet));
                log.info(tag.concat("(".concat(EndPointSN.LocationByQuery().replace("QUERY", query).concat(sparmOffSet)).concat(")")));
                endTime = (System.currentTimeMillis() - startTime);
                resultJson = (JSONObject) parser.parse(result);
                ListSnLocationJson.clear();
                if (resultJson.get("result") != null)
                    ListSnLocationJson = (JSONArray) parser.parse(resultJson.get("result").toString());

                ListSnLocationJson.forEach(snLocationJson -> {
                    snLocation[0] = new SnLocation();
                    try {
                        snLocation[0] = mapper.readValue(snLocationJson.toString(), SnLocation.class);
                        snLocations.add(snLocation[0]);
                        location[0] = new Location();
                        location[0].setActive(snLocation[0].isActive());
                        location[0].setName(snLocation[0].getName());
                        location[0].setCity(snLocation[0].getCity());
                        location[0].setCountry(snLocation[0].getCountry());
                        location[0].setLatitude(snLocation[0].getLatitude());
                        location[0].setLongitude(snLocation[0].getLongitude());
                        location[0].setIntegrationId(snLocation[0].getsys_id());
                        domain[0] = getDomainByIntegrationId((JSONObject) snLocationJson, SnTable.Domain.get(), App.Value());
                        if (domain[0] != null)
                            location[0].setDomain(domain[0]);
                        company[0] = getCompanyByIntegrationId((JSONObject) snLocationJson, SnTable.Company.get(), App.Value());
                        if (company[0] != null)
                            location[0].setCompany(company[0]);

                        tagAction[0] = App.CreateConsole();
                        exists[0] = locationService.findByIntegrationId(location[0].getIntegrationId());
                        if (exists[0] != null) {
                            location[0].setId(exists[0].getId());
                            tagAction[0] = App.UpdateConsole();
                        }
                        locationService.save(location[0]);
                        Util.printData(tag, count[0], tagAction[0].concat(Util.getFieldDisplay(location[0])), Util.getFieldDisplay(company[0]), Util.getFieldDisplay(domain[0]));
                        count[0] = count[0] + 1;
                    } catch (Exception e) {
                        log.info(tag.concat("Exception (I) : ").concat(String.valueOf(e)));
                    }
                });
                apiResponse = mapper.readValue(result, APIResponse.class);
                status.setUri(EndPointSN.Location());
                status.setUserAPI(App.SNUser());
                status.setPasswordAPI(App.SNPassword());
                status.setError(apiResponse.getError());
                status.setMessage(apiResponse.getMessage());
                status.setExecutionTime(endTime);
                statusService.save(status);
            }
        } catch (Exception e) {
            log.info(tag.concat("Exception (II) : ").concat(String.valueOf(e)));
        }
        log.info(App.End());
        return snLocations;
    }

    public Domain getDomainByIntegrationId(JSONObject jsonObject, String levelOne, String levelTwo) {
        String integrationId = Util.getIdByJson(jsonObject, levelOne, levelTwo);
        if (Util.hasData(integrationId)) {
            return domainService.findByIntegrationId(integrationId);
        } else
            return null;
    }

    public Company getCompanyByIntegrationId(JSONObject jsonObject, String levelOne, String levelTwo) {
        String integrationId = Util.getIdByJson(jsonObject, levelOne, levelTwo);
        if (Util.hasData(integrationId)) {
            return companyService.findByIntegrationId(integrationId);
        } else
            return null;
    }
}

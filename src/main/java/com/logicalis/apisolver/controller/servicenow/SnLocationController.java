
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
import com.logicalis.apisolver.services.servicenow.ISnLocationService;
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
public class SnLocationController {

    @Autowired
    private ISnLocationService snLocationService;
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

    private Util util = new Util();
    App app = new App();
    EndPointSN endPointSN = new EndPointSN();
    @GetMapping("/sn_locations")
    public List<SnLocation> show() {
        System.out.println(app.Start());
        APIResponse apiResponse = null;
        List<SnLocation> snLocations = new ArrayList<>();
        String[] sparmOffSets = util.offSets500000();

        long startTime = 0;
        long endTime = 0;
        String tag = "[Location] ";
        Util util = new Util();
        try {
            List<Domain> domains = domainService.findAll();
            System.out.println(tag.concat("(Get All Domains)"));
            List<Company> companies = companyService.findAll();
            System.out.println(tag.concat("(Get All Companies)"));

            startTime = System.currentTimeMillis();
            final int[] count = {1};
            for (String sparmOffSet : sparmOffSets) {
                String result = rest.responseByEndPoint(endPointSN.Location().concat(sparmOffSet));
                System.out.println(tag.concat("(".concat(endPointSN.Location().concat(sparmOffSet)).concat(")")));

                endTime = (System.currentTimeMillis() - startTime);
                ObjectMapper mapper = new ObjectMapper();
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                JSONParser parser = new JSONParser();
                JSONObject resultJson = (JSONObject) parser.parse(result);
                JSONArray ListSnLocationJson = new JSONArray();

                if (resultJson.get("result") != null)
                    ListSnLocationJson = (JSONArray) parser.parse(resultJson.get("result").toString());

                ListSnLocationJson.forEach(snLocationJson -> {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    SnLocation snLocation = new SnLocation();
                    try {
                        snLocation = objectMapper.readValue(snLocationJson.toString(), SnLocation.class);
                        snLocations.add(snLocation);
                        Location location = new Location();
                        location.setActive(snLocation.isActive());
                        location.setName(snLocation.getName());
                        location.setCity(snLocation.getCity());
                        location.setCountry(snLocation.getCountry());
                        location.setLatitude(snLocation.getLatitude());
                        location.setLongitude(snLocation.getLongitude());
                        location.setIntegrationId(snLocation.getsys_id());
                        String domainSysId = util.getIdByJson((JSONObject) snLocationJson, SnTable.Domain.get(), app.Value());
                        Domain domain = util.filterDomain(domains, domainSysId);
                        if (domain != null)
                            location.setDomain(domain);

                        String companySysId = util.getIdByJson((JSONObject) snLocationJson, SnTable.Company.get(), app.Value());
                        Company company = util.filterCompany(companies, companySysId);
                        if (company != null)
                            location.setCompany(company);

                        String tagAction = app.CreateConsole();
                        Location exists = locationService.findByIntegrationId(location.getIntegrationId());
                        if (exists != null) {
                            location.setId(exists.getId());
                            tagAction = app.UpdateConsole();
                        }
                        locationService.save(location);
                        util.printData(tag, count[0], tagAction.concat(util.getFieldDisplay(location)), util.getFieldDisplay(company), util.getFieldDisplay(domain));

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
                status.setExecutionTime(endTime);
                statusService.save(status);
            }

        } catch (Exception e) {
            System.out.println(tag.concat("Exception (II) : ").concat(String.valueOf(e)));
        }
        System.out.println(app.End());
        return snLocations;
    }

    @GetMapping("/sn_locationsSolverByQuery")
    public List<SnLocation> show(String query) {
        System.out.println(app.Start());
        APIResponse apiResponse = null;
        List<SnLocation> snLocations = new ArrayList<>();
        String[] sparmOffSets = util.offSets500000();

        long startTime = 0;
        long endTime = 0;
        String tag = "[Location] ";
        Util util = new Util();
        try {
            startTime = System.currentTimeMillis();
            final int[] count = {1};
            for (String sparmOffSet : sparmOffSets) {
                String result = rest.responseByEndPoint(endPointSN.LocationByQuery().replace("QUERY", query).concat(sparmOffSet));
                System.out.println(tag.concat("(".concat(endPointSN.LocationByQuery().replace("QUERY", query).concat(sparmOffSet)).concat(")")));

                endTime = (System.currentTimeMillis() - startTime);
                ObjectMapper mapper = new ObjectMapper();
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                JSONParser parser = new JSONParser();
                JSONObject resultJson = (JSONObject) parser.parse(result);
                JSONArray ListSnLocationJson = new JSONArray();

                if (resultJson.get("result") != null)
                    ListSnLocationJson = (JSONArray) parser.parse(resultJson.get("result").toString());

                ListSnLocationJson.forEach(snLocationJson -> {

                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    SnLocation snLocation = new SnLocation();

                    try {

                        snLocation = objectMapper.readValue(snLocationJson.toString(), SnLocation.class);
                        snLocations.add(snLocation);
                        Location location = new Location();
                        location.setActive(snLocation.isActive());
                        location.setName(snLocation.getName());
                        location.setCity(snLocation.getCity());
                        location.setCountry(snLocation.getCountry());
                        location.setLatitude(snLocation.getLatitude());
                        location.setLongitude(snLocation.getLongitude());
                        location.setIntegrationId(snLocation.getsys_id());
                        Domain domain = getDomainByIntegrationId((JSONObject) snLocationJson, SnTable.Domain.get(), app.Value());
                        if (domain != null)
                            location.setDomain(domain);

                        Company company = getCompanyByIntegrationId((JSONObject) snLocationJson, SnTable.Company.get(), app.Value());
                        if (company != null)
                            location.setCompany(company);

                        String tagAction = app.CreateConsole();
                        Location exists = locationService.findByIntegrationId(location.getIntegrationId());
                        if (exists != null) {
                            location.setId(exists.getId());
                            tagAction = app.UpdateConsole();
                        }
                        locationService.save(location);
                        util.printData(tag, count[0], tagAction.concat(util.getFieldDisplay(location)), util.getFieldDisplay(company), util.getFieldDisplay(domain));

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
                status.setExecutionTime(endTime);
                statusService.save(status);
            }

        } catch (Exception e) {
            System.out.println(tag.concat("Exception (II) : ").concat(String.valueOf(e)));
        }
        System.out.println(app.End());
        return snLocations;
    }

    public Domain getDomainByIntegrationId(JSONObject jsonObject, String levelOne, String levelTwo) {
        String integrationId = util.getIdByJson(jsonObject, levelOne, levelTwo);
        if (util.hasData(integrationId)) {
            return domainService.findByIntegrationId(integrationId);
        } else
            return null;
    }

    public Company getCompanyByIntegrationId(JSONObject jsonObject, String levelOne, String levelTwo) {
        String integrationId = util.getIdByJson(jsonObject, levelOne, levelTwo);
        if (util.hasData(integrationId)) {
            return companyService.findByIntegrationId(integrationId);
        } else
            return null;
    }

}

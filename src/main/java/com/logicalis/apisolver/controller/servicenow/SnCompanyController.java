
package com.logicalis.apisolver.controller.servicenow;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.logicalis.apisolver.model.APIExecutionStatus;
import com.logicalis.apisolver.model.APIResponse;
import com.logicalis.apisolver.model.Company;
import com.logicalis.apisolver.model.Domain;
import com.logicalis.apisolver.model.enums.App;
import com.logicalis.apisolver.model.enums.EndPointSN;
import com.logicalis.apisolver.model.enums.SnTable;
import com.logicalis.apisolver.model.servicenow.SnCompany;
import com.logicalis.apisolver.services.IAPIExecutionStatusService;
import com.logicalis.apisolver.services.ICompanyService;
import com.logicalis.apisolver.services.IDomainService;
import com.logicalis.apisolver.services.servicenow.ISnCompanyService;
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
public class SnCompanyController {

    @Autowired
    private ISnCompanyService snCompanyService;
    @Autowired
    private ICompanyService companyService;
    @Autowired
    private IAPIExecutionStatusService statusService;
    @Autowired
    private IDomainService domainService;
    @Autowired
    private Rest rest;

    App app = new App();
    EndPointSN endPointSN = new EndPointSN();

    @GetMapping("/sn_companies")
    public List<SnCompany> show() {
        System.out.println(app.Start());
        APIResponse apiResponse = null;
        List<SnCompany> snCompanies = new ArrayList<>();
        long startTime = 0;
        long endTime = 0;
        Util util = new Util();
        String tag = "[Company] ";
        try {
            List<Domain> domains = domainService.findAll();
            System.out.println(tag.concat("(Get All Domains)"));
            startTime = System.currentTimeMillis();
            String result = rest.responseByEndPoint(endPointSN.Company());
            endTime = (System.currentTimeMillis() - startTime);
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JSONParser parser = new JSONParser();
            JSONObject resultJson = (JSONObject) parser.parse(result);
            JSONArray ListSnCompanyJson = new JSONArray();
            if (resultJson.get("result") != null)
                ListSnCompanyJson = (JSONArray) parser.parse(resultJson.get("result").toString());
            final int[] count = {1};
            ListSnCompanyJson.forEach(snCompanyJson -> {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                try {
                    SnCompany snCompany = objectMapper.readValue(snCompanyJson.toString(), SnCompany.class);
                    snCompanies.add(snCompany);
                    Company company = new Company();
                    company.setActive(snCompany.isActive());
                    company.setName(snCompany.getName());
                    company.setIntegrationId(snCompany.getSys_id());
                    company.setSolver(snCompany.getU_solver());

                    String domainSysId = util.getIdByJson((JSONObject) snCompanyJson, SnTable.Domain.get(), app.Value());
                    Domain domain = util.filterDomain(domains, domainSysId);
                    if (domain != null)
                        company.setDomain(domain);


                    Company exists = companyService.findByIntegrationId(company.getIntegrationId());
                    String tagAction = app.CreateConsole();
                    if (exists != null) {
                        company.setId(exists.getId());
                        tagAction = app.UpdateConsole();
                    }

                    util.printData(tag,
                            count[0],
                            tagAction.concat(company != null ? company.getName() != "" ? company.getName() : app.Name() : app.Name()),
                            (domain != null ? domain.getName() != "" ? domain.getName() : app.Domain() : app.Domain()));

                    companyService.save(company);
                    count[0] = count[0] + 1;
                } catch (JsonProcessingException e) {
                    System.out.println(tag.concat("Exception (I) : ").concat(String.valueOf(e)));
                }
            });

            apiResponse = mapper.readValue(result, APIResponse.class);

            APIExecutionStatus status = new APIExecutionStatus();
            status.setUri(endPointSN.Company());
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
        return snCompanies;
    }
}

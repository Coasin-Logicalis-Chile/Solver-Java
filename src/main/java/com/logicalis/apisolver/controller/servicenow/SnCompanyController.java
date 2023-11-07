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
public class SnCompanyController {
    @Autowired
    private ICompanyService companyService;
    @Autowired
    private IAPIExecutionStatusService statusService;
    @Autowired
    private IDomainService domainService;
    @Autowired
    private Rest rest;

    @GetMapping("/sn_companies")
    public List<SnCompany> show() {
        log.info(App.Start());
        APIResponse apiResponse = null;
        List<SnCompany> snCompanies = new ArrayList<>();
        long startTime = 0;
        long endTime = 0;
        String tag = "[Company] ";
        try {
            List<Domain> domains = domainService.findAll();
            log.info(tag.concat("(Get All Domains)"));
            startTime = System.currentTimeMillis();
            String result = rest.responseByEndPoint(EndPointSN.Company());
            endTime = (System.currentTimeMillis() - startTime);
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JSONParser parser = new JSONParser();
            JSONObject resultJson = (JSONObject) parser.parse(result);
            JSONArray ListSnCompanyJson = new JSONArray();
            final SnCompany[] snCompany = new SnCompany[1];
            final Company[] company = new Company[1];
            final String[] domainSysId = new String[1];
            final Domain[] domain = new Domain[1];
            final Company[] exists = new Company[1];
            final String[] tagAction = new String[1];
            APIExecutionStatus status = new APIExecutionStatus();
            if (resultJson.get("result") != null)
                ListSnCompanyJson = (JSONArray) parser.parse(resultJson.get("result").toString());
            final int[] count = {1};
            ListSnCompanyJson.forEach(snCompanyJson -> {
                try {
                    snCompany[0] = mapper.readValue(snCompanyJson.toString(), SnCompany.class);
                    snCompanies.add(snCompany[0]);
                    company[0] = new Company();
                    company[0].setActive(snCompany[0].isActive());
                    company[0].setName(snCompany[0].getName());
                    company[0].setIntegrationId(snCompany[0].getSys_id());
                    company[0].setSolver(snCompany[0].getU_solver());

                    domainSysId[0] = Util.getIdByJson((JSONObject) snCompanyJson, SnTable.Domain.get(), App.Value());
                    domain[0] = Util.filterDomain(domains, domainSysId[0]);
                    if (domain[0] != null)
                        company[0].setDomain(domain[0]);

                    exists[0] = companyService.findByIntegrationId(company[0].getIntegrationId());
                    tagAction[0] = App.CreateConsole();
                    if (!Objects.isNull(exists[0])) {
                        company[0].setId(exists[0].getId());
                        tagAction[0] = App.UpdateConsole();
                    }

                    Util.printData(tag,
                            count[0],
                            tagAction[0].concat(!Objects.isNull(company[0])? ( !Objects.isNull(company[0].getName()) && !company[0].getName().equals("")) ? company[0].getName() : App.Name() : App.Name()),
                            (!Objects.isNull(domain[0]) ? (!Objects.isNull(domain[0].getName()) && !domain[0].getName().equals("")) ? domain[0].getName() : App.Domain() : App.Domain()));

                    companyService.save(company[0]);
                    count[0] = count[0] + 1;
                } catch (JsonProcessingException e) {
                    log.error(tag.concat("Exception (I) : ").concat(String.valueOf(e)));
                }
            });

            apiResponse = mapper.readValue(result, APIResponse.class);
            status.setUri(EndPointSN.Company());
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
        return snCompanies;
    }
}

package com.logicalis.apisolver.controller.servicenow;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.logicalis.apisolver.model.*;
import com.logicalis.apisolver.model.enums.App;
import com.logicalis.apisolver.model.enums.EndPointSN;
import com.logicalis.apisolver.model.enums.SnTable;
import com.logicalis.apisolver.model.servicenow.SnDepartment;
import com.logicalis.apisolver.services.IAPIExecutionStatusService;
import com.logicalis.apisolver.services.ICompanyService;
import com.logicalis.apisolver.services.IDepartmentService;
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
public class SnDepartmentController {
    @Autowired
    private IDepartmentService departmentService;
    @Autowired
    private IAPIExecutionStatusService statusService;
    @Autowired
    private IDomainService domainService;
    @Autowired
    private ICompanyService companyService;
    @Autowired
    private Rest rest;

    @GetMapping("/sn_departments")
    public List<SnDepartment> show() {
        log.info(App.Start());
        APIResponse apiResponse = null;
        List<SnDepartment> snDepartments = new ArrayList<>();
        long startTime = 0;
        long endTime = 0;
        String tag = "[Department] ";
        try {
            List<Domain> domains = domainService.findAll();
            log.info(tag.concat("(Get All Domains)"));
            List<Company> companies = companyService.findAll();
            log.info(tag.concat("(Get All Companies)"));

            startTime = System.currentTimeMillis();
            String result = rest.responseByEndPoint(EndPointSN.Department());
            endTime = (System.currentTimeMillis() - startTime);
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JSONParser parser = new JSONParser();
            JSONObject resultJson = (JSONObject) parser.parse(result);
            JSONArray ListSnDepartmentJson = new JSONArray();
            final Department[] department = {new Department()};
            final String[] domainSysId = new String[1];
            final Domain[] domain = new Domain[1];
            final String[] companySysId = new String[1];
            final Company[] company = new Company[1];
            final Department[] exists = new Department[1];
            final String[] tagAction = new String[1];
            APIExecutionStatus status = new APIExecutionStatus();
            final SnDepartment[] snDepartment = new SnDepartment[1];
            if (resultJson.get("result") != null)
                ListSnDepartmentJson = (JSONArray) parser.parse(resultJson.get("result").toString());
            final int[] count = {1};
            ListSnDepartmentJson.forEach(snDepartmentJson -> {
                try {
                    snDepartment[0] = mapper.readValue(snDepartmentJson.toString(), SnDepartment.class);
                    snDepartments.add(snDepartment[0]);
                    department[0] = new Department();
                    department[0].setActive(snDepartment[0].isActive());
                    department[0].setName(snDepartment[0].getName());
                    department[0].setDescription(snDepartment[0].getDescription());
                    department[0].setIntegrationId(snDepartment[0].getsys_id());

                    domainSysId[0] = Util.getIdByJson((JSONObject) snDepartmentJson, SnTable.Domain.get(), App.Value());
                    domain[0] = Util.filterDomain(domains, domainSysId[0]);
                    if (domain[0] != null)
                        department[0].setDomain(domain[0]);

                    companySysId[0] = Util.getIdByJson((JSONObject) snDepartmentJson, SnTable.Company.get(), App.Value());
                    company[0] = Util.filterCompany(companies, companySysId[0]);
                    if (company[0] != null)
                        department[0].setCompany(company[0]);

                    exists[0] = departmentService.findByIntegrationId(department[0].getIntegrationId());
                    tagAction[0] = App.CreateConsole();
                    if (exists[0] != null) {
                        department[0].setId(exists[0].getId());
                        tagAction[0] = App.UpdateConsole();
                    }

                    Util.printData(tag,
                            count[0],
                            tagAction[0].concat((department[0] != null ? (!Objects.isNull(department[0].getName()) && !department[0].getName().equals("")) ? department[0].getName() : App.Name() : App.Name())),
                            (company[0] != null ? (!Objects.isNull(company[0].getName()) && !company[0].getName().equals("")) ? company[0].getName() : App.Company() : App.Company()));

                    departmentService.save(department[0]);
                    count[0] = count[0] + 1;
                } catch (JsonProcessingException e) {
                    log.error(tag.concat("Exception (I) : ").concat(String.valueOf(e)));
                }
            });
            apiResponse = mapper.readValue(result, APIResponse.class);
            status.setUri(EndPointSN.Department());
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
        return snDepartments;
    }
}


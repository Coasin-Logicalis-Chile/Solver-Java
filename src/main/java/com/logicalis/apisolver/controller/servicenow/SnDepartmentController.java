
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
import com.logicalis.apisolver.services.servicenow.ISnDepartmentService;
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
public class SnDepartmentController {

    @Autowired
    private ISnDepartmentService snDepartmentService;
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
    App app = new App();
    EndPointSN endPointSN = new EndPointSN();

    @GetMapping("/sn_departments")
    public List<SnDepartment> show() {
        System.out.println(app.Start());
        APIResponse apiResponse = null;
        List<SnDepartment> snDepartments = new ArrayList<>();
        Util util = new Util();
        long startTime = 0;
        long endTime = 0;
        String tag = "[Department] ";
        try {

            List<Domain> domains = domainService.findAll();
            System.out.println(tag.concat("(Get All Domains)"));
            List<Company> companies = companyService.findAll();
            System.out.println(tag.concat("(Get All Companies)"));

            startTime = System.currentTimeMillis();
            String result = rest.responseByEndPoint(endPointSN.Department());
            endTime = (System.currentTimeMillis() - startTime);
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JSONParser parser = new JSONParser();
            JSONObject resultJson = (JSONObject) parser.parse(result);

            JSONArray ListSnDepartmentJson = new JSONArray();
            if (resultJson.get("result") != null)
                ListSnDepartmentJson = (JSONArray) parser.parse(resultJson.get("result").toString());
            final int[] count = {1};
            ListSnDepartmentJson.forEach(snDepartmentJson -> {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                try {
                    SnDepartment snDepartment = objectMapper.readValue(snDepartmentJson.toString(), SnDepartment.class);
                    snDepartments.add(snDepartment);
                    Department department = new Department();
                    department.setActive(snDepartment.isActive());
                    department.setName(snDepartment.getName());
                    department.setDescription(snDepartment.getDescription());
                    department.setIntegrationId(snDepartment.getsys_id());

                    String domainSysId = util.getIdByJson((JSONObject) snDepartmentJson, SnTable.Domain.get(), app.Value());
                    Domain domain = util.filterDomain(domains, domainSysId);
                    if (domain != null)
                        department.setDomain(domain);

                    String companySysId = util.getIdByJson((JSONObject) snDepartmentJson, SnTable.Company.get(), app.Value());
                    Company company = util.filterCompany(companies, companySysId);
                    if (company != null)
                        department.setCompany(company);


                    Department exists = departmentService.findByIntegrationId(department.getIntegrationId());
                    String tagAction = app.CreateConsole();
                    if (exists != null) {
                        department.setId(exists.getId());
                        tagAction = app.UpdateConsole();
                    }

                    util.printData(tag,
                            count[0],
                            tagAction.concat((department != null ? department.getName() != "" ? department.getName() : app.Name() : app.Name())),
                            (company != null ? company.getName() != "" ? company.getName() : app.Company() : app.Company()));

                    departmentService.save(department);
                    count[0] = count[0] + 1;
                } catch (JsonProcessingException e) {
                    System.out.println(tag.concat("Exception (I) : ").concat(String.valueOf(e)));
                }
            });

            apiResponse = mapper.readValue(result, APIResponse.class);

            APIExecutionStatus status = new APIExecutionStatus();
            status.setUri(endPointSN.Department());
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
        return snDepartments;
    }
}


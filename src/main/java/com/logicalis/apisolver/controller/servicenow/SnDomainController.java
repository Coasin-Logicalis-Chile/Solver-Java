
package com.logicalis.apisolver.controller.servicenow;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.logicalis.apisolver.model.APIExecutionStatus;
import com.logicalis.apisolver.model.APIResponse;
import com.logicalis.apisolver.model.Domain;
import com.logicalis.apisolver.model.enums.App;
import com.logicalis.apisolver.model.enums.EndPointSN;
import com.logicalis.apisolver.model.servicenow.SnDomain;
import com.logicalis.apisolver.services.IAPIExecutionStatusService;
import com.logicalis.apisolver.services.IDomainService;
import com.logicalis.apisolver.services.servicenow.ISnDomainService;
import com.logicalis.apisolver.util.Rest;
import com.logicalis.apisolver.util.Util;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class SnDomainController {

    @Autowired
    private ISnDomainService snDomainService;
    @Autowired
    private IDomainService domainService;
    @Autowired
    private IAPIExecutionStatusService statusService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private Rest rest;

    App app = new App();
    EndPointSN endPointSN = new EndPointSN();

    @GetMapping("/sn_domains")
    public List<SnDomain> show() {

        System.out.println(app.Start());
        APIResponse apiResponse = null;
        List<SnDomain> snDomains = new ArrayList<>();
        long startTime = 0;
        long endTime = 0;
        String tag = "[Domain] ";
        try {
            startTime = System.currentTimeMillis();
            String result = rest.responseByEndPoint(endPointSN.Domain());
            endTime = (System.currentTimeMillis() - startTime);
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JSONParser parser = new JSONParser();
            JSONObject resultJson = (JSONObject) parser.parse(result);
            JSONArray ListSnDomainJson = new JSONArray();
            if (resultJson.get("result") != null)
                ListSnDomainJson = (JSONArray) parser.parse(resultJson.get("result").toString());
            final int[] count = {1};
            ListSnDomainJson.forEach(snDomainJson -> {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                try {
                    SnDomain snDomain = objectMapper.readValue(snDomainJson.toString(), SnDomain.class);
                    snDomains.add(snDomain);
                    Domain domain = new Domain();
                    domain.setActive(snDomain.isActive());
                    domain.setName(snDomain.getName());
                    domain.setIntegrationId(snDomain.getSys_id());

                    Domain exists = domainService.findByIntegrationId(domain.getIntegrationId());
                    String tagAction = app.CreateConsole();
                    if (exists != null) {
                        domain.setId(exists.getId());
                        tagAction = app.UpdateConsole();
                    }
                    Util.printData(tag, count[0], tagAction.concat(domain != null ? domain.getName() != "" ? domain.getName() : app.Name() : app.Name()));
                    domainService.save(domain);
                    count[0] = count[0] + 1;
                } catch (JsonProcessingException e) {
                    System.out.println(tag.concat("Exception (I) : ").concat(String.valueOf(e)));
                }
            });

            apiResponse = mapper.readValue(result, APIResponse.class);

            APIExecutionStatus status = new APIExecutionStatus();
            status.setUri(endPointSN.Domain());
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
        return snDomains;
    }

}


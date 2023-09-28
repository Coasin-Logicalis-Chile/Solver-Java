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
import com.logicalis.apisolver.util.Rest;
import com.logicalis.apisolver.util.Util;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@Slf4j
public class SnDomainController {
    @Autowired
    private IDomainService domainService;
    @Autowired
    private IAPIExecutionStatusService statusService;

    @Autowired
    private Rest rest;

    @GetMapping("/sn_domains")
    public List<SnDomain> show() {
        log.info(App.Start());
        APIResponse apiResponse = null;
        List<SnDomain> snDomains = new ArrayList<>();
        long startTime = 0;
        long endTime = 0;
        String tag = "[Domain] ";
        try {
            startTime = System.currentTimeMillis();
            String result = rest.responseByEndPoint(EndPointSN.Domain());
            endTime = (System.currentTimeMillis() - startTime);
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JSONParser parser = new JSONParser();
            JSONObject resultJson = (JSONObject) parser.parse(result);
            JSONArray ListSnDomainJson = new JSONArray();
            final Domain[] domain = new Domain[1];
            final SnDomain[] snDomain = new SnDomain[1];
            final Domain[] exists = new Domain[1];
            final String[] tagAction = new String[1];
            APIExecutionStatus status = new APIExecutionStatus();
            if (resultJson.get("result") != null)
                ListSnDomainJson = (JSONArray) parser.parse(resultJson.get("result").toString());
            final int[] count = {1};
            ListSnDomainJson.forEach(snDomainJson -> {
                try {
                    snDomain[0] = mapper.readValue(snDomainJson.toString(), SnDomain.class);
                    snDomains.add(snDomain[0]);
                    domain[0] = new Domain();
                    domain[0].setActive(snDomain[0].isActive());
                    domain[0].setName(snDomain[0].getName());
                    domain[0].setIntegrationId(snDomain[0].getSys_id());
                    exists[0] = domainService.findByIntegrationId(domain[0].getIntegrationId());
                    tagAction[0] = App.CreateConsole();
                    if (exists[0] != null) {
                        domain[0].setId(exists[0].getId());
                        tagAction[0] = App.UpdateConsole();
                    }
                    Util.printData(tag, count[0], tagAction[0].concat(domain[0] != null ? domain[0].getName() != "" ? domain[0].getName() : App.Name() : App.Name()));
                    domainService.save(domain[0]);
                    count[0] = count[0] + 1;
                } catch (JsonProcessingException e) {
                    log.error(tag.concat("Exception (I) : ").concat(String.valueOf(e)));
                }
            });
            apiResponse = mapper.readValue(result, APIResponse.class);
            status.setUri(EndPointSN.Domain());
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
        return snDomains;
    }
}

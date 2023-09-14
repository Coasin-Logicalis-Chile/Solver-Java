package com.logicalis.apisolver.conf;

import com.logicalis.apisolver.util.Rest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ApiSolverConfig {
    @Bean("solverRestTemplate")
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean("rest")
    public Rest rest() {
        return new Rest();
    }
}

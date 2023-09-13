package com.logicalis.apisolver.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ApiSolverConfig {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

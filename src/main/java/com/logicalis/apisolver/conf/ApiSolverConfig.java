package com.logicalis.apisolver.conf;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import com.logicalis.apisolver.util.Rest;

@Configuration
public class ApiSolverConfig {

    // Estas claves deben existir en application.properties
    // app.servicenow.user=solver
    // app.servicenow.password=<PASSWORD_EN_BASE64>
    @Value("${app.servicenow.user:}")
    private String snUser;

    @Value("${app.servicenow.password:}")
    private String snPasswordBase64;

    @Bean("solverRestTemplate")
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        // Decodifica Base64 (si falla, usa el valor tal cual)
        String decodedPass = decodeBase64OrReturn(snPasswordBase64);

        if (!StringUtils.hasText(snUser) || !StringUtils.hasText(decodedPass)) {
            throw new IllegalStateException(
                "Faltan credenciales de ServiceNow: app.servicenow.user / app.servicenow.password");
        }

        return builder
            .setConnectTimeout(Duration.ofSeconds(5))
            .setReadTimeout(Duration.ofSeconds(10))
            .basicAuthentication(snUser, decodedPass)
            .build();
    }

    private String decodeBase64OrReturn(String value) {
        if (!StringUtils.hasText(value)) return value;
        try {
            byte[] dec = Base64.getDecoder().decode(value);
            return new String(dec, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException ex) {
            return value;
        }
    }

    @Bean("rest")
    public Rest rest() {
        // Si tu clase Rest autowirea un RestTemplate, asegúrate de usar @Qualifier("solverRestTemplate") allí.
        return new Rest();
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}

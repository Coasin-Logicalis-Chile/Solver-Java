package com.logicalis.apisolver.controller;
import com.logicalis.apisolver.model.AzureAdConfig;
import com.logicalis.apisolver.services.IAzureAdConfigService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Value;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
public class AzureAdRedirectController {

    private final IAzureAdConfigService azureAdConfigService;

    @Value("${app.redirect.base-url}")
    private String baseUrl;

    public AzureAdRedirectController(IAzureAdConfigService azureAdConfigService) {
        this.azureAdConfigService = azureAdConfigService;
    }

    @GetMapping("/oauth2/redirect")
    public void redirectToAzureAd(@RequestParam String suffix, HttpServletResponse response) throws IOException {
        // Buscar configuración usando el servicio
        AzureAdConfig config = azureAdConfigService.findBySuffix(suffix);

        if (config == null) {
            throw new IllegalArgumentException("No se encontró configuración para el suffix: " + suffix);
        }

        String redirectUri = String.format("%s/login/oauth2/code/azure", baseUrl);

        // Construir la URL de autorización
        String authorizationUri = String.format(
            "https://login.microsoftonline.com/%s/oauth2/v2.0/authorize"
                + "?client_id=%s"
                + "&response_type=code"
                + "&redirect_uri=%s"
                + "&response_mode=query"
                + "&scope=openid%%20profile%%20email"
                + "&state=%s",
            config.getTenantId(),
            config.getClientId(),
            redirectUri,
            suffix
        );

        // Redirigir al usuario al endpoint de autorización
        response.sendRedirect(authorizationUri);
    }
}


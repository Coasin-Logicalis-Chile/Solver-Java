package com.logicalis.apisolver.controller;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.logicalis.apisolver.model.AzureAdConfig;
import com.logicalis.apisolver.services.IAzureAdConfigService;


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

    @PostMapping("/auth/login-ad")
public ResponseEntity<?> loginAD(@RequestBody Map<String, String> body) {
    System.out.println("ENTRO AL LOGIN DE AUTH");
    String email = body.get("email");
    String password = "logicalis$ad"; // hay que pasarlo a application.properties

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    headers.setBasicAuth("angularapp", "12345");

     try {

    String form = "grant_type=password" +
            "&username=" + URLEncoder.encode(email, "UTF-8") +
            "&password=" + URLEncoder.encode(password, "UTF-8");

    HttpEntity<String> request = new HttpEntity<>(form, headers);

    RestTemplate restTemplate = new RestTemplate();

        String tokenUrl = baseUrl + "/oauth/token";
        ResponseEntity<String> response = restTemplate.postForEntity(tokenUrl, request, String.class);
        return ResponseEntity.ok(response.getBody());

      } catch (UnsupportedEncodingException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error de codificación");
    } catch (HttpClientErrorException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login con AD falló");
    }
}

}


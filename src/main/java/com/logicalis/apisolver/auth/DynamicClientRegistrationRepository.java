package com.logicalis.apisolver.auth;

import com.logicalis.apisolver.model.AzureAdConfig;
import com.logicalis.apisolver.services.IAzureAdConfigService;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.context.annotation.ScopedProxyMode;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;

@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class DynamicClientRegistrationRepository implements ClientRegistrationRepository {

    private final IAzureAdConfigService azureAdConfigService;
    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final CustomAuthenticationSuccessHandler successHandler;

    public DynamicClientRegistrationRepository(IAzureAdConfigService azureAdConfigService,
                                               HttpServletRequest request,
                                               HttpServletResponse response,
                                               CustomAuthenticationSuccessHandler successHandler) {
        this.azureAdConfigService = azureAdConfigService;
        this.request = request;
        this.response = response;
        this.successHandler = successHandler;
    }

    @Override
public ClientRegistration findByRegistrationId(String registrationId) {
    // Obtén el suffix solo en la solicitud inicial
    //String suffix = (String) request.getAttribute("suffix");
     HttpSession session = request.getSession(false);
    String suffix = (session != null) ? (String) session.getAttribute("suffix") : null;
    // Caso: El suffix no está disponible (solicitud posterior)
    if (suffix == null) {
        // Verifica si el usuario ya está autenticado
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            try {
                // Redirige al successHandler
                successHandler.onAuthenticationSuccess(request, response, authentication);
                return null; // Salimos del flujo
            } catch (IOException e) {
                throw new RuntimeException("Error al redirigir al SuccessHandler", e);
            }
        }
        throw new IllegalStateException("No se encontró el parámetro 'suffix' ni el usuario está autenticado.");
    }

    // Caso: Resolución dinámica para la solicitud inicial
    AzureAdConfig config = azureAdConfigService.findBySuffix(suffix);
    if (config == null) {
        throw new IllegalArgumentException("No se encontró configuración para el suffix: " + suffix);
    }

    return ClientRegistration.withRegistrationId(registrationId)
            .clientId(config.getClientId())
            .clientSecret(config.getClientSecret())
            .authorizationUri("https://login.microsoftonline.com/" + config.getTenantId() + "/oauth2/v2.0/authorize")
            .tokenUri("https://login.microsoftonline.com/" + config.getTenantId() + "/oauth2/v2.0/token")
            .jwkSetUri("https://login.microsoftonline.com/" + config.getTenantId() + "/discovery/v2.0/keys")
            .redirectUriTemplate("{baseUrl}/login/oauth2/code/{registrationId}")
            .scope("openid", "profile", "email")
            .authorizationGrantType(org.springframework.security.oauth2.core.AuthorizationGrantType.AUTHORIZATION_CODE)
            .build();
}

}

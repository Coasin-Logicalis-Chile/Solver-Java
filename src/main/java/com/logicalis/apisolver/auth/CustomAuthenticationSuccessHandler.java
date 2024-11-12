package com.logicalis.apisolver.auth;


import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        // Obtiene el usuario autenticado como OidcUser
        OidcUser oidcUser = (OidcUser) authentication.getPrincipal();

        // Extrae los claims del OidcUser
        Map<String, Object> claims = oidcUser.getClaims();

        // Obtén el correo electrónico del usuario
        String email = (String) claims.get("email");
        if (email == null) {
            email = (String) claims.get("preferred_username");
        }
        response.sendRedirect("http://localhost:4200/#/sign-in?email=" + email);
    }
}

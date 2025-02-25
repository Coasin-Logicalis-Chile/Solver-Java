package com.logicalis.apisolver.auth;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.web.filter.OncePerRequestFilter;

public class DynamicRegistrationIdFilter extends OncePerRequestFilter {

   @Override
protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
    if (request.getRequestURI().startsWith("/oauth2/authorization") && 
        request.getUserPrincipal() == null) { // Solo actúa para solicitudes iniciales
        String suffix = request.getParameter("suffix");
        if (suffix != null) {
            request.getSession().setAttribute("suffix", suffix); // Almacena el suffix en la sesión
        }
    }

    // Continúa con la cadena de filtros
    filterChain.doFilter(request, response);
}

}

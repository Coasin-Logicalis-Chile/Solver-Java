package com.logicalis.apisolver.auth;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class BearerTokenPresenceFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        // Verifica si el encabezado Authorization está presente y contiene un token
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // Si no hay token, se retorna un error 403 Forbidden
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: Bearer token required");
            return;
        }

        // Si el encabezado Authorization está presente y es válido, continua con el siguiente filtro
        filterChain.doFilter(request, response);
    }
}

package com.logicalis.apisolver.auth;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final TokenStore tokenStore;

    public TokenAuthenticationFilter(TokenStore tokenStore) {
        this.tokenStore = tokenStore;
    }

    @Override
protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws ServletException, IOException {

    String header = request.getHeader("Authorization");

    if (header != null && header.startsWith("Bearer ")) {
        String tokenValue = header.substring(7).trim();

        OAuth2AccessToken token = tokenStore.readAccessToken(tokenValue);

        // Si no existe o está expirado => 401 y cortar la cadena
        if (token == null || token.isExpired()) {
            // Limpia el contexto por si acaso
            SecurityContextHolder.clearContext();

            // (Opcional) eliminar del store
            if (token != null) {
                try { tokenStore.removeAccessToken(token); } catch (Exception ignored) {}
            }

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            // También puedes escribir un JSON de error si quieres
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"invalid_or_expired_token\"}");
            return; // *** clave: NO seguir la cadena ***
        }

        // Token válido -> autenticar
        String username = (String) token.getAdditionalInformation().get("user_name");
        @SuppressWarnings("unchecked")
        Map<String, Object> claims = token.getAdditionalInformation();

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList());
        authentication.setDetails(claims);

        SecurityContextHolder.getContext().setAuthentication(authentication);
    } else {
        // Sin Authorization header -> 401 directamente si tu API es estricta
        // Puedes dejar pasar y que lo maneje la config HTTP; o responder 401 aquí:
        // response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        // return;
    }

    chain.doFilter(request, response);
}

}

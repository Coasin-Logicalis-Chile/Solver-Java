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
            String tokenValue = header.substring(7); // Extrae el token del encabezado
            OAuth2AccessToken token = tokenStore.readAccessToken(tokenValue);

            if (token != null && !token.isExpired()) {
                String username = (String) token.getAdditionalInformation().get("user_name");

                @SuppressWarnings("unchecked")
                Map<String, Object> claims = token.getAdditionalInformation(); // aquí suele venir "company"

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username,
                        null, Collections.emptyList());
                authentication.setDetails(claims); // <-- guarda claims aquí

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        // Continúa con la cadena de filtros
        chain.doFilter(request, response);
    }
}

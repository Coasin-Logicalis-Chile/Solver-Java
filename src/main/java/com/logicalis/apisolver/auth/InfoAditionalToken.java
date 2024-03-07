package com.logicalis.apisolver.auth;

import com.logicalis.apisolver.model.SysUser;

import com.logicalis.apisolver.services.ISysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class InfoAditionalToken implements TokenEnhancer {

    @Autowired
    private ISysUserService sysUserService;


    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken oAuth2AccessToken, OAuth2Authentication oAuth2Authentication) {
        // SysUserFields userFields = sysUserService.findByEmailAndSolver(oAuth2Authentication.getName());

        SysUser  user  = sysUserService.findByEmailAndSolver(oAuth2Authentication.getName(), true);

        if (user.getCompany().getId() == 14 && user.getCompany().getPasswordExpirationDays() != null && user.isPasswordExpired(user.getCompany().getPasswordExpirationDays())) {

            log.info("Password expiration, please change password");

            Map<String, Object> errorInfo = new HashMap<>();
            errorInfo.put("error_descripcion", "Password expiration, change password");
            errorInfo.put("email", oAuth2Authentication.getName());
            errorInfo.put("user", user.getId());
            errorInfo.put("name", user.getName());
            errorInfo.put("integrationId", user.getIntegrationId());
            errorInfo.put("company", user.getCompany());

            DefaultOAuth2AccessToken errorToken = new DefaultOAuth2AccessToken("error");
            errorToken.setAdditionalInformation(errorInfo);
            return errorToken;
        }

        log.info("========================== User Authentication Info ==========================");

        Map<String, Object> info = new HashMap<>();
        info.put("email", oAuth2Authentication.getName());
        info.put("user", user.getId());
        info.put("name", user.getName());
        info.put("integrationId", user.getIntegrationId());
        info.put("company", user.getCompany());

        ((DefaultOAuth2AccessToken) oAuth2AccessToken).setAdditionalInformation(info);

        log.info("email: "+ oAuth2Authentication.getName());
        log.info("user: "+ user.getId());
        log.info("name: "+ user.getName());
        log.info("integrationId: "+ user.getIntegrationId());
        log.info("company: "+ user.getCompany().getName());
        log.info("Access successful, token allowed: oAuth2AccessToken");

        log.info("=================================================================");


        return oAuth2AccessToken;
    }
}

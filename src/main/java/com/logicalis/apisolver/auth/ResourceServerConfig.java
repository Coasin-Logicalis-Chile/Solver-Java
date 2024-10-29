package com.logicalis.apisolver.auth;


import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@Configuration
@EnableResourceServer
@Order(2)
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    @Override
    public void configure(HttpSecurity http) throws Exception {
        // AQUI VAN LAS URL PUBLICAS
        http.authorizeRequests().antMatchers(HttpMethod.GET,
                        "/api/v1/getUsers",
                        "/api/v1/sn_sc_request_item",
                        "/api/v1/sn_domains",
                        "/api/v1/domains",
                        "/api/v1/sn_incidents",
                        "/api/v1/incidents",
                        "/api/v1/sn_groups",
                        "/api/v1/sysGroups",
                        "/api/v1/sn_companies",
                        "/api/v1/companies",
                        "/api/v1/sn_locations",
                        "/api/v1/locations",
                        "/api/v1/sn_sys_users",
                        "/api/v1/sysUsers",
                        "/api/v1/sn_departments",
                        "/api/v1/departments",
                        "/api/v1/sn_choices",
                        "/api/v1/choices",
                        "/api/v1/sn_incidents",
                        "/api/v1/incidents",
                        "/api/v1/sn_configuration_items",
                        "/api/v1/configuration_items",
                        "/api/v1/sn_ci_services",
                        "/api/v1/ci_services",
                        "/api/v1/incidentsByFilter",
                        "/api/v1/findPaginatedIncidentsByFilters",
                        "/api/v1/sn_journals",
                        "/api/v1/journalsByIncident",
                        "/api/v1/journalsByIncident/{id}",
                        "/api/v1/journalsByIncident/",
                        "/api/v1/journalsBySolver",
                        "/api/v1/sysUser/{id}",
                        "/api/v1/countIncidentsByFilters",
                        "/api/v1/sn_catalogs",
                        "/api/v1/sn_sc_category_items",
                        "/api/v1/sn_sc_categories",
                        "/api/v1/sn_requests_by_solver",
                        "/api/v1/sn_choices_by_filter",
                        "/api/v1/findSysGroupsByFilters",
                        "/api/v1/sn_sys_user_groups",
                        "/api/v1/findUserGroupsByFilters",
                        "/api/v1/sn_catalogs_line",
                        "/api/v1/sn_choices_incidents",
                        "/api/v1/snAttachment/{id}",
                        "/api/v1/snAttachment",
                        "/api/v1/attachments/{integrationId}",
                        "/api/v1/attachmentsByIncident/{incident}",
                        "/api/v1/journalsInfoByIncident/{id}",
                        "/api/v1/findPaginatedScRequestsByFilters",
                        "/api/v1/cmnSchedulesSN",
                        "/api/v1/contractsSlaSN",
                        "/api/v1/taskSlasBySolver",
                        "/api/v1/incidentDelete",
                        "/api/v1/incidenByCompany",
                        "/api/v1/scRequestByCompany",
                        "/api/v1/scRequestItemByCompany",
                        "/api/v1/scTaskByCompany",
                        "/api/v1/sn_sc_request_item_by_solver",
                        "/api/v1/findByElement/{integrationId}",
                        "/api/v1/findSolverByElement/{integrationId}",
                        "/api/v1/findBySolver",
                        "/api/v1/journalsSNByElement",
                        "/api/v1/incidenBySolver",
                        "/api/v1/scTaskBySolver",
                        "/api/v1/sysAuditsBySolver",
                        "/api/v1/sysDocumentationsBySolver",
                        "/api/v1/sysUserByEmailAndSolver",
                        "/api/v1/sysUserByEmailAndSolverAndCode",
                        "/api/v1/sendResetPassword/{name}/{phone}/{email}/{code}").permitAll()
                .antMatchers(HttpMethod.POST,
                        "/api/v1/log",
                        "/api/v1/sysUserByEmailAndSolver",
                        "/api/v1/uploadFile/{element}").permitAll()
                .antMatchers(HttpMethod.PUT,
                        "/api/v1/resetPassword",
                        "/api/v1/resetPassword/{id}",
                        "/api/v1/sysUser/{id}",
                        "/api/v1/sysUserCode/{id}",
                        "/api/v1/sysUserCodeUpdate/{id}").permitAll()
                .anyRequest().authenticated()
                .and().cors().configurationSource(corsConfigurationSource());
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:8084", "http://localhost:6050","http://localhost:4200", "*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(Arrays.asList("Content-Type", "Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);


        return source;
    }

    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilter() {
        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<CorsFilter>(new CorsFilter(corsConfigurationSource()));
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }
}


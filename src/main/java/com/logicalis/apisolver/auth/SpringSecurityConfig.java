package com.logicalis.apisolver.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@EnableGlobalMethodSecurity(securedEnabled = true)
@Configuration
@EnableWebSecurity
@Order(1)
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userService;

    @Autowired
    private CustomAuthenticationSuccessHandler successHandler;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    @Autowired
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(this.userService).passwordEncoder(passwordEncoder());
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/api-docs", "/configuration/ui", "/swagger-resources/**",
                "/configuration/security", "/swagger-ui.html", "/webjars/**");
    }

    @Bean
    @Override
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
        .addFilterBefore(new BearerTokenPresenceFilter(), UsernamePasswordAuthenticationFilter.class)
        .cors().configurationSource(corsConfigurationSource())
        .and()
        .csrf().disable()
            //.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .authorizeRequests()
                // Configura las URLs públicas
                .antMatchers(HttpMethod.GET, 
                    "/api/v1/getUsers", "/api/v1/sn_sc_request_item", "/api/v1/sn_domains", 
                    "/api/v1/domains", "/api/v1/sn_incidents", "/api/v1/incidents", 
                    "/api/v1/sn_groups", "/api/v1/sysGroups", "/api/v1/sn_companies", 
                    "/api/v1/companies", "/api/v1/sn_locations", "/api/v1/locations",
                    "/api/v1/sn_sys_users", "/api/v1/sysUsers", "/api/v1/sn_departments",
                    "/api/v1/departments", "/api/v1/sn_choices", "/api/v1/choices",
                    "/api/v1/incidentsByFilter", "/api/v1/findPaginatedIncidentsByFilters",
                    "/api/v1/journalsByIncident", "/api/v1/countIncidentsByFilters",
                    "/api/v1/sn_catalogs", "/api/v1/findSysGroupsByFilters", 
                    "/api/v1/sn_choices_by_filter", "/api/v1/findUserGroupsByFilters", "/oauth/token")
                .permitAll()
                .antMatchers(HttpMethod.POST, "/api/v1/log", "/api/v1/sysUserByEmailAndSolver", "/api/v1/uploadFile/{element}")
                .permitAll()
                .antMatchers(HttpMethod.PUT, "/api/v1/resetPassword", "/api/v1/sysUser/{id}")
                .permitAll()
                .antMatchers("/oauth2/**", "/login/**").permitAll()
                .anyRequest().access("isAuthenticated() or hasAuthority('ROLE_ANONYMOUS')")
                .and()
                .oauth2Login()
                .successHandler(successHandler);
    }

    // Configuración de CORS
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:8084", "http://localhost:6050", "http://localhost:4200", "*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(Arrays.asList("Content-Type", "Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilter() {
        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(corsConfigurationSource()));
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }
}

package com.afb.pos_backend.config.security;

import com.afb.pos_backend.config.security.filter.JwtSecurityFilter;
import com.afb.pos_backend.config.security.handler.CustomAccessDeniedHandler;
import com.afb.pos_backend.config.security.handler.CustomAuthenticationEntryPoint;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@AllArgsConstructor
public class HttpSecurityConfig {

    private final JwtSecurityFilter securityFilter;
    private final CustomAuthenticationEntryPoint entryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(AbstractHttpConfigurer::disable)
                .cors(corsConfig -> {
                })
                .sessionManagement(sessionConfig -> sessionConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(authRequestConfig -> {
                    authRequestConfig.requestMatchers(HttpMethod.POST, "/v1/auth/**").permitAll();
                    authRequestConfig.requestMatchers(HttpMethod.GET, "/").permitAll();
                    authRequestConfig.requestMatchers(HttpMethod.GET, "/logo.png").permitAll();
                    //authRequestConfig.requestMatchers(HttpMethod.POST, "/v1/users").permitAll();
                    authRequestConfig.anyRequest().authenticated();
                })
                .exceptionHandling(exceptionConfig -> {
                    exceptionConfig.authenticationEntryPoint(entryPoint);
                    exceptionConfig.accessDeniedHandler(accessDeniedHandler);
                })
                .build();
    }

}

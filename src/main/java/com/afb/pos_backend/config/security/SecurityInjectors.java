package com.afb.pos_backend.config.security;

import com.afb.pos_backend.config.exception.model.NotFoundException;
import com.afb.pos_backend.common.constant.MessageConstant;
import com.afb.pos_backend.user.persistence.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@AllArgsConstructor
public class SecurityInjectors {

    private final UserRepository repository;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return (username -> this.repository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException(String.format(MessageConstant.NOT_FOUND_ERROR, "nombre de usuario:" + username))));
    }

}

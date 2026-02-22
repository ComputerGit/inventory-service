package com.at.t.ecommerce.inventory.infrastructure.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 1. Disable CSRF (Crucial for REST APIs so POST requests aren't blocked)
            .csrf(AbstractHttpConfigurer::disable) 
            
            // 2. Disable default Basic Auth popups
            .httpBasic(AbstractHttpConfigurer::disable)
            
            // 3. Open up the routing
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/**").permitAll()      // Allow Postman/UI to hit our endpoints
                .requestMatchers("/actuator/**").permitAll() // Allow health checks to pass
                .anyRequest().authenticated()                // Lock down anything else we forgot
            );

        return http.build();
    }
}
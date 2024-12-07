package com.example.demo.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Nonaktifkan CSRF untuk testing
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/login", "/signup").permitAll() // Izinkan akses tanpa autentikasi
                .anyRequest().authenticated() // Semua request lain memerlukan autentikasi
            )
            .formLogin(form -> form.disable()) // Nonaktifkan form login bawaan
            .httpBasic(basic -> basic.disable()); // Nonaktifkan HTTP Basic

        return http.build();
    }
}
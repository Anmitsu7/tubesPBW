package com.example.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.Set;
import java.util.stream.Collectors;

import com.example.demo.service.UserService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

   @Autowired
   private UserService userService;

   @Bean
   public BCryptPasswordEncoder passwordEncoder() {
       return new BCryptPasswordEncoder();
   }

   @Bean
   public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
       return authConfig.getAuthenticationManager();
   }

   @Bean
   public DaoAuthenticationProvider authenticationProvider() {
       DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
       authProvider.setUserDetailsService(userService);
       authProvider.setPasswordEncoder(passwordEncoder());
       return authProvider;
   }

   @Bean
   public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
       http
               .authenticationProvider(authenticationProvider()) // Menambahkan authenticationProvider
               .csrf(csrf -> csrf.disable())
               .authorizeHttpRequests(auth -> auth
                       // Public URLs
                       .requestMatchers(
                           "/",
                           "/home",
                           "/about",
                           "/explore",
                           "/register",
                           "/register-form",
                           "/login",
                           "/login-error",
                           "/error",
                           "/api/public/**",
                           "/homepage-authenticated"
                       ).permitAll()
                       // Static Resources
                       .requestMatchers(
                           "/css/**",
                           "/js/**",
                           "/images/**",
                           "/webjars/**",
                           "/fonts/**",
                           "/covers/**",
                           "/actors/**"
                       ).permitAll()
                       // Admin URLs
                       .requestMatchers("/admin/**").hasRole("ADMIN")
                       // User URLs
                       .requestMatchers("/user/**", "/profile/**", "/rental/**").hasRole("PELANGGAN")
                       // Any other request
                       .anyRequest().authenticated()
               )
               .formLogin(form -> form
                       .loginPage("/login")
                       .loginProcessingUrl("/login")
                       .successHandler((request, response, authentication) -> {
                           Set<String> roles = authentication.getAuthorities().stream()
                                   .map(r -> r.getAuthority())
                                   .collect(Collectors.toSet());

                           if (roles.contains("ROLE_ADMIN")) {
                               response.sendRedirect("/admin/dashboard");
                           } else {
                               response.sendRedirect("/homepage-authenticated");
                           }
                       })
                       .failureUrl("/login?error=true")
                       .permitAll()
               )
               .logout(logout -> logout
                       .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                       .logoutSuccessUrl("/home?logout=true")
                       .deleteCookies("JSESSIONID")
                       .invalidateHttpSession(true)
                       .clearAuthentication(true)
                       .permitAll()
               )
               .exceptionHandling(exception -> exception
                       .accessDeniedPage("/error/403")
               )
               .sessionManagement(session -> session
                       .maximumSessions(1)
                       .expiredUrl("/login?expired=true")
               );

       return http.build();
   }
}
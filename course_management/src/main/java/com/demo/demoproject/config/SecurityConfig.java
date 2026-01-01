package com.demo.demoproject.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.demo.demoproject.security.jwt.JwtUtil;
import com.demo.demoproject.security.filter.GlobalRateLimiter;
import com.demo.demoproject.security.filter.SqlInjectionFilter;
import com.demo.demoproject.security.jwt.JwtAuthorizationFilter;

@EnableAspectJAutoProxy(proxyTargetClass = true)
@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    SqlInjectionFilter sqlInjectionFilter;

    @Autowired
    GlobalRateLimiter globalRateLimiter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        JwtAuthorizationFilter jwtFilter = new com.demo.demoproject.security.jwt.JwtAuthorizationFilter(jwtUtil, userDetailsService);
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth.
                requestMatchers("/auth/**").permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(globalRateLimiter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(sqlInjectionFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}

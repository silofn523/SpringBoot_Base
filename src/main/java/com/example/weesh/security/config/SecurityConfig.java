package com.example.weesh.security.config;

import com.example.weesh.core.auth.application.jwt.TokenResolver;
import com.example.weesh.core.auth.application.jwt.TokenStorage;
import com.example.weesh.core.auth.application.jwt.TokenValidator;
import com.example.weesh.core.foundation.log.LoggingUtil;
import com.example.weesh.security.auth.AccessTokenValidationStrategy;
import com.example.weesh.security.auth.AuthenticationContextManager;
import com.example.weesh.security.auth.CustomUserDetailsService;
import com.example.weesh.security.auth.RefreshTokenValidationStrategy;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {
    private final TokenResolver tokenResolver;
    private final TokenValidator tokenValidator;
    private final TokenStorage tokenStorage;
    private final CustomUserDetailsService userDetailsService;
    private final PathValidator pathValidator;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        LoggingUtil.debug("Configuring SecurityFilterChain");
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(pathValidator.getPublicPaths().toArray(new String[0])).permitAll()
                        .requestMatchers(pathValidator.getRefreshTokenAllowedPaths().toArray(new String[0])).permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        LoggingUtil.debug("Configuring WebSecurityCustomizer");
        return (web) -> web.ignoring().requestMatchers(pathValidator.getPublicPaths().toArray(new String[0]));
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(
                pathValidator,
                new AccessTokenValidationStrategy(tokenValidator, tokenResolver, tokenStorage, userDetailsService, new AuthenticationContextManager()),
                new RefreshTokenValidationStrategy(tokenResolver, pathValidator, tokenValidator),
                new AuthenticationContextManager(),
                new ResponseHandler(objectMapper())
        );
    }
}
package com.example.weesh.security.config.core;

import com.example.weesh.core.auth.application.token.TokenResolver;
import com.example.weesh.core.auth.application.token.TokenStorage;
import com.example.weesh.core.auth.application.token.TokenValidator;
import com.example.weesh.core.foundation.log.LoggingUtil;
import com.example.weesh.security.authentication.filter.JwtAuthenticationFilter;
import com.example.weesh.security.authentication.strategy.AccessTokenValidationStrategy;
import com.example.weesh.security.authentication.context.AuthenticationContextManager;
import com.example.weesh.security.authentication.service.CustomUserDetailsService;
import com.example.weesh.security.authentication.strategy.RefreshTokenValidationStrategy;
import com.example.weesh.security.authorization.handler.CustomAccessDeniedHandler;
import com.example.weesh.security.authorization.validator.PathValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

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
    private final CustomAccessDeniedHandler accessDeniedHandler;
    private final ObjectMapper objectMapper;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        LoggingUtil.debug("Configuring SecurityFilterChain");
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(pathValidator.getPublicPaths().toArray(new String[0])).permitAll()
                        .requestMatchers(pathValidator.getRefreshTokenAllowedPaths().toArray(new String[0])).permitAll()
                        .anyRequest().authenticated())
                .exceptionHandling(exception -> exception
                        .accessDeniedHandler(accessDeniedHandler))
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
                new ResponseHandler(objectMapper)
        );
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("http://172.28.2.238:5173");
        configuration.addAllowedOrigin("http://localhost:5173");
        configuration.addAllowedOrigin("http://172.28.6.25:5173");
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
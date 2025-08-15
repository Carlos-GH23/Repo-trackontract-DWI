package com.example.integradora_trackontract.config;

import com.example.integradora_trackontract.auth.repository.Token;
import com.example.integradora_trackontract.auth.repository.TokenRespository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final TokenRespository tokenRespository;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(request -> {
                    var corsConfiguration = new org.springframework.web.cors.CorsConfiguration();
                    corsConfiguration.setAllowedOrigins(java.util.List.of("http://localhost:5173"));
                    corsConfiguration.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    corsConfiguration.setAllowedHeaders(java.util.List.of("*"));
                    corsConfiguration.setAllowCredentials(true);
                    return corsConfiguration;
                }))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(req ->
                        req.requestMatchers("/auth/**").permitAll()
                                .requestMatchers("/contracts/public/**").permitAll()

                                // Permitir que usuarios autenticados accedan a su propio perfil y funcionalidades básicas
                                .requestMatchers("/users/me", "/users/me/**")
                                .authenticated()

                                // Permitir que usuarios autenticados actualicen su propio perfil
                                .requestMatchers("/users/update")
                                .authenticated()

                                // Solo ADMIN y ABOGADO pueden acceder a gestión de usuarios
                                .requestMatchers("/users/**")
                                .hasAnyRole("ADMIN", "ABOGADO")

                                // Permitir que los clientes accedan a su propio perfil (ANTES de la regla general)
                                .requestMatchers("/clients/me", "/clients/me/**")
                                .hasRole("CLIENT")

                                // Solo ADMIN y ABOGADO pueden acceder a gestión de clientes
                                .requestMatchers("/clients/**")
                                .hasAnyRole("ADMIN", "ABOGADO")

                                // Solo ADMIN puede acceder a cualquier ruta /admin/**
                                .requestMatchers("/admin/**")
                                .hasRole("ADMIN")

                                // Permitir que los clientes accedan a sus propios contratos (ANTES de la regla general)
                                .requestMatchers("/contracts/by-client/**")
                                .hasRole("CLIENT")

                                // Permitir que los clientes accedan a sus contratos por email
                                .requestMatchers("/contracts/by-user-email")
                                .hasRole("CLIENT")

                                // Permitir que los clientes descarguen PDFs de sus contratos
                                .requestMatchers("/contracts/*/pdf")
                                .hasRole("CLIENT")

                                // Solo ADMIN y ABOGADO pueden acceder a gestión de contratos (DESPUÉS de la específica)
                                .requestMatchers("/contracts/**")
                                .hasAnyRole("ADMIN", "ABOGADO")

                                // Solo ADMIN puede acceder a gestión de categorías
                                .requestMatchers("/categories/**")
                                .hasRole("ADMIN")

                                // Solo ADMIN puede acceder a Audit_Logs
                                .requestMatchers("/audit-logs/**").hasRole("ADMIN")

                                // Cualquiera autenticado puede acceder al resto
                                .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .logout(logout ->
                        logout.logoutUrl("auth/logout")
                                .addLogoutHandler((request, response, authentication) -> {
                                    final var authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
                                    logout(authHeader);
                                })
                                .logoutSuccessHandler((request, response, authentication) ->
                                        SecurityContextHolder.clearContext())
                );
        return http.build();
    }

    private void logout(final String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Token No Encontrado");
            }

        final String jwtToken = token.substring(7);
        final Token foundToken = tokenRespository.findByToken(jwtToken)
                .orElseThrow(() -> new IllegalArgumentException("Token No Encontrado"));
        foundToken.setExpired(true);
        foundToken.setRevoked(true);
        tokenRespository.save(foundToken);
    }
}

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
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(req ->
                        req.requestMatchers("/auth/**").permitAll()

                                // Solo ADMIN y ABOGADO pueden acceder a gestión de clientes
                                .requestMatchers("/clients/**")
                                .hasAnyRole("ADMIN", "ABOGADO")

                                // Solo ADMIN puede acceder a cualquier ruta /admin/**
                                .requestMatchers("/admin/**")
                                .hasRole("ADMIN")

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

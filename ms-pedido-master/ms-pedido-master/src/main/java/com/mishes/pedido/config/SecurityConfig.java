package com.mishes.pedido.config;

import com.mishes.pedido.security.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(request -> {
                    var corsConfig = new org.springframework.web.cors.CorsConfiguration();
                    corsConfig.setAllowedOrigins(java.util.List.of("*"));
                    corsConfig.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    corsConfig.setAllowedHeaders(java.util.List.of("*"));
                    return corsConfig;
                }))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()

                        // 2. reglas de negocio originales
                        // Solo los usuarios con rol ADMIN pueden Crear, Modificar o Borrar pedidos
                        .requestMatchers(HttpMethod.POST, "/api/pedidos/**").hasAnyAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/pedidos/**").hasAnyAuthority("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/pedidos/**").hasAnyAuthority("ADMIN")

                        // Tanto ADMIN como CLIENTE pueden consultar el listado de pedidos y sus filtros
                        .requestMatchers(HttpMethod.GET, "/api/pedidos/**").hasAnyAuthority("ADMIN", "CLIENTE")

                        // Cualquier otra petición extraña requiere estar autenticado
                        .anyRequest().authenticated()
                )
                // Enganchamos tu filtro JWT personalizado antes del validador por defecto de Spring
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
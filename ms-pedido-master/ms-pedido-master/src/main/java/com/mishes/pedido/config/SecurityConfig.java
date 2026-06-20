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
                        // 1. Rutas Públicas: Swagger abierto para el Gateway
                        .requestMatchers(
                                "/v3/api-docs",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/error" // <-- ¡ESTO EVITA EL FALSO 403!
                        ).permitAll()

                        // 2. Reglas de negocio: Pedidos
                        // ADMIN puede modificar/eliminar
                        .requestMatchers(HttpMethod.POST, "/api/pedidos/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/pedidos/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/pedidos/**").hasAuthority("ADMIN")

                        // ADMIN y CLIENTE pueden consultar
                        .requestMatchers(HttpMethod.GET, "/api/pedidos/**").hasAnyAuthority("ADMIN", "CLIENTE")

                        // 3. Cualquier otra cosa requiere autenticación
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
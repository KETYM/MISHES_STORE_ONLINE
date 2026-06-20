package com.producto.ms_producto.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

//Este es el "guardia" que interceptará las llamadas de Postman para ver si traen el Token.


@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // 1. ESTE BLOQUE: Ignorar filtro para Swagger y API Docs
        String path = request.getRequestURI();
        if (path.contains("/v3/api-docs") || path.contains("/swagger-ui")) {
            filterChain.doFilter(request, response);
            return; // Sale del filtro y permite la petición
        }

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (jwtUtil.validateToken(token)) {
                String username = jwtUtil.getUsernameFromToken(token);

                // 💡 1. Extraemos el rol dinámico y real que viene viajando en el token
                String rol = jwtUtil.getRolFromToken(token);

                // 💡 2. Le asignamos la autoridad real.
                // Como en tu SecurityConfig usas .hasAuthority("ADMIN"), pasamos el rol limpio directamente.
                java.util.List<org.springframework.security.core.authority.SimpleGrantedAuthority> authorities =
                        java.util.List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority(rol));

                org.springframework.security.authentication.UsernamePasswordAuthenticationToken auth =
                        new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(username, null, authorities);

                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }
        filterChain.doFilter(request, response);
    }}
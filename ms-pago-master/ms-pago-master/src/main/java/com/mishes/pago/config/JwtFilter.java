package com.mishes.pago.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    // Se lo pasamos por constructor limpio
    public JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // 1.ESTE BLOQUE: Ignorar filtro para Swagger y API Docs
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
                String rol = jwtUtil.getRolFromToken(token);

                // Creamos las autoridades correspondientes si el rol existe
                List<SimpleGrantedAuthority> authorities = List.of();
                if (rol != null) {
                    authorities = List.of(new SimpleGrantedAuthority(rol));
                }

                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(username, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }
        filterChain.doFilter(request, response);
    }
}
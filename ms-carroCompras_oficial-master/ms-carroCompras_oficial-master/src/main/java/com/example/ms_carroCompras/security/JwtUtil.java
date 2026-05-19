package com.example.ms_carroCompras.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

//generar el token que Postman usará para autenticarse en el endpoint protegido.
// El token se genera con una clave secreta y tiene una fecha de expiración. Además, el JwtUtil también tiene métodos para validar el token y extraer el nombre de usuario del token.


@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    // Método para validar el token (este ya lo tienes)
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Método para extraer el username (este también lo tienes)
    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // 💡 2. Tu nuevo método definitivo para extraer el rol corregido
    public String getRolFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("rol", String.class);
    }
}
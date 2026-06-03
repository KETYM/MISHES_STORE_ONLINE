package com.mishes.pedido.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;

@Component
public class JwtUtil {

    // 💡 1. Inyección de la clave secreta desde las propiedades (application.properties o application.yml)
    @Value("${jwt.secret}")
    private String secret;

    // 💡 2. Método estándar para validar la firma y expiración del token
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

    // 💡 3. Método para extraer el nombre de usuario (Subject) del token
    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // 💡 4. Método definitivo y robusto para extraer y normalizar el rol del token
    public String getRolFromToken(String token) {
        var claims = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseClaimsJws(token)
                .getBody();

        Object rolesObject = null;

        // Buscamos secuencialmente en las llaves más comunes de la industria
        if (claims.get("roles") != null) {
            rolesObject = claims.get("roles");
        } else if (claims.get("rol") != null) {
            rolesObject = claims.get("rol");
        } else if (claims.get("authorities") != null) {
            rolesObject = claims.get("authorities");
        }

        if (rolesObject == null) {
            return "INVITADO"; // Valor por defecto seguro si el token no trae roles
        }

        String rolString = rolesObject.toString();

        // Limpieza de caracteres por si viene empaquetado como lista: [ADMIN] o [ROLE_ADMIN]
        rolString = rolString.replace("[", "").replace("]", "").trim();

        // Remoción del prefijo "ROLE_" automático de Spring Security si existiera
        if (rolString.startsWith("ROLE_")) {
            rolString = rolString.substring(5);
        }

        // Retorna el rol perfectamente limpio y en mayúsculas (Ej: "ADMIN")
        return rolString.toUpperCase();
    }
}
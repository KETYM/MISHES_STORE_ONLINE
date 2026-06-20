package com.mishes.pago.config;

import feign.RequestInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor requestTokenBearerInterceptor() {
        return requestTemplate -> {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String authorizationHeader = request.getHeader("Authorization");

                // 🔑 Si tu Postman trae el token Bearer, se lo clonamos a la llamada interna de Feign
                if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                    requestTemplate.header("Authorization", authorizationHeader);
                }
            }
        };
    }
}
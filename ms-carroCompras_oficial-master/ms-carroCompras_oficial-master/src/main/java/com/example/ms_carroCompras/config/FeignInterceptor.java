package com.example.ms_carroCompras.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class FeignInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        // 🕵️‍♀️ Capturamos la petición que llegó originalmente desde Postman al Carro
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            String authHeader = request.getHeader("Authorization");

            // 🔑 Si la petición traía un Token, se lo clonamos automáticamente a la llamada de Feign hacia productos
            if (authHeader != null) {
                template.header("Authorization", authHeader);
            }
        }
    }
}
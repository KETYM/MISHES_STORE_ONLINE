package com.example.ms_carroCompras.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-producto",url = "http://localhost:8081/api/v1/productos")
public interface ProductoClient {

    @GetMapping("/{id}")
    Object buscarPorId(@PathVariable("id") Long id);
}

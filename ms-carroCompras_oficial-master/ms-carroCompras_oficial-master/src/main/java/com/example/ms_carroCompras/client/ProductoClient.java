package com.example.ms_carroCompras.client;

import com.example.ms_carroCompras.dto.LibroResponseDTO; // 💡 Importamos tu nuevo DTO
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-producto", url = "http://localhost:8085/api/productos")
public interface ProductoClient {

    // 💡 Feign mapeará el JSON automáticamente dentro de tu clase espejo sin caerse
    @GetMapping("/{id}")
    LibroResponseDTO buscarPorId(@PathVariable("id") Long id);
}
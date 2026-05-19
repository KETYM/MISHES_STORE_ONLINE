package com.mishes.pedido.client;

import com.mishes.pedido.dto.ProductoResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "mishes-producto", url = "${param.productos.url}")
public interface ProductoClient {

    @GetMapping("api/productos/{id}")
    ProductoResponseDTO obtenerPorId(@PathVariable Long id);

}

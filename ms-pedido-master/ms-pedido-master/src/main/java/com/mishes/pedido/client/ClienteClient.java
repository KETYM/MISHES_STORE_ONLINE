package com.mishes.pedido.client;

import com.mishes.pedido.dto.ClienteResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "mishes-cliente", url = "${param.clientes.url}")
public interface ClienteClient {

    @GetMapping("api/clientes/{id}")
    ClienteResponseDTO obtenerPorId(@PathVariable Long id);

}

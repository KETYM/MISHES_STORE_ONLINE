package com.mishes.pago.client;

import com.mishes.pago.dto.ClienteResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-usuario", url = "http://localhost:8081", configuration = com.mishes.pago.config.FeignConfig.class)
public interface ClienteClient {

    @GetMapping("api/clientes/{id}")
    ClienteResponseDTO obtenerPorId(@PathVariable Long id);

}

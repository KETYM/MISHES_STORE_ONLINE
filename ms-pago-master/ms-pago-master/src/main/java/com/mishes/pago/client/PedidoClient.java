package com.mishes.pago.client;

import com.mishes.pago.dto.PedidoResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "mishes-pedido", url = "${param.pedidos.url}")
public interface PedidoClient {

    @GetMapping("api/pedidos/{id}")
    PedidoResponseDTO obtenerPorId(@PathVariable Long id);

}

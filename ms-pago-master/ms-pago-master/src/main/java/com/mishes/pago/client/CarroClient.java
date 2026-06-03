package com.mishes.pago.client;

import com.mishes.pago.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;

// 💡 Apunta directo al microservicio de Carro de Compras en el puerto 8086
@FeignClient(name = "ms-carroCompras", url = "http://localhost:8086",configuration = FeignConfig.class)
public interface CarroClient {


    @GetMapping("/api/carrito/cliente/{idCliente}")
    List<Object> verCarritoPorCliente(@PathVariable("idCliente") Long idCliente);
}
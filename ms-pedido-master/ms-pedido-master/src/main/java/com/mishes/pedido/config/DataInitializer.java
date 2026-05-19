package com.mishes.pedido.config;

import com.mishes.pedido.model.Estado;
import com.mishes.pedido.model.Pedido;
import com.mishes.pedido.repository.EstadoRepository;
import com.mishes.pedido.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final PedidoRepository pedidoRepository;
    private final EstadoRepository estadoRepository;

    @Override
    public void run(String... args) {
        if (pedidoRepository.count() > 0) {
            log.info("[Pedido] Datos iniciales ya cargados. Omitiendo.");
        } else {
            log.info("[Pedido] Cargando datos iniciales...");
            log.info("[Pedido] {} datos agregados", pedidoRepository.count());
        }
        if (estadoRepository.count() > 0) {
            log.info("[Estado] Datos iniciales ya cargados. Omitiendo.");
        } else {
            log.info("[Estado] Cargando datos iniciales...");
            estadoRepository.save(new Estado((short) 1, "Confirmado"));
            estadoRepository.save(new Estado((short) 2, "Preparando"));
            estadoRepository.save(new Estado((short) 3, "Enviado"));
            estadoRepository.save(new Estado((short) 4, "Recibido"));
            estadoRepository.save(new Estado((short) 5, "Fallido"));
            estadoRepository.save(new Estado((short) 6, "Completado"));
            estadoRepository.save(new Estado((short) 7, "Cancelado"));
            estadoRepository.save(new Estado((short) 8, "Reembolsado"));
            log.info("[Estado] {} datos agregados", estadoRepository.count());
        }
    }

}

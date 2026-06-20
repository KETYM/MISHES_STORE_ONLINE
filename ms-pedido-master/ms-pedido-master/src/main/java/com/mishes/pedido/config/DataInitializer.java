package com.mishes.pedido.config;

import com.mishes.pedido.model.Estado;
import com.mishes.pedido.model.Pedido;
import com.mishes.pedido.model.PedidoDetalle;
import com.mishes.pedido.repository.EstadoRepository;
import com.mishes.pedido.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final PedidoRepository pedidoRepository;
    private final EstadoRepository estadoRepository;

    @Override
    public void run(String... args) {
        // 1. PRIMERO: Inicializar los Estados si la tabla está vacía
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

        // 2. SEGUNDO: Inicializar los Pedidos con DataFaker si la tabla está vacía
        if (pedidoRepository.count() > 0) {
            log.info("[Pedido] Datos iniciales ya cargados. Omitiendo.");
        } else {
            log.info("[Pedido] Cargando datos iniciales con DataFaker...");
            Faker faker = new Faker(new Locale("es"));

            // Recuperamos los estados de la BD para asignarlos de forma segura
            List<Estado> estadosDisponibles = estadoRepository.findAll();

            for (int i = 0; i < 5; i++) {
                Pedido pedido = new Pedido();

                // Asignamos una ID de cliente ficticia (ej: del 1 al 10)
                pedido.setIdCli((long) faker.number().numberBetween(1, 10));

                // Generamos una fecha de forma segura con LocalDateTime
                int diasAtras = faker.number().numberBetween(1, 15);
                int horasAtras = faker.number().numberBetween(1, 23);
                pedido.setFechaPedido(LocalDateTime.now().minusDays(diasAtras).minusHours(horasAtras));

                // Seleccionamos un estado aleatorio de nuestra lista de estados
                Estado estadoAleatorio = estadosDisponibles.get(faker.number().numberBetween(0, estadosDisponibles.size()));
                pedido.setIdEstado(estadoAleatorio);

                // Construimos la lista de detalles del pedido
                List<PedidoDetalle> detalles = new ArrayList<>();
                int cantidadProductosDiferentes = faker.number().numberBetween(1, 4); // entre 1 y 3 ítems por compra
                double totalPagar = 0.0;

                for (int j = 0; j < cantidadProductosDiferentes; j++) {
                    PedidoDetalle detalle = new PedidoDetalle();

                    detalle.setIdProc((long) faker.number().numberBetween(1, 10)); // ID de Producto ficticio
                    detalle.setCantidad(faker.number().numberBetween(1, 4)); // cantidad comprada (1 a 3)

                    double precioUnitario = faker.number().randomDouble(0, 4990, 24990);
                    detalle.setPrecioUnitario(precioUnitario);

                    // Establecemos la relación bidireccional (el detalle apunta a su pedido padre)
                    detalle.setPedido(pedido);

                    detalles.add(detalle);
                    totalPagar += (precioUnitario * detalle.getCantidad());
                }

                pedido.setDetalles(detalles);
                pedido.setTotalPagar(totalPagar);

                // Como tu entidad Pedido tiene CascadeType.ALL, al guardar el pedido
                // JPA guardará automáticamente todos los registros hijos en pedido_detalle
                pedidoRepository.save(pedido);
            }
            log.info("[Pedido] {} pedidos de prueba generados exitosamente junto a sus detalles.", pedidoRepository.count());
        }
    }
}
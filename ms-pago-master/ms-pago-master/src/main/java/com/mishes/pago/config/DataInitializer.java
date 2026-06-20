package com.mishes.pago.config;

import com.mishes.pago.model.MetodoPago;
import com.mishes.pago.model.Pago;
import com.mishes.pago.repository.MetodoPagoRepository;
import com.mishes.pago.repository.PagoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Locale;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final PagoRepository pagoRepository;
    private final MetodoPagoRepository metodoPagoRepository;

    @Override
    public void run(String... args) {
        // 1. Inicializar los Métodos de Pago
        if (metodoPagoRepository.count() > 0) {
            log.info("[MetodoPago] Datos iniciales ya cargados. Omitiendo.");
        } else {
            log.info("[MetodoPago] Cargando datos iniciales...");
            metodoPagoRepository.save(new MetodoPago((short) 1, "Efectivo"));
            metodoPagoRepository.save(new MetodoPago((short) 2, "Debito MasterCard"));
            metodoPagoRepository.save(new MetodoPago((short) 3, "Credito MasterCard"));
            metodoPagoRepository.save(new MetodoPago((short) 4, "Debito VISA"));
            metodoPagoRepository.save(new MetodoPago((short) 5, "Credito VISA"));
            log.info("[MetodoPago] {} datos agregados", metodoPagoRepository.count());
        }

        // 2. Inicializar el historial de Pagos usando DataFaker
        if (pagoRepository.count() > 0) {
            log.info("[Pago] Datos iniciales ya cargados. Omitiendo.");
        } else {
            log.info("[Pago] Cargando datos iniciales con DataFaker...");
            Faker faker = new Faker(new Locale("es"));

            for (int i = 0; i < 10; i++) {
                Pago pago = new Pago();

                // Asociamos a clientes (1 al 10) y pedidos ficticios (1 al 5)
                pago.setIdCli((long) faker.number().numberBetween(1, 10));
                pago.setIdPed((long) faker.number().numberBetween(1, 5));

                // Fecha de pago segura sin errores (últimos 15 días)
                int diasAtras = faker.number().numberBetween(1, 15);
                pago.setFechaPago(LocalDateTime.now().minusDays(diasAtras));

                // 💡 CORRECCIÓN 1: Adaptado a Integer (sin randomDouble)
                pago.setMontoTotal(faker.number().numberBetween(5000, 50000));

                // 💡 CORRECCIÓN 2: Asignamos un Short del 1 al 5 directamente
                short idMetodoAleatorio = (short) faker.number().numberBetween(1, 6);
                pago.setIdMetodoPago(idMetodoAleatorio);

                pagoRepository.save(pago);
            }
            log.info("[Pago] {} pagos de prueba generados exitosamente.", pagoRepository.count());
        }
    }
}
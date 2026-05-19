package com.mishes.pago.config;

import com.mishes.pago.model.Pago;
import com.mishes.pago.model.MetodoPago;
import com.mishes.pago.repository.PagoRepository;
import com.mishes.pago.repository.MetodoPagoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final PagoRepository pagoRepository;
    private final MetodoPagoRepository metodoPagoRepository;

    @Override
    public void run(String... args) {
        if (pagoRepository.count() > 0) {
            log.info("[Pago] Datos iniciales ya cargados. Omitiendo.");
        } else {
            log.info("[Pago] Cargando datos iniciales...");
            log.info("[Pago] {} datos agregados", pagoRepository.count());
        }
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
    }

}

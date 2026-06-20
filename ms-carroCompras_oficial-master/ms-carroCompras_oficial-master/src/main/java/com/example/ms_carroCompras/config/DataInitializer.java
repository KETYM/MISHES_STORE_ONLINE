package com.example.ms_carroCompras.config;

import com.example.ms_carroCompras.model.Carrito;
import com.example.ms_carroCompras.repository.carritoRepository;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Locale;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initData(carritoRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                Faker faker = new Faker(new Locale("es"));

                for (int i = 0; i < 15; i++) {
                    Carrito c = new Carrito();

                    // Simulamos que clientes (del 1 al 10) agregaron productos (del 1 al 10)
                    c.setIdCliente((long) faker.number().numberBetween(1, 11));
                    c.setIdProducto((long) faker.number().numberBetween(1, 11));

                    // Cantidad de productos en el carrito (de 1 a 5 unidades)
                    c.setCantidad(faker.number().numberBetween(1, 6));

                    repository.save(c);
                }
                System.out.println("✅ Datos del Carro de Compras cargados exitosamente con DataFaker.");
            } else {
                System.out.println("✅ [CarroCompras] Datos ya existen. Omitiendo carga.");
            }
        };
    }
}
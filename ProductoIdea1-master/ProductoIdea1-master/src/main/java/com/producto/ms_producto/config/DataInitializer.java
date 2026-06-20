package com.producto.ms_producto.config;

import com.producto.ms_producto.model.Libro;
import com.producto.ms_producto.repository.LibroRepository;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Locale;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initData(LibroRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                Faker faker = new Faker(new Locale("es"));

                for (int i = 0; i < 10; i++) {
                    Libro l = new Libro();

                    l.setTitulo(faker.book().title());
                    l.setAutor(faker.book().author());
                    l.setIsbn(faker.code().isbn13());

                    // Precio aleatorio entre 5.000 y 50.000
                    l.setPrecio(faker.number().randomDouble(2, 5000, 50000));

                    // Stock aleatorio entre 0 y 100
                    l.setStock(faker.number().numberBetween(0, 100));

                    repository.save(l);
                }
                System.out.println("✅ Datos de Producto (Libros) cargados correctamente.");
            }
        };
    }
}
package com.producto.ms_producto.repository;

import com.producto.ms_producto.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LibroRepository extends JpaRepository<Libro,Long> {
    Optional<Libro> findByIsbn(String isbn);

}

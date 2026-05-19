package com.example.ms_carroCompras.repository;

import com.example.ms_carroCompras.model.Carrito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface carritoRepository extends JpaRepository<Carrito, Long> {
    List<Carrito> findByIdCliente(Long idCliente);
}

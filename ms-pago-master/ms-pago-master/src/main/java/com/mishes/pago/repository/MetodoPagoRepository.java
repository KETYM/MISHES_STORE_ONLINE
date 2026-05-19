package com.mishes.pago.repository;

import com.mishes.pago.model.MetodoPago;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MetodoPagoRepository extends JpaRepository<MetodoPago, Short> {
}

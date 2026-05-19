package com.mishes.pago.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name = "metodo_pago")
public class MetodoPago {

    @Id
    private Short idMetodoPago;

    @Column(nullable = false, length = 20)
    private String nombreMetodoPago;

}

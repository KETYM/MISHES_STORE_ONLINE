package com.mishes.pago.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name = "pago")
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPag;

    @Column(nullable = false)
    private Long idPed;

    @Column(nullable = false)
    private Long idCli;

    @Column(nullable = false)
    private LocalDateTime fechaPago;

    @Column(nullable = false)
    private Integer montoTotal;

    @Column(name = "id_metodo_pago", nullable = false)
    private Short idMetodoPago;
}

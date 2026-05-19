package com.mishes.pedido.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name = "pedido")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPed;

    @Column(nullable = false)
    private Long idCli;

    @Column(nullable = false)
    private Long idProc;

    @Column(nullable = false)
    private LocalDateTime fechaPedido;

    @ManyToOne
    @JoinColumn(name = "id_estado", nullable = false)
    private Estado idEstado; // En vez de un Short, usas el objeto Estado completo

    @Positive
    @Column(nullable = false, precision = 4)
    private Integer cantidad;

}

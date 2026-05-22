package com.mishes.pedido.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name = "pedido_detalle")
public class PedidoDetalle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPedDetalle;

    @Column(nullable = false)
    private Long idProc; // 💡 Aquí se mudó el ID del Producto que tenía tu compañero

    @Positive
    @Column(nullable = false)
    private Integer cantidad; // 💡 Aquí se mudó la cantidad

    @Column(nullable = false)
    private Double precioUnitario; // Guardamos el precio del manga congelado al momento de comprar

    // 💡 RELACIÓN MUCHOS A UNO: Muchos detalles pertenecen a un mismo pedido principal
    @ManyToOne
    @JoinColumn(name = "id_pedido", nullable = false)
    @JsonIgnore // Evita bucles infinitos en Postman
    private Pedido pedido;
}

package com.example.ms_carroCompras.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "carrito")
@Data
public class Carrito {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCarrito;

    private Long idCliente;
    private Long idProducto;
    private int cantidad;
}
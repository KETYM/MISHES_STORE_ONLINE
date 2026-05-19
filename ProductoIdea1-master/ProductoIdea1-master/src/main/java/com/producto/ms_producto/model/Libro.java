package com.producto.ms_producto.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "productos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Libro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Long idLibro;

    @Column(name = "nombre_producto", nullable = false)
    private String titulo;

    private String autor;


    @Column(name = "precio", columnDefinition = "DECIMAL(10,2)")
    private double precio;

    private Integer stock;
    private String isbn;
}
package com.example.ms_carroCompras.dto;

import lombok.Data;

@Data
public class LibroResponseDTO {
    // 💡 Escribimos la variable idéntica a como te la devolvió el GET de productos
    private Long idLibro;
    private String titulo;
    private String autor;
    private String isbn;
    private double precio;
    private int stock;
}
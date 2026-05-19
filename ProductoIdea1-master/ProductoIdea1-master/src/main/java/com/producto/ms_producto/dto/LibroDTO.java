package com.producto.ms_producto.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LibroDTO {

    private Long idLibro;

    @NotBlank(message = "El titulo del libro es obligatorio.")
    private String titulo;

    @NotBlank(message = "El autor es obligatorio.")
    private String autor;

    @NotNull(message = "El precio es obligatorio.")
    private double precio;

    @NotNull(message = "El Stock es obligatorio.")
    private int stock;

    @NotBlank(message = "El isbn es obligatorio.")
    private String isbn;
}
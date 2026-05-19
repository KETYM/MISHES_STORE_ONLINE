package com.mishes.pedido.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoResponseDTO {

    @NotNull
    private Long idProc;

    @NotBlank
    private String nombreProc;

    @NotNull @PositiveOrZero
    private Double precio;

    @NotBlank
    private String descripcion;

    @Positive
    private Integer cantidad;

}

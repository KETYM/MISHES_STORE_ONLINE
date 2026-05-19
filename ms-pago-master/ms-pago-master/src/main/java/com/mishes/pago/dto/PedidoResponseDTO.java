package com.mishes.pago.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoResponseDTO {

    @NotNull
    private Long idPed;

    @NotNull
    private Long idCli;

    @NotNull
    private Long idProc;

    @NotNull
    private LocalDateTime fechaPedido;

    @NotBlank
    private String nombreEstado;

    @NotNull @Positive
    private Integer cantidad;

    @NotNull @PositiveOrZero
    private Double precioUnitario;

    @NotNull @PositiveOrZero
    private Double precioTotal;

}

package com.mishes.pedido.dto;

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
public class PedidoRequestDTO {

    @NotNull
    private Long idCli;

    @NotNull
    private Long idProc;

    @NotNull
    private LocalDateTime fechaPedido = LocalDateTime.now();

    @NotNull
    private Short idEstado;

    @NotNull @Positive
    private Integer cantidad;

    @PositiveOrZero // 🌟 AGREGA ESTA LÍNEA MÁGICA: Permite que el precio sea dinámico opcionalmente
    private Double precioUnitario;

    private String nombreProducto;
}

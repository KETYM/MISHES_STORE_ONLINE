package com.mishes.pedido.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListaProductosClienteDTO {

    @NotNull @PositiveOrZero
    private Integer cantidadTotal;

    @NotNull @PositiveOrZero
    private Double precioTotal;

    @NotNull
    private LocalDateTime fechaPedido;

    @NotNull
    private String estadoPedido;

    @NotNull
    private List<ProductoResponseDTO> productos;

}

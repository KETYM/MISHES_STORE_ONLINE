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
public class PagoResponseDTO {

    @NotNull
    private Long idPag;

    @NotNull
    private Long idPed;

    @NotNull
    private Long idCli;

    @NotNull
    private LocalDateTime fechaPago;

    @NotNull @PositiveOrZero
    private Integer montoTotal;

    @NotBlank
    private String metodoPago;

}

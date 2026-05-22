package com.example.ms_carroCompras.dto;

import com.fasterxml.jackson.annotation.JsonProperty; //
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CarritoDTO {

    @NotNull(message = "El ID del cliente es obligatorio")
    @JsonProperty("idCliente") // 💡 Fuerza el mapeo exacto de Postman
    private Long idCliente;

    @NotNull(message = "El ID del producto es obligatorio")
    @JsonProperty("idProducto") // 💡 Fuerza el mapeo exacto de Postman
    private Long idProducto;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad mínima a agregar debe ser 1")
    @JsonProperty("cantidad")
    private int cantidad;
}
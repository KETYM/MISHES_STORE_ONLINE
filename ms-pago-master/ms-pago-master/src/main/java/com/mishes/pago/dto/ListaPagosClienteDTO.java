package com.mishes.pago.dto;

import com.mishes.pago.validation.RUT;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListaPagosClienteDTO {

    @NotNull
    private Long idCli;

    @NotBlank @RUT
    private String rutCli;

    @NotBlank
    private String nombreCompleto;

    @NotBlank @Email
    private String correo;

    @NotBlank
    private String telefono;

    private LocalDateTime ultimoPago;

    @NotNull
    private List<PagoResponseDTO> pagos;

}

package com.mishes.pago.dto;

import com.mishes.pago.validation.RUT;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteResponseDTO {

    @NotNull
    private Long idCli;

    @NotBlank @RUT
    private String rutCli;

    @NotBlank
    private String nombreCli;

    @NotBlank
    private String apellidoCli;

    @NotBlank @Email
    private String correo;

    @NotBlank
    private String telefono;

    public String getNombreCompleto() {
        String nombre = nombreCli.trim();
        String apellido = apellidoCli.trim();
        return nombre.substring(0, 1).toUpperCase() + nombre.substring(1).toLowerCase() + " " +
                apellido.substring(0, 1).toUpperCase() + apellido.substring(1).toLowerCase();
    }

}

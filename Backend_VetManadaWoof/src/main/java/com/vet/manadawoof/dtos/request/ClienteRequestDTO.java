package com.vet.manadawoof.dtos.request;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClienteRequestDTO {

    @NotNull
    private Integer idTipoPersonaJuridica;

    @NotBlank
    private String nombre;

    @NotBlank
    private String sexo;

    @NotBlank
    private String documento;

    @NotNull
    private Long idTipoDocumento;

    @Email
    private String correo;

    private String telefono;
    private String direccion;
    private String ciudad;
    private String distrito;

    private Boolean activo;
}

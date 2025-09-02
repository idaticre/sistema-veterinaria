package com.vet.manadawoof.dtos.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EntidadResponseDTO {
    private Integer idEntidad;
    private String codigoEntidad;
    private String nombre;
    private String correo;
    private String telefono;
    private String ciudad;
    private String distrito;
    private String representante;
    private String mensaje;
}

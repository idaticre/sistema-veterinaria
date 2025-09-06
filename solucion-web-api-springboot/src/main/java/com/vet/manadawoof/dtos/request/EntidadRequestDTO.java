package com.vet.manadawoof.dtos.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EntidadRequestDTO {
    private Integer idEntidad;
    private Integer idTipoEntidad;
    private Integer idTipoPersonaJuridica;
    private Integer idTipoDocumento;
    private String nombre;
    private String sexo;
    private String numeroDocumento;
    private String correo;
    private String telefono;
    private String direccion;
    private String ciudad;
    private String distrito;
    private String representante;

    // Para actualización
    private Boolean activo;
}

package com.vet.manadawoof.dtos.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProveedorRequestDTO {
    // Solo para actualizar; null al registrar
    private Integer idEntidad;
    private Integer idTipoPersonaJuridica;
    private String nombre;
    private String sexo;
    private String documento;
    private Integer idTipoDocumento;
    private String correo;
    private String telefono;
    private String direccion;
    private String ciudad;
    private String distrito;
    private String representante;
    // Solo para actualizar
    private Boolean activo;
}

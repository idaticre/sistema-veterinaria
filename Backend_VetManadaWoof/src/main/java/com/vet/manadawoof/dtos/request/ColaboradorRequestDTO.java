package com.vet.manadawoof.dtos.request;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class ColaboradorRequestDTO {
    private Integer id;
    private Integer idEntidad;
    private Integer idUsuario;
    private String nombre;
    private String sexo;
    private String documento;
    private Integer idTipoDocumento;
    private Integer idTipoPersonaJuridica;
    private String correo;
    private String telefono;
    private String direccion;
    private String ciudad;
    private String distrito;
    private String representante;
    private LocalDate fechaIngreso;
    private String foto;
    private Boolean activo;
}

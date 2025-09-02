package com.vet.manadawoof.dtos.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class ColaboradorResponseDTO {
    private Integer idColaborador;
    private Integer idEntidad;
    private Integer idUsuario;
    private String codigoColaborador;
    private String codigoEntidad;
    private String mensaje;
    private LocalDate fechaIngreso;
    private String foto;
    private Boolean activo;
    private String sexo;
}

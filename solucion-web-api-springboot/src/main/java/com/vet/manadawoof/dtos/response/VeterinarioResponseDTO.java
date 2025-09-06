package com.vet.manadawoof.dtos.response;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VeterinarioResponseDTO {
    private Integer id;
    private String codigoVeterinario;
    private String codigoColaborador;
    private String codigoEntidad;
    private String nombreColaborador;
    private String cmp;
    private Boolean activo;
    private String mensaje;
}

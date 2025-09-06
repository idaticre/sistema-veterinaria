package com.vet.manadawoof.dtos.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProveedorResponseDTO {
    private Integer idProveedor;
    private String codigoProveedor;
    private Boolean activo;
    private EntidadResponseDTO entidad;
    private String mensaje;
}

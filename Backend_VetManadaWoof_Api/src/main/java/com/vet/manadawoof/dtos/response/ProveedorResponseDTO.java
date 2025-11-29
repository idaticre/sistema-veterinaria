package com.vet.manadawoof.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProveedorResponseDTO {
    
    private Long id;
    private String codigoProveedor;
    
    // Datos de entidad
    private Long idEntidad;
    private String nombre;
    private String sexo;
    private String documento;
    private Integer idTipoPersonaJuridica;
    private Integer idTipoDocumento;
    private String correo;
    private String telefono;
    private String direccion;
    private String ciudad;
    private String distrito;
    private String representante;
    
    // Datos específicos
    private Boolean activo;
    private LocalDateTime fechaRegistro;
    
    // fallback de SP
    private String mensaje;
}

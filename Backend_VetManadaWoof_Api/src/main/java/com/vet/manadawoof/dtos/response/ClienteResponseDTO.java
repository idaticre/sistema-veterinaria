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
public class ClienteResponseDTO {
    
    // ------------------ Cliente ------------------
    private Long id;
    
    // código generado en tabla
    private String codigoCliente;
    
    private Boolean activo;
    
    // fecha de registro en clientes
    private LocalDateTime fechaRegistro;
    
    // ------------------ Entidad ------------------
    // id de tabla entidades
    private Long idEntidad;
    private String codigoEntidad;
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
    private LocalDateTime fechaRegistroEntidad;
    
    private String mensaje;
}

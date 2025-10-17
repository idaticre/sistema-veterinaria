package com.vet.manadawoof.dtos.response;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClienteResponseDTO {

    private Long id;
    private String codigoCliente;

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

    // Datos específicos
    private Boolean activo;
    private LocalDateTime fechaRegistro;

    // fallback de SP
    private String mensaje;
}

package com.vet.manadawoof.dtos.response;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ColaboradorResponseDTO {

    private Long idColaborador;
    private String codigoColaborador;

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
    private String usuario;
    private Boolean activo;
    private LocalDateTime fechaRegistro;
    private LocalDate fechaIngreso;
    private String foto;

    // fallback de SP
    private String mensaje;
}

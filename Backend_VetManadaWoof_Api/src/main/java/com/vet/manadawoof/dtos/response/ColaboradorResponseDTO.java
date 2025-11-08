package com.vet.manadawoof.dtos.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ColaboradorResponseDTO {
    
    private Long id;
    private String codigoColaborador;
    
    // Datos de la entidad
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
    
    // Datos específicos del colaborador
    private Integer usuario;
    private Boolean activo;
    private LocalDateTime fechaRegistro;
    private LocalDate fechaIngreso;
    private String foto;
    
    // Mensaje de respuesta (para SP o validaciones)
    private String mensaje;
}

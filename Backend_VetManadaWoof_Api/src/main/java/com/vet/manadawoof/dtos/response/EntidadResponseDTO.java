package com.vet.manadawoof.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EntidadResponseDTO {
    
    // Long para congruencia con Entity
    private Long id;
    private String codigo;
    private String nombre;
    private String correo;
    private String telefono;
    private String sexo;
    private String documento;
    private String direccion;
    private Integer idTipoDocumento;
    private Integer idTipoPersonaJuridica;
    private String ciudad;
    private String distrito;
    private String representante;
    
    // Boolean para representar 1/0 en BD
    private Boolean activo;
    private String tipoDocumento;
    private String tipoPersonaJuridica;
    private LocalDateTime fechaRegistro;
}

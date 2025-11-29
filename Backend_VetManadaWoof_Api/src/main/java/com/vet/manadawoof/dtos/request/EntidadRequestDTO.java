package com.vet.manadawoof.dtos.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EntidadRequestDTO {
    
    // Solo para UPDATE
    // Long para consistencia con Entity
    private Long id;
    
    @NotNull
    private Integer idTipoPersonaJuridica;
    
    @NotBlank
    @Size(max = 128)
    private String nombre;
    
    // Alineado con SP que permite 'M','F','O' o NULL
    @Pattern(regexp = "^(M|F|O)?$")
    private String sexo;
    
    @NotBlank
    @Size(max = 20)
    private String documento;
    
    @NotNull
    private Integer idTipoDocumento;
    
    @Email
    @Size(max = 64)
    private String correo;
    
    @Size(max = 15)
    private String telefono;
    
    @Size(max = 128)
    private String direccion;
    
    @Size(max = 64)
    private String ciudad;
    
    @Size(max = 64)
    private String distrito;
    
    @Size(max = 64)
    private String representante;
    
    // Boolean para representar 1/0 en BD
    private Boolean activo;
}

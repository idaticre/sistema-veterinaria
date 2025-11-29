package com.vet.manadawoof.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArchivoClinicoRequestDTO {
    
    @NotNull(message = "Registro de atención es obligatorio")
    private Long idRegistroAtencion;
    
    private Integer idTipoArchivo;
    
    @NotBlank(message = "Nombre de archivo es obligatorio")
    @Size(max = 128, message = "Nombre no debe superar 128 caracteres")
    private String nombreArchivo;
    
    @NotBlank(message = "Extensión es obligatoria")
    @Size(max = 32, message = "Extensión no debe superar 32 caracteres")
    private String extensionArchivo;
    
    @Size(max = 256, message = "Descripción no debe superar 256 caracteres")
    private String descripcion;
}

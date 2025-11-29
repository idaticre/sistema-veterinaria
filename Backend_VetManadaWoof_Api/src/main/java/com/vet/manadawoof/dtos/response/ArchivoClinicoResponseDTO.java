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
public class ArchivoClinicoResponseDTO {
    
    private Long id;
    private String codigo;
    private Long idRegistroAtencion;
    private Integer idTipoArchivo;
    private String nombreArchivo;
    private String extensionArchivo;
    private String descripcion;
    private LocalDateTime fechaSubida;
    
    // Mensaje de respuesta del SP
    private String mensaje;
}

package com.vet.manadawoof.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HistoriaClinicaResponseDTO {
    
    private Long id;
    private String codigo;
    private Long idMascota;
    private LocalDate fechaApertura;
    private String observacionesGenerales;
    private Boolean activo;
    private LocalDateTime fechaRegistro;
    
    // Mensaje de respuesta del SP
    private String mensaje;
}

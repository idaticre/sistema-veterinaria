package com.vet.manadawoof.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VacunaMascotaResponseDTO {
    
    // ID interno del registro
    private Integer id;
    
    // Código generado automáticamente
    private String codigo;
    
    // ID de la vacuna
    private Integer idVacuna;
    
    // ID de la mascota
    private Long idMascota;
    
    // ID de la vía de administración
    private Integer idVia;
    
    // Dosis administrada
    private String dosis;
    
    // Fecha de aplicación
    private LocalDate fechaAplicacion;
    
    // Fecha de modificación
    private LocalDateTime fechaModificacion;
    
    // Durabilidad años
    private Integer durabilidad;
    
    // Próxima dosis
    private LocalDate proxDosis;
    
    // Colaborador que aplicó
    private Long idColaborador;
    
    // Veterinario responsable
    private Long idVeterinario;
    
    // Observaciones adicionales
    private String observaciones;
    
    // Fecha de creación
    private LocalDateTime fechaRegistro;
    
    // Estado lógico (activo/inactivo)
    private Boolean activo;
    
    // Mensaje
    private String mensaje;
    
}

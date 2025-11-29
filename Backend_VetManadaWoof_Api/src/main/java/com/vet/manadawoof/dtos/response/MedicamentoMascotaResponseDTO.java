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
public class MedicamentoMascotaResponseDTO {
    
    // ID interno del registro
    private Integer id;
    
    // Código generado automáticamente
    private String codigo;
    
    // ID de la mascota
    private Long idMascota;
    
    // ID del medicamento
    private Integer idMedicamento;
    
    // ID de la vía de administración
    private Integer idVia;
    
    // Dosis administrada
    private String dosis;
    
    // Fecha de aplicación
    private LocalDate fechaAplicacion;
    
    // Colaborador que aplicó
    private Long idColaborador;
    
    // Veterinario responsable
    private Long idVeterinario;
    
    // Observaciones adicionales
    private String observaciones;
    
    // Fecha de creación
    private LocalDateTime fechaRegistro;
    
    // Fecha de modificación
    private LocalDateTime fechaModificacion;
    
    // Estado lógico (activo/inactivo)
    private Boolean activo;
}

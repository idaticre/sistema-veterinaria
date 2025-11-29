package com.vet.manadawoof.dtos.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VacunaMascotaRequestDTO {
    
    // Solo para UPDATE
    private Integer id;
    
    // ID de la vacuna
    @NotNull
    private Integer idVacuna;
    
    // ID de la mascota
    @NotNull
    private Long idMascota;
    
    // ID de la vía de administración
    @NotNull
    private Integer idVia;
    
    // Dosis administrada
    @Size(max = 32)
    private String dosis;
    
    // Fecha de aplicación
    @NotNull
    private LocalDate fechaAplicacion;
    
    // Durabilidad en años
    @NotNull
    private Integer durabilidad;
    
    // Próxima dosis
    @NotNull
    private LocalDate proxDosis;
    
    // Colaborador que aplicó el medicamento
    private Long idColaborador;
    
    // Veterinario responsable
    private Long idVeterinario;
    
    
    // Observaciones opcionales
    @Size(max = 64)
    private String observaciones;
    
    // Estado lógico, solo para UPDATE
    private Boolean activo = true;
}

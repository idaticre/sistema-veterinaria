package com.vet.manadawoof.dtos.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AtencionMedicaRequestDTO {
    
    // Para actualización
    private Long idRegistro;
    
    @NotNull(message = "Historia clínica es obligatoria")
    private Long idHistoriaClinica;
    
    private Long idAgenda;
    
    private Long idVeterinario;
    
    private Long idColaborador;
    
    @NotNull(message = "Fecha de atención es obligatoria")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate fechaAtencion;
    
    @NotNull(message = "Hora de inicio es obligatoria")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime horaInicio;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime horaFin;
    
    @Size(max = 256, message = "Motivo consulta no debe superar 256 caracteres")
    private String motivoConsulta;
    
    private String anamnesis;
    
    private String examenFisico;
    
    @Size(max = 256, message = "Signos vitales no debe superar 256 caracteres")
    private String signosVitales;
    
    private BigDecimal pesoKg;
    
    private BigDecimal temperaturaC;
    
    private String diagnostico;
    
    private String tratamiento;
    
    private String observaciones;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate proximoControl;
    
    private Integer idEstado;
}

package com.vet.manadawoof.dtos.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AtencionMedicaResponseDTO {
    
    private Long id;
    private String codigo;
    private Long idHistoriaClinica;
    private Long idAgenda;
    private Long idVeterinario;
    private Long idColaborador;
    private LocalDate fechaAtencion;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private String motivoConsulta;
    private String anamnesis;
    private String examenFisico;
    private String signosVitales;
    private BigDecimal pesoKg;
    private BigDecimal temperaturaC;
    private String diagnostico;
    private String tratamiento;
    private String observaciones;
    private LocalDate proximoControl;
    private Integer idEstado;
    private LocalDateTime fechaRegistro;
    
    // Mensaje de respuesta del SP
    private String mensaje;
}

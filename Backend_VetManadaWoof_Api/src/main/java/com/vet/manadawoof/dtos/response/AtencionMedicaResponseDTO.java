package com.vet.manadawoof.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

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
    private String tipoVisita;
    private BigDecimal totalCita;
    private BigDecimal abonoTotal;
    private BigDecimal saldoPendiente;

    // CLÍNICO
    private String motivoConsulta;
    private String anamnesis;
    private String examenFisico;
    private String signosVitales;
    private BigDecimal pesoKg;
    private BigDecimal temperaturaC;
    private String diagnostico;
    private String tratamiento;
    private LocalDate proximoControl;

    // ESTÉTICO
    private String estadoPelaje;
    private String condicionPiel;
    private String observacionesGrooming;

    // HOSPEDAJE - faltaban
    private String comportamientoHospedaje;
    private String alimentacionHospedaje;
    private String actividadHospedaje;

    private String observaciones;
    private Integer idEstado;
    private LocalDateTime fechaRegistro;

    // Mensaje de respuesta
    private String mensaje;
}
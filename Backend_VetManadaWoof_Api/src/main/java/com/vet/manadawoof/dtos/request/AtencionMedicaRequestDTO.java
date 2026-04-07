package com.vet.manadawoof.dtos.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
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

    // Para actualizar
    private Long id;

    @NotNull(message = "Historia clínica es obligatoria")
    private Long idHistoriaClinica;

    private Long idAgenda;

    private Long idVeterinario;

    private Long idColaborador;

    @NotNull(message = "Fecha de atención es obligatoria")
    private LocalDate fechaAtencion;

    @NotNull(message = "Hora inicio es obligatoria")
    private LocalTime horaInicio;

    private LocalTime horaFin;

    @Size(max = 256, message = "Motivo de consulta no debe superar 256 caracteres")
    private String motivoConsulta;

    private String anamnesis;

    private String examenFisico;

    @Size(max = 256, message = "Signos vitales no debe superar 256 caracteres")
    private String signosVitales;

    @DecimalMin(value = "0.0", message = "Peso no puede ser negativo")
    @DecimalMax(value = "999.99", message = "Peso no puede exceder 999.99")
    private BigDecimal pesoKg;

    @DecimalMin(value = "0.0", message = "Temperatura no puede ser negativa")
    @DecimalMax(value = "50.0", message = "Temperatura no realista")
    private BigDecimal temperaturaC;

    private String diagnostico;

    private String tratamiento;

    private String observaciones;

    private LocalDate proximoControl;

    @Size(max = 128, message = "Estado del pelaje no debe superar 128 caracteres")
    private String estadoPelaje;

    @Size(max = 128, message = "Condición de piel no debe superar 128 caracteres")
    private String condicionPiel;

    private String observacionesGrooming;

    private String comportamientoHospedaje;

    @Size(max = 256, message = "Alimentación no debe superar 256 caracteres")
    private String alimentacionHospedaje;

    private String actividadHospedaje;

    private Integer idEstado;
}

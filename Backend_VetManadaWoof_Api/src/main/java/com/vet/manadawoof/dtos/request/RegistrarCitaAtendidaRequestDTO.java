package com.vet.manadawoof.dtos.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegistrarCitaAtendidaRequestDTO {

    // Responsabilidad: Marcar cita como ATENDIDA + crear registro

    @NotNull(message = "ID de agenda es obligatorio")
    private Long idAgenda;

    private Long idVeterinario;

    private Long idColaborador;

    @NotBlank(message = "Tipo de visita es obligatorio")
    @Size(max = 32, message = "Tipo de visita no debe superar 32 caracteres")
    private String tipoVisita;

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

    private LocalDate proximoControl;

    @Size(max = 128, message = "Estado del pelaje no debe superar 128 caracteres")
    private String estadoPelaje;

    @Size(max = 128, message = "Condicion de piel no debe superar 128 caracteres")
    private String condicionPiel;

    private String observacionesGrooming;

    private String comportamientoHospedaje;

    @Size(max = 256, message = "Alimentacion no debe superar 256 caracteres")
    private String alimentacionHospedaje;

    private String actividadHospedaje;

    private String observaciones;
}

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
public class AgendaResponseDTO {
    
    private Long id;
    private String codigo;
    private Long idCliente;
    private Long idMascota;
    private Integer idMedioSolicitud;
    private LocalDate fecha;
    private LocalTime hora;
    private Integer duracionEstimadaMin;
    private BigDecimal abonoInicial;
    private BigDecimal totalCita;
    private Integer idEstado;
    private String observaciones;
    private LocalDateTime fechaRegistro;
    
    // Mensaje de respuesta del SP
    private String mensaje;
}

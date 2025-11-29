package com.vet.manadawoof.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IngresoServicioResponseDTO {
    
    private Long id;
    private String codigo;
    private Long idAgenda;
    private Integer idServicio;
    private Long idColaborador;
    private Long idVeterinario;
    private Integer cantidad;
    private Integer duracionMin;
    private BigDecimal valorServicio;
    private String observaciones;
    private LocalDateTime fechaRegistro;
    
    // Datos adicionales del SP
    private BigDecimal nuevoTotalCita;
    
    // Mensaje de respuesta del SP
    private String mensaje;
}

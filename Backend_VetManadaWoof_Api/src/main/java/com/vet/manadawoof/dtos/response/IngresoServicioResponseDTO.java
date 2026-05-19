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

    // 🔥 NUEVOS CAMPOS
    private String descripcion;

    private String nombreVeterinario;

    private BigDecimal subtotal;

    // 🔥 Datos adicionales
    private BigDecimal nuevoTotalCita;

    private String mensaje;
}
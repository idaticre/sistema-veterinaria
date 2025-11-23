package com.vet.manadawoof.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HorarioVigenteResponseDTO {
    private Long idColaborador;
    private String colaborador;
    private String dia;
    private String horario;
    private String rango;
    private String vigenteDesde;
    private Integer diasConEsteHorario;
    private String vigenteHasta;
    
    // Campos adicionales
    private Boolean esIndefinido;
    private Boolean proximoACambiar; // true si vence en menos de 7 días
    private Integer diasParaVencimiento;
    private LocalTime horaInicio;
    private LocalTime horaFin;
}

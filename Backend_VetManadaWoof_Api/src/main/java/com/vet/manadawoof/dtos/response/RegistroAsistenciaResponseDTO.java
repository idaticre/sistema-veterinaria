package com.vet.manadawoof.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegistroAsistenciaResponseDTO {
    
    // ========================================
    // CAMPOS PARA registrar() - Del SP gestionar_asistencia
    // ========================================
    private Boolean success;
    private String mensaje;
    private Integer tardanzaMinutos;
    private String estadoFinal;
    private LocalTime horaMarcacion;
    private String tipoMarca;
    
    // ========================================
    // CAMPOS PARA verAsistenciaPorRango() - Del SP ver_asistencia_por_rango
    // ========================================
    private Long idColaborador;
    private String colaborador;
    private String horario;
    private LocalDate fecha;
    private String diaSemana;  // Opcional, si tu SP lo retorna
    
    // Marcaciones del día
    private LocalTime horaEntrada;
    private LocalTime horaLunchInicio;
    private LocalTime horaLunchFin;
    private LocalTime horaSalida;
    
    // Cálculos
    private Integer minutosTrabajados;
    private Integer minutosLunch;
    
    // Estado
    private String estadoAsistencia;     // Nombre del estado (PRESENTE, TARDANZA, etc.)
    private Integer idEstadoAsistencia;  // ID del estado
    
    // Otros
    private String observaciones;
    private String registroOrigen;
    
    // Horario programado (del horario base o excepción)
    private LocalTime horaProgramadaInicio;
    private LocalTime horaProgramadaFin;
    
    // Campos adicionales para registrar()
    private String nombreColaborador;  // Para registrar()
}

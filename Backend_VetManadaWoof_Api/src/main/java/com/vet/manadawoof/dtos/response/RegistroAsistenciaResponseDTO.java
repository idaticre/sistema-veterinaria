package com.vet.manadawoof.dtos.response;

import lombok.*;

import java.time.*;

/**
 * DTO que representa un registro de asistencia de un colaborador.
 * Se utiliza para enviar datos al frontend sin exponer la entidad.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegistroAsistenciaResponseDTO {
    
    // ID del colaborador
    private Long idColaborador;
    
    // Nombre colaborador
    private String colaborador;
    
    // Nombre del horario asignado (opcional)
    private String horario;
    
    // Fecha del registro
    private LocalDate fecha;
    
    // Hora de entrada
    private LocalTime horaEntrada;
    
    // Inicio del almuerzo
    private LocalTime horaLunchInicio;
    
    // Fin del almuerzo
    private LocalTime horaLunchFin;
    
    // Hora de salida
    private LocalTime horaSalida;
    
    // Minutos trabajados (calculado)
    private Integer minutosTrabajados;
    
    // Minutos de almuerzo
    private Integer minutosLunch;
    
    // Estado del registro (referencia a la tabla estado_asistencia)
    private String estadoAsistencia;
    
    private Integer tardanzaMinutos;
    
    
    // Observaciones opcionales
    private String observaciones;
    
    // Origen del registro: 'APP', 'BIOMETRICO', 'MANUAL'
    private String registroOrigen;
}

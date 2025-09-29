//package com.vet.manadawoof.dtos.request;
//
//import jakarta.validation.constraints.NotNull;
//import lombok.*;
//
//import java.time.LocalTime;
//
//@Data
//@AllArgsConstructor
//@NoArgsConstructor
//public class HorarioTrabajoRequestDTO {
//
//    private Integer id; // solo para update
//
//    @NotNull(message = "Colaborador requerido")
//    private Integer idColaborador;
//
//    @NotNull(message = "Día requerido")
//    private Integer idDiaSemana;
//
//    private Integer idTipoDia;
//
//    @NotNull(message = "Hora inicio requerida")
//    private LocalTime horaInicio;
//
//    @NotNull(message = "Hora fin requerida")
//    private LocalTime horaFin;
//}
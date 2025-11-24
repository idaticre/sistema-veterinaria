package com.vet.manadawoof.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AsignacionHorarioResponseDTO {
    private Long id;
    private Long idColaborador;
    private String nombreColaborador;
    private Integer idHorarioBase;
    private String nombreHorarioBase;
    private Integer idDiaSemana;
    private String nombreDia;
    private Integer ordenDia;
    private LocalDate fechaInicioVigencia;
    private LocalDate fechaFinVigencia;
    private String motivoCambio;
    private LocalDateTime fechaAsignacion;
    private Boolean activo;
}

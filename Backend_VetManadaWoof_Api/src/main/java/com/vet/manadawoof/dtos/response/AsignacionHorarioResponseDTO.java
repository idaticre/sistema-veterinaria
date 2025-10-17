package com.vet.manadawoof.dtos.response;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AsignacionHorarioResponseDTO {
    private Long id;
    private Long idColaborador;
    private Integer idHorarioBase;
    private Integer idDia;
    private LocalDateTime fechaAsignacion;
    private Boolean activo;
    
    // Nombres legibles para frontend
    private String colaborador;
    private String horario;
    private String dia;
    
    // fallback para operaciones tipo SP
    private String mensaje;
}

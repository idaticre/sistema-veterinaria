package com.vet.manadawoof.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HistorialHorarioResponseDTO {
    private Long id;
    private Long idColaborador;
    private String colaborador;
    private String dia;
    private String horario;
    private String rangoHorario;
    private String desde;
    private String hasta;
    private Integer diasVigencia;
    private String motivoCambio;
    private String estado;
    private String fechaRegistro;
    
    // Contexto adicional
    private Boolean esVigente;
    private Integer diasRestantes;
}

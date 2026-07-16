/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vet.manadawoof.dtos.response;

import com.vet.manadawoof.entity.TipoAccionEntity;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuditoriaResponseDTO {
    
    private Long id;
    private UsuarioAuditoriaResponseDTO usuario;
    private TipoAccionEntity tipoAccion;
    private String entidad;
    private String descripcion;
    private LocalDateTime fecha;
    
}

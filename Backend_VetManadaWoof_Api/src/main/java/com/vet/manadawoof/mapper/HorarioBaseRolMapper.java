package com.vet.manadawoof.mapper;

import com.vet.manadawoof.dtos.response.HorarioBaseRolResponseDTO;
import com.vet.manadawoof.entity.HorarioBaseRolEntity;
import org.springframework.stereotype.Component;

@Component
public class HorarioBaseRolMapper {
    /**
     * Convierte una Entity a Response DTO con información completa
     */
    public HorarioBaseRolResponseDTO toResponse(HorarioBaseRolEntity entity) {
        if(entity == null) {
            return null;
        }
        
        return HorarioBaseRolResponseDTO.builder()
                .id(entity.getId())
                .idRol(entity.getRol().getId())
                .nombreRol(entity.getRol().getNombre())
                .idHorarioBase(entity.getHorarioBase().getId())
                .nombreHorarioBase(entity.getHorarioBase().getNombre())
                .horaInicio(entity.getHorarioBase().getHoraInicio())
                .horaFin(entity.getHorarioBase().getHoraFin())
                .idDiaSemana(entity.getDia().getId())
                .nombreDia(entity.getDia().getNombre())
                .ordenDia(entity.getDia().getOrden())
                .build();
    }
}

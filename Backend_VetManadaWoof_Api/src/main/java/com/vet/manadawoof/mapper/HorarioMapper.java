package com.vet.manadawoof.mapper;

import com.vet.manadawoof.dtos.response.HorarioResponseDTO;
import com.vet.manadawoof.entity.HorarioEntity;
import org.springframework.stereotype.Component;

@Component
public class HorarioMapper {

    public HorarioResponseDTO toResponse(HorarioEntity entity) {
        if (entity == null) {
            return null;
        }

        return HorarioResponseDTO.builder()
                .id(entity.getId())
                .trabajadorId(entity.getColaborador().getId())
                .nombreColaborador(entity.getColaborador().getEntidad().getNombre())
                .diaId(entity.getDia().getId())
                .nombreDia(entity.getDia().getNombre())
                .trabaja(entity.getTrabaja())
                .horaInicio(entity.getHoraInicio())
                .horaFin(entity.getHoraFin())
                .build();
    }
}

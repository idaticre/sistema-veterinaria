package com.vet.manadawoof.mapper;

import com.vet.manadawoof.dtos.response.IngresoServicioResponseDTO;
import com.vet.manadawoof.entity.IngresoServicioEntity;
import org.springframework.stereotype.Component;

@Component
public class IngresoServicioMapper {
    
    public IngresoServicioResponseDTO toDto(IngresoServicioEntity entity) {
        if(entity == null) return null;
        
        return IngresoServicioResponseDTO.builder()
                .id(entity.getId())
                .codigo(entity.getCodigo())
                .idAgenda(entity.getAgenda() != null ? entity.getAgenda().getId() : null)
                .idServicio(entity.getServicio() != null ? entity.getServicio().getId() : null)
                .idColaborador(entity.getColaborador() != null ? entity.getColaborador().getId() : null)
                .idVeterinario(entity.getVeterinario() != null ? entity.getVeterinario().getId() : null)
                .cantidad(entity.getCantidad())
                .duracionMin(entity.getDuracionMin())
                .valorServicio(entity.getValorServicio())
                .observaciones(entity.getObservaciones())
                .fechaRegistro(entity.getFechaRegistro())
                .build();
    }
}

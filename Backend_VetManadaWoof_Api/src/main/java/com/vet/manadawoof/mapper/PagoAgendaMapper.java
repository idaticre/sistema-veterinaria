package com.vet.manadawoof.mapper;

import com.vet.manadawoof.dtos.response.PagoAgendaResponseDTO;
import com.vet.manadawoof.entity.AgendaPagoEntity;
import org.springframework.stereotype.Component;

@Component
public class PagoAgendaMapper {
    
    public PagoAgendaResponseDTO toDto(AgendaPagoEntity entity) {
        if(entity == null) return null;
        
        return PagoAgendaResponseDTO.builder()
                .id(entity.getId())
                .codigo(entity.getCodigo())
                .idAgenda(entity.getAgenda() != null ? entity.getAgenda().getId() : null)
                .idMedioPago(entity.getMedioPago() != null ? entity.getMedioPago().getId() : null)
                .idUsuario(entity.getUsuario() != null ? entity.getUsuario().getId() : null)
                .monto(entity.getMonto())
                .fechaPago(entity.getFechaPago())
                .observaciones(entity.getObservaciones())
                .build();
    }
}

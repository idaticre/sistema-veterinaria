package com.vet.manadawoof.mapper;

import com.vet.manadawoof.dtos.response.AgendaResponseDTO;
import com.vet.manadawoof.entity.AgendaEntity;
import org.springframework.stereotype.Component;

@Component
public class AgendaMapper {
    
    public AgendaResponseDTO toDto(AgendaEntity entity) {
        if(entity == null) return null;
        
        return AgendaResponseDTO.builder()
                .id(entity.getId())
                .codigo(entity.getCodigo())
                .idCliente(entity.getCliente() != null ? entity.getCliente().getId() : null)
                .idMascota(entity.getMascota() != null ? entity.getMascota().getId() : null)
                .idMedioSolicitud(entity.getMedioSolicitud() != null ? entity.getMedioSolicitud().getId() : null)
                .fecha(entity.getFecha())
                .hora(entity.getHora())
                .duracionEstimadaMin(entity.getDuracionEstimadaMin())
                .abonoInicial(entity.getAbonoInicial())
                .totalCita(entity.getTotalCita())
                .idEstado(entity.getEstado() != null ? entity.getEstado().getId() : null)
                .observaciones(entity.getObservaciones())
                .fechaRegistro(entity.getFechaRegistro())
                .build();
    }
}

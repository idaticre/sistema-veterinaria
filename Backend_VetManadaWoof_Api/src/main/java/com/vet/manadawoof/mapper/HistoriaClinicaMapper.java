package com.vet.manadawoof.mapper;

import com.vet.manadawoof.dtos.response.HistoriaClinicaResponseDTO;
import com.vet.manadawoof.entity.HistoriaClinicaEntity;
import org.springframework.stereotype.Component;

@Component
public class HistoriaClinicaMapper {
    
    public HistoriaClinicaResponseDTO toDto(HistoriaClinicaEntity entity) {
        if(entity == null) return null;
        
        return HistoriaClinicaResponseDTO.builder()
                .id(entity.getId())
                .codigo(entity.getCodigo())
                .idMascota(entity.getMascota() != null ? entity.getMascota().getId() : null)
                .fechaApertura(entity.getFechaApertura())
                .observacionesGenerales(entity.getObservacionesGenerales())
                .activo(entity.getActivo())
                .fechaRegistro(entity.getFechaRegistro())
                .build();
    }
}

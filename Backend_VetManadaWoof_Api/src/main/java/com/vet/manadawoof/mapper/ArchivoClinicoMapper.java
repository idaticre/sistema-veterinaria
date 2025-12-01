package com.vet.manadawoof.mapper;

import com.vet.manadawoof.dtos.response.ArchivoClinicoResponseDTO;
import com.vet.manadawoof.entity.HistoriaClinicaArchivoEntity;
import org.springframework.stereotype.Component;

@Component
public class ArchivoClinicoMapper {
    
    public ArchivoClinicoResponseDTO toDto(HistoriaClinicaArchivoEntity entity) {
        if(entity == null) return null;
        
        return ArchivoClinicoResponseDTO.builder()
                .id(entity.getId())
                .codigo(entity.getCodigo())
                .idRegistroAtencion(entity.getRegistroAtencion() != null ? entity.getRegistroAtencion().getId() : null)
                .idTipoArchivo(entity.getTipoArchivo() != null ? entity.getTipoArchivo().getId() : null)
                .nombreArchivo(entity.getNombreArchivo())
                .extensionArchivo(entity.getExtensionArchivo())
                .descripcion(entity.getDescripcion())
                .fechaSubida(entity.getFechaSubida())
                .build();
    }
}

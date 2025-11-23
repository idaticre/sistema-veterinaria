package com.vet.manadawoof.mapper;

import com.vet.manadawoof.dtos.request.HorarioBaseRequestDTO;
import com.vet.manadawoof.dtos.response.HorarioBaseResponseDTO;
import com.vet.manadawoof.entity.HorarioBaseEntity;
import org.springframework.stereotype.Component;

@Component
public class HorarioBaseMapper {
    /**
     * Convierte un Request DTO a Entity
     */
    public HorarioBaseEntity toEntity(HorarioBaseRequestDTO request) {
        if(request == null) {
            return null;
        }
        
        return HorarioBaseEntity.builder()
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .horaInicio(request.getHoraInicio())
                .horaFin(request.getHoraFin())
                .minutoToleranciaEntrada(request.getMinutoToleranciaEntrada())
                .minutosLunch(request.getMinutosLunch())
                .overnight(request.getOvernight())
                .activo(request.getActivo())
                .build();
    }
    
    /**
     * Convierte una Entity a Response DTO
     */
    public HorarioBaseResponseDTO toResponse(HorarioBaseEntity entity) {
        if(entity == null) {
            return null;
        }
        
        return HorarioBaseResponseDTO.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .descripcion(entity.getDescripcion())
                .horaInicio(entity.getHoraInicio())
                .horaFin(entity.getHoraFin())
                .minutoToleranciaEntrada(entity.getMinutoToleranciaEntrada())
                .minutosLunch(entity.getMinutosLunch())
                .overnight(entity.getOvernight())
                .activo(entity.getActivo())
                .build();
    }
    
    /**
     * Actualiza una Entity existente con datos del Request
     */
    public void updateEntityFromRequest(HorarioBaseEntity entity, HorarioBaseRequestDTO request) {
        if(entity == null || request == null) {
            return;
        }
        
        if(request.getNombre() != null) {
            entity.setNombre(request.getNombre());
        }
        if(request.getDescripcion() != null) {
            entity.setDescripcion(request.getDescripcion());
        }
        if(request.getHoraInicio() != null) {
            entity.setHoraInicio(request.getHoraInicio());
        }
        if(request.getHoraFin() != null) {
            entity.setHoraFin(request.getHoraFin());
        }
        if(request.getMinutoToleranciaEntrada() != null) {
            entity.setMinutoToleranciaEntrada(request.getMinutoToleranciaEntrada());
        }
        if(request.getMinutosLunch() != null) {
            entity.setMinutosLunch(request.getMinutosLunch());
        }
        if(request.getOvernight() != null) {
            entity.setOvernight(request.getOvernight());
        }
        if(request.getActivo() != null) {
            entity.setActivo(request.getActivo());
        }
    }
}

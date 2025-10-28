package com.vet.manadawoof.mapper;

import com.vet.manadawoof.dtos.request.VacunaMascotaRequestDTO;
import com.vet.manadawoof.dtos.response.VacunaMascotaResponseDTO;
import com.vet.manadawoof.entity.*;

/**
 * Mapper para VacunaMascotaEntity.
 * Se encarga de:
 * Convertir Entity -> ResponseDTO
 * Actualizar Entity desde RequestDTO
 */
public class VacunaMascotaMapper {
    /**
     * Convierte una Entity a ResponseDTO
     */
    public static VacunaMascotaResponseDTO toResponse(VacunaMascotaEntity entity) {
        if(entity == null) return null;
        
        return VacunaMascotaResponseDTO.builder()
                .id(entity.getId())
                .codigo(entity.getCodigo())
                .idVacuna(entity.getVacuna() != null ? entity.getVacuna().getId() : null)
                .idMascota(entity.getMascota() != null ? entity.getMascota().getId() : null)
                .idVia(entity.getVia() != null ? entity.getVia().getId() : null)
                .dosis(entity.getDosis())
                .fechaAplicacion(entity.getFechaAplicacion())
                .fechaModificacion(entity.getFechaModificacion())
                .durabilidad(entity.getDurabilidad())
                .proxDosis(entity.getProximaDosis())
                .idColaborador(entity.getColaborador() != null ? entity.getColaborador().getId() : null)
                .idVeterinario(entity.getVeterinario() != null ? entity.getVeterinario().getId() : null)
                .observaciones(entity.getObservaciones())
                .fechaRegistro(entity.getFechaRegistro())
                .activo(entity.getActivo())
                .build();
    }
    
    // Actualiza la Entity desde el RequestDTO
    // Solo actualiza los campos que no sean null
    public static void updateEntityFromRequest(
            VacunaMascotaRequestDTO request, VacunaMascotaEntity entity,
            MascotaEntity mascota, VacunaEntity vacuna, AplicacionViaEntity via,
            ColaboradorEntity colaborador, VeterinarioEntity veterinario
    ) {
        if(request.getDosis() != null) entity.setDosis(request.getDosis());
        if(request.getFechaAplicacion() != null) entity.setFechaAplicacion(request.getFechaAplicacion());
        if(request.getDurabilidad() != null) entity.setDurabilidad(request.getDurabilidad());
        if(request.getProxDosis() != null) entity.setProximaDosis(request.getProxDosis());
        if(request.getObservaciones() != null) entity.setObservaciones(request.getObservaciones());
        if(request.getActivo() != null) entity.setActivo(request.getActivo());
        
        // Relaciones
        if(vacuna != null) entity.setVacuna(vacuna);
        if(mascota != null) entity.setMascota(mascota);
        if(via != null) entity.setVia(via);
        if(colaborador != null) entity.setColaborador(colaborador);
        if(veterinario != null) entity.setVeterinario(veterinario);
    }
}

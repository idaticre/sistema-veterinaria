package com.vet.manadawoof.mapper;

import com.vet.manadawoof.dtos.request.MedicamentoMascotaRequestDTO;
import com.vet.manadawoof.dtos.response.MedicamentoMascotaResponseDTO;
import com.vet.manadawoof.entity.*;

/**
 * Mapper para MedicamentoMascotaEntity.
 * Se encarga de:
 * Convertir Entity -> ResponseDTO
 * Actualizar Entity desde RequestDTO
 */
public class MedicamentoMascotaMapper {
    /**
     * Convierte una Entity a ResponseDTO
     */
    public static MedicamentoMascotaResponseDTO toResponse(MedicamentoMascotaEntity entity) {
        if(entity == null) return null;
        
        return MedicamentoMascotaResponseDTO.builder()
                .id(entity.getId())
                .codigo(entity.getCodigo())
                .idMascota(entity.getMascota() != null ? entity.getMascota().getId() : null)
                .idMedicamento(entity.getMedicamento() != null ? entity.getMedicamento().getId() : null)
                .idVia(entity.getVia() != null ? entity.getVia().getId() : null)
                .dosis(entity.getDosis())
                .fechaAplicacion(entity.getFechaAplicacion())
                .idColaborador(entity.getColaborador() != null ? entity.getColaborador().getId() : null)
                .idVeterinario(entity.getVeterinario() != null ? entity.getVeterinario().getId() : null)
                .observaciones(entity.getObservaciones())
                .activo(entity.getActivo())
                .fechaRegistro(entity.getFechaRegistro())
                .fechaModificacion(entity.getFechaModificacion())
                .build();
    }
    
    
    // Actualiza la Entity desde el RequestDTO
    // Solo actualiza los campos que no sean null
    
    public static void updateEntityFromRequest(
            MedicamentoMascotaRequestDTO request, MedicamentoMascotaEntity entity,
            MascotaEntity mascota, MedicamentoEntity medicamento, AplicacionViaEntity via,
            ColaboradorEntity colaborador, VeterinarioEntity veterinario
    ) {
        
        if(request.getDosis() != null) entity.setDosis(request.getDosis());
        if(request.getFechaAplicacion() != null) entity.setFechaAplicacion(request.getFechaAplicacion());
        if(request.getObservaciones() != null) entity.setObservaciones(request.getObservaciones());
        if(request.getActivo() != null) entity.setActivo(request.getActivo());
        
        // Relaciones
        if(mascota != null) entity.setMascota(mascota);
        if(medicamento != null) entity.setMedicamento(medicamento);
        if(via != null) entity.setVia(via);
        if(colaborador != null) entity.setColaborador(colaborador);
        if(veterinario != null) entity.setVeterinario(veterinario);
    }
}

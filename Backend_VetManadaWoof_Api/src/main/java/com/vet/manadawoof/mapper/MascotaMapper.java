package com.vet.manadawoof.mapper;

import com.vet.manadawoof.dtos.request.MascotaRequestDTO;
import com.vet.manadawoof.dtos.response.MascotaResponseDTO;
import com.vet.manadawoof.entity.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Mapper manual para la entidad Mascota, adaptado a los DTOs existentes.
 * - toResponse: Entity -> MascotaResponseDTO
 * - toRequest:  Entity -> MascotaRequestDTO (útil para prellenado / eliminación lógica)
 * - updateEntityFromRequest: MascotaRequestDTO + relaciones cargadas -> MascotaEntity
 * <p>
 * No cambia los DTOs; mapea exactamente las propiedades tal como están definidas.
 */
@Component
public class MascotaMapper {
    
    // ENTITY -> RESPONSE DTO
    
    public MascotaResponseDTO toResponse(MascotaEntity e) {
        if(e == null) return null;
        
        return MascotaResponseDTO.builder()
                .id(e.getId())
                .codigo(e.getCodigo())
                .nombre(e.getNombre())
                .sexo(e.getSexo())
                .idCliente(e.getCliente() != null ? e.getCliente().getId() : null)
                .idRaza(e.getRaza() != null ? e.getRaza().getId() : null)
                .idEspecie(e.getEspecie() != null ? e.getEspecie().getId() : null)
                .idEstado(e.getEstado() != null ? e.getEstado().getId() : null)
                .idTamano(e.getTamano() != null ? e.getTamano().getId() : null)
                .idEtapa(e.getEtapa() != null ? e.getEtapa().getId() : null)
                .fechaNacimiento(e.getFechaNacimiento())
                .pelaje(e.getPelaje())
                .esterilizado(e.getEsterilizado())
                .alergias(e.getAlergias())
                .peso(e.getPeso() != null ? e.getPeso() : BigDecimal.ZERO)
                .chip(e.getChip())
                .pedigree(e.getPedigree())
                .factorDea(e.getFactorDea())
                .agresividad(e.getAgresividad())
                .foto(e.getFoto())
                .fechaRegistro(e.getFechaRegistro())
                .fechaModificacion(e.getFechaModificacion())
                .idColaborador(e.getColaborador() != null ? e.getColaborador().getId() : null)
                .idVeterinario(e.getVeterinario() != null ? e.getVeterinario().getId() : null)
                .build();
    }
    
    // -------------------------------
    // ENTITY -> REQUEST DTO
    // -------------------------------
    public MascotaRequestDTO toRequest(MascotaEntity e) {
        if(e == null) return null;
        
        return MascotaRequestDTO.builder()
                .id(e.getId())
                .nombre(e.getNombre())
                .sexo(e.getSexo())
                .idCliente(e.getCliente() != null ? e.getCliente().getId() : null)
                .idRaza(e.getRaza() != null ? e.getRaza().getId() : null)
                .idEspecie(e.getEspecie() != null ? e.getEspecie().getId() : null)
                .idEstado(e.getEstado() != null ? e.getEstado().getId() : null)
                .fechaNacimiento(e.getFechaNacimiento())
                .pelaje(e.getPelaje())
                .idTamano(e.getTamano() != null ? e.getTamano().getId() : null)
                .idEtapa(e.getEtapa() != null ? e.getEtapa().getId() : null)
                .esterilizado(e.getEsterilizado())
                .alergias(e.getAlergias())
                .peso(e.getPeso() != null ? e.getPeso() : BigDecimal.ZERO)
                .chip(e.getChip())
                .pedigree(e.getPedigree())
                .factorDea(e.getFactorDea())
                .agresividad(e.getAgresividad())
                .foto(e.getFoto())
                .idColaborador(e.getColaborador() != null ? e.getColaborador().getId() : null)
                .idVeterinario(e.getVeterinario() != null ? e.getVeterinario().getId() : null)
                .build();
    }
    
    // -------------------------------
    // REQUEST DTO + ENTIDADES RELACIONADAS -> ENTITY
    // (para crear o actualizar; las entidades relacionadas deben estar cargadas en el service)
    // -------------------------------
    public void updateEntityFromRequest(
            MascotaRequestDTO dto,
            MascotaEntity entity,
            ClienteEntity cliente,
            RazaEntity raza,
            EspecieEntity especie,
            EstadoMascotaEntity estado,
            TamanoMascEntity tamano,
            EtapaVidaEntity etapa,
            ColaboradorEntity colaborador,
            VeterinarioEntity veterinario
    ) {
        if(dto == null || entity == null) return;
        
        // Campos simples (solo si no son null en el request — service decide qué enviar)
        if(dto.getNombre() != null) entity.setNombre(dto.getNombre());
        if(dto.getSexo() != null) entity.setSexo(dto.getSexo());
        if(dto.getFechaNacimiento() != null) entity.setFechaNacimiento(dto.getFechaNacimiento());
        if(dto.getPelaje() != null) entity.setPelaje(dto.getPelaje());
        if(dto.getEsterilizado() != null) entity.setEsterilizado(dto.getEsterilizado());
        if(dto.getAlergias() != null) entity.setAlergias(dto.getAlergias());
        if(dto.getPeso() != null) entity.setPeso(dto.getPeso());
        if(dto.getChip() != null) entity.setChip(dto.getChip());
        if(dto.getPedigree() != null) entity.setPedigree(dto.getPedigree());
        if(dto.getFactorDea() != null) entity.setFactorDea(dto.getFactorDea());
        if(dto.getAgresividad() != null) entity.setAgresividad(dto.getAgresividad());
        if(dto.getFoto() != null) entity.setFoto(dto.getFoto());
        
        // Relaciones: el service debe haber cargado/obtenido estas entidades (o null si no aplica)
        if(cliente != null) entity.setCliente(cliente);
        if(raza != null) entity.setRaza(raza);
        if(especie != null) entity.setEspecie(especie);
        if(estado != null) entity.setEstado(estado);
        if(tamano != null) entity.setTamano(tamano);
        if(etapa != null) entity.setEtapa(etapa);
        if(colaborador != null) entity.setColaborador(colaborador);
        if(veterinario != null) entity.setVeterinario(veterinario);
        
        // NOTA: campos fechaRegistro/fechaModificacion los maneja la capa de persistencia (antes/after persist)
        // Si quieres que el mapper toque fechaModificacion, hazlo explícito aquí (no lo hago por defecto).
    }
}

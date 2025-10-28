package com.vet.manadawoof.mapper;

import com.vet.manadawoof.dtos.request.MascotaRequestDTO;
import com.vet.manadawoof.dtos.response.MascotaResponseDTO;
import com.vet.manadawoof.entity.*;

public class MascotaMapper {
    
    // Convierte Entity -> ResponseDTO
    public static MascotaResponseDTO toResponse(MascotaEntity entity) {
        // Retornar null si no hay entity
        if(entity == null) return null;
        
        return MascotaResponseDTO.builder()
                .id(entity.getId()) // ID interno
                .codigo(entity.getCodigo()) // Código único
                .nombre(entity.getNombre()) // Nombre de la mascota
                .sexo(entity.getSexo()) // Sexo: M/H/O
                .fechaNacimiento(entity.getFechaNacimiento()) // Fecha de nacimiento
                .pelaje(entity.getPelaje()) // Pelaje
                .esterilizado(entity.getEsterilizado()) // Esterilizado
                .alergias(entity.getAlergias()) // Alergias
                .peso(entity.getPeso()) // Peso
                .chip(entity.getChip()) // Tiene chip?
                .pedigree(entity.getPedigree()) // Tiene pedigree?
                .factorDea(entity.getFactorDea()) // Factor DEA
                .agresividad(entity.getAgresividad()) // Agresividad
                .foto(entity.getFoto()) // Foto (URL o path)
                .fechaRegistro(entity.getFechaRegistro()) // Fecha de registro
                .fechaModificacion(entity.getFechaModificacion()) // Fecha última modificación
                // Relaciones solo IDs
                .idCliente(entity.getCliente() != null ? entity.getCliente().getId() : null) // ID cliente
                .idRaza(entity.getRaza() != null ? entity.getRaza().getId() : null) // ID raza
                .idEspecie(entity.getEspecie() != null ? entity.getEspecie().getId() : null) // ID especie
                .idEstado(entity.getEstado() != null ? entity.getEstado().getId() : null) // ID estado
                .idTamano(entity.getTamano() != null ? entity.getTamano().getId() : null) // ID tamaño
                .idEtapa(entity.getEtapa() != null ? entity.getEtapa().getId() : null) // ID etapa
                .build();
    }
    
    // Actualiza los campos de la Entity desde el RequestDTO
    // Solo actualiza relaciones si no son null para no sobreescribir datos existentes
    public static void updateEntityFromRequest(
            MascotaRequestDTO request, MascotaEntity entity,
            ClienteEntity cliente, RazaEntity raza, EspecieEntity especie,
            EstadoMascotaEntity estado, TamanoMascEntity tamano,
            EtapaVidaEntity etapa, ColaboradorEntity colaborador,
            VeterinarioEntity veterinario
    ) {
        
        // Campos simples
        // Nombre
        if(request.getNombre() != null) entity.setNombre(request.getNombre());
        // Sexo
        if(request.getSexo() != null) entity.setSexo(request.getSexo());
        // Fecha nacimiento
        if(request.getFechaNacimiento() != null) entity.setFechaNacimiento(request.getFechaNacimiento());
        // Pelaje
        if(request.getPelaje() != null) entity.setPelaje(request.getPelaje());
        
        // Esterilizado
        if(request.getEsterilizado() != null) entity.setEsterilizado(request.getEsterilizado());
        // Alergias
        if(request.getAlergias() != null) entity.setAlergias(request.getAlergias());
        // Peso
        if(request.getPeso() != null) entity.setPeso(request.getPeso());
        // Chip
        if(request.getChip() != null) entity.setChip(request.getChip());
        // Pedigree
        if(request.getPedigree() != null) entity.setPedigree(request.getPedigree());
        // Factor DEA
        if(request.getFactorDea() != null) entity.setFactorDea(request.getFactorDea());
        // Agresividad
        if(request.getAgresividad() != null) entity.setAgresividad(request.getAgresividad());
        // Foto
        if(request.getFoto() != null) entity.setFoto(request.getFoto());
        
        // Relaciones: solo set si no son null
        // Cliente
        if(cliente != null) entity.setCliente(cliente);
        // Raza
        if(raza != null) entity.setRaza(raza);
        // Especie
        if(especie != null) entity.setEspecie(especie);
        // Estado
        if(estado != null) entity.setEstado(estado);
        // Tamaño
        if(tamano != null) entity.setTamano(tamano);
        // Etapa de vida
        if(etapa != null) entity.setEtapa(etapa);
        // Colaborador asignado
        if(colaborador != null) entity.setColaborador(colaborador);
        // Veterinario asignado
        if(veterinario != null) entity.setVeterinario(veterinario);
    }
    
    
    // Convierte Entity -> RequestDTO (para reutilizar en actualizaciones)
    public static MascotaRequestDTO toRequest(MascotaEntity entity) {
        if(entity == null) return null;
        
        MascotaRequestDTO dto = new MascotaRequestDTO();
        dto.setId(entity.getId());
        dto.setNombre(entity.getNombre());
        dto.setSexo(entity.getSexo());
        dto.setFechaNacimiento(entity.getFechaNacimiento());
        dto.setPelaje(entity.getPelaje());
        dto.setEsterilizado(entity.getEsterilizado());
        dto.setAlergias(entity.getAlergias());
        dto.setPeso(entity.getPeso());
        dto.setChip(entity.getChip());
        dto.setPedigree(entity.getPedigree());
        dto.setFactorDea(entity.getFactorDea());
        dto.setAgresividad(entity.getAgresividad());
        dto.setFoto(entity.getFoto());
        
        // Relaciones por ID
        dto.setIdCliente(entity.getCliente() != null ? entity.getCliente().getId() : null);
        dto.setIdRaza(entity.getRaza() != null ? entity.getRaza().getId() : null);
        dto.setIdEspecie(entity.getEspecie() != null ? entity.getEspecie().getId() : null);
        dto.setIdEstado(entity.getEstado() != null ? entity.getEstado().getId() : null);
        dto.setIdTamano(entity.getTamano() != null ? entity.getTamano().getId() : null);
        dto.setIdEtapa(entity.getEtapa() != null ? entity.getEtapa().getId() : null);
        
        return dto;
    }
    
}

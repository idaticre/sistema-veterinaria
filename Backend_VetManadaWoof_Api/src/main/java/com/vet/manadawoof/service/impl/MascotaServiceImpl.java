package com.vet.manadawoof.service.impl;

import com.vet.manadawoof.dtos.request.MascotaRequestDTO;
import com.vet.manadawoof.dtos.response.MascotaResponseDTO;
import com.vet.manadawoof.entity.*;
import com.vet.manadawoof.mapper.MascotaMapper;
import com.vet.manadawoof.service.MascotaService;
import jakarta.persistence.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MascotaServiceImpl implements MascotaService {
    
    // Inyección del EntityManager para consultas y SPs
    private final EntityManager entityManager;
    
    // ---------------- CREAR MASCOTA ----------------
    @Override
    @Transactional // Transacción de creación de mascota
    public MascotaResponseDTO crearMascota(MascotaRequestDTO request) {
        // Crear StoredProcedureQuery para llamar al SP "registrar_mascota"
        StoredProcedureQuery sp = entityManager.createStoredProcedureQuery("registrar_mascota")
                // Registrar parámetros IN (ORDEN EXACTO DEL SP)
                .registerStoredProcedureParameter("p_nombre", String.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_sexo", String.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_id_cliente", Long.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_id_raza", Integer.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_id_especie", Integer.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_fecha_nacimiento", java.sql.Date.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_pelaje", String.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_id_tamano", Integer.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_id_etapa", Integer.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_esterilizado", Integer.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_alergias", String.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_peso", Double.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_chip", Integer.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_pedigree", Integer.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_factor_dea", Integer.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_agresividad", Integer.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_foto", String.class, ParameterMode.IN)
                
                // Registrar parámetros OUT
                .registerStoredProcedureParameter("p_id_mascota", Long.class, ParameterMode.OUT)
                .registerStoredProcedureParameter("p_codigo_mascota", String.class, ParameterMode.OUT)
                .registerStoredProcedureParameter("p_mensaje", String.class, ParameterMode.OUT);
        // Setear valores de parámetros desde el DTO
        sp.setParameter("p_id_cliente", request.getIdCliente());
        sp.setParameter("p_id_raza", request.getIdRaza());
        sp.setParameter("p_id_especie", request.getIdEspecie());
        sp.setParameter("p_id_tamano", request.getIdTamano());
        sp.setParameter("p_id_etapa", request.getIdEtapa());
        sp.setParameter("p_nombre", request.getNombre());
        sp.setParameter("p_sexo", request.getSexo());
        sp.setParameter("p_fecha_nacimiento", java.sql.Date.valueOf(request.getFechaNacimiento()));
        sp.setParameter("p_pelaje", request.getPelaje());
        sp.setParameter("p_esterilizado", request.getEsterilizado() != null && request.getEsterilizado() ? 1 : 0);
        sp.setParameter("p_alergias", request.getAlergias());
        sp.setParameter("p_peso", request.getPeso() != null ? request.getPeso().doubleValue() : 0.0);
        sp.setParameter("p_chip", request.getChip() != null && request.getChip() ? 1 : 0);
        sp.setParameter("p_pedigree", request.getPedigree() != null && request.getPedigree() ? 1 : 0);
        sp.setParameter("p_factor_dea", request.getFactorDea() != null && request.getFactorDea() ? 1 : 0);
        sp.setParameter("p_agresividad", request.getAgresividad() != null && request.getAgresividad() ? 1 : 0);
        sp.setParameter("p_foto", request.getFoto());
        
        
        // Ejecutar SP
        sp.execute();
        
        // Obtener mensaje de salida del SP
        String mensaje = (String) sp.getOutputParameterValue("p_mensaje");
        // Validar errores del SP
        if(mensaje != null && mensaje.startsWith("ERROR:")) throw new RuntimeException(mensaje);
        
        // Obtener ID generado de la mascota
        Long id = ((Number) sp.getOutputParameterValue("p_id_mascota")).longValue();
        
        // Recuperar entity
        // Esto se ajustará a futuro con los token u otros
        MascotaEntity entity = entityManager.find(MascotaEntity.class, id);
        
        // Asignar colaborador y veterinario
        entity.setColaborador(null); // o colaborador "Sistema"
        entity.setVeterinario(null);
        
        // Mapear a ResponseDTO
        return MascotaMapper.toResponse(entity);
    }
    
    // ---------------- ACTUALIZAR MASCOTA ----------------
    @Override
    @Transactional
    public MascotaResponseDTO actualizarMascota(MascotaRequestDTO request) {
        // Validar ID
        if(request.getId() == null) {
            throw new RuntimeException("ERROR: id es requerido para actualizar.");
        }
        
        // Buscar entity
        MascotaEntity entity = entityManager.find(MascotaEntity.class, request.getId());
        // Validar existencia
        if(entity == null) {
            throw new RuntimeException("ERROR: Mascota no encontrada.");
        }
        
        // Recuperar entidades relacionadas (orden lógico igual que SP)
        ClienteEntity cliente = entityManager.find(ClienteEntity.class, request.getIdCliente());
        RazaEntity raza = request.getIdRaza() != null ? entityManager.find(RazaEntity.class, request.getIdRaza()) : null;
        EspecieEntity especie = entityManager.find(EspecieEntity.class, request.getIdEspecie());
        TamanoMascEntity tamano = entityManager.find(TamanoMascEntity.class, request.getIdTamano());
        EtapaVidaEntity etapa = entityManager.find(EtapaVidaEntity.class, request.getIdEtapa());
        EstadoMascotaEntity estado = request.getIdEstado() != null ? entityManager.find(EstadoMascotaEntity.class, request.getIdEstado()) : null;

// Opcionales (no se reciben en request)
        ColaboradorEntity colaborador = null;
        VeterinarioEntity veterinario = null;
        
        // Actualizar booleanos directos
        entity.setEsterilizado(request.getEsterilizado() != null && request.getEsterilizado());
        entity.setChip(request.getChip() != null && request.getChip());
        entity.setPedigree(request.getPedigree() != null && request.getPedigree());
        entity.setFactorDea(request.getFactorDea() != null && request.getFactorDea());
        entity.setAgresividad(request.getAgresividad() != null && request.getAgresividad());
        
        // Actualizar entity usando Mapper
        MascotaMapper.updateEntityFromRequest(request, entity, cliente, raza, especie, estado, tamano, etapa, colaborador, veterinario);
        
        // Guardar cambios
        entityManager.merge(entity);
        
        // Mapear a ResponseDTO
        return MascotaMapper.toResponse(entity);
    }
    
    // ---------------- ELIMINAR MASCOTA (cambia estado a INACTIVA) ----------------
    @Override
    @Transactional
    public MascotaResponseDTO eliminarMascota(Long id) {
        if(id == null) {
            throw new RuntimeException("ERROR: id es requerido para eliminar.");
        }
        
        MascotaEntity entity = entityManager.find(MascotaEntity.class, id);
        if(entity == null) {
            throw new RuntimeException("ERROR: Mascota no encontrada.");
        }
        
        Integer estadoInactivaId = entityManager
                .createQuery("SELECT e.id FROM EstadoMascotaEntity e WHERE e.nombre LIKE 'INACTIVA%'", Integer.class)
                .setMaxResults(1)
                .getSingleResult();
        
        // Crear DTO completo
        MascotaRequestDTO dto = MascotaMapper.toRequest(entity);
        dto.setIdEstado(estadoInactivaId);
        
        return actualizarMascota(dto);
    }
    
    
    // ---------------- LISTAR MASCOTAS ----------------
    @Override
    @Transactional(readOnly = true)
    public List<MascotaResponseDTO> listarMascotas() {
        // Obtener todas las mascotas de la DB
        List<MascotaEntity> mascotas = entityManager.createQuery("SELECT m FROM MascotaEntity m", MascotaEntity.class).getResultList();
        
        // Mapear a ResponseDTO
        return mascotas.stream().map(MascotaMapper :: toResponse).toList();
    }
}

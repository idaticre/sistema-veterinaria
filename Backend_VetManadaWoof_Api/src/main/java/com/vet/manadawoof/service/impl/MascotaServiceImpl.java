package com.vet.manadawoof.service.impl;

import com.vet.manadawoof.dtos.request.MascotaRequestDTO;
import com.vet.manadawoof.dtos.response.MascotaResponseDTO;
import com.vet.manadawoof.entity.*;
import com.vet.manadawoof.mapper.MascotaMapper;
import com.vet.manadawoof.service.MascotaService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MascotaServiceImpl implements MascotaService {
    
    private final EntityManager entityManager;
    private final MascotaMapper mascotaMapper; // <- inyección correcta del mapper
    
    // ---------------- CREAR MASCOTA ----------------
    @Override
    @Transactional
    public MascotaResponseDTO crearMascota(MascotaRequestDTO request) {
        StoredProcedureQuery sp = entityManager.createStoredProcedureQuery("registrar_mascota")
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
                .registerStoredProcedureParameter("p_peso", java.math.BigDecimal.class, ParameterMode.IN) // cambio de tipo de variable por error de declaracion
                .registerStoredProcedureParameter("p_chip", Integer.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_pedigree", Integer.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_factor_dea", Integer.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_agresividad", Integer.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_foto", String.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_id_mascota", Long.class, ParameterMode.OUT)
                .registerStoredProcedureParameter("p_codigo_mascota", String.class, ParameterMode.OUT)
                .registerStoredProcedureParameter("p_mensaje", String.class, ParameterMode.OUT);
        
        // Setear parámetros desde el DTO
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
        
        String mensaje = (String) sp.getOutputParameterValue("p_mensaje");
        if(mensaje != null && mensaje.startsWith("ERROR:")) throw new RuntimeException(mensaje);
        
        Long id = ((Number) sp.getOutputParameterValue("p_id_mascota")).longValue();
        MascotaEntity entity = entityManager.find(MascotaEntity.class, id);
        
        entity.setColaborador(null); // o asignar colaborador "Sistema"
        entity.setVeterinario(null);
        
        return mascotaMapper.toResponse(entity);
    }
    
    // ---------------- ACTUALIZAR MASCOTA ----------------
    @Override
    @Transactional
    public MascotaResponseDTO actualizarMascota(Long id, MascotaRequestDTO request) {
        // Usamos el id del path, no del request
        MascotaEntity entity = entityManager.find(MascotaEntity.class, id);
        if(entity == null) throw new RuntimeException("ERROR: Mascota no encontrada.");
        
        // Recuperar entidades relacionadas
        ClienteEntity cliente = entityManager.find(ClienteEntity.class, request.getIdCliente());
        RazaEntity raza = request.getIdRaza() != null ? entityManager.find(RazaEntity.class, request.getIdRaza()) : null;
        EspecieEntity especie = entityManager.find(EspecieEntity.class, request.getIdEspecie());
        TamanoMascEntity tamano = entityManager.find(TamanoMascEntity.class, request.getIdTamano());
        EtapaVidaEntity etapa = entityManager.find(EtapaVidaEntity.class, request.getIdEtapa());
        EstadoMascotaEntity estado = request.getIdEstado() != null ? entityManager.find(EstadoMascotaEntity.class, request.getIdEstado()) : null;
        
        ColaboradorEntity colaborador = null;
        VeterinarioEntity veterinario = null;
        
        // Actualizar booleanos
        entity.setEsterilizado(Boolean.TRUE.equals(request.getEsterilizado()));
        entity.setChip(Boolean.TRUE.equals(request.getChip()));
        entity.setPedigree(Boolean.TRUE.equals(request.getPedigree()));
        entity.setFactorDea(Boolean.TRUE.equals(request.getFactorDea()));
        entity.setAgresividad(Boolean.TRUE.equals(request.getAgresividad()));
        
        // Mapear el resto de campos
        mascotaMapper.updateEntityFromRequest(request, entity, cliente, raza, especie, estado, tamano, etapa, colaborador, veterinario);
        
        entityManager.merge(entity);
        
        return mascotaMapper.toResponse(entity);
    }
    
    
    // ---------------- ELIMINAR MASCOTA (INACTIVA) ----------------
    @Override
    @Transactional
    public MascotaResponseDTO eliminarMascota(Long id) {
        if(id == null) throw new RuntimeException("ERROR: id es requerido para eliminar.");
        
        MascotaEntity entity = entityManager.find(MascotaEntity.class, id);
        if(entity == null) throw new RuntimeException("ERROR: Mascota no encontrada.");
        
        Integer estadoInactivaId = entityManager
                .createQuery("SELECT e.id FROM EstadoMascotaEntity e WHERE e.nombre LIKE 'INACTIVA%'", Integer.class)
                .setMaxResults(1)
                .getSingleResult();
        
        MascotaRequestDTO dto = mascotaMapper.toRequest(entity);
        dto.setIdEstado(estadoInactivaId);
        
        return actualizarMascota(entity.getId(), dto);
    }
    
    // ---------------- LISTAR MASCOTAS ----------------
    @Override
    @Transactional
    public List<MascotaResponseDTO> listarMascotas() {
        List<MascotaEntity> mascotas = entityManager
                .createQuery("SELECT m FROM MascotaEntity m", MascotaEntity.class)
                .getResultList();
        
        return mascotas.stream().map(mascotaMapper :: toResponse).toList();
    }
    
    @Override
    @Transactional
    public MascotaResponseDTO obtenerPorId(Long id) {
        MascotaEntity entity = entityManager.find(MascotaEntity.class, id);
        if(entity == null) throw new RuntimeException("Mascota no encontrada");
        return mascotaMapper.toResponse(entity);
    }
    
}

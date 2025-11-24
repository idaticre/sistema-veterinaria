package com.vet.manadawoof.service.impl;

import com.vet.manadawoof.dtos.request.MedicamentoMascotaRequestDTO;
import com.vet.manadawoof.dtos.response.MedicamentoMascotaResponseDTO;
import com.vet.manadawoof.entity.*;
import com.vet.manadawoof.mapper.MedicamentoMascotaMapper;
import com.vet.manadawoof.service.MedicamentoMascotaService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementación del servicio de MedicamentoMascota
 * usando SPs para la creación y actualización.
 */
@Service
@RequiredArgsConstructor
public class MedicamentoMascotaServiceImpl implements MedicamentoMascotaService {
    
    private final EntityManager entityManager;
    
    // ---------------- CREAR MEDICAMENTO MASCOTA ----------------
    @Override
    @Transactional
    public MedicamentoMascotaResponseDTO crearMedicamentoMascota(MedicamentoMascotaRequestDTO request) {
        StoredProcedureQuery sp = entityManager.createStoredProcedureQuery("registrar_medicamento_mascota")
                .registerStoredProcedureParameter("p_id_mascota", Long.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_id_medicamento", Integer.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_id_via", Integer.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_dosis", String.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_fecha_aplicacion", java.sql.Date.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_id_colaborador", Long.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_id_veterinario", Long.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_observaciones", String.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_mensaje", String.class, ParameterMode.OUT);
        
        sp.setParameter("p_id_mascota", request.getIdMascota());
        sp.setParameter("p_id_medicamento", request.getIdMedicamento());
        sp.setParameter("p_id_via", request.getIdVia());
        sp.setParameter("p_dosis", request.getDosis());
        sp.setParameter("p_fecha_aplicacion", request.getFechaAplicacion() != null ? java.sql.Date.valueOf(request.getFechaAplicacion()) : null);
        sp.setParameter("p_id_colaborador", request.getIdColaborador() != null ? request.getIdColaborador() : null);
        sp.setParameter("p_id_veterinario", request.getIdVeterinario() != null ? request.getIdVeterinario() : null);
        sp.setParameter("p_observaciones", request.getObservaciones());
        
        sp.execute();
        
        String mensaje = (String) sp.getOutputParameterValue("p_mensaje");
        if(mensaje != null && mensaje.startsWith("ERROR:")) {
            throw new RuntimeException(mensaje);
        }
        
        MedicamentoMascotaEntity entity = entityManager.createQuery(
                        "SELECT m FROM MedicamentoMascotaEntity m WHERE m.mascota.id = :idMascota AND m.fechaAplicacion = :fecha ORDER BY m.id DESC",
                        MedicamentoMascotaEntity.class)
                .setParameter("idMascota", request.getIdMascota())
                .setParameter("fecha", request.getFechaAplicacion())
                .setMaxResults(1)
                .getSingleResult();
        
        return MedicamentoMascotaMapper.toResponse(entity);
    }
    
    // ---------------- ACTUALIZAR MEDICAMENTO MASCOTA ----------------
    @Override
    @Transactional
    public MedicamentoMascotaResponseDTO actualizarMedicamentoMascota(MedicamentoMascotaRequestDTO request) {
        if(request.getId() == null) {
            throw new RuntimeException("ERROR: id es requerido para actualizar.");
        }
        
        MedicamentoMascotaEntity entity = entityManager.find(MedicamentoMascotaEntity.class, request.getId());
        if(entity == null) {
            throw new RuntimeException("ERROR: Registro de medicamento no encontrado.");
        }
        
        MascotaEntity mascota = entityManager.find(MascotaEntity.class, request.getIdMascota());
        MedicamentoEntity medicamento = entityManager.find(MedicamentoEntity.class, request.getIdMedicamento());
        AplicacionViaEntity via = entityManager.find(AplicacionViaEntity.class, request.getIdVia());
        ColaboradorEntity colaborador = request.getIdColaborador() != null ? entityManager.find(ColaboradorEntity.class, request.getIdColaborador()) : null;
        VeterinarioEntity veterinario = request.getIdVeterinario() != null ? entityManager.find(VeterinarioEntity.class, request.getIdVeterinario()) : null;
        
        MedicamentoMascotaMapper.updateEntityFromRequest(request, entity, mascota, medicamento, via, colaborador, veterinario);
        entityManager.merge(entity);
        
        return MedicamentoMascotaMapper.toResponse(entity);
    }
    
    // ---------------- LISTAR MEDICAMENTOS ----------------
    @Override
    @Transactional
    public List<MedicamentoMascotaResponseDTO> listarMedicamentosMascota() {
        List<MedicamentoMascotaEntity> lista = entityManager.createQuery("SELECT m FROM MedicamentoMascotaEntity m", MedicamentoMascotaEntity.class)
                .getResultList();
        
        return lista.stream()
                .map(MedicamentoMascotaMapper :: toResponse)
                .toList();
    }
    
    // ---------------- ELIMINAR MEDICAMENTO (lógico) ----------------
    @Override
    @Transactional
    public MedicamentoMascotaResponseDTO eliminarMedicamento(Integer id) {
        if(id == null) {
            throw new RuntimeException("ERROR: id es requerido para eliminar.");
        }
        
        MedicamentoMascotaEntity entity = entityManager.find(MedicamentoMascotaEntity.class, id);
        if(entity == null) {
            throw new RuntimeException("ERROR: Registro de medicamento no encontrado.");
        }
        
        StoredProcedureQuery sp = entityManager.createStoredProcedureQuery("actualizar_medicamento_mascota")
                .registerStoredProcedureParameter("p_id_registro", Integer.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_id_mascota", Long.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_id_medicamento", Integer.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_id_via", Integer.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_dosis", String.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_fecha_aplicacion", java.sql.Date.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_id_colaborador", Long.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_id_veterinario", Long.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_observaciones", String.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_activo", Integer.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_mensaje", String.class, ParameterMode.OUT);
        
        sp.setParameter("p_id_registro", entity.getId());
        sp.setParameter("p_id_mascota", entity.getMascota().getId());
        sp.setParameter("p_id_medicamento", entity.getMedicamento().getId());
        sp.setParameter("p_id_via", entity.getVia().getId());
        sp.setParameter("p_dosis", entity.getDosis());
        sp.setParameter("p_fecha_aplicacion", entity.getFechaAplicacion() != null ? java.sql.Date.valueOf(entity.getFechaAplicacion()) : null);
        sp.setParameter("p_id_colaborador", entity.getColaborador() != null ? entity.getColaborador().getId() : null);
        sp.setParameter("p_id_veterinario", entity.getVeterinario() != null ? entity.getVeterinario().getId() : null);
        sp.setParameter("p_observaciones", entity.getObservaciones());
        sp.setParameter("p_activo", 0);
        
        sp.execute();
        
        String mensaje = (String) sp.getOutputParameterValue("p_mensaje");
        if(mensaje != null && mensaje.startsWith("ERROR:")) {
            throw new RuntimeException(mensaje);
        }
        
        entityManager.refresh(entity);
        return MedicamentoMascotaMapper.toResponse(entity);
    }
    
}

package com.vet.manadawoof.service.impl;

import com.vet.manadawoof.dtos.request.VacunaMascotaRequestDTO;
import com.vet.manadawoof.dtos.response.VacunaMascotaResponseDTO;
import com.vet.manadawoof.entity.*;
import com.vet.manadawoof.mapper.VacunaMascotaMapper;
import com.vet.manadawoof.repository.VacunaMascotaRepository;
import com.vet.manadawoof.service.VacunaMascotaService;
import jakarta.persistence.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VacunaMascotaServiceImpl implements VacunaMascotaService {
    
    private final VacunaMascotaRepository repository;
    
    @PersistenceContext
    private EntityManager entityManager;
    
    // ---------------- LISTAR VACUNAS ----------------
    @Override
    @Transactional
    public List<VacunaMascotaResponseDTO> listarVacunasMascota() {
        List<VacunaMascotaEntity> lista = repository.findAll();
        return lista.stream()
                .map(VacunaMascotaMapper :: toResponse)
                .toList();
    }
    
    // ---------------- CREAR VACUNA MASCOTA ----------------
    @Override
    @Transactional
    public VacunaMascotaResponseDTO crearVacunaMascota(VacunaMascotaRequestDTO request) {
        StoredProcedureQuery sp = entityManager.createStoredProcedureQuery("registrar_vacuna_mascota")
                .registerStoredProcedureParameter("p_id_mascota", Long.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_id_vacuna", Integer.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_id_via", Integer.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_dosis", String.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_fecha_aplicacion", Date.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_durabilidad_anios", Integer.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_id_colaborador", Long.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_id_veterinario", Long.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_observaciones", String.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_mensaje", String.class, ParameterMode.OUT);
        
        sp.setParameter("p_id_mascota", request.getIdMascota());
        sp.setParameter("p_id_vacuna", request.getIdVacuna());
        sp.setParameter("p_id_via", request.getIdVia());
        sp.setParameter("p_dosis", request.getDosis());
        sp.setParameter("p_fecha_aplicacion", Date.valueOf(request.getFechaAplicacion()));
        sp.setParameter("p_durabilidad_anios", request.getDurabilidad());
        sp.setParameter("p_id_colaborador", request.getIdColaborador() != null ? request.getIdColaborador() : null);
        sp.setParameter("p_id_veterinario", request.getIdVeterinario() != null ? request.getIdVeterinario() : null);
        sp.setParameter("p_observaciones", request.getObservaciones());
        
        sp.execute();
        
        String mensaje = (String) sp.getOutputParameterValue("p_mensaje");
        if(mensaje != null && mensaje.startsWith("ERROR:")) {
            throw new RuntimeException(mensaje);
        }
        
        // Recuperar el último registro insertado (por mascota y fecha)
        VacunaMascotaEntity entity = entityManager.createQuery(
                        "SELECT v FROM VacunaMascotaEntity v WHERE v.mascota.id = :idMascota AND v.fechaAplicacion = :fecha ORDER BY v.id DESC",
                        VacunaMascotaEntity.class)
                .setParameter("idMascota", request.getIdMascota())
                .setParameter("fecha", request.getFechaAplicacion())
                .setMaxResults(1)
                .getSingleResult();
        
        VacunaMascotaResponseDTO response = VacunaMascotaMapper.toResponse(entity);
        response.setMensaje(mensaje);
        
        return response;
    }
    
    // ---------------- ACTUALIZAR VACUNA MASCOTA ----------------
    @Override
    @Transactional
    public VacunaMascotaResponseDTO actualizarVacunaMascota(VacunaMascotaRequestDTO request) {
        if(request.getId() == null) {
            throw new RuntimeException("ERROR: id es requerido para actualizar.");
        }
        
        VacunaMascotaEntity entity = entityManager.find(VacunaMascotaEntity.class, request.getId());
        if(entity == null) {
            throw new RuntimeException("ERROR: Registro de vacuna no encontrado.");
        }
        
        MascotaEntity mascota = entityManager.find(MascotaEntity.class, request.getIdMascota());
        VacunaEntity vacuna = entityManager.find(VacunaEntity.class, request.getIdVacuna());
        AplicacionViaEntity via = entityManager.find(AplicacionViaEntity.class, request.getIdVia());
        ColaboradorEntity colaborador = request.getIdColaborador() != null
                ? entityManager.find(ColaboradorEntity.class, request.getIdColaborador())
                : null;
        VeterinarioEntity veterinario = request.getIdVeterinario() != null
                ? entityManager.find(VeterinarioEntity.class, request.getIdVeterinario())
                : null;
        
        VacunaMascotaMapper.updateEntityFromRequest(request, entity, mascota, vacuna, via, colaborador, veterinario);
        entityManager.merge(entity);
        
        return VacunaMascotaMapper.toResponse(entity);
    }
    
    // ---------------- ELIMINAR VACUNA (lógico) ----------------
    @Override
    @Transactional
    public VacunaMascotaResponseDTO eliminarVacuna(Integer id) {
        if(id == null) {
            throw new RuntimeException("ERROR: id es requerido para eliminar.");
        }
        
        VacunaMascotaEntity entity = entityManager.find(VacunaMascotaEntity.class, id);
        if(entity == null) {
            throw new RuntimeException("ERROR: Registro de vacuna no encontrado.");
        }
        
        StoredProcedureQuery sp = entityManager.createStoredProcedureQuery("actualizar_vacuna_mascota")
                .registerStoredProcedureParameter("p_id_vacuna_mascota", Long.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_id_vacuna", Integer.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_id_mascota", Long.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_id_via", Integer.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_dosis", String.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_fecha_aplicacion", Date.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_durabilidad_anios", Integer.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_id_colaborador", Long.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_id_veterinario", Long.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_observaciones", String.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_activo", Integer.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_mensaje", String.class, ParameterMode.OUT);
        
        sp.setParameter("p_id_vacuna_mascota", entity.getId());
        sp.setParameter("p_id_vacuna", entity.getVacuna().getId());
        sp.setParameter("p_id_mascota", entity.getMascota().getId());
        sp.setParameter("p_id_via", entity.getVia().getId());
        sp.setParameter("p_dosis", entity.getDosis());
        sp.setParameter("p_fecha_aplicacion", Date.valueOf(entity.getFechaAplicacion()));
        sp.setParameter("p_durabilidad_anios", entity.getDurabilidad());
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
        VacunaMascotaResponseDTO response = VacunaMascotaMapper.toResponse(entity);
        response.setMensaje(mensaje);
        return response;
    }
}

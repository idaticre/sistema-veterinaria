package com.vet.manadawoof.service.impl;

import com.vet.manadawoof.dtos.request.HistoriaClinicaRequestDTO;
import com.vet.manadawoof.dtos.response.HistoriaClinicaResponseDTO;
import com.vet.manadawoof.entity.HistoriaClinicaEntity;
import com.vet.manadawoof.mapper.HistoriaClinicaMapper;
import com.vet.manadawoof.repository.HistoriaClinicaRepository;
import com.vet.manadawoof.service.HistoriaClinicaService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class HistoriaClinicaServiceImpl implements HistoriaClinicaService {
    
    @PersistenceContext
    private final EntityManager entityManager;
    private final HistoriaClinicaRepository historiaClinicaRepository;
    private final HistoriaClinicaMapper historiaClinicaMapper;
    
    @Override
    @Transactional
    public HistoriaClinicaResponseDTO crear(HistoriaClinicaRequestDTO dto) {
        StoredProcedureQuery sp = entityManager.createStoredProcedureQuery("crear_historia_clinica");
        
        registrarParametros(sp);
        
        sp.setParameter("p_id_mascota", dto.getIdMascota());
        sp.setParameter("p_observaciones_generales", dto.getObservacionesGenerales());
        
        sp.execute();
        
        Long idHistoria = ((Number) sp.getOutputParameterValue("p_id_historia")).longValue();
        String mensaje = (String) sp.getOutputParameterValue("p_mensaje");
        
        if(mensaje != null && mensaje.startsWith("ERROR")) {
            return HistoriaClinicaResponseDTO.builder().mensaje(mensaje).build();
        }
        
        HistoriaClinicaResponseDTO response = obtenerPorId(idHistoria); response.setMensaje(mensaje); return response;
    }
    
    @Override
    @Transactional
    public HistoriaClinicaResponseDTO obtenerPorId(Long id) {
        HistoriaClinicaEntity entity = historiaClinicaRepository.findById(id).orElse(null);
        if(entity == null) return null; return historiaClinicaMapper.toDto(entity);
    }
    
    @Override
    @Transactional
    public HistoriaClinicaResponseDTO obtenerPorMascota(Long idMascota) {
        HistoriaClinicaEntity entity = historiaClinicaRepository.findByMascotaId(idMascota).orElse(null);
        if(entity == null) return null; return historiaClinicaMapper.toDto(entity);
    }
    
    @Override
    @Transactional
    public Page<HistoriaClinicaResponseDTO> listar(Pageable pageable) {
        Page<HistoriaClinicaEntity> page = historiaClinicaRepository.findByActivoTrue(pageable);
        return page.map(historiaClinicaMapper :: toDto);
    }
    
    @Override
    @Transactional
    public Map<String, Object> consultarHistorialMascota(Long idMascota) {
        StoredProcedureQuery sp = entityManager.createStoredProcedureQuery("sp_consultar_historial_mascota");
        
        sp.registerStoredProcedureParameter("p_id_mascota", Long.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_mensaje", String.class, ParameterMode.OUT);
        
        sp.setParameter("p_id_mascota", idMascota);
        
        sp.execute(); String mensaje = (String) sp.getOutputParameterValue("p_mensaje");
        
        Map<String, Object> resultado = new HashMap<>(); resultado.put("mensaje", mensaje);
        
        if(mensaje != null && mensaje.startsWith("ERROR")) {
            return resultado;
        }
        
        // Resultado 1: Info general mascota + historia
        List<?> infoGeneral = sp.getResultList(); resultado.put("infoGeneral", infoGeneral);
        
        // Resultado 2: Registros de atención
        if(sp.hasMoreResults()) {
            List<?> registros = sp.getResultList(); resultado.put("registros", registros);
        }
        
        // Resultado 3: Archivos médicos
        if(sp.hasMoreResults()) {
            List<?> archivos = sp.getResultList(); resultado.put("archivos", archivos);
        }
        
        return resultado;
    }
    
    private void registrarParametros(StoredProcedureQuery sp) {
        sp.registerStoredProcedureParameter("p_id_mascota", Long.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_observaciones_generales", String.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_id_historia", Long.class, ParameterMode.OUT);
        sp.registerStoredProcedureParameter("p_codigo", String.class, ParameterMode.OUT);
        sp.registerStoredProcedureParameter("p_mensaje", String.class, ParameterMode.OUT);
    }
}

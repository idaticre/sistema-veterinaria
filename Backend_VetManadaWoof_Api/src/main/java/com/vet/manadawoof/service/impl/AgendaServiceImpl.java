package com.vet.manadawoof.service.impl;

import com.vet.manadawoof.dtos.request.AgendaRequestDTO;
import com.vet.manadawoof.dtos.response.AgendaResponseDTO;
import com.vet.manadawoof.entity.AgendaEntity;
import com.vet.manadawoof.mapper.AgendaMapper;
import com.vet.manadawoof.repository.AgendaRepository;
import com.vet.manadawoof.service.AgendaService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;

@Service
@RequiredArgsConstructor
public class AgendaServiceImpl implements AgendaService {
    
    @PersistenceContext
    private final EntityManager entityManager;
    private final AgendaRepository agendaRepository;
    private final AgendaMapper agendaMapper;
    
    @Override
    @Transactional
    public AgendaResponseDTO crear(AgendaRequestDTO dto) {
        StoredProcedureQuery sp = entityManager.createStoredProcedureQuery("gestionar_agenda");
        
        registrarParametros(sp);
        
        sp.setParameter("p_accion", "CREAR");
        sp.setParameter("p_id_agenda", null);
        sp.setParameter("p_id_cliente", dto.getIdCliente());
        sp.setParameter("p_id_mascota", dto.getIdMascota());
        sp.setParameter("p_id_medio_solicitud", dto.getIdMedioSolicitud());
        sp.setParameter("p_fecha", Date.valueOf(dto.getFecha()));
        sp.setParameter("p_hora", Time.valueOf(dto.getHora()));
        sp.setParameter("p_duracion_estimada_min", dto.getDuracionEstimadaMin());
        sp.setParameter("p_id_estado", dto.getIdEstado());
        sp.setParameter("p_abono_inicial", dto.getAbonoInicial() != null ? dto.getAbonoInicial() : BigDecimal.ZERO);
        sp.setParameter("p_observaciones", dto.getObservaciones());
        
        sp.execute();
        
        Object resultIdObject = sp.getOutputParameterValue("p_id_resultado");
        String mensaje = (String) sp.getOutputParameterValue("p_mensaje");
        
        if(mensaje != null && mensaje.startsWith("ERROR")) {
            return AgendaResponseDTO.builder().mensaje(mensaje).build();
        }
        Long idResultado = ((Number) sp.getOutputParameterValue("p_id_resultado")).longValue();
        return obtenerPorId(idResultado);
    }
    
    @Override
    @Transactional
    public AgendaResponseDTO actualizar(AgendaRequestDTO dto) {
        StoredProcedureQuery sp = entityManager.createStoredProcedureQuery("gestionar_agenda");
        
        registrarParametros(sp);
        
        sp.setParameter("p_accion", "ACTUALIZAR");
        sp.setParameter("p_id_agenda", dto.getId());
        sp.setParameter("p_id_cliente", dto.getIdCliente());
        sp.setParameter("p_id_mascota", dto.getIdMascota());
        sp.setParameter("p_id_medio_solicitud", dto.getIdMedioSolicitud());
        sp.setParameter("p_fecha", Date.valueOf(dto.getFecha()));
        sp.setParameter("p_hora", Time.valueOf(dto.getHora()));
        sp.setParameter("p_duracion_estimada_min", dto.getDuracionEstimadaMin());
        sp.setParameter("p_id_estado", dto.getIdEstado());
        sp.setParameter("p_abono_inicial", dto.getAbonoInicial() != null ? dto.getAbonoInicial() : BigDecimal.ZERO);
        sp.setParameter("p_observaciones", dto.getObservaciones());
        
        sp.execute();
        
        Object resultIdObject = sp.getOutputParameterValue("p_id_resultado");
        String mensaje = (String) sp.getOutputParameterValue("p_mensaje");
        
        if(mensaje != null && mensaje.startsWith("ERROR")) {
            return AgendaResponseDTO.builder().mensaje(mensaje).build();
        }
        Long idResultado = ((Number) sp.getOutputParameterValue("p_id_resultado")).longValue();
        return obtenerPorId(idResultado);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<AgendaResponseDTO> listar(Pageable pageable) {
        Page<AgendaEntity> page = agendaRepository.findAll(pageable);
        return page.map(agendaMapper :: toDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public AgendaResponseDTO obtenerPorId(Long idAgenda) {
        AgendaEntity agenda = agendaRepository.findById(idAgenda).orElse(null);
        if(agenda == null) return null;
        return agendaMapper.toDto(agenda);
    }
    
    private void registrarParametros(StoredProcedureQuery sp) {
        sp.registerStoredProcedureParameter("p_accion", String.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_id_agenda", Long.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_id_cliente", Long.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_id_mascota", Long.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_id_medio_solicitud", Integer.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_fecha", Date.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_hora", Time.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_duracion_estimada_min", Integer.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_id_estado", Integer.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_abono_inicial", BigDecimal.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_observaciones", String.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_id_resultado", Long.class, ParameterMode.OUT);
        sp.registerStoredProcedureParameter("p_codigo", String.class, ParameterMode.OUT);
        sp.registerStoredProcedureParameter("p_mensaje", String.class, ParameterMode.OUT);
    }
}

package com.vet.manadawoof.service.impl;

import com.vet.manadawoof.dtos.request.AgendaRequestDTO;
import com.vet.manadawoof.dtos.response.AgendaResponseDTO;
import com.vet.manadawoof.mapper.AgendaMapper;
import com.vet.manadawoof.service.AgendaService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AgendaServiceImpl implements AgendaService {
    
    @PersistenceContext
    private final EntityManager entityManager;
    private final AgendaMapper agendaMapper;
    
    @Override
    @Transactional
    public AgendaResponseDTO crear(AgendaRequestDTO dto) {
        StoredProcedureQuery sp = entityManager.createStoredProcedureQuery("sp_gestionar_agenda");
        
        // Registrar parámetros
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
        
        // Asignar valores
        sp.setParameter("p_accion", "CREAR"); sp.setParameter("p_id_agenda", null);
        sp.setParameter("p_id_cliente", dto.getIdCliente()); sp.setParameter("p_id_mascota", dto.getIdMascota());
        sp.setParameter("p_id_medio_solicitud", dto.getIdMedioSolicitud());
        sp.setParameter("p_fecha", Date.valueOf(dto.getFecha()));
        sp.setParameter("p_hora", Time.valueOf(dto.getHora()));
        sp.setParameter("p_duracion_estimada_min", dto.getDuracionEstimadaMin());
        sp.setParameter("p_id_estado", dto.getIdEstado());
        sp.setParameter("p_abono_inicial", dto.getAbonoInicial() != null ? dto.getAbonoInicial() : BigDecimal.ZERO);
        sp.setParameter("p_observaciones", dto.getObservaciones());
        
        sp.execute();
        
        Long idResultado = ((Number) sp.getOutputParameterValue("p_id_resultado")).longValue();
        String codigo = (String) sp.getOutputParameterValue("p_codigo");
        String mensaje = (String) sp.getOutputParameterValue("p_mensaje");
        
        if(mensaje != null && mensaje.startsWith("ERROR")) {
            return AgendaResponseDTO.builder().mensaje(mensaje).build();
        }
        
        return obtenerPorId(idResultado);
    }
    
    @Override
    @Transactional
    public AgendaResponseDTO actualizar(AgendaRequestDTO dto) {
        StoredProcedureQuery sp = entityManager.createStoredProcedureQuery("sp_gestionar_agenda");
        
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
        
        sp.setParameter("p_accion", "ACTUALIZAR"); sp.setParameter("p_id_agenda", dto.getId());
        sp.setParameter("p_id_cliente", dto.getIdCliente()); sp.setParameter("p_id_mascota", dto.getIdMascota());
        sp.setParameter("p_id_medio_solicitud", dto.getIdMedioSolicitud());
        sp.setParameter("p_fecha", Date.valueOf(dto.getFecha()));
        sp.setParameter("p_hora", Time.valueOf(dto.getHora()));
        sp.setParameter("p_duracion_estimada_min", dto.getDuracionEstimadaMin());
        sp.setParameter("p_id_estado", dto.getIdEstado()); sp.setParameter("p_abono_inicial", dto.getAbonoInicial());
        sp.setParameter("p_observaciones", dto.getObservaciones());
        
        sp.execute();
        
        Long idResultado = ((Number) sp.getOutputParameterValue("p_id_resultado")).longValue();
        String mensaje = (String) sp.getOutputParameterValue("p_mensaje");
        
        if(mensaje != null && mensaje.startsWith("ERROR")) {
            return AgendaResponseDTO.builder().mensaje(mensaje).build();
        }
        
        return obtenerPorId(idResultado);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<AgendaResponseDTO> listar() {
        List<Object[]> results = entityManager.createNativeQuery("SELECT id, codigo, id_cliente, id_mascota, id_medio_solicitud, fecha, hora, " + "duracion_estimada_min, abono_inicial, total_cita, id_estado, observaciones, fecha_registro " + "FROM agenda ORDER BY fecha DESC, hora DESC").getResultList();
        
        return results.stream().map(this :: mapToDto).collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public AgendaResponseDTO obtenerPorId(Long idAgenda) {
        Object[] row = (Object[]) entityManager.createNativeQuery("SELECT id, codigo, id_cliente, id_mascota, id_medio_solicitud, fecha, hora, " + "duracion_estimada_min, abono_inicial, total_cita, id_estado, observaciones, fecha_registro " + "FROM agenda WHERE id = ?1").setParameter(1, idAgenda).getSingleResult();
        
        return mapToDto(row);
    }
    
    private AgendaResponseDTO mapToDto(Object[] row) {
        return agendaMapper.toDto(row);
    }
}

package com.vet.manadawoof.service.impl;

import com.vet.manadawoof.dtos.request.PagoAgendaRequestDTO;
import com.vet.manadawoof.dtos.response.PagoAgendaResponseDTO;
import com.vet.manadawoof.mapper.PagoAgendaMapper;
import com.vet.manadawoof.service.PagoAgendaService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PagoAgendaServiceImpl implements PagoAgendaService {
    
    @PersistenceContext
    private final EntityManager entityManager;
    private final PagoAgendaMapper pagoAgendaMapper;
    
    @Override
    @Transactional
    public PagoAgendaResponseDTO crear(PagoAgendaRequestDTO dto) {
        StoredProcedureQuery sp = entityManager.createStoredProcedureQuery("sp_gestionar_pago_agenda");
        
        sp.registerStoredProcedureParameter("p_accion", String.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_id_pago", Long.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_id_agenda", Long.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_id_medio_pago", Integer.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_id_usuario", Integer.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_monto", BigDecimal.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_observaciones", String.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_id_resultado", Long.class, ParameterMode.OUT);
        sp.registerStoredProcedureParameter("p_codigo", String.class, ParameterMode.OUT);
        sp.registerStoredProcedureParameter("p_total_abonado", BigDecimal.class, ParameterMode.OUT);
        sp.registerStoredProcedureParameter("p_saldo_pendiente", BigDecimal.class, ParameterMode.OUT);
        sp.registerStoredProcedureParameter("p_mensaje", String.class, ParameterMode.OUT);
        
        sp.setParameter("p_accion", "CREAR"); sp.setParameter("p_id_pago", null);
        sp.setParameter("p_id_agenda", dto.getIdAgenda()); sp.setParameter("p_id_medio_pago", dto.getIdMedioPago());
        sp.setParameter("p_id_usuario", dto.getIdUsuario()); sp.setParameter("p_monto", dto.getMonto());
        sp.setParameter("p_observaciones", dto.getObservaciones());
        
        sp.execute();
        
        Long idResultado = ((Number) sp.getOutputParameterValue("p_id_resultado")).longValue();
        String codigo = (String) sp.getOutputParameterValue("p_codigo");
        BigDecimal totalAbonado = (BigDecimal) sp.getOutputParameterValue("p_total_abonado");
        BigDecimal saldoPendiente = (BigDecimal) sp.getOutputParameterValue("p_saldo_pendiente");
        String mensaje = (String) sp.getOutputParameterValue("p_mensaje");
        
        if(mensaje != null && mensaje.startsWith("ERROR")) {
            return PagoAgendaResponseDTO.builder().mensaje(mensaje).build();
        }
        
        PagoAgendaResponseDTO response = obtenerPorId(idResultado); response.setTotalAbonado(totalAbonado);
        response.setSaldoPendiente(saldoPendiente); response.setMensaje(mensaje); return response;
    }
    
    @Override
    @Transactional
    public PagoAgendaResponseDTO eliminar(Long idPago, Long idAgenda) {
        StoredProcedureQuery sp = entityManager.createStoredProcedureQuery("sp_gestionar_pago_agenda");
        
        sp.registerStoredProcedureParameter("p_accion", String.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_id_pago", Long.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_id_agenda", Long.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_id_medio_pago", Integer.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_id_usuario", Integer.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_monto", BigDecimal.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_observaciones", String.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_id_resultado", Long.class, ParameterMode.OUT);
        sp.registerStoredProcedureParameter("p_codigo", String.class, ParameterMode.OUT);
        sp.registerStoredProcedureParameter("p_total_abonado", BigDecimal.class, ParameterMode.OUT);
        sp.registerStoredProcedureParameter("p_saldo_pendiente", BigDecimal.class, ParameterMode.OUT);
        sp.registerStoredProcedureParameter("p_mensaje", String.class, ParameterMode.OUT);
        
        sp.setParameter("p_accion", "ELIMINAR"); sp.setParameter("p_id_pago", idPago);
        sp.setParameter("p_id_agenda", idAgenda); sp.setParameter("p_id_medio_pago", null);
        sp.setParameter("p_id_usuario", null); sp.setParameter("p_monto", null);
        sp.setParameter("p_observaciones", null);
        
        sp.execute();
        
        String codigo = (String) sp.getOutputParameterValue("p_codigo");
        BigDecimal totalAbonado = (BigDecimal) sp.getOutputParameterValue("p_total_abonado");
        BigDecimal saldoPendiente = (BigDecimal) sp.getOutputParameterValue("p_saldo_pendiente");
        String mensaje = (String) sp.getOutputParameterValue("p_mensaje");
        
        return PagoAgendaResponseDTO.builder().codigo(codigo).totalAbonado(totalAbonado).saldoPendiente(saldoPendiente).mensaje(mensaje).build();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PagoAgendaResponseDTO> listarPorAgenda(Long idAgenda) {
        List<Object[]> results = entityManager.createNativeQuery("SELECT id, codigo, id_agenda, id_medio_pago, id_usuario, monto, " + "fecha_pago, observaciones " + "FROM agenda_pagos WHERE id_agenda = ?1 ORDER BY fecha_pago DESC").setParameter(1, idAgenda).getResultList();
        
        return results.stream().map(this :: mapToDto).collect(Collectors.toList());
    }
    
    private PagoAgendaResponseDTO obtenerPorId(Long id) {
        Object[] row = (Object[]) entityManager.createNativeQuery("SELECT id, codigo, id_agenda, id_medio_pago, id_usuario, monto, " + "fecha_pago, observaciones " + "FROM agenda_pagos WHERE id = ?1").setParameter(1, id).getSingleResult();
        
        return mapToDto(row);
    }
    
    private PagoAgendaResponseDTO mapToDto(Object[] row) {
        return pagoAgendaMapper.toDto(row);
    }
}

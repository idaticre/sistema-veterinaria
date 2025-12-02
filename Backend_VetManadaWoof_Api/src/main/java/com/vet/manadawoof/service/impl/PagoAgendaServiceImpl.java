package com.vet.manadawoof.service.impl;

import com.vet.manadawoof.dtos.request.PagoAgendaRequestDTO;
import com.vet.manadawoof.dtos.response.PagoAgendaResponseDTO;
import com.vet.manadawoof.entity.AgendaPagoEntity;
import com.vet.manadawoof.enums.AccionPagoAgenda;
import com.vet.manadawoof.mapper.PagoAgendaMapper;
import com.vet.manadawoof.repository.PagoAgendaRepository;
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

@Service
@RequiredArgsConstructor
public class PagoAgendaServiceImpl implements PagoAgendaService {
    
    @PersistenceContext
    private final EntityManager entityManager;
    private final PagoAgendaRepository pagoAgendaRepository;
    private final PagoAgendaMapper pagoAgendaMapper;
    
    @Override
    @Transactional
    public PagoAgendaResponseDTO crear(PagoAgendaRequestDTO dto) {
        StoredProcedureQuery sp = entityManager.createStoredProcedureQuery("gestionar_pago_agenda");
        
        registrarParametros(sp);
        
        sp.setParameter("p_accion", AccionPagoAgenda.CREAR.name());
        sp.setParameter("p_id_pago", null);
        sp.setParameter("p_id_agenda", dto.getIdAgenda());
        sp.setParameter("p_id_medio_pago", dto.getIdMedioPago());
        sp.setParameter("p_id_usuario", dto.getIdUsuario());
        sp.setParameter("p_monto", dto.getMonto());
        sp.setParameter("p_observaciones", dto.getObservaciones());
        
        sp.execute();
        
        // 1. Obtener el valor de salida como Object
        Object resultIdObject = sp.getOutputParameterValue("p_id_resultado");
        
        // 2. Obtener los demás parámetros de salida
        String mensaje = (String) sp.getOutputParameterValue("p_mensaje");
        BigDecimal totalAbonado = (BigDecimal) sp.getOutputParameterValue("p_total_abonado");
        BigDecimal saldoPendiente = (BigDecimal) sp.getOutputParameterValue("p_saldo_pendiente");
        
        // 3. Manejar error de negocio devuelto por el SP
        if(mensaje != null && mensaje.startsWith("ERROR")) {
            return PagoAgendaResponseDTO.builder().mensaje(mensaje).build();
        }
        
        // 4. 🔑 CORRECCIÓN: Verificar si el ID de resultado es NULL
        if (resultIdObject == null || !(resultIdObject instanceof Number)) {
            // Esto asegura que no se lance el NullPointerException
            return PagoAgendaResponseDTO.builder()
                .mensaje("ERROR: La operación fue exitosa, pero no se recuperó el ID del pago. Revise el SP 'gestionar_pago_agenda'.").build();
        }
        
        // 5. Conversión segura del ID
        Long idResultado = ((Number) resultIdObject).longValue();
        
        PagoAgendaResponseDTO response = obtenerPorId(idResultado);
        
        // 6. Asignar los demás datos al DTO de respuesta
        if (response != null) {
            response.setTotalAbonado(totalAbonado);
            response.setSaldoPendiente(saldoPendiente);
            response.setMensaje(mensaje);
        }
        
        return response;
    }
    
    @Override
    @Transactional
    public PagoAgendaResponseDTO eliminar(Long idPago, Long idAgenda) {
        StoredProcedureQuery sp = entityManager.createStoredProcedureQuery("gestionar_pago_agenda");
        
        registrarParametros(sp);
        
        sp.setParameter("p_accion", AccionPagoAgenda.ELIMINAR.name());
        sp.setParameter("p_id_pago", idPago);
        sp.setParameter("p_id_agenda", idAgenda);
        sp.setParameter("p_id_medio_pago", null);
        sp.setParameter("p_id_usuario", null);
        sp.setParameter("p_monto", null);
        sp.setParameter("p_observaciones", null);
        
        sp.execute();
        
        String codigo = (String) sp.getOutputParameterValue("p_codigo");
        String mensaje = (String) sp.getOutputParameterValue("p_mensaje");
        BigDecimal totalAbonado = (BigDecimal) sp.getOutputParameterValue("p_total_abonado");
        BigDecimal saldoPendiente = (BigDecimal) sp.getOutputParameterValue("p_saldo_pendiente");
        
        return PagoAgendaResponseDTO.builder()
                .codigo(codigo)
                .totalAbonado(totalAbonado)
                .saldoPendiente(saldoPendiente)
                .mensaje(mensaje)
                .build();
    }
    
    @Override
    @Transactional(readOnly = true)
    public PagoAgendaResponseDTO obtenerPorId(Long id) {
        AgendaPagoEntity entity = pagoAgendaRepository.findById(id).orElse(null);
        if(entity == null) return null;
        return pagoAgendaMapper.toDto(entity);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PagoAgendaResponseDTO> listarPorAgenda(Long idAgenda) {
        List<AgendaPagoEntity> pagos = pagoAgendaRepository.findByAgendaId(idAgenda);
        return pagos.stream()
                .map(pagoAgendaMapper :: toDto)
                .toList();
    }
    
    private void registrarParametros(StoredProcedureQuery sp) {
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
    }
}
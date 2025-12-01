package com.vet.manadawoof.service.impl;

import com.vet.manadawoof.dtos.request.IngresoServicioRequestDTO;
import com.vet.manadawoof.dtos.response.IngresoServicioResponseDTO;
import com.vet.manadawoof.entity.IngresoServicioEntity;
import com.vet.manadawoof.enums.AccionIngresoServicio;
import com.vet.manadawoof.mapper.IngresoServicioMapper;
import com.vet.manadawoof.repository.IngresoServicioRepository;
import com.vet.manadawoof.service.IngresoServicioService;
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
public class IngresoServicioServiceImpl implements IngresoServicioService {
    
    @PersistenceContext
    private final EntityManager entityManager;
    private final IngresoServicioRepository ingresoServicioRepository;
    private final IngresoServicioMapper ingresoServicioMapper;
    
    @Override
    @Transactional
    public IngresoServicioResponseDTO crear(IngresoServicioRequestDTO dto) {
        StoredProcedureQuery sp = entityManager.createStoredProcedureQuery("gestionar_ingreso_servicio");
        
        registrarParametros(sp);
        
        sp.setParameter("p_accion", AccionIngresoServicio.CREAR.name());
        sp.setParameter("p_id_ingreso", null);
        sp.setParameter("p_id_agenda", dto.getIdAgenda());
        sp.setParameter("p_id_servicio", dto.getIdServicio());
        sp.setParameter("p_id_colaborador", dto.getIdColaborador());
        sp.setParameter("p_id_veterinario", dto.getIdVeterinario());
        sp.setParameter("p_cantidad", dto.getCantidad());
        sp.setParameter("p_duracion_min", dto.getDuracionMin());
        sp.setParameter("p_valor_servicio", dto.getValorServicio());
        sp.setParameter("p_observaciones", dto.getObservaciones());
        
        sp.execute();
        
        Long idResultado = ((Number) sp.getOutputParameterValue("p_id_resultado")).longValue();
        String mensaje = (String) sp.getOutputParameterValue("p_mensaje");
        BigDecimal nuevoTotal = (BigDecimal) sp.getOutputParameterValue("p_nuevo_total_cita");
        
        if(mensaje != null && mensaje.startsWith("ERROR")) {
            return IngresoServicioResponseDTO.builder().mensaje(mensaje).build();
        }
        
        IngresoServicioResponseDTO response = obtenerPorId(idResultado);
        response.setNuevoTotalCita(nuevoTotal);
        response.setMensaje(mensaje);
        return response;
    }
    
    @Override
    @Transactional
    public IngresoServicioResponseDTO actualizar(IngresoServicioRequestDTO dto) {
        StoredProcedureQuery sp = entityManager.createStoredProcedureQuery("gestionar_ingreso_servicio");
        
        registrarParametros(sp);
        
        sp.setParameter("p_accion", AccionIngresoServicio.ACTUALIZAR.name());
        sp.setParameter("p_id_ingreso", dto.getIdIngreso());
        sp.setParameter("p_id_agenda", dto.getIdAgenda());
        sp.setParameter("p_id_servicio", dto.getIdServicio());
        sp.setParameter("p_id_colaborador", dto.getIdColaborador());
        sp.setParameter("p_id_veterinario", dto.getIdVeterinario());
        sp.setParameter("p_cantidad", dto.getCantidad());
        sp.setParameter("p_duracion_min", dto.getDuracionMin());
        sp.setParameter("p_valor_servicio", dto.getValorServicio());
        sp.setParameter("p_observaciones", dto.getObservaciones());
        
        sp.execute();
        
        Long idResultado = ((Number) sp.getOutputParameterValue("p_id_resultado")).longValue();
        String mensaje = (String) sp.getOutputParameterValue("p_mensaje");
        BigDecimal nuevoTotal = (BigDecimal) sp.getOutputParameterValue("p_nuevo_total_cita");
        
        if(mensaje != null && mensaje.startsWith("ERROR")) {
            return IngresoServicioResponseDTO.builder().mensaje(mensaje).build();
        }
        
        IngresoServicioResponseDTO response = obtenerPorId(idResultado);
        response.setNuevoTotalCita(nuevoTotal);
        response.setMensaje(mensaje);
        return response;
    }
    
    @Override
    @Transactional
    public IngresoServicioResponseDTO eliminar(Long idIngreso, Long idAgenda) {
        StoredProcedureQuery sp = entityManager.createStoredProcedureQuery("gestionar_ingreso_servicio");
        
        registrarParametros(sp);
        
        sp.setParameter("p_accion", AccionIngresoServicio.ELIMINAR.name());
        sp.setParameter("p_id_ingreso", idIngreso);
        sp.setParameter("p_id_agenda", idAgenda);
        sp.setParameter("p_id_servicio", null);
        sp.setParameter("p_id_colaborador", null);
        sp.setParameter("p_id_veterinario", null);
        sp.setParameter("p_cantidad", null);
        sp.setParameter("p_duracion_min", null);
        sp.setParameter("p_valor_servicio", null);
        sp.setParameter("p_observaciones", null);
        
        sp.execute();
        
        String codigo = (String) sp.getOutputParameterValue("p_codigo");
        String mensaje = (String) sp.getOutputParameterValue("p_mensaje");
        BigDecimal nuevoTotal = (BigDecimal) sp.getOutputParameterValue("p_nuevo_total_cita");
        
        return IngresoServicioResponseDTO.builder()
                .codigo(codigo)
                .nuevoTotalCita(nuevoTotal)
                .mensaje(mensaje)
                .build();
    }
    
    @Override
    @Transactional(readOnly = true)
    public IngresoServicioResponseDTO obtenerPorId(Long id) {
        IngresoServicioEntity entity = ingresoServicioRepository.findById(id).orElse(null);
        if(entity == null) return null;
        return ingresoServicioMapper.toDto(entity);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<IngresoServicioResponseDTO> listarPorAgenda(Long idAgenda) {
        List<IngresoServicioEntity> ingresos = ingresoServicioRepository.findByAgendaId(idAgenda);
        return ingresos.stream()
                .map(ingresoServicioMapper :: toDto)
                .toList();
    }
    
    private void registrarParametros(StoredProcedureQuery sp) {
        sp.registerStoredProcedureParameter("p_accion", String.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_id_ingreso", Long.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_id_agenda", Long.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_id_servicio", Integer.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_id_colaborador", Long.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_id_veterinario", Long.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_cantidad", Integer.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_duracion_min", Integer.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_valor_servicio", BigDecimal.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_observaciones", String.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_id_resultado", Long.class, ParameterMode.OUT);
        sp.registerStoredProcedureParameter("p_codigo", String.class, ParameterMode.OUT);
        sp.registerStoredProcedureParameter("p_nuevo_total_cita", BigDecimal.class, ParameterMode.OUT);
        sp.registerStoredProcedureParameter("p_mensaje", String.class, ParameterMode.OUT);
    }
}

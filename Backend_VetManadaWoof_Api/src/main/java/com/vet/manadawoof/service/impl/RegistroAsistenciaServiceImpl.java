package com.vet.manadawoof.service.impl;

import com.vet.manadawoof.dtos.request.RegistrarAsistenciaRequestDTO;
import com.vet.manadawoof.dtos.response.RegistroAsistenciaResponseDTO;
import com.vet.manadawoof.entity.EstadoAsistenciaEntity;
import com.vet.manadawoof.entity.RegistroAsistenciaEntity;
import com.vet.manadawoof.mapper.RegistroAsistenciaMapper;
import com.vet.manadawoof.repository.EstadoAsistenciaRepository;
import com.vet.manadawoof.repository.RegistroAsistenciaRepository;
import com.vet.manadawoof.service.RegistroAsistenciaService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Implementación del servicio de Registro de Asistencia.
 * Consume procedimientos almacenados y mapea resultados a DTOs.
 */
@Service
@RequiredArgsConstructor
public class RegistroAsistenciaServiceImpl implements RegistroAsistenciaService {
    
    private final EstadoAsistenciaRepository estadoRepository;
    
    private final RegistroAsistenciaRepository repository;
    
    @PersistenceContext
    private EntityManager entityManager;
    
    /**
     * Registra o actualiza la asistencia usando el SP 'gestionar_asistencia'.
     */
    @Override
    @Transactional
    public String registrar(RegistrarAsistenciaRequestDTO request) {
        StoredProcedureQuery sp = entityManager
                .createStoredProcedureQuery("gestionar_asistencia")
                .registerStoredProcedureParameter("p_colaborador_id", Integer.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_tipo_marca", String.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_mensaje", String.class, ParameterMode.OUT);
        
        sp.setParameter("p_colaborador_id", request.getIdColaborador());
        sp.setParameter("p_tipo_marca", request.getTipoMarca().name());
        sp.execute();
        
        String mensaje = (String) sp.getOutputParameterValue("p_mensaje");
        
        repository.findTopByColaborador_IdAndFechaOrderByIdDesc(
                request.getIdColaborador(), LocalDate.now()
        ).ifPresent(asistencia -> {
            String estadoDerivado = deducirEstadoDesdeMensaje(mensaje);
            
            EstadoAsistenciaEntity estado = estadoRepository.findByNombre(estadoDerivado)
                    .orElseThrow(() -> new RuntimeException("Estado de asistencia no encontrado: " + estadoDerivado));
            
            asistencia.setEstadoAsistencia(estado);
            repository.save(asistencia);
        });
        
        
        return mensaje;
    }
    
    /**
     * Obtiene la asistencia en un rango de fechas usando el SP 'ver_asistencia_por_rango'.
     */
    @Override
    public List<RegistroAsistenciaResponseDTO> verAsistenciaPorRango(LocalDate fechaInicio, LocalDate fechaFin, Integer idEstado) {
        
        StoredProcedureQuery sp = entityManager
                .createStoredProcedureQuery("ver_asistencia_por_rango")
                .registerStoredProcedureParameter("p_fecha_inicio", java.sql.Date.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_fecha_fin", java.sql.Date.class, ParameterMode.IN);
        
        sp.setParameter("p_fecha_inicio", java.sql.Date.valueOf(fechaInicio));
        sp.setParameter("p_fecha_fin", java.sql.Date.valueOf(fechaFin));
        sp.setParameter("p_id_estado", idEstado); // puede ser null
        
        List<Object[]> results = sp.getResultList();
        
        return results.stream().map(row -> RegistroAsistenciaResponseDTO.builder()
                .idColaborador(((Number) row[0]).longValue())
                .colaborador((String) row[1])
                .horario((String) row[2])
                .fecha(((java.sql.Date) row[3]).toLocalDate())
                .horaEntrada(row[4] != null ? ((java.sql.Time) row[4]).toLocalTime() : null)
                .horaLunchInicio(row[5] != null ? ((java.sql.Time) row[5]).toLocalTime() : null)
                .horaLunchFin(row[6] != null ? ((java.sql.Time) row[6]).toLocalTime() : null)
                .horaSalida(row[7] != null ? ((java.sql.Time) row[7]).toLocalTime() : null)
                .minutosTrabajados(row[8] != null ? ((Number) row[8]).intValue() : null)
                .minutosLunch(row[9] != null ? ((Number) row[9]).intValue() : null)
                .tardanzaMinutos(row[10] != null ? ((Number) row[10]).intValue() : null)
                .estadoAsistencia((String) row[11])
                .observaciones((String) row[12])
                .build()).toList();
    }
    
    
    /**
     * derivar el estado a partir del mensaje SP
     */
    private String deducirEstadoDesdeMensaje(String mensaje) {
        if(mensaje == null) return "PENDIENTE";
        
        if(mensaje.contains("Entrada registrada")) return "PRESENTE";
        if(mensaje.contains("Inicio de almuerzo")) return "ALMUERZO";
        if(mensaje.contains("Fin de almuerzo")) return "PRESENTE";
        if(mensaje.contains("Salida registrada")) return "COMPLETADO";
        
        return "PENDIENTE";
    }
}

package com.vet.manadawoof.service.impl;

import com.vet.manadawoof.dtos.request.RegistrarAsistenciaRequestDTO;
import com.vet.manadawoof.dtos.response.RegistroAsistenciaResponseDTO;
import com.vet.manadawoof.entity.ColaboradorEntity;
import com.vet.manadawoof.mapper.RegistroAsistenciaMapper;
import com.vet.manadawoof.repository.ColaboradorRepository;
import com.vet.manadawoof.repository.RegistroAsistenciaRepository;
import com.vet.manadawoof.service.RegistroAsistenciaService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RegistroAsistenciaServiceImpl implements RegistroAsistenciaService {
    
    private final RegistroAsistenciaMapper mapper;
    private final ColaboradorRepository colaboradorRepository;
    private final RegistroAsistenciaRepository registroRepository;
    private final EntityManager entityManager;
    
    @Override
    @Transactional(readOnly = false)
    public RegistroAsistenciaResponseDTO registrar(RegistrarAsistenciaRequestDTO request) {
        LocalDate hoy = LocalDate.now();
        LocalTime horaActual = LocalTime.now();
        
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("gestionar_asistencia");
        
        // Registrar parámetros
        query.registerStoredProcedureParameter("p_colaborador_id", Long.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_fecha", java.sql.Date.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_hora", java.sql.Time.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_tipo_movimiento", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_mensaje", String.class, ParameterMode.OUT);
        query.registerStoredProcedureParameter("p_success", Integer.class, ParameterMode.OUT);
        query.registerStoredProcedureParameter("p_tardanza_minutos", Integer.class, ParameterMode.OUT);
        query.registerStoredProcedureParameter("p_estado_final", String.class, ParameterMode.OUT);
        
        // Setear valores
        query.setParameter("p_colaborador_id", request.getIdColaborador());
        query.setParameter("p_fecha", java.sql.Date.valueOf(hoy));
        query.setParameter("p_hora", java.sql.Time.valueOf(horaActual));
        query.setParameter("p_tipo_movimiento", request.getTipoMarca().name());
        
        query.execute();
        
        @SuppressWarnings("unchecked")
        List<Object[]> resultList = query.getResultList();
        
        if(resultList == null || resultList.isEmpty()) {
            throw new RuntimeException("No se recibió respuesta del procedimiento");
        }
        
        Object[] row = resultList.get(0);
        String horaMarcacionStr = row[6] != null ? row[6].toString() : null;
        LocalTime horaMarcacion = null;
        if(horaMarcacionStr != null) {
            try {
                horaMarcacion = LocalTime.parse(horaMarcacionStr);
            } catch (Exception e) {
                // Si viene en formato HH:mm:ss, parsear correctamente
                horaMarcacion = LocalTime.parse(horaMarcacionStr,
                        java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));
            }
        }
        return RegistroAsistenciaResponseDTO.builder()
                .mensaje(row[0] != null ? row[0].toString() : "Sin mensaje")
                .success(row[1] != null && ((Number) row[1]).intValue() == 1)
                .tardanzaMinutos(row[2] != null ? ((Number) row[2]).intValue() : 0)
                .estadoFinal(row[3] != null ? row[3].toString() : "")
                .idColaborador(row[4] != null ? ((Number) row[4]).longValue() : null)
                .colaborador(row[5] != null ? row[5].toString() : "Desconocido")
                .horaMarcacion(horaMarcacion)
                .tipoMarca(row[7] != null ? row[7].toString() : null)
                .build();
    }
    
    @Override
    @Transactional
    public List<RegistroAsistenciaResponseDTO> verAsistenciaPorRango(LocalDate fechaInicio, LocalDate fechaFin, Long idColaborador, Integer idEstado) {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("ver_asistencia_por_rango");
        
        query.registerStoredProcedureParameter("p_fecha_inicio", Date.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_fecha_fin", Date.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_id_colaborador", Long.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_id_estado", Integer.class, ParameterMode.IN);
        
        query.setParameter("p_fecha_inicio", Date.valueOf(fechaInicio));
        query.setParameter("p_fecha_fin", Date.valueOf(fechaFin));
        query.setParameter("p_id_colaborador", idColaborador); query.setParameter("p_id_estado", idEstado);
        
        @SuppressWarnings("unchecked") List<Object[]> rows = query.getResultList();
        
        return rows.stream().map(mapper :: toResponseDTO).toList();
    }
}

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
    @Transactional
    public RegistroAsistenciaResponseDTO registrar(RegistrarAsistenciaRequestDTO request) {
        LocalDate hoy = LocalDate.now();
        LocalTime horaActual = LocalTime.now();

        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("gestionar_asistencia");

        // Registrar parámetros
        query.registerStoredProcedureParameter("p_colaborador_id", Long.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_fecha", Date.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_hora", Time.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_tipo_movimiento", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_mensaje", String.class, ParameterMode.OUT);
        query.registerStoredProcedureParameter("p_success", Integer.class, ParameterMode.OUT);
        query.registerStoredProcedureParameter("p_tardanza_minutos", Integer.class, ParameterMode.OUT);
        query.registerStoredProcedureParameter("p_estado_final", String.class, ParameterMode.OUT);

        // Setear valores
        query.setParameter("p_colaborador_id", request.getIdColaborador());
        query.setParameter("p_fecha", Date.valueOf(hoy));
        query.setParameter("p_hora", Time.valueOf(horaActual));
        query.setParameter("p_tipo_movimiento", request.getTipoMarca().name());

        query.execute();

        @SuppressWarnings("unchecked")
        List<Object[]> resultList = query.getResultList();

        if (resultList == null || resultList.isEmpty()) {
            throw new RuntimeException("No se recibió respuesta del procedimiento");
        }

        Object[] row = resultList.get(0);

        LocalTime horaMarcacion = toLocalTime(row[6]);

        return RegistroAsistenciaResponseDTO.builder()
                .mensaje(toStringSafe(row[0], "Sin mensaje"))
                .success(toBoolean(row[1]))
                .tardanzaMinutos(toInteger(row[2]))
                .estadoFinal(toStringSafe(row[3], ""))
                .idColaborador(toLong(row[4]))
                .colaborador(toStringSafe(row[5], "Desconocido"))
                .horaMarcacion(horaMarcacion)
                .tipoMarca(toStringSafe(row[7], null))
                .build();
    }

    @Override
    @Transactional
    public List<RegistroAsistenciaResponseDTO> verAsistenciaPorRango(
            LocalDate fechaInicio, LocalDate fechaFin, Long idColaborador, Integer idEstado) {

        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("ver_asistencia_por_rango");

        query.registerStoredProcedureParameter("p_fecha_inicio", Date.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_fecha_fin", Date.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_id_colaborador", Long.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_id_estado", Integer.class, ParameterMode.IN);

        query.setParameter("p_fecha_inicio", Date.valueOf(fechaInicio));
        query.setParameter("p_fecha_fin", Date.valueOf(fechaFin));
        query.setParameter("p_id_colaborador", idColaborador);
        query.setParameter("p_id_estado", idEstado);

        @SuppressWarnings("unchecked")
        List<Object[]> rows = query.getResultList();

        return rows.stream().map(mapper::toResponseDTO).toList();
    }

    // =======================
    //  MÉTODOS SEGUROS
    // =======================

    private Integer toInteger(Object obj) {
        if (obj == null) return null;
        if (obj instanceof Number) return ((Number) obj).intValue();
        if (obj instanceof Boolean) return (Boolean) obj ? 1 : 0;
        try { return Integer.valueOf(obj.toString()); } catch (Exception e) { return null; }
    }

    private Long toLong(Object obj) {
        if (obj == null) return null;
        if (obj instanceof Number) return ((Number) obj).longValue();
        try { return Long.valueOf(obj.toString()); } catch (Exception e) { return null; }
    }

    private boolean toBoolean(Object obj) {
        if (obj == null) return false;
        if (obj instanceof Boolean) return (Boolean) obj;
        if (obj instanceof Number) return ((Number) obj).intValue() == 1;
        return obj.toString().equalsIgnoreCase("1")
                || obj.toString().equalsIgnoreCase("true")
                || obj.toString().equalsIgnoreCase("si");
    }

    private LocalTime toLocalTime(Object obj) {
        if (obj instanceof Time) return ((Time) obj).toLocalTime();
        try {
            return LocalTime.parse(obj.toString());
        } catch (Exception e) {
            return null;
        }
    }

    private String toStringSafe(Object obj, String def) {
        return obj != null ? obj.toString() : def;
    }
}

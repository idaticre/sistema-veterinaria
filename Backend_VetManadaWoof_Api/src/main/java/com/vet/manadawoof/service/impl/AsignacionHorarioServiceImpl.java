package com.vet.manadawoof.service.impl;

import com.vet.manadawoof.dtos.request.AsignacionHorarioRequestDTO;
import com.vet.manadawoof.dtos.response.ApiResponse;
import com.vet.manadawoof.dtos.response.AsignacionHorarioResponseDTO;
import com.vet.manadawoof.service.AsignacionHorarioService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AsignacionHorarioServiceImpl implements AsignacionHorarioService {
    
    private final EntityManager em; // EntityManager para ejecutar SPs y queries nativos
    
    /**
     * Asigna un horario a un colaborador en un día específico usando SP.
     * Retorna los datos de la asignación recién creada.
     */
    @Override
    @Transactional
    public ApiResponse<AsignacionHorarioResponseDTO> asignarHorarioDia(AsignacionHorarioRequestDTO dto) {
        // Ejecuta SP que realiza la asignación en la BD
        StoredProcedureQuery sp = em.createStoredProcedureQuery("asignar_horario_dia");
        sp.registerStoredProcedureParameter("p_id_colaborador", Long.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_id_horario_base", Integer.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_id_dia_semana", Integer.class, ParameterMode.IN);
        sp.setParameter("p_id_colaborador", dto.getIdColaborador());
        sp.setParameter("p_id_horario_base", dto.getIdHorarioBase());
        sp.setParameter("p_id_dia_semana", dto.getIdDia());
        sp.execute();
        
        // Obtiene los datos completos de la asignación para devolver al frontend
        Object[] row = (Object[]) em.createNativeQuery(
                        "SELECT ah.id, ah.id_colaborador, ah.id_horario_base, ah.id_dia_semana, " +
                                "e.nombre AS colaborador, h.nombre AS horario, d.nombre AS dia, " +
                                "ah.fecha_asignacion, ah.activo " +
                                "FROM asignacion_horarios ah " +
                                "JOIN colaboradores c ON ah.id_colaborador = c.id " +
                                "JOIN entidades e ON c.id_entidad = e.id " +
                                "JOIN horarios_base h ON ah.id_horario_base = h.id " +
                                "JOIN dias_semana d ON ah.id_dia_semana = d.id " +
                                "WHERE ah.id_colaborador = ?1 AND ah.id_dia_semana = ?2"
                )
                .setParameter(1, dto.getIdColaborador())
                .setParameter(2, dto.getIdDia())
                .getSingleResult();
        
        // Mapea la fila obtenida a DTO
        AsignacionHorarioResponseDTO response = AsignacionHorarioResponseDTO.builder()
                .id(((Number) row[0]).longValue())
                .idColaborador(((Number) row[1]).longValue())
                .idHorarioBase(((Number) row[2]).intValue())
                .idDia(((Number) row[3]).intValue())
                .colaborador((String) row[4])
                .horario((String) row[5])
                .dia((String) row[6])
                .fechaAsignacion(((java.sql.Timestamp) row[7]).toLocalDateTime())
                .activo(((Number) row[8]).intValue() == 1)
                .mensaje("Horario asignado correctamente")
                .build();
        
        return new ApiResponse<>(true, "Operación exitosa", response);
    }
    
    /**
     * Desasigna un horario de un colaborador en un día específico usando SP.
     * Devuelve la información actualizada de la asignación.
     */
    @Override
    @Transactional
    public ApiResponse<AsignacionHorarioResponseDTO> desasignarHorarioDia(AsignacionHorarioRequestDTO dto) {
        // Ejecuta SP que desasigna el horario
        StoredProcedureQuery sp = em.createStoredProcedureQuery("desasignar_horario_dia");
        sp.registerStoredProcedureParameter("p_id_colaborador", Long.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_id_dia_semana", Integer.class, ParameterMode.IN);
        sp.setParameter("p_id_colaborador", dto.getIdColaborador());
        sp.setParameter("p_id_dia_semana", dto.getIdDia());
        sp.execute();
        
        // Obtiene datos actualizados después de desasignar
        Object[] row = (Object[]) em.createNativeQuery(
                        "SELECT ah.id, ah.id_colaborador, ah.id_horario_base, ah.id_dia_semana, " +
                                "e.nombre AS colaborador, h.nombre AS horario, d.nombre AS dia, " +
                                "ah.fecha_asignacion, ah.activo " +
                                "FROM asignacion_horarios ah " +
                                "JOIN colaboradores c ON ah.id_colaborador = c.id " +
                                "JOIN entidades e ON c.id_entidad = e.id " +
                                "JOIN horarios_base h ON ah.id_horario_base = h.id " +
                                "JOIN dias_semana d ON ah.id_dia_semana = d.id " +
                                "WHERE ah.id_colaborador = ?1 AND ah.id_dia_semana = ?2"
                )
                .setParameter(1, dto.getIdColaborador())
                .setParameter(2, dto.getIdDia())
                .getSingleResult();
        
        AsignacionHorarioResponseDTO response = AsignacionHorarioResponseDTO.builder()
                .id(((Number) row[0]).longValue())
                .idColaborador(((Number) row[1]).longValue())
                .idHorarioBase(((Number) row[2]).intValue())
                .idDia(((Number) row[3]).intValue())
                .colaborador((String) row[4])
                .horario((String) row[5])
                .dia((String) row[6])
                .fechaAsignacion(((java.sql.Timestamp) row[7]).toLocalDateTime())
                .activo(((Number) row[8]).intValue() == 1)
                .mensaje("Horario desasignado correctamente")
                .build();
        
        return new ApiResponse<>(true, "Operación exitosa", response);
    }
    
    /**
     * Asigna un mismo horario a todos los días laborales de un colaborador.
     * Devuelve la lista de asignaciones para la semana.
     */
    @Override
    @Transactional
    public ApiResponse<List<AsignacionHorarioResponseDTO>> asignarHorarioSemana(AsignacionHorarioRequestDTO dto) {
        // Ejecuta SP que asigna horario a la semana
        StoredProcedureQuery sp = em.createStoredProcedureQuery("asignar_horario_semana");
        sp.registerStoredProcedureParameter("p_id_colaborador", Long.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_id_horario_base", Integer.class, ParameterMode.IN);
        sp.setParameter("p_id_colaborador", dto.getIdColaborador());
        sp.setParameter("p_id_horario_base", dto.getIdHorarioBase());
        sp.execute();
        
        // Obtiene todas las asignaciones activas del colaborador
        List<Object[]> rows = em.createNativeQuery(
                        "SELECT ah.id, ah.id_colaborador, ah.id_horario_base, ah.id_dia_semana, " +
                                "e.nombre AS colaborador, h.nombre AS horario, d.nombre AS dia, " +
                                "ah.fecha_asignacion, ah.activo " +
                                "FROM asignacion_horarios ah " +
                                "JOIN colaboradores c ON ah.id_colaborador = c.id " +
                                "JOIN entidades e ON c.id_entidad = e.id " +
                                "JOIN horarios_base h ON ah.id_horario_base = h.id " +
                                "JOIN dias_semana d ON ah.id_dia_semana = d.id " +
                                "WHERE ah.id_colaborador = ?1 " +
                                "ORDER BY ah.id_dia_semana"
                )
                .setParameter(1, dto.getIdColaborador())
                .getResultList();
        
        List<AsignacionHorarioResponseDTO> responses = rows.stream().map(r ->
                AsignacionHorarioResponseDTO.builder()
                        .id(((Number) r[0]).longValue())
                        .idColaborador(((Number) r[1]).longValue())
                        .idHorarioBase(((Number) r[2]).intValue())
                        .idDia(((Number) r[3]).intValue())
                        .colaborador((String) r[4])
                        .horario((String) r[5])
                        .dia((String) r[6])
                        .fechaAsignacion(((java.sql.Timestamp) r[7]).toLocalDateTime())
                        .activo(((Number) r[8]).intValue() == 1)
                        .mensaje("Horario asignado correctamente")
                        .build()
        ).collect(Collectors.toList());
        
        return new ApiResponse<>(true, "Horarios asignados para la semana", responses);
    }
    
    /**
     * Desasigna todos los horarios de un colaborador (lunes a sábado).
     */
    @Override
    @Transactional
    public ApiResponse<String> desasignarHorarioSemana(Long idColaborador) {
        StoredProcedureQuery sp = em.createStoredProcedureQuery("desasignar_horario_semana");
        sp.registerStoredProcedureParameter("p_id_colaborador", Long.class, ParameterMode.IN);
        sp.setParameter("p_id_colaborador", idColaborador);
        sp.execute();
        
        return new ApiResponse<>(true, "Horarios desasignados para la semana", "ok");
    }
    
    /**
     * Lista todas las asignaciones activas con datos legibles.
     */
    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<AsignacionHorarioResponseDTO>> listarAsignaciones() {
        List<Object[]> results = em.createNativeQuery(
                "SELECT ah.id, ah.id_colaborador, ah.id_horario_base, ah.id_dia_semana, " +
                        "e.nombre AS colaborador, h.nombre AS horario, d.nombre AS dia, " +
                        "ah.fecha_asignacion, ah.activo " +
                        "FROM asignacion_horarios ah " +
                        "JOIN colaboradores c ON ah.id_colaborador = c.id " +
                        "JOIN entidades e ON c.id_entidad = e.id " +
                        "JOIN horarios_base h ON ah.id_horario_base = h.id " +
                        "JOIN dias_semana d ON ah.id_dia_semana = d.id " +
                        "WHERE ah.activo = 1 " +
                        "ORDER BY ah.id_colaborador, ah.id_dia_semana"
        ).getResultList();
        
        // Mapea resultados a DTO
        List<AsignacionHorarioResponseDTO> responses = results.stream().map(r ->
                AsignacionHorarioResponseDTO.builder()
                        .id(((Number) r[0]).longValue())
                        .idColaborador(((Number) r[1]).longValue())
                        .idHorarioBase(((Number) r[2]).intValue())
                        .idDia(((Number) r[3]).intValue())
                        .colaborador((String) r[4])
                        .horario((String) r[5])
                        .dia((String) r[6])
                        .fechaAsignacion(((java.sql.Timestamp) r[7]).toLocalDateTime())
                        .activo(((Number) r[8]).intValue() == 1)
                        .mensaje("Asignación activa")
                        .build()
        ).collect(Collectors.toList());
        
        return new ApiResponse<>(true, "Listado de asignaciones", responses);
    }
}

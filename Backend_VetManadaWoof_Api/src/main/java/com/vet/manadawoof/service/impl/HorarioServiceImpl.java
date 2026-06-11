package com.vet.manadawoof.service.impl;

import com.vet.manadawoof.dtos.request.HorarioRequestDTO;
import com.vet.manadawoof.dtos.response.HorarioResponseDTO;
import com.vet.manadawoof.mapper.HorarioMapper;
import com.vet.manadawoof.repository.HorarioRepository;
import com.vet.manadawoof.service.HorarioService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class HorarioServiceImpl implements HorarioService {

    private final HorarioRepository horarioRepository;
    private final HorarioMapper horarioMapper;

    @PersistenceContext
    private EntityManager em;

    // ----------------------------------------------------------------
    // GET /api/horarios
    // ----------------------------------------------------------------
    @Override
    @Transactional
    public List<HorarioResponseDTO> listarTodos() {
        StoredProcedureQuery sp = em.createStoredProcedureQuery("sp_horarios_listar_todos");
        sp.execute();
        return mapResultList(sp.getResultList());
    }

    // ----------------------------------------------------------------
    // GET /api/horarios?dia={diaId}
    // Devuelve Map con "nombreDia" (String) y "horarios" (List)
    // para que el controller arme el mensaje dinámico
    // ----------------------------------------------------------------
    @Override
    @Transactional
    public Map<String, Object> listarPorDia(Integer diaId) {
        StoredProcedureQuery sp = em.createStoredProcedureQuery("sp_horarios_listar_por_dia");
        sp.registerStoredProcedureParameter("p_dia_id", Integer.class, ParameterMode.IN);
        sp.setParameter("p_dia_id", diaId);
        sp.execute();

        List<HorarioResponseDTO> horarios = mapResultList(sp.getResultList());

        // nombre_dia viene en row[4]; si nadie trabaja ese día la lista estará vacía,
        // así que lo obtenemos del SP directamente con una consulta al nombre del día.
        String nombreDia = horarios.isEmpty()
                ? obtenerNombreDia(diaId)
                : horarios.get(0).getNombreDia();

        return Map.of("nombreDia", nombreDia, "horarios", horarios);
    }

    // ----------------------------------------------------------------
    // POST /api/asignar-horario
    // ----------------------------------------------------------------
    @Override
    @Transactional
    public HorarioResponseDTO asignarHorario(HorarioRequestDTO request) {
        StoredProcedureQuery sp = em.createStoredProcedureQuery("sp_horarios_insertar");
        sp.registerStoredProcedureParameter("p_trabajador_id", Long.class,      ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_dia_id",        Integer.class,   ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_trabaja",       Boolean.class,   ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_hora_inicio",   LocalTime.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_hora_fin",      LocalTime.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_id",            Long.class,      ParameterMode.OUT);

        sp.setParameter("p_trabajador_id", request.getTrabajadorId());
        sp.setParameter("p_dia_id",        request.getDiaId());
        sp.setParameter("p_trabaja",       request.getTrabaja());
        sp.setParameter("p_hora_inicio",   request.getHoraInicio());
        sp.setParameter("p_hora_fin",      request.getHoraFin());

        sp.execute();

        Long nuevoId = (Long) sp.getOutputParameterValue("p_id");
        return horarioRepository.findById(nuevoId)
                .map(horarioMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Error al recuperar el horario creado"));
    }

    // ----------------------------------------------------------------
    // PUT /api/asignar-horario/{id}
    // ----------------------------------------------------------------
    @Override
    @Transactional
    public HorarioResponseDTO editarHorario(Long id, HorarioRequestDTO request) {
        StoredProcedureQuery sp = em.createStoredProcedureQuery("sp_horarios_actualizar");
        sp.registerStoredProcedureParameter("p_id",            Long.class,      ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_trabajador_id", Long.class,      ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_dia_id",        Integer.class,   ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_trabaja",       Boolean.class,   ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_hora_inicio",   LocalTime.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_hora_fin",      LocalTime.class, ParameterMode.IN);

        sp.setParameter("p_id",            id);
        sp.setParameter("p_trabajador_id", request.getTrabajadorId());
        sp.setParameter("p_dia_id",        request.getDiaId());
        sp.setParameter("p_trabaja",       request.getTrabaja());
        sp.setParameter("p_hora_inicio",   request.getHoraInicio());
        sp.setParameter("p_hora_fin",      request.getHoraFin());

        sp.execute();

        return horarioRepository.findById(id)
                .map(horarioMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Horario no encontrado con ID: " + id));
    }

    // ----------------------------------------------------------------
    // DELETE /api/eliminar-horario/{trabajadorId}
    // ----------------------------------------------------------------
    @Override
    @Transactional
    public void eliminarHorariosPorColaborador(Long trabajadorId) {
        StoredProcedureQuery sp = em.createStoredProcedureQuery("sp_horarios_eliminar_por_colaborador");
        sp.registerStoredProcedureParameter("p_trabajador_id",   Long.class,    ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_filas_afectadas", Integer.class, ParameterMode.OUT);

        sp.setParameter("p_trabajador_id", trabajadorId);
        sp.execute();

        Integer filas = (Integer) sp.getOutputParameterValue("p_filas_afectadas");
        if (filas == null || filas == 0) {
            throw new RuntimeException(
                "No se encontraron horarios para el colaborador con ID: " + trabajadorId);
        }
    }

    // ----------------------------------------------------------------
    // Obtiene el nombre del día directamente desde dias_semana.
    // Se usa solo cuando listarPorDia devuelve lista vacía.
    // ----------------------------------------------------------------
    private String obtenerNombreDia(Integer diaId) {
        Object result = em.createNativeQuery(
                "SELECT nombre FROM dias_semana WHERE id = :id")
                .setParameter("id", diaId)
                .getSingleResult();
        return result != null ? result.toString() : "día " + diaId;
    }

    // ----------------------------------------------------------------
    // Mapea el Object[] que devuelve el SP a HorarioResponseDTO
    // Orden de columnas: id, trabajador_id, nombre_colaborador,
    //                    dia_id, nombre_dia, trabaja, hora_inicio, hora_fin
    // ----------------------------------------------------------------
    @SuppressWarnings("unchecked")
    private List<HorarioResponseDTO> mapResultList(List<Object[]> rows) {
        return rows.stream().map(row -> HorarioResponseDTO.builder()
                .id(((Number) row[0]).longValue())
                .trabajadorId(((Number) row[1]).longValue())
                .nombreColaborador((String) row[2])
                .diaId(((Number) row[3]).intValue())
                .nombreDia((String) row[4])
                .trabaja((Boolean) row[5])
                .horaInicio(row[6] != null ? ((java.sql.Timestamp) row[6]).toLocalDateTime().toLocalTime() : null)
                .horaFin(row[7]   != null ? ((java.sql.Timestamp) row[7]).toLocalDateTime().toLocalTime() : null)
                .build()
        ).toList();
    }

    // ----------------------------------------------------------------
    // GET /api/horarios?colaborador={colaboradorId}
    // ----------------------------------------------------------------
    @Override
    @Transactional
    public Map<String, Object> listarPorColaborador(Long colaboradorId) {
        StoredProcedureQuery sp = em.createStoredProcedureQuery("sp_horarios_listar_por_colaborador");
        sp.registerStoredProcedureParameter("p_trabajador_id", Long.class, ParameterMode.IN);
        sp.setParameter("p_trabajador_id", colaboradorId);
        sp.execute();

        List<HorarioResponseDTO> horarios = mapResultList(sp.getResultList());

        String nombreColaborador = horarios.isEmpty()
                ? obtenerNombreColaborador(colaboradorId)
                : horarios.get(0).getNombreColaborador();

        return Map.of("nombreColaborador", nombreColaborador, "horarios", horarios);
    }

    // ----------------------------------------------------------------
    // Obtiene el nombre del colaborador cuando la lista viene vacía.
    // ----------------------------------------------------------------
    private String obtenerNombreColaborador(Long colaboradorId) {
        Object result = em.createNativeQuery(
                "SELECT e.nombre FROM colaboradores c " +
                "INNER JOIN entidades e ON c.id_entidad = e.id " +
                "WHERE c.id = :id")
                .setParameter("id", colaboradorId)
                .getSingleResult();
        return result != null ? result.toString() : "colaborador " + colaboradorId;
    }

}
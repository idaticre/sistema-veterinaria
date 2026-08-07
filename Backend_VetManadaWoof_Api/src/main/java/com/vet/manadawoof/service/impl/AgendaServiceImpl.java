package com.vet.manadawoof.service.impl;

import com.vet.manadawoof.dtos.request.AgendaRequestDTO;
import com.vet.manadawoof.dtos.response.AgendaResponseDTO;
import com.vet.manadawoof.mapper.AgendaMapper;
import com.vet.manadawoof.repository.AgendaRepository;
import com.vet.manadawoof.entity.ServicioEntity;
import com.vet.manadawoof.entity.SalaEntity;
import com.vet.manadawoof.repository.ServicioRepository;
import com.vet.manadawoof.repository.SalaRepository;
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
import java.util.List;

@Service
@RequiredArgsConstructor
public class AgendaServiceImpl implements AgendaService {

    @PersistenceContext
    private EntityManager entityManager;

    private final AgendaRepository agendaRepository;
    private final ServicioRepository servicioRepository;
    private final SalaRepository salaRepository;
    private final AgendaMapper agendaMapper;

@Override
@Transactional
public AgendaResponseDTO crear(AgendaRequestDTO dto) {

    // 1. Validar colaborador ocupado
    if (dto.getIdColaborador() != null) {

        long ocupadas = agendaRepository.contarColaboradorOcupado(
                dto.getIdColaborador(),
                dto.getFecha(),
                dto.getHora()
        );

        if (ocupadas > 0) {
            return AgendaResponseDTO.builder()
                    .mensaje("ERROR: El colaborador ya tiene una cita en ese horario. Seleccione otro colaborador.")
                    .build();
        }
    }

    // 2. Validar salas (existencia de disponibles + que la elegida sea válida)
    AgendaResponseDTO errorSala = validarSala(dto);
    if (errorSala != null) {
        return errorSala;
    }

    // 3. Crear la agenda
    return ejecutarProcedimiento("CREAR", dto);
}

    @Override
    @Transactional
    public AgendaResponseDTO actualizar(AgendaRequestDTO dto) {

        // 🆕 Misma validación de sala al editar/reprogramar una cita
        AgendaResponseDTO errorSala = validarSala(dto);
        if (errorSala != null) {
            return errorSala;
        }

        return ejecutarProcedimiento("ACTUALIZAR", dto);
    }

    // 🆕 VALIDA QUE, SI EL SERVICIO REQUIERE SALA, EXISTA UNA DISPONIBLE Y QUE LA ELEGIDA SEA VÁLIDA
    private AgendaResponseDTO validarSala(AgendaRequestDTO dto) {

        ServicioEntity servicio = servicioRepository
                .findById(dto.getIdServicio())
                .orElseThrow();

        if (!Boolean.TRUE.equals(servicio.getRequiereSala())) {
            return null; // Este servicio no requiere sala, no hay nada que validar
        }

        List<SalaEntity> salasDisponibles = salaRepository.buscarSalasDisponibles(
                dto.getFecha(),
                dto.getHora()
        );

        if (dto.getIdSala() == null) {
            return AgendaResponseDTO.builder()
                    .mensaje("ERROR: Debe seleccionar una sala para este servicio.")
                    .build();
        }

        boolean salaValida = salasDisponibles.stream()
                .anyMatch(s -> s.getId().equals(dto.getIdSala()));

        // 🆕 Si no aparece como "disponible", puede ser porque ya está ocupada
        // por esta MISMA cita (edición sin cambiar horario) — eso sí es válido.
        if (!salaValida && dto.getId() != null) {
            salaValida = agendaRepository.findById(dto.getId())
                    .map(a -> a.getSala() != null && a.getSala().getId().equals(dto.getIdSala()))
                    .orElse(false);
        }

        if (!salaValida) {
            return AgendaResponseDTO.builder()
                    .mensaje("ERROR: La sala seleccionada no está disponible para el horario elegido.")
                    .build();
        }

        return null; // Todo correcto
    }

    private AgendaResponseDTO ejecutarProcedimiento(String accion, AgendaRequestDTO dto) {
    StoredProcedureQuery sp = entityManager.createStoredProcedureQuery("sp_gestionar_agenda");
    registrarParametros(sp);

    // USAMOS NÚMEROS (1 al 12 son IN)
    sp.setParameter(1, accion);
    sp.setParameter(2, "CREAR".equals(accion) ? null : dto.getId());
    sp.setParameter(3, dto.getIdCliente());
    sp.setParameter(4, dto.getIdMascota());
    sp.setParameter(5, dto.getIdMedioSolicitud());
    sp.setParameter(6, Date.valueOf(dto.getFecha()));
    sp.setParameter(7, Time.valueOf(dto.getHora() + ":00")); // Aseguramos formato HH:mm:ss
    sp.setParameter(8, dto.getDuracionEstimadaMin());
    sp.setParameter(9, dto.getIdEstado());
    sp.setParameter(10, dto.getAbonoInicial() != null ? dto.getAbonoInicial() : BigDecimal.ZERO);
    sp.setParameter(11, dto.getTotalCita() != null ? dto.getTotalCita() : BigDecimal.ZERO);
    sp.setParameter(12, dto.getObservaciones());

    sp.execute();

    // LEEMOS LOS OUT (13, 14, 15)
    String mensaje = (String) sp.getOutputParameterValue(15);
    
    if (mensaje != null && mensaje.startsWith("ERROR")) {
        return AgendaResponseDTO.builder().mensaje(mensaje).build();
    }

    Object resultadoId = sp.getOutputParameterValue(13);
    Long idResultado = (resultadoId instanceof Number) ? ((Number) resultadoId).longValue() : null;

    // 🆕 ASIGNAR (O LIBERAR) LA SALA DE LA CITA YA CREADA/ACTUALIZADA POR EL SP
    asignarSala(idResultado, dto);

    return obtenerPorId(idResultado);
}

    // 🆕 Asigna la sala elegida a la agenda, o la libera si ya no aplica
    private void asignarSala(Long idAgenda, AgendaRequestDTO dto) {
        if (idAgenda == null) return;

        agendaRepository.findById(idAgenda).ifPresent(agenda -> {
            if (dto.getIdSala() != null) {
                SalaEntity sala = salaRepository.findById(dto.getIdSala()).orElse(null);
                agenda.setSala(sala);
            } else {
                agenda.setSala(null);
            }
            agendaRepository.save(agenda);
        });
    }

private void registrarParametros(StoredProcedureQuery sp) {
    // REGISTRO POR ÍNDICE (Obligatorio para MySQL en la nube)
    sp.registerStoredProcedureParameter(1, String.class, ParameterMode.IN);      // p_accion
    sp.registerStoredProcedureParameter(2, Long.class, ParameterMode.IN);        // p_id_agenda
    sp.registerStoredProcedureParameter(3, Long.class, ParameterMode.IN);        // p_id_cliente
    sp.registerStoredProcedureParameter(4, Long.class, ParameterMode.IN);        // p_id_mascota
    sp.registerStoredProcedureParameter(5, Integer.class, ParameterMode.IN);     // p_id_medio_solicitud
    sp.registerStoredProcedureParameter(6, Date.class, ParameterMode.IN);        // p_fecha
    sp.registerStoredProcedureParameter(7, Time.class, ParameterMode.IN);        // p_hora
    sp.registerStoredProcedureParameter(8, Integer.class, ParameterMode.IN);     // p_duracion_estimada_min
    sp.registerStoredProcedureParameter(9, Integer.class, ParameterMode.IN);     // p_id_estado
    sp.registerStoredProcedureParameter(10, BigDecimal.class, ParameterMode.IN); // p_abono_inicial
    sp.registerStoredProcedureParameter(11, BigDecimal.class, ParameterMode.IN); // p_total_cita
    sp.registerStoredProcedureParameter(12, String.class, ParameterMode.IN);     // p_observaciones
    
    sp.registerStoredProcedureParameter(13, Long.class, ParameterMode.OUT);     // p_id_resultado
    sp.registerStoredProcedureParameter(14, String.class, ParameterMode.OUT);    // p_codigo
    sp.registerStoredProcedureParameter(15, String.class, ParameterMode.OUT);    // p_mensaje
}

    @Override
    @Transactional(readOnly = true)
    public Page<AgendaResponseDTO> listar(Pageable pageable) {
        return agendaRepository.findAll(pageable).map(agendaMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public AgendaResponseDTO obtenerPorId(Long idAgenda) {
        return agendaRepository.findById(idAgenda).map(agendaMapper::toDto).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AgendaResponseDTO> listarPorCliente(Long idCliente) {
        return agendaRepository.findByCliente_Id(idCliente).stream()
                .map(agendaMapper::toDto).toList();
    }
}
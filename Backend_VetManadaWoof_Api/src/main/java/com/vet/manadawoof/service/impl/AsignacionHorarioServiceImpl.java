package com.vet.manadawoof.service.impl;

import com.vet.manadawoof.dtos.request.AsignacionHorarioRequestDTO;
import com.vet.manadawoof.dtos.request.GestionDiaEspecialRequestDTO;
import com.vet.manadawoof.dtos.request.GestionRangoRequestDTO;
import com.vet.manadawoof.dtos.response.*;
import com.vet.manadawoof.entity.AsignacionHorarioEntity;
import com.vet.manadawoof.entity.ColaboradorEntity;
import com.vet.manadawoof.entity.DiaEntity;
import com.vet.manadawoof.entity.HorarioBaseEntity;
import com.vet.manadawoof.mapper.AsignacionHorarioMapper;
import com.vet.manadawoof.repository.AsignacionHorarioRepository;
import com.vet.manadawoof.repository.ColaboradorRepository;
import com.vet.manadawoof.repository.DiaRepository;
import com.vet.manadawoof.repository.HorarioBaseRepository;
import com.vet.manadawoof.service.AsignacionHorarioService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AsignacionHorarioServiceImpl implements AsignacionHorarioService {
    
    private final AsignacionHorarioRepository repository;
    private final ColaboradorRepository colaboradorRepository;
    private final HorarioBaseRepository horarioBaseRepository;
    private final DiaRepository diaRepository;
    private final AsignacionHorarioMapper mapper;
    private final EntityManager entityManager;
    
    // ===================================================================
    // CREAR / ACTUALIZAR / ELIMINAR ASIGNACIÓN BASE
    // ===================================================================
    @Override
    @Transactional
    public AsignacionHorarioResponseDTO crearAsignacion(AsignacionHorarioRequestDTO request) {
        ColaboradorEntity colaborador = colaboradorRepository.findById(request.getIdColaborador()).orElseThrow(() -> new RuntimeException("Colaborador no encontrado"));
        
        HorarioBaseEntity horarioBase = horarioBaseRepository.findById(request.getIdHorarioBase()).orElseThrow(() -> new RuntimeException("Horario base no encontrado"));
        
        DiaEntity dia = diaRepository.findById(request.getIdDiaSemana()).orElseThrow(() -> new RuntimeException("Día no encontrado"));
        
        if(request.getFechaFinVigencia() != null && request.getFechaFinVigencia().isBefore(request.getFechaInicioVigencia())) {
            throw new RuntimeException("La fecha fin no puede ser anterior a la fecha inicio");
        }
        
        AsignacionHorarioEntity entity = AsignacionHorarioEntity.builder().colaborador(colaborador).horarioBase(horarioBase).dia(dia).fechaInicioVigencia(request.getFechaInicioVigencia()).fechaFinVigencia(request.getFechaFinVigencia()).motivoCambio(request.getMotivoCambio()).activo(true).build();
        
        return mapper.toResponse(repository.save(entity));
    }
    
    @Override
    @Transactional
    public AsignacionHorarioResponseDTO actualizarAsignacion(Long id, AsignacionHorarioRequestDTO request) {
        AsignacionHorarioEntity existente = repository.findById(id).orElseThrow(() -> new RuntimeException("Asignación no encontrada"));
        
        if(request.getIdHorarioBase() != null) {
            HorarioBaseEntity horarioBase = horarioBaseRepository.findById(request.getIdHorarioBase()).orElseThrow(() -> new RuntimeException("Horario base no encontrado"));
            existente.setHorarioBase(horarioBase);
        }
        
        if(request.getFechaInicioVigencia() != null) {
            existente.setFechaInicioVigencia(request.getFechaInicioVigencia());
        } existente.setFechaFinVigencia(request.getFechaFinVigencia());
        existente.setMotivoCambio(request.getMotivoCambio());
        
        return mapper.toResponse(repository.save(existente));
    }
    
    @Override
    @Transactional
    public void eliminarAsignacion(Long id) {
        if(! repository.existsById(id)) {
            throw new RuntimeException("Asignación no encontrada");
        } repository.deleteById(id);
    }
    
    // GESTIÓN DÍA ESPECIAL
    @Override
    @Transactional
    public GestionDiaEspecialResponseDTO gestionarDiaEspecial(GestionDiaEspecialRequestDTO request) {
        try {
            StoredProcedureQuery query = entityManager.createStoredProcedureQuery("gestionar_dia_especial");
            
            query.registerStoredProcedureParameter("p_id_colaborador", Long.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("p_fecha", java.sql.Date.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("p_tipo_accion", String.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("p_hora_inicio", java.sql.Time.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("p_hora_fin", java.sql.Time.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("p_id_usuario", Long.class, ParameterMode.IN);
            
            query.setParameter("p_id_colaborador", request.getIdColaborador());
            query.setParameter("p_fecha", java.sql.Date.valueOf(request.getFecha()));
            query.setParameter("p_tipo_accion", request.getTipoAccion().toUpperCase());
            query.setParameter("p_hora_inicio", request.getHoraInicio() != null ? java.sql.Time.valueOf(request.getHoraInicio()) : null);
            query.setParameter("p_hora_fin", request.getHoraFin() != null ? java.sql.Time.valueOf(request.getHoraFin()) : null);
            query.setParameter("p_id_usuario", request.getIdUsuario() != null ? request.getIdUsuario() : 1L);
            
            query.execute();
            
            @SuppressWarnings("unchecked") List<Object[]> resultList = query.getResultList();
            
            if(resultList != null && ! resultList.isEmpty()) {
                Object[] row = resultList.get(0); String status = row[0] != null ? row[0].toString() : "OK";
                String mensaje = row[1] != null ? row[1].toString() : "Operación completada";
                
                if("ERROR".equalsIgnoreCase(status)) {
                    throw new RuntimeException(mensaje);
                }
                
                // Obtener datos del colaborador
                ColaboradorEntity colaborador = colaboradorRepository.findById(request.getIdColaborador()).orElseThrow(() -> new RuntimeException("Colaborador no encontrado"));
                
                String diaSemana = calcularDiaSemana(request.getFecha());
                
                return GestionDiaEspecialResponseDTO.builder().status("OK").mensaje(mensaje).idColaborador(request.getIdColaborador()).nombreColaborador(colaborador.getEntidad().getNombre()).fecha(request.getFecha()).diaSemana(diaSemana).tipoAccion(request.getTipoAccion()).horaInicio(request.getHoraInicio()).horaFin(request.getHoraFin()).esExcepcion(! "DESASIGNAR".equals(request.getTipoAccion())).build();
            }
            
            throw new RuntimeException("No se recibió respuesta del procedimiento almacenado");
            
        } catch (Exception e) {
            throw new RuntimeException("Error al gestionar día especial: " + e.getMessage(), e);
        }
    }
    
    // GESTIÓN RANGO DE FECHAS
    @Override
    @Transactional
    public GestionRangoResponseDTO gestionarRangoFechas(GestionRangoRequestDTO request) {
        try {
            StoredProcedureQuery query = entityManager.createStoredProcedureQuery("gestionar_asignar_rango");
            
            query.registerStoredProcedureParameter("p_id_colaborador", Long.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("p_fecha_inicio", java.sql.Date.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("p_fecha_fin", java.sql.Date.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("p_tipo_accion", String.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("p_id_horario_base", Integer.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("p_id_usuario", Long.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("p_motivo", String.class, ParameterMode.IN);
            
            query.setParameter("p_id_colaborador", request.getIdColaborador());
            query.setParameter("p_fecha_inicio", java.sql.Date.valueOf(request.getFechaInicio()));
            query.setParameter("p_fecha_fin", java.sql.Date.valueOf(request.getFechaFin()));
            query.setParameter("p_tipo_accion", request.getTipoAccion().toUpperCase());
            query.setParameter("p_id_horario_base", request.getIdHorarioBase());
            query.setParameter("p_id_usuario", request.getIdUsuario() != null ? request.getIdUsuario() : 1L);
            query.setParameter("p_motivo", request.getMotivo() != null ? request.getMotivo() : "");
            
            query.execute();
            
            @SuppressWarnings("unchecked") List<Object[]> resultList = query.getResultList();
            
            if(resultList != null && ! resultList.isEmpty()) {
                Object[] row = resultList.get(0); String status = row[0] != null ? row[0].toString() : "ERROR";
                String mensaje = row[1] != null ? row[1].toString() : "Sin respuesta del servidor";
                
                return GestionRangoResponseDTO.builder().status(status).mensaje(mensaje).build();
            }
            
            return GestionRangoResponseDTO.builder().status("ERROR").mensaje("No se recibió respuesta del procedimiento almacenado").build();
            
        } catch (Exception e) {
            return GestionRangoResponseDTO.builder().status("ERROR").mensaje("Error al gestionar rango: " + e.getMessage()).build();
        }
    }
    
    // CONSULTAS
    @Override
    @Transactional
    public List<HistorialHorarioResponseDTO> consultarHistorialHorarios(Long idColaborador, Integer idDiaSemana) {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("consultar_historial_horarios");
        query.registerStoredProcedureParameter("p_id_colaborador", Long.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_id_dia_semana", Integer.class, ParameterMode.IN);
        query.setParameter("p_id_colaborador", idColaborador); query.setParameter("p_id_dia_semana", idDiaSemana);
        query.execute();
        
        @SuppressWarnings("unchecked") List<Object[]> results = query.getResultList();
        
        LocalDate hoy = LocalDate.now();
        
        return results.stream().map(row -> {
            String hasta = row[7] != null ? row[7].toString() : null; boolean esVigente = "ACTIVO".equals(row[10]);
            
            Integer diasRestantes = null; if(esVigente && hasta != null && ! "ACTUAL".equals(hasta)) {
                try {
                    LocalDate fechaHasta = parsearFecha(hasta);
                    diasRestantes = (int) java.time.temporal.ChronoUnit.DAYS.between(hoy, fechaHasta);
                } catch (Exception ignored) {
                }
            }
            
            return HistorialHorarioResponseDTO.builder().id(((Number) row[0]).longValue()).idColaborador(((Number) row[1]).longValue()).colaborador((String) row[2]).dia((String) row[3]).horario((String) row[4]).rangoHorario((String) row[5]).desde((String) row[6]).hasta(hasta).diasVigencia(((Number) row[8]).intValue()).motivoCambio((String) row[9]).estado((String) row[10]).fechaRegistro((String) row[11]).esVigente(esVigente).diasRestantes(diasRestantes).build();
        }).collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public List<HorarioVigenteResponseDTO> verHorariosVigentes(Long idColaborador) {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("ver_horarios_vigentes");
        query.registerStoredProcedureParameter("p_id_colaborador", Long.class, ParameterMode.IN);
        query.setParameter("p_id_colaborador", idColaborador); query.execute();
        
        @SuppressWarnings("unchecked") List<Object[]> results = query.getResultList();
        
        LocalDate hoy = LocalDate.now();
        
        return results.stream().map(row -> {
            String vigenteHasta = row[7] != null ? row[7].toString() : null;
            boolean esIndefinido = "INDEFINIDO".equals(vigenteHasta);
            
            Integer diasParaVencimiento = null; boolean proximoACambiar = false;
            
            if(! esIndefinido && vigenteHasta != null) {
                try {
                    LocalDate fechaHasta = parsearFecha(vigenteHasta);
                    diasParaVencimiento = (int) java.time.temporal.ChronoUnit.DAYS.between(hoy, fechaHasta);
                    proximoACambiar = diasParaVencimiento <= 7 && diasParaVencimiento >= 0;
                } catch (Exception ignored) {
                }
            }
            
            return HorarioVigenteResponseDTO.builder().idColaborador(((Number) row[0]).longValue()).colaborador((String) row[1]).dia((String) row[2]).horario((String) row[3]).rango((String) row[4]).vigenteDesde((String) row[5]).diasConEsteHorario(((Number) row[6]).intValue()).vigenteHasta(vigenteHasta).esIndefinido(esIndefinido).proximoACambiar(proximoACambiar).diasParaVencimiento(diasParaVencimiento).build();
        }).collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public Map<String, Object> resumenHorariosColaborador(Long idColaborador) {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("resumen_horarios_colaborador");
        query.registerStoredProcedureParameter("p_id_colaborador", Long.class, ParameterMode.IN);
        query.setParameter("p_id_colaborador", idColaborador); query.execute();
        
        Object[] row = (Object[]) query.getSingleResult();
        
        Map<String, Object> map = new HashMap<>(); map.put("id", row[0]); map.put("colaborador", row[1]);
        map.put("lunes", row[2]); map.put("martes", row[3]); map.put("miercoles", row[4]); map.put("jueves", row[5]);
        map.put("viernes", row[6]); map.put("sabado", row[7]); map.put("domingo", row[8]); return map;
    }
    
    // UTILIDADES
    private String calcularDiaSemana(LocalDate fecha) {
        String[] dias = {"LUNES", "MARTES", "MIERCOLES", "JUEVES", "VIERNES", "SABADO", "DOMINGO"};
        return dias[fecha.getDayOfWeek().getValue() - 1];
    }
    
    private LocalDate parsearFecha(String fecha) {
        // Formato: dd/MM/yyyy -> yyyy-MM-dd
        return LocalDate.parse(fecha.replaceAll("(\\d{2})/(\\d{2})/(\\d{4})", "$3-$2-$1"));
    }
}

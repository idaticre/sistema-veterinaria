package com.vet.manadawoof.service.impl;

import com.vet.manadawoof.dtos.request.AtencionMedicaRequestDTO;
import com.vet.manadawoof.dtos.request.RegistrarCitaAtendidaRequestDTO;
import com.vet.manadawoof.dtos.response.AtencionMedicaResponseDTO;
import com.vet.manadawoof.entity.HistoriaClinicaRegistroEntity;
import com.vet.manadawoof.mapper.AtencionMedicaMapper;
import com.vet.manadawoof.repository.AtencionMedicaRepository;
import com.vet.manadawoof.service.AtencionMedicaService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.sql.Time;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AtencionMedicaServiceImpl implements AtencionMedicaService {
    
    @PersistenceContext
    private final EntityManager entityManager;
    private final AtencionMedicaRepository atencionMedicaRepository;
    private final AtencionMedicaMapper atencionMedicaMapper;
    
    /**
     * NUEVO: Registrar cita como atendida (flujo principal)
     * Llama a: registrar_cita_atendida()
     * Responsabilidad: Marcar cita como ATENDIDA + crear registro en historia
     * Soporta: MÉDICA, ESTÉTICA, HOSPEDAJE, GENERAL
     */
    @Override
    @Transactional
    public AtencionMedicaResponseDTO registrarCitaAtendida(RegistrarCitaAtendidaRequestDTO dto) {
        StoredProcedureQuery sp = entityManager.createStoredProcedureQuery("registrar_cita_atendida");
        
        registrarParametrosRegistrarCitaAtendida(sp);
        
        // Parámetros de entrada - Datos administrativos
        sp.setParameter("p_id_agenda", dto.getIdAgenda());
        sp.setParameter("p_id_veterinario", dto.getIdVeterinario());
        sp.setParameter("p_id_colaborador", dto.getIdColaborador());
        sp.setParameter("p_tipo_visita", dto.getTipoVisita());
        
        // Datos clínicos (pueden ser NULL)
        sp.setParameter("p_motivo_consulta", dto.getMotivoConsulta());
        sp.setParameter("p_anamnesis", dto.getAnamnesis());
        sp.setParameter("p_examen_fisico", dto.getExamenFisico());
        sp.setParameter("p_signos_vitales", dto.getSignosVitales());
        sp.setParameter("p_peso_kg", dto.getPesoKg());
        sp.setParameter("p_temperatura_c", dto.getTemperaturaC());
        sp.setParameter("p_diagnostico", dto.getDiagnostico());
        sp.setParameter("p_tratamiento", dto.getTratamiento());
        sp.setParameter("p_proximo_control", dto.getProximoControl() != null ? Date.valueOf(dto.getProximoControl()) : null);
        
        // Datos estética (pueden ser NULL)
        sp.setParameter("p_estado_pelaje", dto.getEstadoPelaje());
        sp.setParameter("p_condicion_piel", dto.getCondicionPiel());
        sp.setParameter("p_observaciones_grooming", dto.getObservacionesGrooming());
        
        // Datos hospedaje (pueden ser NULL)
        sp.setParameter("p_comportamiento_hospedaje", dto.getComportamientoHospedaje());
        sp.setParameter("p_alimentacion_hospedaje", dto.getAlimentacionHospedaje());
        sp.setParameter("p_actividad_hospedaje", dto.getActividadHospedaje());
        
        // Notas generales
        sp.setParameter("p_observaciones", dto.getObservaciones());
        
        sp.execute();
        
        Long idRegistro = ((Number) sp.getOutputParameterValue("p_id_registro")).longValue();
        String mensaje = (String) sp.getOutputParameterValue("p_mensaje");
        
        if(mensaje != null && mensaje.startsWith("ERROR")) {
            return AtencionMedicaResponseDTO.builder().mensaje(mensaje).build();
        }
        
        AtencionMedicaResponseDTO response = obtenerPorId(idRegistro);
        response.setMensaje(mensaje);
        return response;
    }
    
    /**
     * ANTIGUO: Crear atención manual (casos especiales)
     * Llama a: registrar_atencion()
     * Responsabilidad: Crear registro sin necesidad de cita previa
     * Uso: Emergencias, datos retrospectivos, casos especiales
     */
    @Override
    @Transactional
    public AtencionMedicaResponseDTO crear(AtencionMedicaRequestDTO dto) {
        StoredProcedureQuery sp = entityManager.createStoredProcedureQuery("registrar_atencion");
        
        registrarParametrosRegistrarAtencion(sp, true);
        
        sp.setParameter("p_id_historia_clinica", dto.getIdHistoriaClinica());
        sp.setParameter("p_id_agenda", dto.getIdAgenda());
        sp.setParameter("p_id_veterinario", dto.getIdVeterinario());
        sp.setParameter("p_id_colaborador", dto.getIdColaborador());
        sp.setParameter("p_fecha_atencion", Date.valueOf(dto.getFechaAtencion()));
        sp.setParameter("p_hora_inicio", Time.valueOf(dto.getHoraInicio()));
        sp.setParameter("p_hora_fin", dto.getHoraFin() != null ? Time.valueOf(dto.getHoraFin()) : null);
        sp.setParameter("p_motivo_consulta", dto.getMotivoConsulta());
        sp.setParameter("p_anamnesis", dto.getAnamnesis());
        sp.setParameter("p_examen_fisico", dto.getExamenFisico());
        sp.setParameter("p_signos_vitales", dto.getSignosVitales());
        sp.setParameter("p_peso_kg", dto.getPesoKg());
        sp.setParameter("p_temperatura_c", dto.getTemperaturaC());
        sp.setParameter("p_diagnostico", dto.getDiagnostico());
        sp.setParameter("p_tratamiento", dto.getTratamiento());
        sp.setParameter("p_observaciones", dto.getObservaciones());
        sp.setParameter("p_proximo_control", dto.getProximoControl() != null ? Date.valueOf(dto.getProximoControl()) : null);
        
        sp.execute();
        
        Long idRegistro = ((Number) sp.getOutputParameterValue("p_id_registro")).longValue();
        String mensaje = (String) sp.getOutputParameterValue("p_mensaje");
        
        if(mensaje != null && mensaje.startsWith("ERROR")) {
            return AtencionMedicaResponseDTO.builder().mensaje(mensaje).build();
        }
        
        AtencionMedicaResponseDTO response = obtenerPorId(idRegistro);
        response.setMensaje(mensaje);
        return response;
    }
    
    /**
     * Actualizar atención existente
     * Llama a: actualizar_atencion()
     * Soporta: datos clínicos, estéticos y hospedaje
     */
    @Override
    @Transactional
    public AtencionMedicaResponseDTO actualizar(AtencionMedicaRequestDTO dto) {
        StoredProcedureQuery sp = entityManager.createStoredProcedureQuery("actualizar_atencion");
        
        registrarParametrosActualizarAtencion(sp);
        
        sp.setParameter("p_id_registro", dto.getId());
        sp.setParameter("p_id_veterinario", dto.getIdVeterinario());
        sp.setParameter("p_id_colaborador", dto.getIdColaborador());
        sp.setParameter("p_hora_fin", dto.getHoraFin() != null ? Time.valueOf(dto.getHoraFin()) : null);
        
        // Datos clínicos
        sp.setParameter("p_motivo_consulta", dto.getMotivoConsulta());
        sp.setParameter("p_anamnesis", dto.getAnamnesis());
        sp.setParameter("p_examen_fisico", dto.getExamenFisico());
        sp.setParameter("p_signos_vitales", dto.getSignosVitales());
        sp.setParameter("p_peso_kg", dto.getPesoKg());
        sp.setParameter("p_temperatura_c", dto.getTemperaturaC());
        sp.setParameter("p_diagnostico", dto.getDiagnostico());
        sp.setParameter("p_tratamiento", dto.getTratamiento());
        sp.setParameter("p_proximo_control", dto.getProximoControl() != null ? Date.valueOf(dto.getProximoControl()) : null);
        
        // Datos estética
        sp.setParameter("p_estado_pelaje", dto.getEstadoPelaje());
        sp.setParameter("p_condicion_piel", dto.getCondicionPiel());
        sp.setParameter("p_observaciones_grooming", dto.getObservacionesGrooming());
        
        // Datos hospedaje
        sp.setParameter("p_comportamiento_hospedaje", dto.getComportamientoHospedaje());
        sp.setParameter("p_alimentacion_hospedaje", dto.getAlimentacionHospedaje());
        sp.setParameter("p_actividad_hospedaje", dto.getActividadHospedaje());
        
        // Notas generales
        sp.setParameter("p_observaciones", dto.getObservaciones());
        sp.setParameter("p_id_estado", dto.getIdEstado());
        
        sp.execute();
        
        String mensaje = (String) sp.getOutputParameterValue("p_mensaje");
        
        if(mensaje != null && mensaje.startsWith("ERROR")) {
            return AtencionMedicaResponseDTO.builder().mensaje(mensaje).build();
        }
        
        AtencionMedicaResponseDTO response = obtenerPorId(dto.getId());
        response.setMensaje(mensaje);
        return response;
    }
    
    /**
     * Eliminar atención (solo si está en estado editable)
     * Llama a: eliminar_atencion()
     */
    @Override
    @Transactional
    public AtencionMedicaResponseDTO eliminar(Long id) {
        StoredProcedureQuery sp = entityManager.createStoredProcedureQuery("eliminar_atencion");
        
        sp.registerStoredProcedureParameter("p_id_registro", Long.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_codigo", String.class, ParameterMode.OUT);
        sp.registerStoredProcedureParameter("p_mensaje", String.class, ParameterMode.OUT);
        
        sp.setParameter("p_id_registro", id);
        sp.execute();
        
        String mensaje = (String) sp.getOutputParameterValue("p_mensaje");
        
        return AtencionMedicaResponseDTO.builder()
                .mensaje(mensaje)
                .build();
    }
    
    @Override
    @Transactional
    public AtencionMedicaResponseDTO obtenerPorId(Long id) {
        HistoriaClinicaRegistroEntity entity = atencionMedicaRepository.findById(id).orElse(null);
        if(entity == null) return null;
        return atencionMedicaMapper.toDto(entity);
    }
    
    @Override
    @Transactional
    public List<AtencionMedicaResponseDTO> listarPorHistoria(Long idHistoriaClinica) {
        List<HistoriaClinicaRegistroEntity> registros = atencionMedicaRepository.findByHistoriaClinicaId(idHistoriaClinica);
        return registros.stream().map(atencionMedicaMapper :: toDto).toList();
    }
    
    @Override
    @Transactional
    public Page<AtencionMedicaResponseDTO> listarPorHistoriaPaginado(Long idHistoriaClinica, Pageable pageable) {
        Page<HistoriaClinicaRegistroEntity> page = atencionMedicaRepository.findByHistoriaClinicaId(idHistoriaClinica, pageable);
        return page.map(atencionMedicaMapper :: toDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<AtencionMedicaResponseDTO> listarPorVeterinario(Long idVeterinario) {
        List<HistoriaClinicaRegistroEntity> registros = atencionMedicaRepository.findByVeterinarioId(idVeterinario);
        return registros.stream().map(atencionMedicaMapper :: toDto).toList();
    }
    
    // ========================================
    // MÉTODOS PRIVADOS PARA REGISTRAR PARÁMETROS
    // ========================================
    
    /**
     * Registra parámetros para registrar_cita_atendida()
     */
    private void registrarParametrosRegistrarCitaAtendida(StoredProcedureQuery sp) {
        // Parámetros de entrada
        sp.registerStoredProcedureParameter("p_id_agenda", Long.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_id_veterinario", Long.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_id_colaborador", Long.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_tipo_visita", String.class, ParameterMode.IN);
        
        // Datos clínicos
        sp.registerStoredProcedureParameter("p_motivo_consulta", String.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_anamnesis", String.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_examen_fisico", String.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_signos_vitales", String.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_peso_kg", java.math.BigDecimal.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_temperatura_c", java.math.BigDecimal.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_diagnostico", String.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_tratamiento", String.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_proximo_control", Date.class, ParameterMode.IN);
        
        // Datos estética
        sp.registerStoredProcedureParameter("p_estado_pelaje", String.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_condicion_piel", String.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_observaciones_grooming", String.class, ParameterMode.IN);
        
        // Datos hospedaje
        sp.registerStoredProcedureParameter("p_comportamiento_hospedaje", String.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_alimentacion_hospedaje", String.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_actividad_hospedaje", String.class, ParameterMode.IN);
        
        // Notas generales
        sp.registerStoredProcedureParameter("p_observaciones", String.class, ParameterMode.IN);
        
        // Parámetros de salida
        sp.registerStoredProcedureParameter("p_id_registro", Long.class, ParameterMode.OUT);
        sp.registerStoredProcedureParameter("p_codigo_registro", String.class, ParameterMode.OUT);
        sp.registerStoredProcedureParameter("p_mensaje", String.class, ParameterMode.OUT);
    }
    
    /**
     * Registra parámetros para registrar_atencion()
     */
    private void registrarParametrosRegistrarAtencion(StoredProcedureQuery sp, boolean esCrear) {
        if(esCrear) {
            sp.registerStoredProcedureParameter("p_id_historia_clinica", Long.class, ParameterMode.IN);
            sp.registerStoredProcedureParameter("p_id_agenda", Long.class, ParameterMode.IN);
            sp.registerStoredProcedureParameter("p_id_veterinario", Long.class, ParameterMode.IN);
            sp.registerStoredProcedureParameter("p_id_colaborador", Long.class, ParameterMode.IN);
            sp.registerStoredProcedureParameter("p_fecha_atencion", Date.class, ParameterMode.IN);
            sp.registerStoredProcedureParameter("p_hora_inicio", Time.class, ParameterMode.IN);
            sp.registerStoredProcedureParameter("p_hora_fin", Time.class, ParameterMode.IN);
            sp.registerStoredProcedureParameter("p_motivo_consulta", String.class, ParameterMode.IN);
            sp.registerStoredProcedureParameter("p_anamnesis", String.class, ParameterMode.IN);
            sp.registerStoredProcedureParameter("p_examen_fisico", String.class, ParameterMode.IN);
            sp.registerStoredProcedureParameter("p_signos_vitales", String.class, ParameterMode.IN);
            sp.registerStoredProcedureParameter("p_peso_kg", java.math.BigDecimal.class, ParameterMode.IN);
            sp.registerStoredProcedureParameter("p_temperatura_c", java.math.BigDecimal.class, ParameterMode.IN);
            sp.registerStoredProcedureParameter("p_diagnostico", String.class, ParameterMode.IN);
            sp.registerStoredProcedureParameter("p_tratamiento", String.class, ParameterMode.IN);
            sp.registerStoredProcedureParameter("p_observaciones", String.class, ParameterMode.IN);
            sp.registerStoredProcedureParameter("p_proximo_control", Date.class, ParameterMode.IN);
            sp.registerStoredProcedureParameter("p_id_registro", Long.class, ParameterMode.OUT);
            sp.registerStoredProcedureParameter("p_codigo", String.class, ParameterMode.OUT);
            sp.registerStoredProcedureParameter("p_mensaje", String.class, ParameterMode.OUT);
        }
    }
    
    /**
     * Registra parámetros para actualizar_atencion()
     */
    private void registrarParametrosActualizarAtencion(StoredProcedureQuery sp) {
        sp.registerStoredProcedureParameter("p_id_registro", Long.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_id_veterinario", Long.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_id_colaborador", Long.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_hora_fin", Time.class, ParameterMode.IN);
        
        // Datos clínicos
        sp.registerStoredProcedureParameter("p_motivo_consulta", String.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_anamnesis", String.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_examen_fisico", String.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_signos_vitales", String.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_peso_kg", java.math.BigDecimal.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_temperatura_c", java.math.BigDecimal.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_diagnostico", String.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_tratamiento", String.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_proximo_control", Date.class, ParameterMode.IN);
        
        // Datos estética
        sp.registerStoredProcedureParameter("p_estado_pelaje", String.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_condicion_piel", String.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_observaciones_grooming", String.class, ParameterMode.IN);
        
        // Datos hospedaje
        sp.registerStoredProcedureParameter("p_comportamiento_hospedaje", String.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_alimentacion_hospedaje", String.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_actividad_hospedaje", String.class, ParameterMode.IN);
        
        // Notas generales
        sp.registerStoredProcedureParameter("p_observaciones", String.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_id_estado", Integer.class, ParameterMode.IN);
        
        // Parámetros de salida
        sp.registerStoredProcedureParameter("p_codigo", String.class, ParameterMode.OUT);
        sp.registerStoredProcedureParameter("p_mensaje", String.class, ParameterMode.OUT);
    }
}

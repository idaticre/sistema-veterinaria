package com.vet.manadawoof.service.impl;

import com.vet.manadawoof.dtos.request.ArchivoClinicoRequestDTO;
import com.vet.manadawoof.dtos.request.AtencionMedicaRequestDTO;
import com.vet.manadawoof.dtos.request.HistoriaClinicaRequestDTO;
import com.vet.manadawoof.dtos.response.ArchivoClinicoResponseDTO;
import com.vet.manadawoof.dtos.response.AtencionMedicaResponseDTO;
import com.vet.manadawoof.dtos.response.HistoriaClinicaResponseDTO;
import com.vet.manadawoof.mapper.ArchivoClinicoMapper;
import com.vet.manadawoof.mapper.AtencionMedicaMapper;
import com.vet.manadawoof.mapper.HistoriaClinicaMapper;
import com.vet.manadawoof.service.HistoriaClinicaService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class HistoriaClinicaServiceImpl implements HistoriaClinicaService {
    
    @PersistenceContext
    private final EntityManager entityManager;
    private final HistoriaClinicaMapper historiaClinicaMapper;
    private final AtencionMedicaMapper atencionMedicaMapper;
    private final ArchivoClinicoMapper archivoClinicoMapper;
    
    @Override
    @Transactional
    public HistoriaClinicaResponseDTO crear(HistoriaClinicaRequestDTO dto) {
        StoredProcedureQuery sp = entityManager.createStoredProcedureQuery("sp_crear_historia_clinica");
        
        sp.registerStoredProcedureParameter("p_id_mascota", Long.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_observaciones_generales", String.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_id_historia", Long.class, ParameterMode.OUT);
        sp.registerStoredProcedureParameter("p_codigo", String.class, ParameterMode.OUT);
        sp.registerStoredProcedureParameter("p_mensaje", String.class, ParameterMode.OUT);
        
        sp.setParameter("p_id_mascota", dto.getIdMascota());
        sp.setParameter("p_observaciones_generales", dto.getObservacionesGenerales());
        
        sp.execute();
        
        Long idHistoria = ((Number) sp.getOutputParameterValue("p_id_historia")).longValue();
        String codigo = (String) sp.getOutputParameterValue("p_codigo");
        String mensaje = (String) sp.getOutputParameterValue("p_mensaje");
        
        if(mensaje != null && mensaje.startsWith("ERROR")) {
            return HistoriaClinicaResponseDTO.builder().mensaje(mensaje).build();
        }
        
        // Consultar datos creados
        Object[] row = (Object[]) entityManager.createNativeQuery(
                "SELECT id, codigo, id_mascota, fecha_apertura, observaciones_generales, " +
                        "activa, fecha_registro FROM historia_clinica WHERE id = ?1"
        ).setParameter(1, idHistoria).getSingleResult();
        
        return mapHistoriaToDto(row, mensaje);
    }
    
    @Override
    @Transactional
    public AtencionMedicaResponseDTO registrarAtencion(AtencionMedicaRequestDTO dto) {
        StoredProcedureQuery sp = entityManager.createStoredProcedureQuery("sp_registrar_atencion_medica");
        
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
        sp.registerStoredProcedureParameter("p_peso_kg", BigDecimal.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_temperatura_c", BigDecimal.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_diagnostico", String.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_tratamiento", String.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_observaciones", String.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_proximo_control", Date.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_id_registro", Long.class, ParameterMode.OUT);
        sp.registerStoredProcedureParameter("p_codigo", String.class, ParameterMode.OUT);
        sp.registerStoredProcedureParameter("p_mensaje", String.class, ParameterMode.OUT);
        
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
        String codigo = (String) sp.getOutputParameterValue("p_codigo");
        String mensaje = (String) sp.getOutputParameterValue("p_mensaje");
        
        if(mensaje != null && mensaje.startsWith("ERROR")) {
            return AtencionMedicaResponseDTO.builder().mensaje(mensaje).build();
        }
        
        AtencionMedicaResponseDTO response = obtenerAtencionPorId(idRegistro);
        response.setMensaje(mensaje);
        return response;
    }
    
    @Override
    @Transactional
    public AtencionMedicaResponseDTO actualizarAtencion(AtencionMedicaRequestDTO dto) {
        StoredProcedureQuery sp = entityManager.createStoredProcedureQuery("sp_actualizar_atencion_medica");
        
        sp.registerStoredProcedureParameter("p_id_registro", Long.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_id_veterinario", Long.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_id_colaborador", Long.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_hora_fin", Time.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_motivo_consulta", String.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_anamnesis", String.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_examen_fisico", String.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_signos_vitales", String.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_peso_kg", BigDecimal.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_temperatura_c", BigDecimal.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_diagnostico", String.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_tratamiento", String.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_observaciones", String.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_proximo_control", Date.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_id_estado", Integer.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_codigo", String.class, ParameterMode.OUT);
        sp.registerStoredProcedureParameter("p_mensaje", String.class, ParameterMode.OUT);
        
        sp.setParameter("p_id_registro", dto.getIdRegistro());
        sp.setParameter("p_id_veterinario", dto.getIdVeterinario());
        sp.setParameter("p_id_colaborador", dto.getIdColaborador());
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
        sp.setParameter("p_id_estado", dto.getIdEstado());
        
        sp.execute();
        
        String codigo = (String) sp.getOutputParameterValue("p_codigo");
        String mensaje = (String) sp.getOutputParameterValue("p_mensaje");
        
        if(mensaje != null && mensaje.startsWith("ERROR")) {
            return AtencionMedicaResponseDTO.builder().mensaje(mensaje).build();
        }
        
        AtencionMedicaResponseDTO response = obtenerAtencionPorId(dto.getIdRegistro());
        response.setMensaje(mensaje);
        return response;
    }
    
    @Override
    @Transactional
    public ArchivoClinicoResponseDTO subirArchivo(ArchivoClinicoRequestDTO dto) {
        StoredProcedureQuery sp = entityManager.createStoredProcedureQuery("sp_subir_archivo_clinico");
        
        sp.registerStoredProcedureParameter("p_id_registro_atencion", Long.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_id_tipo_archivo", Integer.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_nombre_archivo", String.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_extension_archivo", String.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_descripcion", String.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_id_archivo", Long.class, ParameterMode.OUT);
        sp.registerStoredProcedureParameter("p_codigo", String.class, ParameterMode.OUT);
        sp.registerStoredProcedureParameter("p_mensaje", String.class, ParameterMode.OUT);
        
        sp.setParameter("p_id_registro_atencion", dto.getIdRegistroAtencion());
        sp.setParameter("p_id_tipo_archivo", dto.getIdTipoArchivo());
        sp.setParameter("p_nombre_archivo", dto.getNombreArchivo());
        sp.setParameter("p_extension_archivo", dto.getExtensionArchivo());
        sp.setParameter("p_descripcion", dto.getDescripcion());
        
        sp.execute();
        
        Long idArchivo = ((Number) sp.getOutputParameterValue("p_id_archivo")).longValue();
        String codigo = (String) sp.getOutputParameterValue("p_codigo");
        String mensaje = (String) sp.getOutputParameterValue("p_mensaje");
        
        if(mensaje != null && mensaje.startsWith("ERROR")) {
            return ArchivoClinicoResponseDTO.builder().mensaje(mensaje).build();
        }
        
        ArchivoClinicoResponseDTO response = obtenerArchivoPorId(idArchivo);
        response.setMensaje(mensaje);
        return response;
    }
    
    @Override
    @Transactional
    public ArchivoClinicoResponseDTO eliminarArchivo(Long idArchivo) {
        StoredProcedureQuery sp = entityManager.createStoredProcedureQuery("sp_eliminar_archivo_clinico");
        
        sp.registerStoredProcedureParameter("p_id_archivo", Long.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_codigo", String.class, ParameterMode.OUT);
        sp.registerStoredProcedureParameter("p_mensaje", String.class, ParameterMode.OUT);
        
        sp.setParameter("p_id_archivo", idArchivo);
        
        sp.execute();
        
        String codigo = (String) sp.getOutputParameterValue("p_codigo");
        String mensaje = (String) sp.getOutputParameterValue("p_mensaje");
        
        return ArchivoClinicoResponseDTO.builder()
                .codigo(codigo)
                .mensaje(mensaje)
                .build();
    }
    
    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> consultarHistorialMascota(Long idMascota) {
        StoredProcedureQuery sp = entityManager.createStoredProcedureQuery("sp_consultar_historial_mascota");
        
        sp.registerStoredProcedureParameter("p_id_mascota", Long.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_mensaje", String.class, ParameterMode.OUT);
        
        sp.setParameter("p_id_mascota", idMascota);
        
        boolean hasResults = sp.execute();
        String mensaje = (String) sp.getOutputParameterValue("p_mensaje");
        
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("mensaje", mensaje);
        
        if(mensaje != null && mensaje.startsWith("ERROR")) {
            return resultado;
        }
        
        // Resultado 1: Info general mascota + historia
        List<?> infoGeneral = sp.getResultList();
        resultado.put("infoGeneral", infoGeneral);
        
        // Resultado 2: Registros de atención
        hasResults = sp.hasMoreResults();
        if(hasResults) {
            List<?> registros = sp.getResultList();
            resultado.put("registros", registros);
        }
        
        // Resultado 3: Archivos médicos
        hasResults = sp.hasMoreResults();
        if(hasResults) {
            List<?> archivos = sp.getResultList();
            resultado.put("archivos", archivos);
        }
        
        return resultado;
    }
    
    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> consultarRegistroAtencion(Long idRegistro) {
        StoredProcedureQuery sp = entityManager.createStoredProcedureQuery("sp_consultar_registro_atencion");
        
        sp.registerStoredProcedureParameter("p_id_registro", Long.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_mensaje", String.class, ParameterMode.OUT);
        
        sp.setParameter("p_id_registro", idRegistro);
        
        boolean hasResults = sp.execute();
        String mensaje = (String) sp.getOutputParameterValue("p_mensaje");
        
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("mensaje", mensaje);
        
        if(mensaje != null && mensaje.startsWith("ERROR")) {
            return resultado;
        }
        
        // Resultado 1: Datos del registro
        List<?> datosRegistro = sp.getResultList();
        resultado.put("registro", datosRegistro);
        
        // Resultado 2: Archivos de este registro
        hasResults = sp.hasMoreResults();
        if(hasResults) {
            List<?> archivos = sp.getResultList();
            resultado.put("archivos", archivos);
        }
        
        return resultado;
    }
    
    // Métodos auxiliares
    private HistoriaClinicaResponseDTO mapHistoriaToDto(Object[] row, String mensaje) {
        return historiaClinicaMapper.toDto(row, mensaje);
    }
    
    private AtencionMedicaResponseDTO obtenerAtencionPorId(Long id) {
        Object[] row = (Object[]) entityManager.createNativeQuery(
                "SELECT id, codigo, id_historia_clinica, id_agenda, id_veterinario, id_colaborador, " +
                        "fecha_atencion, hora_inicio, hora_fin, motivo_consulta, anamnesis, examen_fisico, " +
                        "signos_vitales, peso_kg, temperatura_c, diagnostico, tratamiento, observaciones, " +
                        "proximo_control, id_estado, fecha_registro FROM historia_clinica_registros WHERE id = ?1"
        ).setParameter(1, id).getSingleResult();
        
        return atencionMedicaMapper.toDto(row);
    }
    
    private AtencionMedicaResponseDTO mapAtencionToDto(Object[] row) {
        return atencionMedicaMapper.toDto(row);
    }
    
    private ArchivoClinicoResponseDTO obtenerArchivoPorId(Long id) {
        Object[] row = (Object[]) entityManager.createNativeQuery(
                "SELECT id, codigo, id_registro_atencion, id_tipo_archivo, nombre_archivo, " +
                        "extension_archivo, descripcion, fecha_subida FROM historia_clinica_archivos WHERE id = ?1"
        ).setParameter(1, id).getSingleResult();
        
        return archivoClinicoMapper.toDto(row);
    }
    
    private ArchivoClinicoResponseDTO mapArchivoToDto(Object[] row) {
        return archivoClinicoMapper.toDto(row);
    }
}

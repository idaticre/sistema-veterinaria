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
    private final EntityManager em;
    private final RegistroAsistenciaMapper mapper;
    private final ColaboradorRepository colaboradorRepository;
    private final RegistroAsistenciaRepository registroRepository;
    
    @Override
    @Transactional
    public RegistroAsistenciaResponseDTO registrar(RegistrarAsistenciaRequestDTO request) {
        LocalDate fechaActual = LocalDate.now(); LocalTime horaActual = LocalTime.now();
        
        try {
            StoredProcedureQuery query = em.createStoredProcedureQuery("gestionar_asistencia");
            
            // Parámetros IN
            query.registerStoredProcedureParameter("p_colaborador_id", Long.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("p_fecha", Date.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("p_hora", Time.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("p_tipo_movimiento", String.class, ParameterMode.IN);
            
            // Parámetros OUT
            query.registerStoredProcedureParameter("p_mensaje", String.class, ParameterMode.OUT);
            query.registerStoredProcedureParameter("p_success", Integer.class, ParameterMode.OUT);
            query.registerStoredProcedureParameter("p_tardanza_minutos", Integer.class, ParameterMode.OUT);
            query.registerStoredProcedureParameter("p_estado_final", String.class, ParameterMode.OUT);
            
            query.setParameter("p_colaborador_id", request.getIdColaborador());
            query.setParameter("p_fecha", Date.valueOf(fechaActual));
            query.setParameter("p_hora", Time.valueOf(horaActual));
            query.setParameter("p_tipo_movimiento", request.getTipoMarca().name());
            
            query.execute();
            
            // Extraer parámetros OUT
            Integer successInt = (Integer) query.getOutputParameterValue("p_success");
            Boolean success = successInt != null && successInt == 1;
            String mensaje = (String) query.getOutputParameterValue("p_mensaje");
            Integer tardanzaMinutos = (Integer) query.getOutputParameterValue("p_tardanza_minutos");
            String estadoFinal = (String) query.getOutputParameterValue("p_estado_final");
            
            if(! success) {
                throw new IllegalArgumentException(mensaje != null ? mensaje : "Error al registrar asistencia");
            }
            
            // Obtener datos del colaborador
            ColaboradorEntity colaborador = colaboradorRepository.findById(request.getIdColaborador()).orElseThrow(() -> new RuntimeException("Colaborador no encontrado"));
            
            // Obtener registro actualizado de la BD
            var registroActual = registroRepository.findByColaboradorIdAndFecha(request.getIdColaborador(), fechaActual).orElse(null);
            
            return RegistroAsistenciaResponseDTO.builder().success(true).mensaje(mensaje != null ? mensaje : "Registro exitoso").tardanzaMinutos(tardanzaMinutos != null ? tardanzaMinutos : 0).estadoFinal(estadoFinal != null ? estadoFinal : "").fecha(fechaActual).horaMarcacion(horaActual).tipoMarca(request.getTipoMarca().name()).idColaborador(request.getIdColaborador()).nombreColaborador(colaborador.getEntidad().getNombre()).horaEntrada(registroActual != null ? registroActual.getHoraEntrada() : null).horaLunchInicio(registroActual != null ? registroActual.getHoraLunchInicio() : null).horaLunchFin(registroActual != null ? registroActual.getHoraLunchFin() : null).horaSalida(registroActual != null ? registroActual.getHoraSalida() : null).minutosTrabajados(registroActual != null ? registroActual.getMinutosTrabajados() : null).minutosLunch(registroActual != null ? registroActual.getMinutosLunch() : null).build();
            
        } catch (Exception e) {
            throw new RuntimeException("Error al registrar asistencia: " + e.getMessage(), e);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<RegistroAsistenciaResponseDTO> verAsistenciaPorRango(LocalDate fechaInicio, LocalDate fechaFin, Long idColaborador, Integer idEstado) {
        StoredProcedureQuery query = em.createStoredProcedureQuery("ver_asistencia_por_rango");
        
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

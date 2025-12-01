package com.vet.manadawoof.service.impl;

import com.vet.manadawoof.dtos.request.ArchivoClinicoRequestDTO;
import com.vet.manadawoof.dtos.response.ArchivoClinicoResponseDTO;
import com.vet.manadawoof.entity.HistoriaClinicaArchivoEntity;
import com.vet.manadawoof.mapper.ArchivoClinicoMapper;
import com.vet.manadawoof.repository.ArchivoClinicoRepository;
import com.vet.manadawoof.service.ArchivoClinicoService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ArchivoClinicoServiceImpl implements ArchivoClinicoService {
    
    @PersistenceContext
    private final EntityManager entityManager;
    private final ArchivoClinicoRepository archivoClinicoRepository;
    private final ArchivoClinicoMapper archivoClinicoMapper;
    
    @Override
    @Transactional
    public ArchivoClinicoResponseDTO subir(ArchivoClinicoRequestDTO dto) {
        StoredProcedureQuery sp = entityManager.createStoredProcedureQuery("subir_archivo_clinico");
        
        registrarParametros(sp, true);
        
        sp.setParameter("p_id_registro_atencion", dto.getIdRegistroAtencion());
        sp.setParameter("p_id_tipo_archivo", dto.getIdTipoArchivo());
        sp.setParameter("p_nombre_archivo", dto.getNombreArchivo());
        sp.setParameter("p_extension_archivo", dto.getExtensionArchivo());
        sp.setParameter("p_descripcion", dto.getDescripcion());
        
        sp.execute();
        
        Long idArchivo = ((Number) sp.getOutputParameterValue("p_id_archivo")).longValue();
        String mensaje = (String) sp.getOutputParameterValue("p_mensaje");
        
        if(mensaje != null && mensaje.startsWith("ERROR")) {
            return ArchivoClinicoResponseDTO.builder().mensaje(mensaje).build();
        }
        
        ArchivoClinicoResponseDTO response = obtenerPorId(idArchivo);
        response.setMensaje(mensaje);
        return response;
    }
    
    @Override
    @Transactional
    public ArchivoClinicoResponseDTO eliminar(Long idArchivo) {
        StoredProcedureQuery sp = entityManager.createStoredProcedureQuery("eliminar_archivo_clinico");
        
        registrarParametros(sp, false);
        
        sp.setParameter("p_id_archivo", idArchivo);
        
        sp.execute();
        
        String codigo = (String) sp.getOutputParameterValue("p_codigo");
        String mensaje = (String) sp.getOutputParameterValue("p_mensaje");
        
        if(mensaje != null && mensaje.startsWith("ERROR")) {
            return ArchivoClinicoResponseDTO.builder().mensaje(mensaje).build();
        }
        
        return ArchivoClinicoResponseDTO.builder()
                .codigo(codigo)
                .mensaje(mensaje)
                .build();
    }
    
    @Override
    @Transactional(readOnly = true)
    public ArchivoClinicoResponseDTO obtenerPorId(Long id) {
        HistoriaClinicaArchivoEntity entity = archivoClinicoRepository.findById(id).orElse(null);
        if(entity == null) return null;
        return archivoClinicoMapper.toDto(entity);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ArchivoClinicoResponseDTO> listarPorRegistro(Long idRegistroAtencion) {
        List<HistoriaClinicaArchivoEntity> archivos = archivoClinicoRepository.findByRegistroAtencionId(idRegistroAtencion);
        return archivos.stream()
                .map(archivoClinicoMapper :: toDto)
                .toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ArchivoClinicoResponseDTO> listarPorRegistroPaginado(Long idRegistroAtencion, Pageable pageable) {
        Page<HistoriaClinicaArchivoEntity> page = archivoClinicoRepository.findByRegistroAtencionId(idRegistroAtencion, pageable);
        return page.map(archivoClinicoMapper :: toDto);
    }
    
    private void registrarParametros(StoredProcedureQuery sp, boolean esSubir) {
        if(esSubir) {
            sp.registerStoredProcedureParameter("p_id_registro_atencion", Long.class, ParameterMode.IN);
            sp.registerStoredProcedureParameter("p_id_tipo_archivo", Integer.class, ParameterMode.IN);
            sp.registerStoredProcedureParameter("p_nombre_archivo", String.class, ParameterMode.IN);
            sp.registerStoredProcedureParameter("p_extension_archivo", String.class, ParameterMode.IN);
            sp.registerStoredProcedureParameter("p_descripcion", String.class, ParameterMode.IN);
            sp.registerStoredProcedureParameter("p_id_archivo", Long.class, ParameterMode.OUT);
            sp.registerStoredProcedureParameter("p_codigo", String.class, ParameterMode.OUT);
            sp.registerStoredProcedureParameter("p_mensaje", String.class, ParameterMode.OUT);
        } else {
            sp.registerStoredProcedureParameter("p_id_archivo", Long.class, ParameterMode.IN);
            sp.registerStoredProcedureParameter("p_codigo", String.class, ParameterMode.OUT);
            sp.registerStoredProcedureParameter("p_mensaje", String.class, ParameterMode.OUT);
        }
    }
}

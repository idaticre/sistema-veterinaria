package com.vet.manadawoof.service.impl;

import com.vet.manadawoof.dtos.response.AuditoriaResponseDTO;
import com.vet.manadawoof.entity.AuditoriaEntity;
import com.vet.manadawoof.entity.TipoAccionEntity;
import com.vet.manadawoof.entity.UsuarioEntity;
import com.vet.manadawoof.mapper.AuditoriaMapper;
import com.vet.manadawoof.repository.AuditoriaRepository;
import com.vet.manadawoof.repository.TipoAccionRepository;
import com.vet.manadawoof.repository.UsuarioRepository;
import com.vet.manadawoof.service.AuditoriaService;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuditoriaServiceImpl implements AuditoriaService {
    
    private final AuditoriaRepository auditoriaRepository;
    private final AuditoriaMapper auditoriaMapper;
    private final UsuarioRepository usuarioRepository;
    private final TipoAccionRepository tipoAccionRepository;
    
    @Override
    @Transactional
    public void crear(
            Integer idTipoAccion,
            String entidad,
            String descripcion) {

        // Obtener el username del usuario autenticado
        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        // Buscar el usuario en la BD
        UsuarioEntity usuario = usuarioRepository
                .findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Buscar el tipo de acción
        TipoAccionEntity tipoAccion = tipoAccionRepository
                .findById(idTipoAccion)
                .orElseThrow(() -> new RuntimeException("Tipo de acción no encontrado"));

        // Crear la auditoría
        AuditoriaEntity auditoria = new AuditoriaEntity();
        auditoria.setUsuario(usuario);
        auditoria.setTipoAccion(tipoAccion);
        auditoria.setEntidad(entidad);
        auditoria.setDescripcion(descripcion);
        auditoria.setFecha(LocalDateTime.now());

        auditoriaRepository.save(auditoria);
    }
    
    @Override
    @Transactional(readOnly =  true)
    public List<AuditoriaResponseDTO> listar(){
        return auditoriaRepository.findAll().stream().map(auditoriaMapper :: toResponse).toList();
    }
    
}

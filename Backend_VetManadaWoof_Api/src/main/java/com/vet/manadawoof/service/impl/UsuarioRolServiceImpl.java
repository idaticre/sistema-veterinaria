package com.vet.manadawoof.service.impl;

import com.vet.manadawoof.dtos.request.UsuarioRolRequestDTO;
import com.vet.manadawoof.dtos.response.UsuarioRolResponseDTO;
import com.vet.manadawoof.service.UsuarioRolService;
import jakarta.persistence.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioRolServiceImpl implements UsuarioRolService {
    
    @PersistenceContext
    private final EntityManager entityManager;
    
    @Override
    @Transactional
    public UsuarioRolResponseDTO ejecutarAccion(UsuarioRolRequestDTO dto) {
        // Normalizar acción
        String accion = dto.getAccion().toUpperCase().trim();
        
        // Obtener datos ANTES de ejecutar el SP (para ELIMINAR)
        Object[] datosUsuarioRol = null;
        if("ELIMINAR".equals(accion)) {
            try {
                datosUsuarioRol = (Object[]) entityManager.createNativeQuery(
                                "SELECT u.id, u.username, r.nombre, ur.fecha_asignacion " +
                                        "FROM usuarios_roles ur " +
                                        "JOIN usuarios u ON ur.id_usuario = u.id " +
                                        "JOIN roles r ON ur.id_rol = r.id " +
                                        "WHERE ur.id_usuario = ?1 AND ur.id_rol = ?2")
                        .setParameter(1, dto.getIdUsuario())
                        .setParameter(2, dto.getIdRol())
                        .getSingleResult();
            } catch (NoResultException e) {
                return UsuarioRolResponseDTO.builder()
                        .idUsuario(dto.getIdUsuario())
                        .username(null)
                        .rol(null)
                        .fechaAsignacion(null)
                        .accion(accion)
                        .mensaje("ERROR: La asignacion usuario-rol no existe")
                        .build();
            }
        }
        
        // Ejecutar SP
        StoredProcedureQuery sp = entityManager.createStoredProcedureQuery("gestionar_rol_usuario");
        sp.registerStoredProcedureParameter("p_accion", String.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_id_usuario", Integer.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_id_rol", Integer.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_mensaje", String.class, ParameterMode.OUT);
        
        sp.setParameter("p_accion", accion);
        sp.setParameter("p_id_usuario", dto.getIdUsuario());
        sp.setParameter("p_id_rol", dto.getIdRol());
        sp.execute();
        
        String mensaje = (String) sp.getOutputParameterValue("p_mensaje");
        
        // Si fue ELIMINAR, usar los datos guardados previamente
        if("ELIMINAR".equals(accion)) {
            return UsuarioRolResponseDTO.builder()
                    .idUsuario((Integer) datosUsuarioRol[0])
                    .username((String) datosUsuarioRol[1])
                    .rol((String) datosUsuarioRol[2])
                    .fechaAsignacion(datosUsuarioRol[3] != null ? datosUsuarioRol[3].toString() : null)
                    .accion(accion)
                    .mensaje(mensaje)
                    .build();
        }
        
        // Si fue ASIGNAR, obtener los datos DESPUÉS del SP
        try {
            Object[] row = (Object[]) entityManager.createNativeQuery(
                            "SELECT u.id, u.username, r.nombre, ur.fecha_asignacion " +
                                    "FROM usuarios_roles ur " +
                                    "JOIN usuarios u ON ur.id_usuario = u.id " +
                                    "JOIN roles r ON ur.id_rol = r.id " +
                                    "WHERE ur.id_usuario = ?1 AND ur.id_rol = ?2")
                    .setParameter(1, dto.getIdUsuario())
                    .setParameter(2, dto.getIdRol())
                    .getSingleResult();
            
            return UsuarioRolResponseDTO.builder()
                    .idUsuario((Integer) row[0])
                    .username((String) row[1])
                    .rol((String) row[2])
                    .fechaAsignacion(row[3] != null ? row[3].toString() : null)
                    .accion(accion)
                    .mensaje(mensaje)
                    .build();
            
        } catch (NoResultException e) {
            // Si por alguna razón no se encontró después de ASIGNAR
            return UsuarioRolResponseDTO.builder()
                    .idUsuario(dto.getIdUsuario())
                    .username(null)
                    .rol(null)
                    .fechaAsignacion(null)
                    .accion(accion)
                    .mensaje(mensaje)
                    .build();
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<UsuarioRolResponseDTO> listar() {
        List<Object[]> results = entityManager.createNativeQuery(
                        "SELECT u.id, u.username, r.nombre, ur.fecha_asignacion " +
                                "FROM usuarios_roles ur " +
                                "JOIN usuarios u ON ur.id_usuario = u.id " +
                                "JOIN roles r ON ur.id_rol = r.id")
                .getResultList();
        
        return results.stream()
                .map(row -> UsuarioRolResponseDTO.builder()
                        .idUsuario((Integer) row[0])
                        .username((String) row[1])
                        .rol((String) row[2])
                        .fechaAsignacion(row[3] != null ? row[3].toString() : null)
                        .mensaje("Operacion exitosa")
                        .build())
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<UsuarioRolResponseDTO> listarPorUsuario(Integer idUsuario) {
        List<Object[]> results = entityManager.createNativeQuery(
                        "SELECT u.id, u.username, r.nombre, ur.fecha_asignacion " +
                                "FROM usuarios_roles ur " +
                                "JOIN usuarios u ON ur.id_usuario = u.id " +
                                "JOIN roles r ON ur.id_rol = r.id " +
                                "WHERE u.id = ?1")
                .setParameter(1, idUsuario)
                .getResultList();
        
        return results.stream()
                .map(row -> UsuarioRolResponseDTO.builder()
                        .idUsuario((Integer) row[0])
                        .username((String) row[1])
                        .rol((String) row[2])
                        .fechaAsignacion(row[3] != null ? row[3].toString() : null)
                        .mensaje("Operacion exitosa")
                        .build())
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public UsuarioRolResponseDTO eliminar(Integer idUsuario, Integer idRol) {
        UsuarioRolRequestDTO dto = UsuarioRolRequestDTO.builder()
                .accion("ELIMINAR")
                .idUsuario(idUsuario)
                .idRol(idRol)
                .build();
        
        return ejecutarAccion(dto);
    }
}

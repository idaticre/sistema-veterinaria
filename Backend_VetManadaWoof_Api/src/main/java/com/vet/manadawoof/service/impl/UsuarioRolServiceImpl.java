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
    
    /**
     * Ejecuta una acción sobre la relación usuario-rol usando el SP `sp_gestionar_usuario_rol`.
     */
    @Override
    @Transactional
    public UsuarioRolResponseDTO ejecutarAccion(UsuarioRolRequestDTO dto) {
        StoredProcedureQuery sp = entityManager.createStoredProcedureQuery("gestionar_rol_usuario");
        sp.registerStoredProcedureParameter("p_accion", String.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_id_usuario", Integer.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_id_rol", Integer.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_mensaje", String.class, ParameterMode.OUT);
        
        sp.setParameter("p_accion", dto.getAccion());
        sp.setParameter("p_id_usuario", dto.getIdUsuario());
        sp.setParameter("p_id_rol", dto.getIdRol());
        sp.execute();
        
        String mensaje = (String) sp.getOutputParameterValue("p_mensaje");
        
        // Traemos los nombres para responder al frontend
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
                .accion(dto.getAccion())
                .mensaje(mensaje)
                .build();
    }
    
    /**
     * Lista todos los usuarios con los roles asignados (muestra nombres, no IDs).
     */
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
                        .mensaje("Operación exitosa")
                        .build())
                .collect(Collectors.toList());
    }
    
    /**
     * Lista los roles asignados a un usuario específico (muestra nombres).
     */
    @Override
    @Transactional(readOnly = true)
    public List<UsuarioRolResponseDTO> listarPorUsuario(Integer idUsuario) {
        List<Object[]> results = entityManager.createNativeQuery(
                        "SELECT u.id, u.username, r.nombre, ur.fecha_asignacion" +
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
                        .mensaje("Operación exitosa")
                        .build())
                .collect(Collectors.toList());
    }
    
    /**
     * Elimina una asignación usuario-rol usando el SP con acción 'ELIMINAR'.
     */
    @Override
    @Transactional
    public UsuarioRolResponseDTO eliminar(Integer idUsuario, Integer idRol) {
        Object[] row;
        try {
            row = (Object[]) entityManager.createNativeQuery(
                            "SELECT u.id, u.username, r.nombre, ur.fecha_asignacion " +
                                    "FROM usuarios_roles ur " +
                                    "JOIN usuarios u ON ur.id_usuario = u.id " +
                                    "JOIN roles r ON ur.id_rol = r.id " +
                                    "WHERE u.id = ?1 AND r.id = ?2")
                    .setParameter(1, idUsuario)
                    .setParameter(2, idRol)
                    .getSingleResult();
        } catch (NoResultException e) {
            return UsuarioRolResponseDTO.builder()
                    .idUsuario(idUsuario)
                    .rol(null)
                    .accion("ELIMINAR")
                    .mensaje("ERROR: La asignación usuario-rol no existe")
                    .build();
        }
        
        StoredProcedureQuery sp = entityManager.createStoredProcedureQuery("gestionar_rol_usuario");
        sp.registerStoredProcedureParameter("p_accion", String.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_id_usuario", Integer.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_id_rol", Integer.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_mensaje", String.class, ParameterMode.OUT);
        
        sp.setParameter("p_accion", "ELIMINAR");
        sp.setParameter("p_id_usuario", idUsuario);
        sp.setParameter("p_id_rol", idRol);
        sp.execute();
        
        String mensaje = (String) sp.getOutputParameterValue("p_mensaje");
        
        return UsuarioRolResponseDTO.builder()
                .idUsuario((Integer) row[0])
                .username((String) row[1])
                .rol((String) row[2])
                .fechaAsignacion(row[3] != null ? row[3].toString() : null)
                .accion("ELIMINAR")
                .mensaje(mensaje)
                .build();
    }
    
}

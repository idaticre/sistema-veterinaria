package com.vet.manadawoof.service.impl;

import com.vet.manadawoof.dtos.request.UsuarioRolRequestDTO;
import com.vet.manadawoof.dtos.response.UsuarioRolResponseDTO;
import com.vet.manadawoof.service.UsuarioRolService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioRolServiceImpl implements UsuarioRolService {

    private final EntityManager entityManager;

    @Override
    @Transactional
    public UsuarioRolResponseDTO gestionarUsuarioRol(UsuarioRolRequestDTO requestDTO) {
        StoredProcedureQuery query = entityManager
                .createStoredProcedureQuery("sp_usuarios_roles")
                .registerStoredProcedureParameter("p_accion", String.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_id", Integer.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_usuario_id", Integer.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_rol_id", Integer.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_mensaje", String.class, ParameterMode.OUT);

        query.setParameter("p_accion", requestDTO.getAccion());
        query.setParameter("p_id", null);
        query.setParameter("p_usuario_id", requestDTO.getUsuarioId());
        query.setParameter("p_rol_id", requestDTO.getRolId());

        query.execute();

        String mensaje = (String) query.getOutputParameterValue("p_mensaje");

        return UsuarioRolResponseDTO.builder()
                .usuarioId(requestDTO.getUsuarioId())
                .rolId(requestDTO.getRolId())
                .mensaje(mensaje)
                .build();
    }

    @Override
    @Transactional
    public List<UsuarioRolResponseDTO> listarRolesPorUsuario(Integer usuarioId) {
        StoredProcedureQuery query = entityManager
                .createStoredProcedureQuery("sp_usuarios_roles")
                .registerStoredProcedureParameter("p_accion", String.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_id", Integer.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_usuario_id", Integer.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_rol_id", Integer.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_mensaje", String.class, ParameterMode.OUT);

        query.setParameter("p_accion", "READ");
        query.setParameter("p_id", null);
        query.setParameter("p_usuario_id", usuarioId);
        query.setParameter("p_rol_id", null);

        List<Object[]> results = query.getResultList();
        List<UsuarioRolResponseDTO> responseList = new ArrayList<>();

        for (Object[] row : results) {
            responseList.add(UsuarioRolResponseDTO.builder()
                    .id((Integer) row[0])
                    .usuarioId((Integer) row[1])
                    .rolId((Integer) row[2])
                    .build());
        }

        return responseList;
    }
}

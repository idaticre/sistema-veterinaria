package com.vet.manadawoof.service.impl;

import com.vet.manadawoof.dtos.response.UsuarioResponseDTO;
import com.vet.manadawoof.entity.UsuarioEntity;
import com.vet.manadawoof.repository.UsuarioRepository;
import com.vet.manadawoof.service.UsuarioService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository repository;

    @PersistenceContext
    private EntityManager em;

    private StoredProcedureQuery prepareSp(String accion, Integer id, String usuario, String clave, Integer activo) {
        StoredProcedureQuery query = em.createStoredProcedureQuery("sp_usuarios", UsuarioEntity.class);
        query.registerStoredProcedureParameter("p_accion", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_id", Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_usuario", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_clave", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_activo", Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_mensaje", String.class, ParameterMode.OUT);

        query.setParameter("p_accion", accion);
        query.setParameter("p_id", id);
        query.setParameter("p_usuario", usuario);
        query.setParameter("p_clave", clave);
        query.setParameter("p_activo", activo);

        return query;
    }

    @Override
    @Transactional
    public UsuarioResponseDTO createUser(UsuarioEntity user) {
        StoredProcedureQuery sp = prepareSp(
                "CREATE", null, user.getUsername(), user.getPasswordHash(),
                user.getActivo() != null && user.getActivo() ? 1 : 0
        );
        sp.execute();
        String mensaje = (String) sp.getOutputParameterValue("p_mensaje");

        return UsuarioResponseDTO.builder()
                .username(user.getUsername())
                .activo(user.getActivo())
                .mensaje(mensaje)
                .build();
    }

    @Override
    @Transactional
    public UsuarioResponseDTO updateUser(UsuarioEntity user) {
        StoredProcedureQuery sp = prepareSp(
                "UPDATE", user.getId().intValue(), user.getUsername(), user.getPasswordHash(),
                user.getActivo() != null && user.getActivo() ? 1 : 0
        );
        sp.execute();
        String mensaje = (String) sp.getOutputParameterValue("p_mensaje");

        return UsuarioResponseDTO.builder()
                .idUsuario(user.getId())
                .username(user.getUsername())
                .activo(user.getActivo())
                .mensaje(mensaje)
                .build();
    }

    @Override
    @Transactional
    public UsuarioResponseDTO deleteUser(Integer id) {
        StoredProcedureQuery sp = prepareSp("DELETE", id.intValue(), null, null, 0);
        sp.execute();
        String mensaje = (String) sp.getOutputParameterValue("p_mensaje");

        return UsuarioResponseDTO.builder()
                .idUsuario(id)
                .mensaje(mensaje)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponseDTO findUserById(Integer id) {
        StoredProcedureQuery sp = prepareSp("READ", id.intValue(), null, null, 1);
        List<UsuarioEntity> result = sp.getResultList();
        if (result.isEmpty()) return null;

        UsuarioEntity user = result.get(0);
        return UsuarioResponseDTO.builder()
                .idUsuario(user.getId())
                .codigoUsuario(user.getCodigo())
                .username(user.getUsername())
                .activo(user.getActivo())
                .mensaje("Usuario encontrado")
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> findAllUsers() {
        StoredProcedureQuery sp = prepareSp("READ", null, null, null, 1);
        List<UsuarioEntity> results = sp.getResultList();

        return results.stream()
                .map(user -> UsuarioResponseDTO.builder()
                        .idUsuario(user.getId())
                        .codigoUsuario(user.getCodigo())
                        .username(user.getUsername())
                        .activo(user.getActivo())
                        .mensaje("Usuario listado")
                        .build())
                .toList();
    }
}

package com.vet.manadawoof.service;

import com.vet.manadawoof.entity.UsuarioEntity;
import com.vet.manadawoof.repository.UsuarioRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository repository;

    @PersistenceContext
    private EntityManager em;

    @Override
    public String createUser(UsuarioEntity user) {
        return repository.callSpUsuarios(
                "CREATE",
                null,
                user.getUsername(),
                user.getPasswordHash(),
                user.getActivo() != null && user.getActivo() ? 1 : 0
        );
    }

    @Override
    public String updateUser(UsuarioEntity user) {
        return repository.callSpUsuarios(
                "UPDATE",
                user.getId().intValue(),
                user.getUsername(),
                user.getPasswordHash(),
                user.getActivo() != null && user.getActivo() ? 1 : 0
        );
    }

    @Override
    public String deleteUser(Long id) {
        return repository.callSpUsuarios(
                "DELETE",
                id.intValue(),
                null,
                null,
                0
        );
    }

    @Override
    public UsuarioEntity findUserById(Long id) {
        StoredProcedureQuery query = em.createStoredProcedureQuery("sp_usuarios", UsuarioEntity.class);
        query.registerStoredProcedureParameter("p_accion", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_id", Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_usuario", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_clave", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_activo", Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_mensaje", String.class, ParameterMode.OUT);

        query.setParameter("p_accion", "READ");
        query.setParameter("p_id", id.intValue());
        query.setParameter("p_usuario", null);
        query.setParameter("p_clave", null);
        query.setParameter("p_activo", 1);

        return (UsuarioEntity) query.getSingleResult();
    }

    @Override
    public List<UsuarioEntity> findAllUsers() {
        StoredProcedureQuery query = em.createStoredProcedureQuery("sp_usuarios", UsuarioEntity.class);
        query.registerStoredProcedureParameter("p_accion", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_id", Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_usuario", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_clave", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_activo", Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_mensaje", String.class, ParameterMode.OUT);

        query.setParameter("p_accion", "READ");
        query.setParameter("p_id", null);
        query.setParameter("p_usuario", null);
        query.setParameter("p_clave", null);
        query.setParameter("p_activo", 1);

        return query.getResultList();
    }
}

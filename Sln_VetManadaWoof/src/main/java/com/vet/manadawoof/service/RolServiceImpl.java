package com.vet.manadawoof.service;

import com.vet.manadawoof.entity.RolEntity;
import com.vet.manadawoof.repository.RolRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RolServiceImpl implements RolService {

    private final RolRepository rolRepository;

    @PersistenceContext
    private EntityManager entityManager;

    // Método auxiliar para preparar el SP
    private StoredProcedureQuery prepareSP(String accion, Integer id, String nombre, String descripcion, Integer activo) {
        StoredProcedureQuery query = entityManager
                .createStoredProcedureQuery("sp_roles")
                .registerStoredProcedureParameter("p_accion", String.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_id", Integer.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_nombre", String.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_descripcion", String.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_activo", Integer.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_mensaje", String.class, ParameterMode.OUT);

        query.setParameter("p_accion", accion);
        query.setParameter("p_id", id);
        query.setParameter("p_nombre", nombre);
        query.setParameter("p_descripcion", descripcion);
        query.setParameter("p_activo", activo);

        return query;
    }

    @Override
    public String createRol(RolEntity rol) {
        StoredProcedureQuery query = prepareSP("CREATE", null, rol.getNombre(), rol.getDescripcion(), rol.getActivo());
        query.execute();
        return (String) query.getOutputParameterValue("p_mensaje");
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<RolEntity> readRoles(Long id) {
        Integer idInt = id != null ? id.intValue() : null;
        StoredProcedureQuery query = entityManager
                .createStoredProcedureQuery("sp_roles", RolEntity.class)
                .registerStoredProcedureParameter("p_accion", String.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_id", Integer.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_nombre", String.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_descripcion", String.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_activo", Integer.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_mensaje", String.class, ParameterMode.OUT);

        query.setParameter("p_accion", "READ");
        query.setParameter("p_id", idInt);
        query.setParameter("p_nombre", null);
        query.setParameter("p_descripcion", null);
        query.setParameter("p_activo", null);

        query.execute();
        return query.getResultList();
    }

    @Override
    public String updateRol(RolEntity rol) {
        Integer idInt = rol.getId() != null ? rol.getId().intValue() : null;
        StoredProcedureQuery query = prepareSP("UPDATE", idInt, rol.getNombre(), rol.getDescripcion(), rol.getActivo());
        query.execute();
        return (String) query.getOutputParameterValue("p_mensaje");
    }

    @Override
    public String deleteRol(Long id) {
        Integer idInt = id != null ? id.intValue() : null;
        StoredProcedureQuery query = prepareSP("DELETE", idInt, null, null, null);
        query.execute();
        return (String) query.getOutputParameterValue("p_mensaje");
    }
}

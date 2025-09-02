package com.vet.manadawoof.service.impl;

import com.vet.manadawoof.entity.RolEntity;
import com.vet.manadawoof.repository.RolRepository;
import com.vet.manadawoof.service.RolService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RolServiceImpl implements RolService {

    private final RolRepository repository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public String crearRol(RolEntity rol) {
        StoredProcedureQuery sp = entityManager
                .createNamedStoredProcedureQuery("RolEntity.spRoles");
        sp.setParameter("p_accion", "CREATE");
        sp.setParameter("p_id", null);
        sp.setParameter("p_nombre", rol.getNombre());
        sp.setParameter("p_descripcion", rol.getDescripcion());
        sp.setParameter("p_activo", rol.getActivo() ? 1 : 0);
        sp.execute();
        return (String) sp.getOutputParameterValue("p_mensaje");
    }

    @Override
    @Transactional(readOnly = true)
    public List<RolEntity> listarRoles() {
        // Ejecuta SP con READ para consistencia
        StoredProcedureQuery sp = entityManager
                .createNamedStoredProcedureQuery("RolEntity.spRoles");
        sp.setParameter("p_accion", "READ");
        sp.setParameter("p_id", null);
        sp.setParameter("p_nombre", null);
        sp.setParameter("p_descripcion", null);
        sp.setParameter("p_activo", null);
        sp.execute();
        return repository.findAll();
    }

    @Override
    @Transactional
    public String actualizarRol(RolEntity rol) {
        StoredProcedureQuery sp = entityManager
                .createNamedStoredProcedureQuery("RolEntity.spRoles");
        sp.setParameter("p_accion", "UPDATE");
        sp.setParameter("p_id", rol.getId().intValue());
        sp.setParameter("p_nombre", rol.getNombre());
        sp.setParameter("p_descripcion", rol.getDescripcion());
        sp.setParameter("p_activo", rol.getActivo() ? 1 : 0);
        sp.execute();
        return (String) sp.getOutputParameterValue("p_mensaje");
    }

    @Override
    public String eliminarRol(Integer id) {
        StoredProcedureQuery sp = entityManager
                .createNamedStoredProcedureQuery("RolEntity.spRoles");
        sp.setParameter("p_accion", "DELETE");
        sp.setParameter("p_id", id.intValue());
        sp.setParameter("p_nombre", null);
        sp.setParameter("p_descripcion", null);
        sp.setParameter("p_activo", null);
        sp.execute();
        return (String) sp.getOutputParameterValue("p_mensaje");
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RolEntity> obtenerPorId(Integer id) {
        return repository.findById(id);
    }
}

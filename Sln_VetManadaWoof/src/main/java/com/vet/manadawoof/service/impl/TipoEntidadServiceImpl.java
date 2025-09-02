package com.vet.manadawoof.service.impl;

import com.vet.manadawoof.entity.TipoEntidadEntity;
import com.vet.manadawoof.repository.TipoEntidadRepository;
import com.vet.manadawoof.service.TipoEntidadService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TipoEntidadServiceImpl implements TipoEntidadService {

    private final TipoEntidadRepository repository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public String crearTipoEntidad(String nombre, Boolean activo) {
        StoredProcedureQuery sp = entityManager.createNamedStoredProcedureQuery(
                "TipoEntidadEntity.spTipoEntidad");
        sp.setParameter("p_accion", "CREATE");
        sp.setParameter("p_id", null);
        sp.setParameter("p_nombre", nombre);
        sp.setParameter("p_activo", activo);
        sp.execute();
        return (String) sp.getOutputParameterValue("p_mensaje");
    }

    @Override
    @Transactional
    public String actualizarTipoEntidad(Integer id, String nombre, Boolean activo) {
        StoredProcedureQuery sp = entityManager.createNamedStoredProcedureQuery(
                "TipoEntidadEntity.spTipoEntidad");
        sp.setParameter("p_accion", "UPDATE");
        sp.setParameter("p_id", id);
        sp.setParameter("p_nombre", nombre);
        sp.setParameter("p_activo", activo);
        sp.execute();
        return (String) sp.getOutputParameterValue("p_mensaje");
    }

    @Override
    @Transactional
    public String eliminarTipoEntidad(Integer id) {
        StoredProcedureQuery sp = entityManager.createNamedStoredProcedureQuery("TipoEntidadEntity.spTipoEntidad");
        sp.setParameter("p_accion", "DELETE");
        sp.setParameter("p_id", id);
        sp.setParameter("p_nombre", null);
        sp.setParameter("p_activo", null);
        sp.execute();
        return (String) sp.getOutputParameterValue("p_mensaje");
    }

    @Override
    @Transactional(readOnly = true)
    public List<TipoEntidadEntity> listarTipoEntidad(Integer id) {
        StoredProcedureQuery sp = entityManager.createNamedStoredProcedureQuery(
                "TipoEntidadEntity.spTipoEntidad");
        sp.setParameter("p_accion", "READ");
        sp.setParameter("p_id", id);
        sp.setParameter("p_nombre", null);
        sp.setParameter("p_activo", null);
        sp.execute();

        // Retorna todas las filas si id es null, o la específica
        return id == null ? repository.findAll() :
                repository.findById(id).map(List::of).orElse(List.of());
    }
}

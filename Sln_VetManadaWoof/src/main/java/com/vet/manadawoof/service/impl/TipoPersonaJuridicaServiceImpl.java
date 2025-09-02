package com.vet.manadawoof.service.impl;

import com.vet.manadawoof.entity.TipoPersonaJuridicaEntity;
import com.vet.manadawoof.repository.TipoPersonaJuridicaRepository;
import com.vet.manadawoof.service.TipoPersonaJuridicaService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TipoPersonaJuridicaServiceImpl implements TipoPersonaJuridicaService {

    private final TipoPersonaJuridicaRepository repository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional(readOnly = true)
    public List<TipoPersonaJuridicaEntity> listarTiposPersonaJuridica() {
        return repository.findAll();
    }

    @Override
    @Transactional
    public String registrarTipoPersonaJuridica(TipoPersonaJuridicaEntity entity) {
        StoredProcedureQuery sp = entityManager
                .createNamedStoredProcedureQuery("sp_tipo_persona_juridica");
        sp.setParameter("p_accion", "CREATE");
        sp.setParameter("p_id", null);
        sp.setParameter("p_nombre", entity.getNombre());
        sp.setParameter("p_descripcion", entity.getDescripcion());
        sp.setParameter("p_activo", entity.getActivo());
        sp.execute();
        return (String) sp.getOutputParameterValue("p_mensaje");
    }

    @Override
    @Transactional
    public String actualizarTipoPersonaJuridica(TipoPersonaJuridicaEntity entity) {
        StoredProcedureQuery sp = entityManager
                .createNamedStoredProcedureQuery("sp_tipo_persona_juridica");
        sp.setParameter("p_accion", "UPDATE");
        sp.setParameter("p_id", entity.getId());
        sp.setParameter("p_nombre", entity.getNombre());
        sp.setParameter("p_descripcion", entity.getDescripcion());
        sp.setParameter("p_activo", entity.getActivo());
        sp.execute();
        return (String) sp.getOutputParameterValue("p_mensaje");
    }

    @Override
    @Transactional
    public String eliminarTipoPersonaJuridica(Integer id) {
        StoredProcedureQuery sp = entityManager
                .createNamedStoredProcedureQuery("sp_tipo_persona_juridica");
        sp.setParameter("p_accion", "DELETE");
        sp.setParameter("p_id", id);
        sp.setParameter("p_nombre", null);
        sp.setParameter("p_descripcion", null);
        sp.setParameter("p_activo", null);
        sp.execute();
        return (String) sp.getOutputParameterValue("p_mensaje");
    }
}

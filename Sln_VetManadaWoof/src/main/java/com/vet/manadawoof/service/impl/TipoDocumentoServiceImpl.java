package com.vet.manadawoof.service.impl;

import com.vet.manadawoof.entity.TipoDocumentoEntity;
import com.vet.manadawoof.repository.TipoDocumentoRepository;
import com.vet.manadawoof.service.TipoDocumentoService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TipoDocumentoServiceImpl implements TipoDocumentoService {

    private final TipoDocumentoRepository repository;
    private final EntityManager entityManager;

    @Override
    @Transactional
    public String crearTipoDocumento(TipoDocumentoEntity tipoDocumento) {
        return repository.spTipoDocumento(
                "CREATE",
                null,
                tipoDocumento.getDescripcion(),
                tipoDocumento.getActivo() ? 1 : 0
        );
    }

    @Override
    @Transactional
    public String actualizarTipoDocumento(TipoDocumentoEntity tipoDocumento) {
        return repository.spTipoDocumento(
                "UPDATE",
                tipoDocumento.getId(),
                tipoDocumento.getDescripcion(),
                tipoDocumento.getActivo() ? 1 : 0
        );
    }

    @Override
    @Transactional
    public String eliminarTipoDocumento(Integer id) {
        return repository.spTipoDocumento(
                "DELETE",
                id,
                null,
                null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TipoDocumentoEntity> listarTiposDocumento() {
        StoredProcedureQuery query = entityManager
                .createStoredProcedureQuery(
                        "sp_tipo_documento", TipoDocumentoEntity.class)
                .registerStoredProcedureParameter("p_accion", String.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_id", Integer.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_descripcion", String.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_activo", Integer.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_mensaje", String.class, ParameterMode.OUT);

        query.setParameter("p_accion", "READ");
        query.setParameter("p_id", null);
        query.setParameter("p_descripcion", null);
        query.setParameter("p_activo", null);

        return query.getResultList();
    }
}

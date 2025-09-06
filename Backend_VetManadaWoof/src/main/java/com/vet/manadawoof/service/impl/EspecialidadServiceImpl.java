package com.vet.manadawoof.service.impl;

import com.vet.manadawoof.dtos.response.EspecialidadResponseDTO;
import com.vet.manadawoof.entity.EspecialidadEntity;
import com.vet.manadawoof.service.EspecialidadService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EspecialidadServiceImpl implements EspecialidadService {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public EspecialidadResponseDTO crearEspecialidad(EspecialidadEntity especialidad) {
        StoredProcedureQuery sp = entityManager.createNamedStoredProcedureQuery("EspecialidadEntity.spEspecialidades");
        sp.setParameter("p_accion", "CREATE");
        sp.setParameter("p_id", null);
        sp.setParameter("p_nombre", especialidad.getNombre());
        sp.setParameter("p_activo", especialidad.getActivo());
        sp.execute();

        return EspecialidadResponseDTO.builder()
                .mensaje((String) sp.getOutputParameterValue("p_mensaje"))
                .build();
    }

    @Override
    @Transactional
    public EspecialidadResponseDTO actualizarEspecialidad(EspecialidadEntity especialidad) {
        StoredProcedureQuery sp = entityManager.createNamedStoredProcedureQuery("EspecialidadEntity.spEspecialidades");
        sp.setParameter("p_accion", "UPDATE");
        sp.setParameter("p_id", especialidad.getId());
        sp.setParameter("p_nombre", especialidad.getNombre());
        sp.setParameter("p_activo", especialidad.getActivo());
        sp.execute();

        return EspecialidadResponseDTO.builder()
                .mensaje((String) sp.getOutputParameterValue("p_mensaje"))
                .build();
    }

    @Override
    @Transactional
    public EspecialidadResponseDTO eliminarEspecialidad(Integer id) {
        StoredProcedureQuery sp = entityManager.createNamedStoredProcedureQuery("EspecialidadEntity.spEspecialidades");
        sp.setParameter("p_accion", "DELETE");
        sp.setParameter("p_id", id);
        sp.setParameter("p_nombre", null);
        sp.setParameter("p_activo", null);
        sp.execute();

        return EspecialidadResponseDTO.builder()
                .mensaje((String) sp.getOutputParameterValue("p_mensaje"))
                .build();
    }

    @Override
    @Transactional
    public List<EspecialidadResponseDTO> listarEspecialidades() {
        StoredProcedureQuery sp = entityManager.createNamedStoredProcedureQuery("EspecialidadEntity.spEspecialidades");
        sp.setParameter("p_accion", "READ");
        sp.setParameter("p_id", null);
        sp.setParameter("p_nombre", null);
        sp.setParameter("p_activo", null);
        sp.execute();

        @SuppressWarnings("unchecked")
        List<Object[]> results = sp.getResultList();

        return results.stream()
                .map(row -> EspecialidadResponseDTO.builder()
                        .id(((Number) row[0]).intValue())
                        .codigo((String) row[1])
                        .nombre((String) row[2])
                        .activo((Boolean) row[3])
                        .mensaje("Especialidad listada")
                        .build())
                .toList();
    }

}

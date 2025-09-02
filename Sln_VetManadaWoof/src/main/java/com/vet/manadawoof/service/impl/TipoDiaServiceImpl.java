package com.vet.manadawoof.service.impl;

import com.vet.manadawoof.dtos.response.TipoDiaResponseDTO;
import com.vet.manadawoof.entity.TipoDiaEntity;
import com.vet.manadawoof.repository.TipoDiaRepository;
import com.vet.manadawoof.service.TipoDiaService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TipoDiaServiceImpl implements TipoDiaService {

    private final TipoDiaRepository repository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public TipoDiaResponseDTO crearTipoDia(TipoDiaEntity tipoDia) {
        StoredProcedureQuery sp = entityManager.createNamedStoredProcedureQuery(
                "TipoDiaEntity.spTiposDia");

        sp.setParameter("p_accion", "CREATE");
        sp.setParameter("p_id", null);
        sp.setParameter("p_nombre", tipoDia.getNombre());

        sp.execute();

        return TipoDiaResponseDTO.builder()
                .nombre(tipoDia.getNombre())
                .mensaje((String) sp.getOutputParameterValue(
                        "p_mensaje"))
                .build();
    }

    @Override
    @Transactional
    public TipoDiaResponseDTO actualizarTipoDia(TipoDiaEntity tipoDia) {
        StoredProcedureQuery sp = entityManager.createNamedStoredProcedureQuery(
                "TipoDiaEntity.spTiposDia");

        sp.setParameter("p_accion", "UPDATE");
        sp.setParameter("p_id", tipoDia.getId());
        sp.setParameter("p_nombre", tipoDia.getNombre());

        sp.execute();

        return TipoDiaResponseDTO.builder()
                .nombre(tipoDia.getNombre())
                .mensaje((String) sp.getOutputParameterValue("p_mensaje"))
                .build();
    }

    @Override
    @Transactional
    public TipoDiaResponseDTO eliminarTipoDia(Integer id) {
        StoredProcedureQuery sp = entityManager.createNamedStoredProcedureQuery(
                "TipoDiaEntity.spTiposDia");

        sp.setParameter("p_accion", "DELETE");
        sp.setParameter("p_id", id);
        sp.setParameter("p_nombre", null);
        sp.setParameter("p_activo", null);

        sp.execute();

        return TipoDiaResponseDTO.builder()
                .mensaje((String) sp.getOutputParameterValue(
                        "p_mensaje"))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TipoDiaResponseDTO> listarTiposDia() {
        return repository.findAll().stream()
                .map(tipoDia -> TipoDiaResponseDTO.builder()
                        .id(tipoDia.getId())
                        .codigo(tipoDia.getCodigo())
                        .nombre(tipoDia.getNombre())
                        .mensaje("Listado exitoso")
                        .build())
                .collect(Collectors.toList());
    }
}

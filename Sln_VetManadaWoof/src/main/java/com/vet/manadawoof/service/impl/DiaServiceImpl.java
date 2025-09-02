package com.vet.manadawoof.service.impl;

import com.vet.manadawoof.dtos.request.DiaRequestDTO;
import com.vet.manadawoof.dtos.response.DiaResponseDTO;
import com.vet.manadawoof.entity.DiaEntity;
import com.vet.manadawoof.repository.DiaRepository;
import com.vet.manadawoof.service.DiaService;
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
public class DiaServiceImpl implements DiaService {

    private final DiaRepository repository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public DiaResponseDTO crearDia(DiaRequestDTO dto) {
        StoredProcedureQuery sp = entityManager
                .createNamedStoredProcedureQuery("DiaEntity.spDiasSemana");
        sp.setParameter("p_accion", "CREATE");
        sp.setParameter("p_id", null);
        sp.setParameter("p_nombre", dto.getNombre());
        sp.setParameter("p_activo", dto.getActivo());
        sp.execute();

        String mensaje = (String) sp.getOutputParameterValue("p_mensaje");

        // Recuperar el registro recién creado usando el nombre (suponiendo que sea único)
        DiaEntity dia = repository.findAll().stream()
                .filter(d -> d.getNombre().equals(dto.getNombre()))
                .findFirst()
                .orElse(null);

        return DiaResponseDTO.builder()
                .idDia(dia != null ? dia.getId() : null)
                .nombre(dto.getNombre())
                .activo(dto.getActivo())
                .mensaje(mensaje)
                .build();
    }

    @Override
    @Transactional
    public DiaResponseDTO actualizarDia(DiaRequestDTO dto) {
        StoredProcedureQuery sp = entityManager
                .createNamedStoredProcedureQuery("DiaEntity.spDiasSemana");
        sp.setParameter("p_accion", "UPDATE");
        sp.setParameter("p_id", dto.getIdDia());
        sp.setParameter("p_nombre", dto.getNombre());
        sp.setParameter("p_activo", dto.getActivo());
        sp.execute();

        String mensaje = (String) sp.getOutputParameterValue("p_mensaje");

        DiaEntity dia = repository.findById(dto.getIdDia()).orElse(null);

        return DiaResponseDTO.builder()
                .idDia(dia != null ? dia.getId() : null)
                .nombre(dto.getNombre())
                .activo(dto.getActivo())
                .mensaje(mensaje)
                .build();
    }

    @Override
    @Transactional
    public DiaResponseDTO eliminarDia(Integer id) {
        StoredProcedureQuery sp = entityManager
                .createNamedStoredProcedureQuery("DiaEntity.spDiasSemana");
        sp.setParameter("p_accion", "DELETE");
        sp.setParameter("p_id", id);
        sp.setParameter("p_nombre", null);
        sp.setParameter("p_activo", null);
        sp.execute();

        String mensaje = (String) sp.getOutputParameterValue("p_mensaje");

        return DiaResponseDTO.builder()
                .idDia(id)
                .mensaje(mensaje)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DiaResponseDTO> listarDias() {
        StoredProcedureQuery sp = entityManager
                .createNamedStoredProcedureQuery("DiaEntity.spDiasSemana");
        sp.setParameter("p_accion", "READ");
        sp.setParameter("p_id", null);
        sp.setParameter("p_nombre", null);
        sp.setParameter("p_activo", null);
        sp.execute();

        return repository.findAll().stream()
                .map(d -> DiaResponseDTO.builder()
                        .idDia(d.getId())
                        .nombre(d.getNombre())
                        .activo(d.getActivo())
                        .mensaje("Listado obtenido")
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public DiaEntity findById(Integer id) {
        return repository.findById(id).orElse(null);
    }
}

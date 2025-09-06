package com.vet.manadawoof.service.impl;

import com.vet.manadawoof.dtos.request.EntidadRequestDTO;
import com.vet.manadawoof.dtos.response.EntidadResponseDTO;
import com.vet.manadawoof.entity.EntidadEntity;
import com.vet.manadawoof.repository.EntidadRepository;
import com.vet.manadawoof.service.EntidadService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EntidadServiceImpl implements EntidadService {

    private final EntidadRepository repository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public EntidadResponseDTO registrarEntidad(EntidadRequestDTO dto) {
        StoredProcedureQuery sp = entityManager
                .createNamedStoredProcedureQuery("EntidadEntity.spRegistrarEntidadBase");

        sp.setParameter("p_id_tipo_entidad", dto.getIdTipoEntidad());
        sp.setParameter("p_id_tipo_persona_juridica", dto.getIdTipoPersonaJuridica());
        sp.setParameter("p_nombre", dto.getNombre());
        sp.setParameter("p_sexo", dto.getSexo());
        sp.setParameter("p_documento", dto.getNumeroDocumento());
        sp.setParameter("p_id_tipo_documento", dto.getIdTipoDocumento());
        sp.setParameter("p_correo", dto.getCorreo());
        sp.setParameter("p_telefono", dto.getTelefono());
        sp.setParameter("p_direccion", dto.getDireccion());
        sp.setParameter("p_ciudad", dto.getCiudad());
        sp.setParameter("p_distrito", dto.getDistrito());
        sp.setParameter("p_representante", dto.getRepresentante());
        sp.execute();

        return EntidadResponseDTO.builder()
                .idEntidad((Integer) sp.getOutputParameterValue("p_id_entidad"))
                .codigoEntidad((String) sp.getOutputParameterValue("p_codigo_entidad"))
                .mensaje((String) sp.getOutputParameterValue("p_mensaje"))
                .build();
    }

    @Override
    @Transactional
    public EntidadResponseDTO actualizarEntidad(EntidadRequestDTO dto) {
        StoredProcedureQuery sp = entityManager
                .createNamedStoredProcedureQuery("EntidadEntity.spActualizarEntidadBase");

        sp.setParameter("p_id_entidad", dto.getIdEntidad());
        sp.setParameter("p_id_tipo_persona_juridica", dto.getIdTipoPersonaJuridica());
        sp.setParameter("p_nombre", dto.getNombre());
        sp.setParameter("p_sexo", dto.getSexo());
        sp.setParameter("p_documento", dto.getNumeroDocumento());
        sp.setParameter("p_id_tipo_documento", dto.getIdTipoDocumento());
        sp.setParameter("p_correo", dto.getCorreo());
        sp.setParameter("p_telefono", dto.getTelefono());
        sp.setParameter("p_direccion", dto.getDireccion());
        sp.setParameter("p_ciudad", dto.getCiudad());
        sp.setParameter("p_distrito", dto.getDistrito());
        sp.setParameter("p_representante", dto.getRepresentante());
        sp.setParameter("p_activo", dto.getActivo());
        sp.execute();

        return EntidadResponseDTO.builder()
                .mensaje((String) sp.getOutputParameterValue("p_mensaje"))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public EntidadEntity findById(Integer id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EntidadEntity> findAll() {
        return repository.findAll();
    }
}

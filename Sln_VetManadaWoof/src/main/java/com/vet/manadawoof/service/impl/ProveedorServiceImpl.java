package com.vet.manadawoof.service.impl;

import com.vet.manadawoof.dtos.request.ProveedorRequestDTO;
import com.vet.manadawoof.dtos.response.EntidadResponseDTO;
import com.vet.manadawoof.entity.ProveedorEntity;
import com.vet.manadawoof.repository.ProveedorRepository;
import com.vet.manadawoof.service.ProveedorService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProveedorServiceImpl implements ProveedorService {

    private final ProveedorRepository repository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public EntidadResponseDTO registrarProveedor(ProveedorRequestDTO dto) {
        StoredProcedureQuery query = entityManager.createNamedStoredProcedureQuery("ProveedorEntity.registrarProveedor");

        query.setParameter("p_id_tipo_persona_juridica", dto.getIdTipoPersonaJuridica());
        query.setParameter("p_nombre", dto.getNombre());
        query.setParameter("p_sexo", dto.getSexo());
        query.setParameter("p_documento", dto.getDocumento());
        query.setParameter("p_id_tipo_documento", dto.getIdTipoDocumento());
        query.setParameter("p_correo", dto.getCorreo());
        query.setParameter("p_telefono", dto.getTelefono());
        query.setParameter("p_direccion", dto.getDireccion());
        query.setParameter("p_ciudad", dto.getCiudad());
        query.setParameter("p_distrito", dto.getDistrito());
        query.setParameter("p_representante", dto.getRepresentante());

        query.execute();

        return EntidadResponseDTO.builder()
                .codigoEntidad((String) query.getOutputParameterValue("p_codigo_entidad"))
                .mensaje((String) query.getOutputParameterValue("p_mensaje"))
                .build();
    }

    @Override
    @Transactional
    public EntidadResponseDTO actualizarProveedor(ProveedorRequestDTO dto) {
        StoredProcedureQuery query = entityManager.createNamedStoredProcedureQuery("ProveedorEntity.actualizarProveedor");

        query.setParameter("p_id_entidad", dto.getIdEntidad());
        query.setParameter("p_id_tipo_persona_juridica", dto.getIdTipoPersonaJuridica());
        query.setParameter("p_nombre", dto.getNombre());
        query.setParameter("p_sexo", dto.getSexo());
        query.setParameter("p_documento", dto.getDocumento());
        query.setParameter("p_id_tipo_documento", dto.getIdTipoDocumento());
        query.setParameter("p_correo", dto.getCorreo());
        query.setParameter("p_telefono", dto.getTelefono());
        query.setParameter("p_direccion", dto.getDireccion());
        query.setParameter("p_ciudad", dto.getCiudad());
        query.setParameter("p_distrito", dto.getDistrito());
        query.setParameter("p_representante", dto.getRepresentante());
        query.setParameter("p_activo", dto.getActivo());

        query.execute();

        return EntidadResponseDTO.builder()
                .idEntidad(dto.getIdEntidad())
                .mensaje((String) query.getOutputParameterValue("p_mensaje"))
                .build();
    }

    @Override
    @Transactional
    public ProveedorEntity findById(Integer id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public List<ProveedorEntity> findAll() {
        return repository.findAll();
    }
}

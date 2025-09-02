package com.vet.manadawoof.service.impl;

import com.vet.manadawoof.dtos.response.EntidadResponseDTO;
import com.vet.manadawoof.dtos.request.ProveedorRequestDTO;
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

    private Integer safeId(Integer id) {
        return id != null ? id.intValue() : null;
    }

    @Override
    @Transactional
    public EntidadResponseDTO registrarProveedor(ProveedorRequestDTO dto) {
        StoredProcedureQuery sp = entityManager.createNamedStoredProcedureQuery("ProveedorEntity.registrarProveedor");

        sp.setParameter("p_id_tipo_persona_juridica", dto.getIdTipoPersonaJuridica());
        sp.setParameter("p_nombre", dto.getNombre());
        sp.setParameter("p_sexo", dto.getSexo());
        sp.setParameter("p_documento", dto.getDocumento());
        sp.setParameter("p_id_tipo_documento", dto.getIdTipoDocumento());
        sp.setParameter("p_correo", dto.getCorreo());
        sp.setParameter("p_telefono", dto.getTelefono());
        sp.setParameter("p_direccion", dto.getDireccion());
        sp.setParameter("p_ciudad", dto.getCiudad());
        sp.setParameter("p_distrito", dto.getDistrito());
        sp.setParameter("p_representante", dto.getRepresentante());

        sp.execute();

        String mensaje = (String) sp.getOutputParameterValue("p_mensaje");
        String codigoProveedor = (String) sp.getOutputParameterValue("p_codigo_proveedor");
        String codigoEntidad = (String) sp.getOutputParameterValue("p_codigo_entidad");

        return EntidadResponseDTO.builder()
                .idEntidad(null)
                .codigoEntidad(codigoEntidad)
                .nombre(dto.getNombre())
                .correo(dto.getCorreo())
                .telefono(dto.getTelefono())
                .ciudad(dto.getCiudad())
                .distrito(dto.getDistrito())
                .representante(dto.getRepresentante())
                .build();
    }

    @Override
    @Transactional
    public EntidadResponseDTO actualizarProveedor(ProveedorRequestDTO dto) {
        StoredProcedureQuery sp = entityManager.createNamedStoredProcedureQuery("ProveedorEntity.actualizarProveedor");

        sp.setParameter("p_id_entidad", dto.getIdEntidad());
        sp.setParameter("p_id_tipo_persona_juridica", dto.getIdTipoPersonaJuridica());
        sp.setParameter("p_nombre", dto.getNombre());
        sp.setParameter("p_sexo", dto.getSexo());
        sp.setParameter("p_documento", dto.getDocumento());
        sp.setParameter("p_id_tipo_documento", dto.getIdTipoDocumento());
        sp.setParameter("p_correo", dto.getCorreo());
        sp.setParameter("p_telefono", dto.getTelefono());
        sp.setParameter("p_direccion", dto.getDireccion());
        sp.setParameter("p_ciudad", dto.getCiudad());
        sp.setParameter("p_distrito", dto.getDistrito());
        sp.setParameter("p_representante", dto.getRepresentante());
        sp.setParameter("p_activo", dto.getActivo());

        sp.execute();

        String mensaje = (String) sp.getOutputParameterValue("p_mensaje");

        return EntidadResponseDTO.builder()
                .idEntidad(dto.getIdEntidad() != null ? dto.getIdEntidad().intValue(): null)
                .codigoEntidad(null)
                .nombre(dto.getNombre())
                .correo(dto.getCorreo())
                .telefono(dto.getTelefono())
                .ciudad(dto.getCiudad())
                .distrito(dto.getDistrito())
                .representante(dto.getRepresentante())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ProveedorEntity findById(Integer id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProveedorEntity> findAll() {
        return repository.findAll();
    }
}

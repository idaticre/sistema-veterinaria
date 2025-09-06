package com.vet.manadawoof.service.impl;

import com.vet.manadawoof.dtos.request.ColaboradorRequestDTO;
import com.vet.manadawoof.dtos.response.ColaboradorResponseDTO;
import com.vet.manadawoof.entity.ColaboradorEntity;
import com.vet.manadawoof.repository.ColaboradorRepository;
import com.vet.manadawoof.service.ColaboradorService;
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
public class ColaboradorServiceImpl implements ColaboradorService {

    private final ColaboradorRepository repository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public ColaboradorResponseDTO registrarColaborador(ColaboradorRequestDTO dto) {
        StoredProcedureQuery sp = entityManager
                .createNamedStoredProcedureQuery("ColaboradorEntity.spRegistrarColaborador");

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
        sp.setParameter("p_fecha_ingreso", dto.getFechaIngreso());
        sp.setParameter("p_id_usuario", dto.getIdUsuario());
        sp.setParameter("p_foto", dto.getFoto());

        sp.execute();

        return ColaboradorResponseDTO.builder()
                .codigoEntidad((String) sp.getOutputParameterValue("p_codigo_entidad"))
                .codigoColaborador((String) sp.getOutputParameterValue("p_codigo_colaborador"))
                .mensaje((String) sp.getOutputParameterValue("p_mensaje"))
                .build();
    }

    @Override
    @Transactional
    public ColaboradorResponseDTO actualizarColaborador(ColaboradorRequestDTO dto) {
        StoredProcedureQuery sp = entityManager
                .createNamedStoredProcedureQuery("ColaboradorEntity.spActualizarColaborador");

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
        sp.setParameter("p_fecha_ingreso", dto.getFechaIngreso());
        sp.setParameter("p_id_usuario", dto.getIdUsuario());
        sp.setParameter("p_foto", dto.getFoto());
        sp.setParameter("p_activo", dto.getActivo());

        sp.execute();

        return ColaboradorResponseDTO.builder()
                .mensaje((String) sp.getOutputParameterValue("p_mensaje"))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ColaboradorResponseDTO buscarPorId(Integer id) {
        ColaboradorEntity entity = repository.findById(id).orElseThrow();
        return mapToResponseDTO(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ColaboradorResponseDTO> listarColaboradores() {
        return repository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    private ColaboradorResponseDTO mapToResponseDTO(ColaboradorEntity entity) {
        return ColaboradorResponseDTO.builder()
                .idColaborador(entity.getId())
                .idEntidad(entity.getEntidad().getId())
                .idUsuario(entity.getUsuario().getId().intValue())
                .codigoColaborador(entity.getCodigo())
                .fechaIngreso(entity.getFechaIngreso())
                .foto(entity.getFoto())
                .activo(entity.getActivo())
                .sexo(entity.getEntidad().getSexo())
                .build();
    }
}

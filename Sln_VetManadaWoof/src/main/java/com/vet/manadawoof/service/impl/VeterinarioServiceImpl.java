package com.vet.manadawoof.service.impl;

import com.vet.manadawoof.dtos.request.ColaboradorRequestDTO;
import com.vet.manadawoof.dtos.request.VeterinarioRequestDTO;
import com.vet.manadawoof.dtos.response.VeterinarioResponseDTO;
import com.vet.manadawoof.repository.VeterinarioRepository;
import com.vet.manadawoof.service.VeterinarioService;
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
public class VeterinarioServiceImpl implements VeterinarioService {

    private final VeterinarioRepository repository;

    @PersistenceContext
    private EntityManager entityManager;

    private Integer safeId(Integer id) {
        return id != null ? id.intValue() : null;
    }

    @Override
    @Transactional
    public VeterinarioResponseDTO registrarVeterinario(VeterinarioRequestDTO dto) {
        StoredProcedureQuery sp = entityManager.createStoredProcedureQuery("registrar_veterinario");

        ColaboradorRequestDTO c = dto.getColaborador();

        sp.registerStoredProcedureParameter("p_id_entidad", Integer.class, jakarta.persistence.ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_id_tipo_persona_juridica", Integer.class, jakarta.persistence.ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_nombre", String.class, jakarta.persistence.ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_sexo", String.class, jakarta.persistence.ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_documento", String.class, jakarta.persistence.ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_id_tipo_documento", Integer.class, jakarta.persistence.ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_correo", String.class, jakarta.persistence.ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_telefono", String.class, jakarta.persistence.ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_direccion", String.class, jakarta.persistence.ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_ciudad", String.class, jakarta.persistence.ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_distrito", String.class, jakarta.persistence.ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_representante", String.class, jakarta.persistence.ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_id_especialidad", Integer.class, jakarta.persistence.ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_cmp", String.class, jakarta.persistence.ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_codigo_entidad", String.class, jakarta.persistence.ParameterMode.OUT);
        sp.registerStoredProcedureParameter("p_codigo_colaborador", String.class, jakarta.persistence.ParameterMode.OUT);
        sp.registerStoredProcedureParameter("p_codigo_veterinario", String.class, jakarta.persistence.ParameterMode.OUT);
        sp.registerStoredProcedureParameter("p_mensaje", String.class, jakarta.persistence.ParameterMode.OUT);

        sp.setParameter("p_id_entidad", c != null ? safeId(c.getIdEntidad()) : null);
        sp.setParameter("p_id_tipo_persona_juridica", c != null ? safeId(c.getIdTipoPersonaJuridica()) : null);
        sp.setParameter("p_nombre", c != null ? c.getNombre() : null);
        sp.setParameter("p_sexo", c != null ? c.getSexo() : null);
        sp.setParameter("p_documento", c != null ? c.getDocumento() : null);
        sp.setParameter("p_id_tipo_documento", c != null ? safeId(c.getIdTipoDocumento()) : null);
        sp.setParameter("p_correo", c != null ? c.getCorreo() : null);
        sp.setParameter("p_telefono", c != null ? c.getTelefono() : null);
        sp.setParameter("p_direccion", c != null ? c.getDireccion() : null);
        sp.setParameter("p_ciudad", c != null ? c.getCiudad() : null);
        sp.setParameter("p_distrito", c != null ? c.getDistrito() : null);
        sp.setParameter("p_representante", c != null ? c.getRepresentante() : null);
        sp.setParameter("p_id_especialidad", safeId(dto.getIdEspecialidad()));
        sp.setParameter("p_cmp", dto.getCmp());

        sp.execute();

        return VeterinarioResponseDTO.builder()
                .codigoEntidad((String) sp.getOutputParameterValue("p_codigo_entidad"))
                .codigoColaborador((String) sp.getOutputParameterValue("p_codigo_colaborador"))
                .codigoVeterinario((String) sp.getOutputParameterValue("p_codigo_veterinario"))
                .mensaje((String) sp.getOutputParameterValue("p_mensaje"))
                .cmp(dto.getCmp())
                .activo(true)
                .nombreColaborador(c != null ? c.getNombre() : null)
                .build();
    }

    @Override
    @Transactional
    public VeterinarioResponseDTO actualizarVeterinario(VeterinarioRequestDTO dto) {
        StoredProcedureQuery sp = entityManager.createStoredProcedureQuery("actualizar_veterinario");

        ColaboradorRequestDTO c = dto.getColaborador();

        sp.registerStoredProcedureParameter("p_id_entidad", Integer.class, jakarta.persistence.ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_id_tipo_persona_juridica", Integer.class, jakarta.persistence.ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_nombre", String.class, jakarta.persistence.ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_sexo", String.class, jakarta.persistence.ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_documento", String.class, jakarta.persistence.ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_id_tipo_documento", Integer.class, jakarta.persistence.ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_correo", String.class, jakarta.persistence.ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_telefono", String.class, jakarta.persistence.ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_direccion", String.class, jakarta.persistence.ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_ciudad", String.class, jakarta.persistence.ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_distrito", String.class, jakarta.persistence.ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_representante", String.class, jakarta.persistence.ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_id_usuario", Integer.class, jakarta.persistence.ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_foto", String.class, jakarta.persistence.ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_id_especialidad", Integer.class, jakarta.persistence.ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_cmp", String.class, jakarta.persistence.ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_activo", Integer.class, jakarta.persistence.ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_mensaje", String.class, jakarta.persistence.ParameterMode.OUT);

        sp.setParameter("p_id_entidad", c != null ? safeId(c.getIdEntidad()) : null);
        sp.setParameter("p_id_tipo_persona_juridica", c != null ? safeId(c.getIdTipoPersonaJuridica()) : null);
        sp.setParameter("p_nombre", c != null ? c.getNombre() : null);
        sp.setParameter("p_sexo", c != null ? c.getSexo() : null);
        sp.setParameter("p_documento", c != null ? c.getDocumento() : null);
        sp.setParameter("p_id_tipo_documento", c != null ? safeId(c.getIdTipoDocumento()) : null);
        sp.setParameter("p_correo", c != null ? c.getCorreo() : null);
        sp.setParameter("p_telefono", c != null ? c.getTelefono() : null);
        sp.setParameter("p_direccion", c != null ? c.getDireccion() : null);
        sp.setParameter("p_ciudad", c != null ? c.getCiudad() : null);
        sp.setParameter("p_distrito", c != null ? c.getDistrito() : null);
        sp.setParameter("p_representante", c != null ? c.getRepresentante() : null);
        sp.setParameter("p_id_usuario", c != null ? safeId(c.getIdUsuario()) : null);
        sp.setParameter("p_foto", c != null ? c.getFoto() : null);
        sp.setParameter("p_id_especialidad", safeId(dto.getIdEspecialidad()));
        sp.setParameter("p_cmp", dto.getCmp());
        sp.setParameter("p_activo", dto.getActivo() != null && dto.getActivo() ? 1 : 0);

        sp.execute();

        return VeterinarioResponseDTO.builder()
                .mensaje((String) sp.getOutputParameterValue("p_mensaje"))
                .cmp(dto.getCmp())
                .nombreColaborador(c != null ? c.getNombre() : null)
                .activo(dto.getActivo())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public VeterinarioResponseDTO obtenerPorId(Integer id) {
        return repository.findById(id).map(v ->
                VeterinarioResponseDTO.builder()
                        .id(v.getId())
                        .codigoVeterinario(v.getCodigo())
                        .cmp(v.getCmp())
                        .activo(v.getActivo())
                        .nombreColaborador(v.getColaborador() != null ? v.getColaborador().getEntidad().getNombre() : null)
                        .build()
        ).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VeterinarioResponseDTO> listar() {
        return repository.findAll().stream().map(v ->
                VeterinarioResponseDTO.builder()
                        .id(v.getId())
                        .codigoVeterinario(v.getCodigo())
                        .cmp(v.getCmp())
                        .activo(v.getActivo())
                        .nombreColaborador(v.getColaborador() != null ? v.getColaborador().getEntidad().getNombre() : null)
                        .build()
        ).collect(Collectors.toList());
    }
}

package com.vet.manadawoof.service.impl;

import com.vet.manadawoof.dtos.request.VeterinarioRequestDTO;
import com.vet.manadawoof.dtos.response.VeterinarioResponseDTO;
import com.vet.manadawoof.service.VeterinarioService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VeterinarioServiceImpl implements VeterinarioService {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public VeterinarioResponseDTO registrar(VeterinarioRequestDTO dto) {
        StoredProcedureQuery sp = entityManager.createStoredProcedureQuery("registrar_veterinario");

        sp.registerStoredProcedureParameter("p_id_entidad", Long.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_id_tipo_persona_juridica", Integer.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_nombre", String.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_sexo", String.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_documento", String.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_id_tipo_documento", Integer.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_correo", String.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_telefono", String.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_direccion", String.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_ciudad", String.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_distrito", String.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_representante", String.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_id_especialidad", Integer.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_cmp", String.class, ParameterMode.IN);

        sp.registerStoredProcedureParameter("p_codigo_entidad", String.class, ParameterMode.OUT);
        sp.registerStoredProcedureParameter("p_codigo_colaborador", String.class, ParameterMode.OUT);
        sp.registerStoredProcedureParameter("p_codigo_veterinario", String.class, ParameterMode.OUT);
        sp.registerStoredProcedureParameter("p_mensaje", String.class, ParameterMode.OUT);

        sp.setParameter("p_id_entidad", dto.getId() != null ? dto.getId() : 0L);
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
        sp.setParameter("p_representante", ""); // opcional
        sp.setParameter("p_id_especialidad", dto.getIdEspecialidad());
        sp.setParameter("p_cmp", dto.getCmp());

        sp.execute();

        return VeterinarioResponseDTO.builder()
                .codigoColaborador((String) sp.getOutputParameterValue("p_codigo_colaborador"))
                .codigo((String) sp.getOutputParameterValue("p_codigo_veterinario"))
                .nombre(dto.getNombre())
                .sexo(dto.getSexo())
                .documento(dto.getDocumento())
                .idTipoPersonaJuridica(dto.getIdTipoPersonaJuridica())
                .idTipoDocumento(dto.getIdTipoDocumento())
                .correo(dto.getCorreo())
                .telefono(dto.getTelefono())
                .direccion(dto.getDireccion())
                .ciudad(dto.getCiudad())
                .distrito(dto.getDistrito())
                .usuario("") // SP no devuelve
                .activo(dto.getActivo())
                .foto(dto.getFoto())
                .cmp(dto.getCmp())
                .especialidad("") // se puede mapear si se hace select adicional
                .mensaje((String) sp.getOutputParameterValue("p_mensaje"))
                .build();
    }

    @Override
    @Transactional
    public VeterinarioResponseDTO actualizar(VeterinarioRequestDTO dto) {
        StoredProcedureQuery sp = entityManager.createStoredProcedureQuery("actualizar_veterinario");

        sp.registerStoredProcedureParameter("p_id_entidad", Long.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_id_tipo_persona_juridica", Integer.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_nombre", String.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_sexo", String.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_documento", String.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_id_tipo_documento", Integer.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_correo", String.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_telefono", String.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_direccion", String.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_ciudad", String.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_distrito", String.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_representante", String.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_id_usuario", Integer.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_foto", String.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_id_especialidad", Integer.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_cmp", String.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_activo", Boolean.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_mensaje", String.class, ParameterMode.OUT);

        sp.setParameter("p_id_entidad", dto.getId());
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
        sp.setParameter("p_representante", "");
        sp.setParameter("p_id_usuario", dto.getIdUsuario());
        sp.setParameter("p_foto", dto.getFoto());
        sp.setParameter("p_id_especialidad", dto.getIdEspecialidad());
        sp.setParameter("p_cmp", dto.getCmp());
        sp.setParameter("p_activo", dto.getActivo());

        sp.execute();

        return VeterinarioResponseDTO.builder()
                .codigoColaborador("") // SP no devuelve
                .codigo("") // SP no devuelve
                .nombre(dto.getNombre())
                .sexo(dto.getSexo())
                .documento(dto.getDocumento())
                .idTipoPersonaJuridica(dto.getIdTipoPersonaJuridica())
                .idTipoDocumento(dto.getIdTipoDocumento())
                .correo(dto.getCorreo())
                .telefono(dto.getTelefono())
                .direccion(dto.getDireccion())
                .ciudad(dto.getCiudad())
                .distrito(dto.getDistrito())
                .usuario("")
                .activo(dto.getActivo())
                .foto(dto.getFoto())
                .cmp(dto.getCmp())
                .especialidad("")
                .mensaje((String) sp.getOutputParameterValue("p_mensaje"))
                .build();
    }

    @Override
    @Transactional
    public VeterinarioResponseDTO obtenerPorId(Long id) {
        StoredProcedureQuery sp = entityManager.createStoredProcedureQuery("obtener_veterinario_por_id");
        sp.registerStoredProcedureParameter("p_id_entidad", Long.class, ParameterMode.IN);
        sp.setParameter("p_id_entidad", id);

        Object[] row = (Object[]) sp.getSingleResult();
        return mapRowToDto(row);
    }

    @Override
    @Transactional
    public List<VeterinarioResponseDTO> listar() {
        StoredProcedureQuery sp = entityManager.createStoredProcedureQuery("listar_veterinarios");
        List<Object[]> resultList = sp.getResultList();
        return resultList.stream().map(this::mapRowToDto).collect(Collectors.toList());
    }

    private VeterinarioResponseDTO mapRowToDto(Object[] row) {
        if (row == null) return null;
        return VeterinarioResponseDTO.builder()
                .idColaborador(row[0] != null ? ((Number) row[0]).longValue() : null)
                .codigoColaborador(row[1] != null ? row[1].toString() : null)
                .idEntidad(row[2] != null ? ((Number) row[2]).longValue() : null)
                .nombre(row[3] != null ? row[3].toString() : null)
                .sexo(row[4] != null ? row[4].toString() : null)
                .documento(row[5] != null ? row[5].toString() : null)
                .idTipoPersonaJuridica(row[6] != null ? ((Number) row[6]).intValue() : null)
                .idTipoDocumento(row[7] != null ? ((Number) row[7]).intValue() : null)
                .correo(row[8] != null ? row[8].toString() : null)
                .telefono(row[9] != null ? row[9].toString() : null)
                .direccion(row[10] != null ? row[10].toString() : null)
                .ciudad(row[11] != null ? row[11].toString() : null)
                .distrito(row[12] != null ? row[12].toString() : null)
                .usuario(row[13] != null ? row[13].toString() : null)
                .activo(row[14] != null ? ((Number) row[14]).intValue() == 1 : null)
                .foto(row[15] != null ? row[15].toString() : null)
                .cmp(row.length > 16 && row[16] != null ? row[16].toString() : null)
                .mensaje(row.length > 17 && row[17] != null ? row[17].toString() : "Operación exitosa")
                .build();
    }
}

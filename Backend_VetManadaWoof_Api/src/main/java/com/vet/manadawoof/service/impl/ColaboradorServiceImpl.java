package com.vet.manadawoof.service.impl;

import com.vet.manadawoof.dtos.request.ColaboradorRequestDTO;
import com.vet.manadawoof.dtos.response.ColaboradorResponseDTO;
import com.vet.manadawoof.dtos.response.EntidadResponseDTO;
import com.vet.manadawoof.service.ColaboradorService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
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

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public ColaboradorResponseDTO registrar(ColaboradorRequestDTO dto) {
        StoredProcedureQuery sp = buildSP(dto, "CREATE");
        sp.execute();
        Object[] row = (Object[]) sp.getSingleResult();
        return mapRowToDto(row);
    }

    @Override
    @Transactional
    public ColaboradorResponseDTO actualizar(Long idColaborador, ColaboradorRequestDTO dto) {
        if (idColaborador == null) {
            throw new RuntimeException("ID de colaborador requerido para actualizar");
        }
        dto.setId(idColaborador);
        StoredProcedureQuery sp = buildSP(dto, "UPDATE");
        sp.execute();
        Object[] row = (Object[]) sp.getSingleResult();
        return mapRowToDto(row);
    }

    @Override
    @Transactional
    public List<ColaboradorResponseDTO> listar() {
        List<Object[]> results = entityManager.createNativeQuery("""
        SELECT
            c.id AS id_colaborador,
            c.codigo AS codigo_colaborador,
            e.id AS id_entidad,
            e.nombre,
            e.sexo,
            e.documento,
            e.id_tipo_persona_juridica,
            e.id_tipo_documento,
            e.correo,
            e.telefono,
            e.direccion,
            e.ciudad,
            e.distrito,
            u.username AS usuario,
            c.activo,
            c.fecha_registro,
            c.fecha_ingreso,
            c.foto
        FROM colaboradores c
        JOIN entidades e ON c.id_entidad = e.id
        JOIN usuarios u ON c.id_usuario = u.id;
        """).getResultList();

        return results.stream()
                .map(this::mapRow)
                .collect(Collectors.toList());

    }

    @Override
    @Transactional
    public ColaboradorResponseDTO buscarPorId(Long id) {
        StoredProcedureQuery sp = entityManager.createStoredProcedureQuery("obtener_colaborador_por_id");
        sp.registerStoredProcedureParameter("p_id_entidad", Long.class, ParameterMode.IN);
        sp.setParameter("p_id_entidad", id);
        Object[] row = (Object[]) sp.getSingleResult();
        return row != null ? mapRowToDto(row) : null;
    }

    private StoredProcedureQuery buildSP(ColaboradorRequestDTO dto, String accion) {
        StoredProcedureQuery sp;
        if ("CREATE".equalsIgnoreCase(accion)) {
            sp = entityManager.createStoredProcedureQuery("registrar_colaborador");
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
            sp.registerStoredProcedureParameter("p_fecha_ingreso", java.sql.Date.class, ParameterMode.IN);
            sp.registerStoredProcedureParameter("p_id_usuario", Integer.class, ParameterMode.IN);
            sp.registerStoredProcedureParameter("p_foto", String.class, ParameterMode.IN);
            sp.registerStoredProcedureParameter("p_codigo_entidad", String.class, ParameterMode.OUT);
            sp.registerStoredProcedureParameter("p_codigo_colaborador", String.class, ParameterMode.OUT);
            sp.registerStoredProcedureParameter("p_mensaje", String.class, ParameterMode.OUT);

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
        } else if ("UPDATE".equalsIgnoreCase(accion)) {
            sp = entityManager.createStoredProcedureQuery("actualizar_colaborador");
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
            sp.registerStoredProcedureParameter("p_fecha_ingreso", java.sql.Date.class, ParameterMode.IN);
            sp.registerStoredProcedureParameter("p_id_usuario", Integer.class, ParameterMode.IN);
            sp.registerStoredProcedureParameter("p_foto", String.class, ParameterMode.IN);
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
            sp.setParameter("p_fecha_ingreso", dto.getFechaIngreso());
            sp.setParameter("p_id_usuario", dto.getIdUsuario());
            sp.setParameter("p_foto", dto.getFoto());
            sp.setParameter("p_activo", dto.getActivo());
        } else {
            throw new RuntimeException("Acción de SP inválida: " + accion);
        }
        return sp;
    }

    private ColaboradorResponseDTO mapRowToDto(Object[] row) {
        if (row == null) {
            return null;
        }

        Long idColaborador = row[0] != null ? ((Number) row[0]).longValue() : null;
        String codigoColaborador = row[1] != null ? row[1].toString() : null;
        Long idEntidad = row[2] != null ? ((Number) row[2]).longValue() : null;

        // Consulto la entidad
        Object[] entRow = (Object[]) entityManager.createNativeQuery(
                "SELECT e.id, e.codigo, e.nombre, e.sexo, e.documento, e.id_tipo_persona_juridica, "
                + "e.id_tipo_documento, e.correo, e.telefono, e.direccion, e.ciudad, e.distrito, "
                + "e.representante, e.activo, e.fecha_registro "
                + "FROM entidades e WHERE e.id = ?1")
                .setParameter(1, idEntidad)
                .getSingleResult();

        EntidadResponseDTO entidadDTO = EntidadResponseDTO.builder()
                .id(((Number) entRow[0]).longValue())
                .codigo((String) entRow[1])
                .nombre((String) entRow[2])
                .sexo((String) entRow[3])
                .documento((String) entRow[4])
                .idTipoPersonaJuridica(((Number) entRow[5]).intValue())
                .idTipoDocumento(((Number) entRow[6]).intValue())
                .correo((String) entRow[7])
                .telefono((String) entRow[8])
                .direccion((String) entRow[9])
                .ciudad((String) entRow[10])
                .distrito((String) entRow[11])
                .representante((String) entRow[12])
                .activo(entRow[13] != null ? ((Number) entRow[13]).intValue() == 1 : false)
                .fechaRegistro(entRow[14] != null ? ((java.sql.Timestamp) entRow[14]).toLocalDateTime() : null)
                .build();

        return ColaboradorResponseDTO.builder()
                .idColaborador(idColaborador)
                .codigoColaborador(codigoColaborador)
                .idEntidad(idEntidad)
                .nombre(entidadDTO.getNombre())
                .documento(entidadDTO.getDocumento())
                .correo(entidadDTO.getCorreo())
                .telefono(entidadDTO.getTelefono())
                .direccion(entidadDTO.getDireccion())
                .ciudad(entidadDTO.getCiudad())
                .distrito(entidadDTO.getDistrito())
                .activo(entidadDTO.getActivo())
                .mensaje("Operación exitosa")
                .build();
    }

    private ColaboradorResponseDTO mapRow(Object[] row) {
        return ColaboradorResponseDTO.builder()
                .idColaborador(((Number) row[0]).longValue())
                .codigoColaborador((String) row[1])
                .idEntidad(((Number) row[2]).longValue())
                .nombre((String) row[3])
                .sexo((String) row[4])
                .documento((String) row[5])
                .idTipoPersonaJuridica(((Number) row[6]).intValue())
                .idTipoDocumento(((Number) row[7]).intValue())
                .correo((String) row[8])
                .telefono((String) row[9])
                .direccion((String) row[10])
                .ciudad((String) row[11])
                .distrito((String) row[12])
                .usuario((String) row[13]) // ✅ corregido
                .activo(((Number) row[14]).intValue() == 1)
                .fechaRegistro(((java.sql.Timestamp) row[15]).toLocalDateTime())
                .fechaIngreso(row[16] != null ? ((java.sql.Date) row[16]).toLocalDate() : null)
                .foto((String) row[17])
                .build();
    }

}

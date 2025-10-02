package com.vet.manadawoof.service.impl;

import com.vet.manadawoof.dtos.request.VeterinarioRequestDTO;
import com.vet.manadawoof.dtos.response.VeterinarioResponseDTO;
import com.vet.manadawoof.service.VeterinarioService;
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
public class VeterinarioServiceImpl implements VeterinarioService {

    @PersistenceContext
    private EntityManager entityManager;

    // ==============================
    // Registrar nuevo veterinario
    // ==============================
    @Override
    @Transactional
    public VeterinarioResponseDTO registrar(VeterinarioRequestDTO dto) {
        StoredProcedureQuery sp = buildSP(dto, "CREATE");
        sp.execute();
        Object[] row = (Object[]) sp.getSingleResult();
        return mapRowToDto(row);
    }

    // ==============================
    // Actualizar veterinario existente
    // ==============================
    @Override
    @Transactional
    public VeterinarioResponseDTO actualizar(Long idVeterinario, VeterinarioRequestDTO dto) {
        if (idVeterinario == null) {
            throw new RuntimeException("ID de veterinario requerido para actualizar");
        }
        dto.setId(idVeterinario);
        StoredProcedureQuery sp = buildSP(dto, "UPDATE");
        sp.execute();
        Object[] row = (Object[]) sp.getSingleResult();
        return mapRowToDto(row);
    }

    // ==============================
    // Listar todos los veterinarios
    // ==============================
    @Override
    @Transactional
    public List<VeterinarioResponseDTO> listar() {
        List<Object[]> results = entityManager.createNativeQuery("""
            SELECT
                v.id, v.codigo, c.id, c.codigo, e.id, e.nombre, e.sexo, e.documento,
                e.id_tipo_persona_juridica, e.id_tipo_documento, e.correo, e.telefono,
                e.direccion, e.ciudad, e.distrito, u.username, c.activo, c.foto,
                v.cmp, s.nombre
            FROM veterinarios v
            JOIN colaboradores c ON v.id_colaborador = c.id
            JOIN entidades e ON c.id_entidad = e.id
            JOIN usuarios u ON c.id_usuario = u.id
            JOIN especialidades s ON v.id_especialidad = s.id
        """).getResultList();

        return results.stream()
                .map(this::mapRow)
                .collect(Collectors.toList());
    }

    // ==============================
    // Obtener veterinario por ID
    // ==============================
    @Override
    @Transactional
    public VeterinarioResponseDTO obtenerPorId(Long id) {
        Object[] row = (Object[]) entityManager.createNativeQuery("""
            SELECT
                v.id, v.codigo, c.id, c.codigo, e.id, e.nombre, e.sexo, e.documento,
                e.id_tipo_persona_juridica, e.id_tipo_documento, e.correo, e.telefono,
                e.direccion, e.ciudad, e.distrito, u.username, c.activo, c.foto,
                v.cmp, s.nombre
            FROM veterinarios v
            JOIN colaboradores c ON v.id_colaborador = c.id
            JOIN entidades e ON c.id_entidad = e.id
            JOIN usuarios u ON c.id_usuario = u.id
            JOIN especialidades s ON v.id_especialidad = s.id
            WHERE v.id = ?1
        """).setParameter(1, id).getSingleResult();

        return row != null ? mapRow(row) : null;
    }

    // ==============================
    // Construcción de SP para CREATE/UPDATE
    // ==============================
    private StoredProcedureQuery buildSP(VeterinarioRequestDTO dto, String accion) {
        StoredProcedureQuery sp;
        if ("CREATE".equalsIgnoreCase(accion)) {
            sp = entityManager.createStoredProcedureQuery("registrar_veterinario");
            // Registro de parámetros IN/OUT
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

            // Setear parámetros
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
            sp.setParameter("p_representante", "");
            sp.setParameter("p_id_especialidad", dto.getIdEspecialidad());
            sp.setParameter("p_cmp", dto.getCmp());

        } else if ("UPDATE".equalsIgnoreCase(accion)) {
            sp = entityManager.createStoredProcedureQuery("actualizar_veterinario");
            // Registro parámetros
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
            sp.registerStoredProcedureParameter("p_codigo_entidad", String.class, ParameterMode.OUT);
            sp.registerStoredProcedureParameter("p_codigo_colaborador", String.class, ParameterMode.OUT);
            sp.registerStoredProcedureParameter("p_codigo_veterinario", String.class, ParameterMode.OUT);
            sp.registerStoredProcedureParameter("p_mensaje", String.class, ParameterMode.OUT);

            // Setear parámetros
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
        } else {
            throw new RuntimeException("Acción de SP inválida: " + accion);
        }
        return sp;
    }

    // ==============================
    // Mapeo de fila a DTO seguro
    // ==============================
    private VeterinarioResponseDTO mapRowToDto(Object[] row) {
        if (row == null) {
            return null;
        }

        return VeterinarioResponseDTO.builder()
                .id(safeLong(row[0]))
                .codigo(row[1] != null ? row[1].toString() : null)
                .idColaborador(safeLong(row[2]))
                .codigoColaborador(row[3] != null ? row[3].toString() : null)
                .idEntidad(safeLong(row[4]))
                .nombre(row[5] != null ? row[5].toString() : null)
                .sexo(row[6] != null ? row[6].toString() : null)
                .documento(row[7] != null ? row[7].toString() : null)
                .idTipoPersonaJuridica(safeInteger(row[8]))
                .idTipoDocumento(safeInteger(row[9]))
                .correo(row[10] != null ? row[10].toString() : null)
                .telefono(row[11] != null ? row[11].toString() : null)
                .direccion(row[12] != null ? row[12].toString() : null)
                .ciudad(row[13] != null ? row[13].toString() : null)
                .distrito(row[14] != null ? row[14].toString() : null)
                .usuario(row[15] != null ? row[15].toString() : null)
                .activo(row[16] != null && ((Number) row[16]).intValue() == 1)
                .foto(row[17] != null ? row[17].toString() : null)
                .cmp(row[18] != null ? row[18].toString() : null)
                .especialidad(row[19] != null ? row[19].toString() : null)
                .mensaje("Operación exitosa")
                .build();
    }

    // ==============================
    // Reutilizado para listar()
    // ==============================
    private VeterinarioResponseDTO mapRow(Object[] row) {
        return mapRowToDto(row);
    }

    // ==============================
    // Métodos de seguridad de casteo
    // ==============================
    private Long safeLong(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof Number) {
            return ((Number) obj).longValue();
        }
        if (obj instanceof String) {
            return Long.parseLong((String) obj);
        }
        throw new RuntimeException("No se pudo convertir a Long: " + obj.getClass());
    }

    private Integer safeInteger(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof Number) {
            return ((Number) obj).intValue();
        }
        if (obj instanceof String) {
            return Integer.parseInt((String) obj);
        }
        throw new RuntimeException("No se pudo convertir a Integer: " + obj.getClass());
    }
}

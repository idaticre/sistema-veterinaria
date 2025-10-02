package com.vet.manadawoof.service.impl;

import com.vet.manadawoof.dtos.request.ProveedorRequestDTO;
import com.vet.manadawoof.dtos.response.EntidadResponseDTO;
import com.vet.manadawoof.dtos.response.ProveedorResponseDTO;
import com.vet.manadawoof.service.ProveedorService;
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
public class ProveedorServiceImpl implements ProveedorService {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public ProveedorResponseDTO registrar(ProveedorRequestDTO dto) {
        StoredProcedureQuery sp = buildSP(dto, "CREATE");
        sp.execute();
        Object[] row = (Object[]) sp.getSingleResult();
        return mapRowToDto(row);
    }

    @Override
    @Transactional
    public ProveedorResponseDTO actualizar(ProveedorRequestDTO dto) {
        if (dto.getId() == null) {
            throw new RuntimeException("ID de entidad requerido para actualizar proveedor");
        }
        StoredProcedureQuery sp = buildSP(dto, "UPDATE");
        sp.execute();
        Object[] row = (Object[]) sp.getSingleResult();
        return mapRowToDto(row);
    }

    @Override
    @Transactional
    public List<ProveedorResponseDTO> listar() {
        List<Object[]> results = entityManager.createNativeQuery(
                "SELECT p.id, p.codigo, p.activo, e.id, e.codigo, e.nombre, e.sexo, e.documento, "
                + "e.id_tipo_persona_juridica, e.id_tipo_documento, e.correo, e.telefono, "
                + "e.direccion, e.ciudad, e.distrito, e.representante, e.activo, e.fecha_registro "
                + "FROM proveedores p "
                + "JOIN entidades e ON p.id_entidad = e.id"
        ).getResultList();

        return results.stream()
                .map(this::mapRowToFullDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProveedorResponseDTO obtenerPorId(Long id) {
        StoredProcedureQuery sp = entityManager.createStoredProcedureQuery("obtener_proveedor_por_id");
        sp.registerStoredProcedureParameter("p_id_entidad", Long.class, ParameterMode.IN);
        sp.setParameter("p_id_entidad", id);
        Object[] row = (Object[]) sp.getSingleResult();
        return row != null ? mapRowToDto(row) : null;
    }

    private StoredProcedureQuery buildSP(ProveedorRequestDTO dto, String accion) {
        StoredProcedureQuery sp;
        if ("CREATE".equalsIgnoreCase(accion)) {
            sp = entityManager.createStoredProcedureQuery("registrar_proveedor");
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
            sp.registerStoredProcedureParameter("p_codigo_entidad", String.class, ParameterMode.OUT);
            sp.registerStoredProcedureParameter("p_codigo_proveedor", String.class, ParameterMode.OUT);
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
            sp.setParameter("p_representante", dto.getRepresentante());

        } else if ("UPDATE".equalsIgnoreCase(accion)) {
            sp = entityManager.createStoredProcedureQuery("actualizar_proveedor");
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
            sp.setParameter("p_representante", dto.getRepresentante());
            sp.setParameter("p_activo", dto.getActivo());

        } else {
            throw new RuntimeException("Acción de SP inválida: " + accion);
        }
        return sp;
    }

    private ProveedorResponseDTO mapRowToDto(Object[] row) {
        if (row == null) {
            return null;
        }

        Long idProveedor = row[0] != null ? ((Number) row[0]).longValue() : null;
        String codigoProveedor = row[1] != null ? row[1].toString() : null;
        Boolean activo = row[2] != null ? ((Number) row[2]).intValue() == 1 : null;
        Long idEntidad = row[3] != null ? ((Number) row[3]).longValue() : null;

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

        return ProveedorResponseDTO.builder()
                .id(idProveedor)
                .codigoProveedor(codigoProveedor)
                .nombre(entidadDTO.getNombre())
                .documento(entidadDTO.getDocumento())
                .correo(entidadDTO.getCorreo())
                .telefono(entidadDTO.getTelefono())
                .direccion(entidadDTO.getDireccion())
                .ciudad(entidadDTO.getCiudad())
                .distrito(entidadDTO.getDistrito())
                .representante(entidadDTO.getRepresentante())
                .activo(entidadDTO.getActivo())
                .mensaje("Operación exitosa")
                .build();
    }

    private ProveedorResponseDTO mapRowToFullDto(Object[] row) {
        return ProveedorResponseDTO.builder()
                .id(((Number) row[0]).longValue())
                .codigoProveedor((String) row[1])
                .activo(((Number) row[2]).intValue() == 1)
                .idEntidad(((Number) row[3]).longValue())
                .nombre((String) row[5])
                .sexo((String) row[6])
                .documento((String) row[7])
                .idTipoPersonaJuridica(((Number) row[8]).intValue())
                .idTipoDocumento(((Number) row[9]).intValue())
                .correo((String) row[10])
                .telefono((String) row[11])
                .direccion((String) row[12])
                .ciudad((String) row[13])
                .distrito((String) row[14])
                .representante((String) row[15])
                .fechaRegistro(row[17] != null ? ((java.sql.Timestamp) row[17]).toLocalDateTime() : null)
                .mensaje("Operación exitosa")
                .build();
    }
}

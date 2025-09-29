package com.vet.manadawoof.service.impl;

import com.vet.manadawoof.dtos.request.ProveedorRequestDTO;
import com.vet.manadawoof.dtos.response.ProveedorResponseDTO;
import com.vet.manadawoof.repository.ProveedorRepository;
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

    private final ProveedorRepository repository;

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
        if (dto.getId() == null) throw new RuntimeException("ID de proveedor requerido para actualizar");
        StoredProcedureQuery sp = buildSP(dto, "UPDATE");
        sp.execute();
        Object[] row = (Object[]) sp.getSingleResult();
        return mapRowToDto(row);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProveedorResponseDTO> listar() {
        StoredProcedureQuery sp = entityManager.createStoredProcedureQuery("listar_proveedores");
        List<Object[]> resultList = sp.getResultList();
        return resultList.stream().map(this::mapRowToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ProveedorResponseDTO obtenerPorId(Long id) {
        StoredProcedureQuery sp = entityManager.createStoredProcedureQuery("obtener_proveedor_por_id");
        sp.registerStoredProcedureParameter(1, Long.class, ParameterMode.IN);
        sp.setParameter(1, id);
        Object rowObj = sp.getSingleResult();
        if(rowObj == null) return null;
        Object[] row = (Object[]) rowObj;
        return mapRowToDto(row);
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

        } else {
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
        }
        return sp;
    }

    private ProveedorResponseDTO mapRowToDto(Object[] row) {
        if (row == null) return null;

        Long idProveedor = row[0] != null ? ((Number) row[0]).longValue() : null;
        String codigoProveedor = row[1] != null ? row[1].toString() : null;
        Boolean activo = row[2] != null ? ((Number) row[2]).intValue() == 1 : null;
        Long idEntidad = row[3] != null ? ((Number) row[3]).longValue() : null;

        Object[] entRow = (Object[]) entityManager.createNativeQuery(
                        "SELECT e.id, e.codigo, e.nombre, e.sexo, e.documento, e.id_tipo_persona_juridica, " +
                                "e.id_tipo_documento, e.correo, e.telefono, e.direccion, e.ciudad, e.distrito, " +
                                "e.representante, e.activo, e.fecha_registro " +
                                "FROM entidades e WHERE e.id = ?1")
                .setParameter(1, idEntidad)
                .getSingleResult();

        return ProveedorResponseDTO.builder()
                .id(idProveedor)
                .codigoProveedor(codigoProveedor)
                .activo(activo)
                .idEntidad(entRow[0] != null ? ((Number) entRow[0]).longValue() : null)
                .nombre((String) entRow[2])
                .sexo((String) entRow[3])
                .documento((String) entRow[4])
                .idTipoPersonaJuridica(entRow[5] != null ? ((Number) entRow[5]).intValue() : null)
                .idTipoDocumento(entRow[6] != null ? ((Number) entRow[6]).intValue() : null)
                .correo((String) entRow[7])
                .telefono((String) entRow[8])
                .direccion((String) entRow[9])
                .ciudad((String) entRow[10])
                .distrito((String) entRow[11])
                .representante((String) entRow[12])
                .fechaRegistro(entRow[14] != null ? ((java.sql.Timestamp) entRow[14]).toLocalDateTime() : null)
                .mensaje("Operación exitosa")
                .build();
    }
}

package com.vet.manadawoof.service.impl;

import com.vet.manadawoof.dtos.request.ClienteRequestDTO;
import com.vet.manadawoof.dtos.response.ClienteResponseDTO;
import com.vet.manadawoof.service.ClienteService;
import jakarta.persistence.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClienteServiceImpl implements ClienteService {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public ClienteResponseDTO registrar(ClienteRequestDTO dto) {
        StoredProcedureQuery sp = buildSP(dto, "CREATE");
        sp.setParameter("p_sexo", dto.getSexo() != null ? dto.getSexo() : null);
        sp.execute();

        String codigoEntidad = (String) sp.getOutputParameterValue("p_codigo_entidad");
        String codigoCliente = (String) sp.getOutputParameterValue("p_codigo_cliente");
        String mensaje = (String) sp.getOutputParameterValue("p_mensaje");

        if (mensaje != null && mensaje.startsWith("ERROR")) {
            return ClienteResponseDTO.builder().mensaje(mensaje).build();
        }

        Object[] entRow = (Object[]) entityManager.createNativeQuery(
                        "SELECT id, codigo, nombre, sexo, documento, id_tipo_persona_juridica, " +
                                "id_tipo_documento, correo, telefono, direccion, ciudad, distrito, " +
                                "representante, activo, fecha_registro FROM entidades WHERE codigo = ?1")
                .setParameter(1, codigoEntidad)
                .getSingleResult();

        Long idEntidad = ((Number) entRow[0]).longValue();

        // Obtener ID real de cliente
        Long idCliente = ((Number) entityManager.createNativeQuery(
                        "SELECT id FROM clientes WHERE id_entidad = ?1")
                .setParameter(1, idEntidad)
                .getSingleResult()).longValue();

        return ClienteResponseDTO.builder()
                .id(idCliente)
                .idEntidad(idEntidad)
                .codigoCliente(codigoCliente)
                .nombre((String) entRow[2])
                .sexo(entRow[3] != null ? entRow[3].toString() : null)
                .documento((String) entRow[4])
                .idTipoPersonaJuridica(entRow[5] != null ? ((Number) entRow[5]).intValue() : null)
                .idTipoDocumento(entRow[6] != null ? ((Number) entRow[6]).intValue() : null)
                .correo((String) entRow[7])
                .telefono((String) entRow[8])
                .direccion((String) entRow[9])
                .ciudad((String) entRow[10])
                .distrito((String) entRow[11])
                .activo(entRow[13] != null ? ((Number) entRow[13]).intValue() == 1 : true)
                .fechaRegistro(entRow[14] != null ? ((Timestamp) entRow[14]).toLocalDateTime() : null)
                .mensaje(mensaje)
                .build();
    }

    @Override
    @Transactional
    public ClienteResponseDTO actualizar(ClienteRequestDTO dto) {
        if (dto.getIdEntidad() == null) {
            throw new RuntimeException("ID de entidad requerido para actualizar");
        }

        StoredProcedureQuery sp = buildSP(dto, "UPDATE");
        sp.setParameter("p_sexo", dto.getSexo() != null ? dto.getSexo() : null);
        sp.execute();

        String mensaje = (String) sp.getOutputParameterValue("p_mensaje");
        if (mensaje != null && mensaje.startsWith("ERROR")) {
            return ClienteResponseDTO.builder().mensaje(mensaje).build();
        }

        Object[] entRow = (Object[]) entityManager.createNativeQuery(
                        "SELECT id, codigo, nombre, sexo, documento, id_tipo_persona_juridica, " +
                                "id_tipo_documento, correo, telefono, direccion, ciudad, distrito, " +
                                "representante, activo, fecha_registro FROM entidades WHERE id = ?1")
                .setParameter(1, dto.getIdEntidad())
                .getSingleResult();

        // ID real de cliente
        Long idCliente = ((Number) entityManager.createNativeQuery(
                        "SELECT id FROM clientes WHERE id_entidad = ?1")
                .setParameter(1, dto.getIdEntidad())
                .getSingleResult()).longValue();

        // Código de cliente
        String codigoCliente = (String) entityManager.createNativeQuery(
                        "SELECT codigo FROM clientes WHERE id_entidad = ?1")
                .setParameter(1, dto.getIdEntidad())
                .getSingleResult();

        return ClienteResponseDTO.builder()
                .id(idCliente)
                .idEntidad(dto.getIdEntidad())
                .codigoCliente(codigoCliente)
                .nombre((String) entRow[2])
                .sexo(entRow[3] != null ? entRow[3].toString() : null)
                .documento((String) entRow[4])
                .idTipoPersonaJuridica(entRow[5] != null ? ((Number) entRow[5]).intValue() : null)
                .idTipoDocumento(entRow[6] != null ? ((Number) entRow[6]).intValue() : null)
                .correo((String) entRow[7])
                .telefono((String) entRow[8])
                .direccion((String) entRow[9])
                .ciudad((String) entRow[10])
                .distrito((String) entRow[11])
                .activo(entRow[13] != null ? ((Number) entRow[13]).intValue() == 1 : true)
                .fechaRegistro(entRow[14] != null ? ((Timestamp) entRow[14]).toLocalDateTime() : null)
                .mensaje(mensaje)
                .build();
    }

    @Override
    @Transactional
    public List<ClienteResponseDTO> listar() {
        List<Object[]> results = entityManager.createNativeQuery(
                        "SELECT c.id, c.codigo, c.activo, e.id, e.codigo, e.nombre, e.sexo, e.documento, " +
                                "e.id_tipo_persona_juridica, e.id_tipo_documento, e.correo, e.telefono, " +
                                "e.direccion, e.ciudad, e.distrito, e.representante, e.activo, e.fecha_registro " +
                                "FROM clientes c " +
                                "JOIN entidades e ON c.id_entidad = e.id")
                .getResultList();

        return results.stream()
                .map(this::mapRowToFullDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ClienteResponseDTO obtenerPorId(Long id) {
        StoredProcedureQuery sp = entityManager.createStoredProcedureQuery("obtener_cliente_por_id");
        sp.registerStoredProcedureParameter("p_id_entidad", Long.class, ParameterMode.IN);
        sp.setParameter("p_id_entidad", id);
        Object[] row = (Object[]) sp.getSingleResult();
        return row != null ? mapRowToDto(row) : null;
    }

    private StoredProcedureQuery buildSP(ClienteRequestDTO dto, String accion) {
        StoredProcedureQuery sp;
        if ("CREATE".equalsIgnoreCase(accion)) {
            sp = entityManager.createStoredProcedureQuery("registrar_cliente");
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
            sp.registerStoredProcedureParameter("p_codigo_entidad", String.class, ParameterMode.OUT);
            sp.registerStoredProcedureParameter("p_codigo_cliente", String.class, ParameterMode.OUT);
            sp.registerStoredProcedureParameter("p_mensaje", String.class, ParameterMode.OUT);

            sp.setParameter("p_id_tipo_persona_juridica", dto.getIdTipoPersonaJuridica());
            sp.setParameter("p_nombre", dto.getNombre());
            sp.setParameter("p_sexo", dto.getSexo() != null ? dto.getSexo().substring(0, 1) : null);
            sp.setParameter("p_documento", dto.getDocumento());
            sp.setParameter("p_id_tipo_documento", dto.getIdTipoDocumento());
            sp.setParameter("p_correo", dto.getCorreo());
            sp.setParameter("p_telefono", dto.getTelefono());
            sp.setParameter("p_direccion", dto.getDireccion());
            sp.setParameter("p_ciudad", dto.getCiudad());
            sp.setParameter("p_distrito", dto.getDistrito());

        } else if ("UPDATE".equalsIgnoreCase(accion)) {
            sp = entityManager.createStoredProcedureQuery("actualizar_cliente");
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
            sp.registerStoredProcedureParameter("p_activo", Boolean.class, ParameterMode.IN);
            sp.registerStoredProcedureParameter("p_mensaje", String.class, ParameterMode.OUT);

            sp.setParameter("p_id_entidad", dto.getIdEntidad());
            sp.setParameter("p_id_tipo_persona_juridica", dto.getIdTipoPersonaJuridica());
            sp.setParameter("p_nombre", dto.getNombre());
            sp.setParameter("p_sexo", dto.getSexo() != null ? dto.getSexo().substring(0, 1) : null);
            sp.setParameter("p_documento", dto.getDocumento());
            sp.setParameter("p_id_tipo_documento", dto.getIdTipoDocumento());
            sp.setParameter("p_correo", dto.getCorreo());
            sp.setParameter("p_telefono", dto.getTelefono());
            sp.setParameter("p_direccion", dto.getDireccion());
            sp.setParameter("p_ciudad", dto.getCiudad());
            sp.setParameter("p_distrito", dto.getDistrito());
            sp.setParameter("p_activo", dto.getActivo());

        } else {
            throw new RuntimeException("Acción de SP inválida: " + accion);
        }
        return sp;
    }

    private ClienteResponseDTO mapRowToDto(Object[] row) {
        if (row == null) return null;

        Long idCliente = row[0] != null ? ((Number) row[0]).longValue() : null;
        String codigoCliente = row[1] != null ? row[1].toString() : null;
        Boolean activo = row[2] != null ? ((Number) row[2]).intValue() == 1 : null;
        Long idEntidad = row[3] != null ? ((Number) row[3]).longValue() : null;

        Object[] entRow = (Object[]) entityManager.createNativeQuery(
                        "SELECT e.id, e.codigo, e.nombre, e.sexo, e.documento, e.id_tipo_persona_juridica, " +
                                "e.id_tipo_documento, e.correo, e.telefono, e.direccion, e.ciudad, e.distrito, " +
                                "e.representante, e.activo, e.fecha_registro " +
                                "FROM entidades e WHERE e.id = ?1")
                .setParameter(1, idEntidad)
                .getSingleResult();

        return ClienteResponseDTO.builder()
                .id(idCliente)
                .codigoCliente(codigoCliente)
                .idEntidad(((Number) entRow[0]).longValue())
                .nombre((String) entRow[2])
                .sexo(entRow[3] != null ? entRow[3].toString() : null)
                .documento((String) entRow[4])
                .idTipoPersonaJuridica(entRow[5] != null ? ((Number) entRow[5]).intValue() : null)
                .idTipoDocumento(entRow[6] != null ? ((Number) entRow[6]).intValue() : null)
                .correo((String) entRow[7])
                .telefono((String) entRow[8])
                .direccion((String) entRow[9])
                .ciudad((String) entRow[10])
                .distrito((String) entRow[11])
                .activo(entRow[13] != null ? ((Number) entRow[13]).intValue() == 1 : true)
                .fechaRegistro(entRow[14] != null ? ((Timestamp) entRow[14]).toLocalDateTime() : null)
                .mensaje("Operación exitosa")
                .build();
    }

    private ClienteResponseDTO mapRowToFullDto(Object[] row) {
        if (row == null) return null;

        return ClienteResponseDTO.builder()
                .id(row[0] != null ? ((Number) row[0]).longValue() : null)              // c.id
                .codigoCliente(row[1] != null ? row[1].toString() : null)               // c.codigo
                .activo(row[2] != null ? ((Number) row[2]).intValue() == 1 : true)      // c.activo
                .idEntidad(row[3] != null ? ((Number) row[3]).longValue() : null)       // e.id
                .nombre((String) row[5])
                .sexo(row[6] != null ? row[6].toString() : null)
                .documento((String) row[7])
                .idTipoPersonaJuridica(row[8] != null ? ((Number) row[8]).intValue() : null)
                .idTipoDocumento(row[9] != null ? ((Number) row[9]).intValue() : null)
                .correo((String) row[10])
                .telefono((String) row[11])
                .direccion((String) row[12])
                .ciudad((String) row[13])
                .distrito((String) row[14])
                .fechaRegistro(row[17] != null ? ((Timestamp) row[17]).toLocalDateTime() : null)
                .mensaje("Operación exitosa")
                .build();
    }

    @Override
    @Transactional
    public ClienteResponseDTO eliminar(Long idCliente) {
        // Paso 1: Verificar que el cliente exista
        Object[] row = (Object[]) entityManager.createNativeQuery(
                        "SELECT c.id, c.codigo, c.id_entidad, c.activo " +
                                "FROM clientes c WHERE c.id = ?1")
                .setParameter(1, idCliente)
                .getSingleResult();

        if (row == null) {
            return ClienteResponseDTO.builder()
                    .mensaje("ERROR: Cliente no encontrado")
                    .build();
        }

        Long idEntidad = ((Number) row[2]).longValue();

        // Paso 2: Actualizar el estado lógico (desactivar cliente)
        entityManager.createNativeQuery(
                        "UPDATE clientes SET activo = 0 WHERE id = ?1")
                .setParameter(1, idCliente)
                .executeUpdate();

        // Paso 3: Obtener info actualizada de la entidad
        Object[] entRow = (Object[]) entityManager.createNativeQuery(
                        "SELECT e.id, e.codigo, e.nombre, e.sexo, e.documento, e.id_tipo_persona_juridica, " +
                                "e.id_tipo_documento, e.correo, e.telefono, e.direccion, e.ciudad, e.distrito, " +
                                "e.representante, e.activo, e.fecha_registro " +
                                "FROM entidades e WHERE e.id = ?1")
                .setParameter(1, idEntidad)
                .getSingleResult();

        return ClienteResponseDTO.builder()
                .id(idCliente)
                .idEntidad(idEntidad)
                .codigoCliente(row[1] != null ? row[1].toString() : null)
                .nombre((String) entRow[2])
                .sexo(entRow[3] != null ? entRow[3].toString() : null)
                .documento((String) entRow[4])
                .idTipoPersonaJuridica(entRow[5] != null ? ((Number) entRow[5]).intValue() : null)
                .idTipoDocumento(entRow[6] != null ? ((Number) entRow[6]).intValue() : null)
                .correo((String) entRow[7])
                .telefono((String) entRow[8])
                .direccion((String) entRow[9])
                .ciudad((String) entRow[10])
                .distrito((String) entRow[11])
                // desactivar
                .activo(false)
                .fechaRegistro(entRow[14] != null ? ((Timestamp) entRow[14]).toLocalDateTime() : null)
                .mensaje("Cliente eliminado lógicamente con éxito")
                .build();
    }

}

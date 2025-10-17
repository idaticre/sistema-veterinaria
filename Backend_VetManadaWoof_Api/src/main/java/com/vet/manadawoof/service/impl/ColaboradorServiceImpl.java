package com.vet.manadawoof.service.impl;

import com.vet.manadawoof.dtos.request.ColaboradorRequestDTO;
import com.vet.manadawoof.dtos.response.ColaboradorResponseDTO;
import com.vet.manadawoof.service.ColaboradorService;
import jakarta.persistence.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
// ===============================================================
// Servicio de gestión de colaboradores
// Controla el registro, actualización, listado, búsqueda y eliminación
// usando procedimientos almacenados y consultas SQL nativas.
// ===============================================================
public class ColaboradorServiceImpl implements ColaboradorService {
    
    @PersistenceContext
    private final EntityManager entityManager;
    
    /**
     * Registra un nuevo colaborador en la base de datos usando el SP `registrar_colaborador`.
     */
    @Override
    @Transactional
    public ColaboradorResponseDTO registrar(ColaboradorRequestDTO dto) {
        StoredProcedureQuery sp = buildSP(dto, "CREATE");
        sp.execute();
        
        // Salidas del SP
        String codigoEntidad = (String) sp.getOutputParameterValue("p_codigo_entidad");
        String codigoColaborador = (String) sp.getOutputParameterValue("p_codigo_colaborador");
        String mensaje = (String) sp.getOutputParameterValue("p_mensaje");
        
        // Validar error
        if(mensaje != null && mensaje.startsWith("ERROR")) {
            return ColaboradorResponseDTO.builder().mensaje(mensaje).build();
        }
        
        // Consultar entidad creada
        Object[] entRow = (Object[]) entityManager.createNativeQuery(
                        "SELECT id, codigo, nombre, sexo, documento, id_tipo_persona_juridica, " +
                                "id_tipo_documento, correo, telefono, direccion, ciudad, distrito, " +
                                "activo, fecha_registro FROM entidades WHERE codigo = ?1")
                .setParameter(1, codigoEntidad)
                .getSingleResult();
        
        // Obtener IDs reales
        Long idEntidad = ((Number) entRow[0]).longValue();
        Long idColaborador = ((Number) entityManager.createNativeQuery(
                        "SELECT id FROM colaboradores WHERE id_entidad = ?1")
                .setParameter(1, idEntidad)
                .getSingleResult()).longValue();
        
        return ColaboradorResponseDTO.builder()
                .id(idColaborador)
                .idEntidad(idEntidad)
                .codigoColaborador(codigoColaborador)
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
                .activo(entRow[12] != null && ((Number) entRow[12]).intValue() == 1)
                .fechaRegistro(entRow[13] != null ? ((Timestamp) entRow[13]).toLocalDateTime() : null)
                .mensaje(mensaje)
                .build();
    }
    
    /**
     * Actualiza la información de un colaborador existente usando el SP `actualizar_colaborador`.
     */
    @Override
    @Transactional
    public ColaboradorResponseDTO actualizar(ColaboradorRequestDTO dto) {
        if(dto.getIdEntidad() == null)
            throw new RuntimeException("ID de entidad requerido para actualizar");
        
        StoredProcedureQuery sp = buildSP(dto, "UPDATE");
        sp.execute();
        
        String mensaje = (String) sp.getOutputParameterValue("p_mensaje");
        if(mensaje != null && mensaje.startsWith("ERROR")) {
            return ColaboradorResponseDTO.builder().mensaje(mensaje).build();
        }
        
        // Consultar datos actualizados
        Object[] entRow = (Object[]) entityManager.createNativeQuery(
                        "SELECT id, codigo, nombre, sexo, documento, id_tipo_persona_juridica, " +
                                "id_tipo_documento, correo, telefono, direccion, ciudad, distrito, " +
                                "activo, fecha_registro FROM entidades WHERE id = ?1")
                .setParameter(1, dto.getIdEntidad())
                .getSingleResult();
        
        Long idColaborador = ((Number) entityManager.createNativeQuery(
                        "SELECT id FROM colaboradores WHERE id_entidad = ?1")
                .setParameter(1, dto.getIdEntidad())
                .getSingleResult()).longValue();
        
        String codigoColaborador = (String) entityManager.createNativeQuery(
                        "SELECT codigo FROM colaboradores WHERE id_entidad = ?1")
                .setParameter(1, dto.getIdEntidad())
                .getSingleResult();
        
        return ColaboradorResponseDTO.builder()
                .id(idColaborador)
                .idEntidad(dto.getIdEntidad())
                .codigoColaborador(codigoColaborador)
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
                .activo(entRow[12] != null && ((Number) entRow[12]).intValue() == 1)
                .fechaRegistro(entRow[13] != null ? ((Timestamp) entRow[13]).toLocalDateTime() : null)
                .mensaje(mensaje)
                .build();
    }
    
    /**
     * Lista todos los colaboradores con los datos generales de su entidad asociada.
     */
    @Override
    @Transactional(readOnly = true)
    public List<ColaboradorResponseDTO> listar() {
        List<Object[]> results = entityManager.createNativeQuery(
                        "SELECT c.id, c.codigo, c.id_entidad, c.fecha_ingreso, c.fecha_registro, " +
                                "c.activo, c.foto, e.nombre, e.sexo, e.documento, e.id_tipo_persona_juridica, " +
                                "e.id_tipo_documento, e.correo, e.telefono, e.direccion, e.ciudad, e.distrito " +
                                "FROM colaboradores c JOIN entidades e ON c.id_entidad = e.id")
                .getResultList();
        
        return results.stream().map(this :: mapRowToFullDto).collect(Collectors.toList());
    }
    
    /**
     * Obtiene la información completa de un colaborador por su ID.
     */
    @Override
    @Transactional(readOnly = true)
    public ColaboradorResponseDTO obtenerPorId(Long id) {
        Object[] row = (Object[]) entityManager.createNativeQuery(
                        "SELECT c.id, c.codigo, c.id_entidad, c.fecha_ingreso, c.fecha_registro, " +
                                "c.activo, c.foto, e.nombre, e.sexo, e.documento, e.id_tipo_persona_juridica, " +
                                "e.id_tipo_documento, e.correo, e.telefono, e.direccion, e.ciudad, e.distrito " +
                                "FROM colaboradores c JOIN entidades e ON c.id_entidad = e.id WHERE c.id = ?1")
                .setParameter(1, id)
                .getSingleResult();
        
        return mapRowToFullDto(row);
    }
    
    /**
     * Elimina lógicamente un colaborador (activo = 0).
     */
    @Override
    @Transactional
    public ColaboradorResponseDTO eliminar(Long idColaborador) {
        Object[] row = (Object[]) entityManager.createNativeQuery(
                        "SELECT id, codigo, id_entidad FROM colaboradores WHERE id = ?1")
                .setParameter(1, idColaborador)
                .getSingleResult();
        
        if(row == null) {
            return ColaboradorResponseDTO.builder().mensaje("ERROR: Colaborador no encontrado").build();
        }
        
        Long idEntidad = ((Number) row[2]).longValue();
        
        entityManager.createNativeQuery("UPDATE colaboradores SET activo = 0 WHERE id = ?1")
                .setParameter(1, idColaborador)
                .executeUpdate();
        
        return ColaboradorResponseDTO.builder()
                .id(idColaborador)
                .idEntidad(idEntidad)
                .codigoColaborador(row[1] != null ? row[1].toString() : null)
                .activo(false)
                .mensaje("Colaborador eliminado lógicamente con éxito")
                .build();
    }
    
    /**
     * Construye el Stored Procedure de registro o actualización.
     */
    private StoredProcedureQuery buildSP(ColaboradorRequestDTO dto, String accion) {
        StoredProcedureQuery sp;
        
        if("CREATE".equalsIgnoreCase(accion)) {
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
            sp.registerStoredProcedureParameter("p_id_usuario", Integer.class, ParameterMode.IN);
            sp.registerStoredProcedureParameter("p_fecha_ingreso", Date.class, ParameterMode.IN);
            sp.registerStoredProcedureParameter("p_foto", String.class, ParameterMode.IN);
            sp.registerStoredProcedureParameter("p_codigo_entidad", String.class, ParameterMode.OUT);
            sp.registerStoredProcedureParameter("p_codigo_colaborador", String.class, ParameterMode.OUT);
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
            sp.setParameter("p_id_usuario", dto.getIdUsuario());
            sp.setParameter("p_foto", dto.getFoto());
            
        } else {
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
            sp.registerStoredProcedureParameter("p_id_usuario", Integer.class, ParameterMode.IN);
            sp.registerStoredProcedureParameter("p_fecha_ingreso", Date.class, ParameterMode.IN);
            sp.registerStoredProcedureParameter("p_foto", String.class, ParameterMode.IN);
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
            sp.setParameter("p_id_usuario", dto.getIdUsuario());
            sp.setParameter("p_foto", dto.getFoto());
            sp.setParameter("p_activo", dto.getActivo());
        }
        
        return sp;
    }
    
    /**
     * Mapea una fila completa (JOIN colaboradores + entidades) al DTO.
     */
    private ColaboradorResponseDTO mapRowToFullDto(Object[] row) {
        if(row == null) return null;
        
        return ColaboradorResponseDTO.builder()
                .id(row[0] != null ? ((Number) row[0]).longValue() : null)
                .codigoColaborador(row[1] != null ? row[1].toString() : null)
                .idEntidad(row[2] != null ? ((Number) row[2]).longValue() : null)
                .fechaIngreso(row[3] != null ? ((Date) row[3]).toLocalDate() : null)
                .fechaRegistro(row[4] != null ? ((Timestamp) row[4]).toLocalDateTime() : null)
                .activo(row[5] != null && ((Number) row[5]).intValue() == 1)
                .foto((String) row[6])
                .nombre((String) row[7])
                .sexo(row[6] != null ? row[6].toString() : null)
                .documento((String) row[9])
                .idTipoPersonaJuridica(row[10] != null ? ((Number) row[10]).intValue() : null)
                .idTipoDocumento(row[11] != null ? ((Number) row[11]).intValue() : null)
                .correo((String) row[12])
                .telefono((String) row[13])
                .direccion((String) row[14])
                .ciudad((String) row[15])
                .distrito((String) row[16])
                .mensaje("Operación exitosa")
                .build();
    }
}

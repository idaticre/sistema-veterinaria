package com.vet.manadawoof.service.impl;

import com.vet.manadawoof.dtos.request.ColaboradorRequestDTO;
import com.vet.manadawoof.dtos.response.ColaboradorResponseDTO;
import com.vet.manadawoof.mapper.ColaboradorMapper;
import com.vet.manadawoof.service.ColaboradorService;
import jakarta.persistence.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;


// Servicio de gestión de colaboradores
// Controla el registro, actualización, listado, búsqueda y eliminación
// usando procedimientos almacenados y consultas SQL nativas.
@Service
@RequiredArgsConstructor
public class ColaboradorServiceImpl implements ColaboradorService {
    
    @PersistenceContext
    private final EntityManager entityManager;
    private final ColaboradorMapper colaboradorMapper;
    
    /**
     * Registra un nuevo colaborador en la base de datos usando el SP `registrar_colaborador`.
     */
    @Override
    @Transactional
    public ColaboradorResponseDTO registrar(ColaboradorRequestDTO dto) {
        // Ejecutar SP para registrar colaborador
        StoredProcedureQuery sp = buildSP(dto, "CREATE");
        sp.execute();
        
        // Obtener valores de salida del SP
        String codigoEntidad = (String) sp.getOutputParameterValue("p_codigo_entidad");
        String codigoColaborador = (String) sp.getOutputParameterValue("p_codigo_colaborador");
        String mensaje = (String) sp.getOutputParameterValue("p_mensaje");
        
        // Validar mensaje de error desde el SP
        if(mensaje != null && mensaje.startsWith("ERROR")) {
            return ColaboradorResponseDTO.builder().mensaje(mensaje).build();
        }
        
        // Consultar datos generales de la entidad base
        Object[] entRow = (Object[]) entityManager.createNativeQuery(
                        "SELECT id, codigo, nombre, sexo, documento, id_tipo_persona_juridica, " +
                                "id_tipo_documento, correo, telefono, direccion, ciudad, distrito, representante, activo, fecha_registro " +
                                "FROM entidades WHERE codigo = ?1")
                .setParameter(1, codigoEntidad)
                .getSingleResult();
        
        Long idEntidad = ((Number) entRow[0]).longValue();
        
        // Consultar datos específicos del colaborador registrado
        Object[] colRow = (Object[]) entityManager.createNativeQuery(
                        "SELECT id, codigo, id_usuario, fecha_ingreso, foto, activo " +
                                "FROM colaboradores WHERE id_entidad = ?1")
                .setParameter(1, idEntidad)
                .getSingleResult();
        
        // Mapear resultado combinado al DTO de respuesta
        return colaboradorMapper.toDto(entRow, colRow, mensaje);
    }
    
    // Actualiza la información de un colaborador existente
    // usando el SP `actualizar_colaborador`.
    @Override
    @Transactional
    public ColaboradorResponseDTO actualizar(ColaboradorRequestDTO dto) {
        // Verificar entidad asociada
        Long idEntidad = dto.getIdEntidad();
        if(idEntidad == null) {
            idEntidad = ((Number) entityManager.createNativeQuery(
                            "SELECT id_entidad FROM colaboradores WHERE id = ?1")
                    .setParameter(1, dto.getId())
                    .getSingleResult()).longValue();
            dto.setIdEntidad(idEntidad);
        }
        
        // Ejecutar SP de actualización
        StoredProcedureQuery sp = buildSP(dto, "UPDATE");
        sp.execute();
        
        String mensaje = (String) sp.getOutputParameterValue("p_mensaje");
        if(mensaje != null && mensaje.startsWith("ERROR")) {
            return ColaboradorResponseDTO.builder().mensaje(mensaje).build();
        }
        
        // Consultar datos actualizados de entidad y colaborador
        Object[] entRow = (Object[]) entityManager.createNativeQuery(
                        "SELECT id, codigo, nombre, sexo, documento, id_tipo_persona_juridica, " +
                                "id_tipo_documento, correo, telefono, direccion, ciudad, distrito, activo, fecha_registro " +
                                "FROM entidades WHERE id = ?1")
                .setParameter(1, dto.getIdEntidad())
                .getSingleResult();
        
        Object[] colRow = (Object[]) entityManager.createNativeQuery(
                        "SELECT id, codigo, id_usuario, fecha_ingreso, foto, activo " +
                                "FROM colaboradores WHERE id_entidad = ?1")
                .setParameter(1, dto.getIdEntidad())
                .getSingleResult();
        
        // Mapear datos combinados al DTO de respuesta
        return colaboradorMapper.toDto(entRow, colRow, mensaje);
    }
    
    // Lista todos los colaboradores con los datos generales de su entidad asociada.
    @Override
    @Transactional
    public List<ColaboradorResponseDTO> listar() {
        List<Object[]> results = entityManager.createNativeQuery(
                        "SELECT c.id, c.codigo, c.id_entidad, c.fecha_ingreso, e.fecha_registro, " +
                                "c.activo, c.foto, e.nombre, e.sexo, e.documento, e.id_tipo_persona_juridica, " +
                                "e.id_tipo_documento, e.correo, e.telefono, e.direccion, e.ciudad, e.distrito " +
                                "FROM colaboradores c " +
                                "LEFT JOIN entidades e ON c.id_entidad = e.id")
                .getResultList();
        
        // Mapeo con mapper centralizado
        return results.stream()
                .map(colaboradorMapper :: toFullDto)
                .collect(Collectors.toList());
    }
    
    
    // Obtiene la información completa de un colaborador por su ID.
    @Override
    @Transactional
    public ColaboradorResponseDTO obtenerPorId(Long id) {
        Object[] row = (Object[]) entityManager.createNativeQuery(
                        "SELECT c.id, c.codigo, c.id_entidad, c.fecha_ingreso, c.fecha_registro, " +
                                "c.activo, c.foto, e.nombre, e.sexo, e.documento, e.id_tipo_persona_juridica, " +
                                "e.id_tipo_documento, e.correo, e.telefono, e.direccion, e.ciudad, e.distrito " +
                                "FROM colaboradores c JOIN entidades e ON c.id_entidad = e.id WHERE c.id = ?1")
                .setParameter(1, id)
                .getSingleResult();
        
        return colaboradorMapper.toFullDto(row);
    }
    
    // Elimina lógicamente un colaborador (activo = 0).
    
    @Override
    @Transactional
    public ColaboradorResponseDTO eliminar(Long idColaborador) {
        // Obtener datos básicos
        Object[] colRow = (Object[]) entityManager.createNativeQuery(
                        "SELECT id, codigo, id_entidad FROM colaboradores WHERE id = ?1")
                .setParameter(1, idColaborador)
                .getSingleResult();
        
        if(colRow == null) {
            return ColaboradorResponseDTO.builder()
                    .mensaje("ERROR: Colaborador no encontrado")
                    .build();
        }
        
        // === Desactivar colaborador ===
        entityManager.createNativeQuery("UPDATE colaboradores SET activo = 0 WHERE id = ?1")
                .setParameter(1, idColaborador)
                .executeUpdate();
        
        // === Consultar datos combinados post-eliminación ===
        Object[] row = (Object[]) entityManager.createNativeQuery(
                        "SELECT c.id, c.codigo, c.id_entidad, c.fecha_ingreso, e.fecha_registro, " +
                                "c.activo, c.foto, e.nombre, e.sexo, e.documento, e.id_tipo_persona_juridica, " +
                                "e.id_tipo_documento, e.correo, e.telefono, e.direccion, e.ciudad, e.distrito " +
                                "FROM colaboradores c " +
                                "LEFT JOIN entidades e ON c.id_entidad = e.id " +
                                "WHERE c.id = ?1")
                .setParameter(1, idColaborador)
                .getSingleResult();
        
        // === Mapeo y mensaje final ===
        ColaboradorResponseDTO dto = colaboradorMapper.toFullDto(row);
        dto.setActivo(false);
        dto.setMensaje("Colaborador eliminado lógicamente con éxito");
        return dto;
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
            sp.registerStoredProcedureParameter("p_fecha_ingreso", Date.class, ParameterMode.IN);
            sp.registerStoredProcedureParameter("p_id_usuario", Integer.class, ParameterMode.IN);
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
            sp.setParameter("p_fecha_ingreso",
                    dto.getFechaIngreso() != null ? Date.valueOf(dto.getFechaIngreso()) : Date.valueOf(LocalDate.now()));
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
            sp.registerStoredProcedureParameter("p_fecha_ingreso", Date.class, ParameterMode.IN);
            sp.registerStoredProcedureParameter("p_id_usuario", Integer.class, ParameterMode.IN);
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
            sp.setParameter("p_fecha_ingreso", dto.getFechaIngreso() != null
                    ? Date.valueOf(dto.getFechaIngreso()) : Date.valueOf(LocalDate.now()));
            sp.setParameter("p_id_usuario", dto.getIdUsuario());
            sp.setParameter("p_foto", dto.getFoto());
            sp.setParameter("p_activo", dto.getActivo());
        }
        
        return sp;
    }
}

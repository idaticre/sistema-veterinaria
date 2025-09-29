package com.vet.manadawoof.service.impl;

import com.vet.manadawoof.dtos.request.ColaboradorRequestDTO;
import com.vet.manadawoof.dtos.response.ColaboradorResponseDTO;
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
    private final EntityManager entityManager;

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
        if (idColaborador == null) throw new RuntimeException("ID de colaborador requerido para actualizar");
        dto.setId(idColaborador);
        StoredProcedureQuery sp = buildSP(dto, "UPDATE");
        sp.execute();
        Object[] row = (Object[]) sp.getSingleResult();
        return mapRowToDto(row);
    }

    @Override
    @Transactional
    public List<ColaboradorResponseDTO> listar() {
        StoredProcedureQuery sp = entityManager.createStoredProcedureQuery("listar_colaboradores");
        List<Object[]> resultList = sp.getResultList();
        return resultList.stream().map(this::mapRowToDto).collect(Collectors.toList());
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
        if (row == null) return null;

        return ColaboradorResponseDTO.builder()
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
                .fechaIngreso(row[15] != null ? ((java.sql.Date) row[15]).toLocalDate() : null)
                .foto(row[16] != null ? row[16].toString() : null)
                .mensaje(row.length > 17 && row[17] != null ? row[17].toString() : "Operación exitosa")
                .build();
    }
}

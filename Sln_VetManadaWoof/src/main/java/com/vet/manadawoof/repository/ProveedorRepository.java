package com.vet.manadawoof.repository;

import com.vet.manadawoof.entity.ProveedorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.stereotype.Repository;

@Repository
public interface ProveedorRepository extends JpaRepository<ProveedorEntity, Long> {

    @Procedure(name = "ProveedorEntity.registrarProveedor")
    String registrarProveedor(
            Integer p_id_tipo_persona_juridica,
            String p_nombre,
            String p_sexo,
            String p_documento,
            Integer p_id_tipo_documento,
            String p_correo,
            String p_telefono,
            String p_direccion,
            String p_ciudad,
            String p_distrito,
            String p_representante
    );

    @Procedure(name = "ProveedorEntity.actualizarProveedor")
    String actualizarProveedor(
            Long p_id_entidad,
            Integer p_id_tipo_persona_juridica,
            String p_nombre,
            String p_sexo,
            String p_documento,
            Integer p_id_tipo_documento,
            String p_correo,
            String p_telefono,
            String p_direccion,
            String p_ciudad,
            String p_distrito,
            String p_representante,
            Boolean p_activo
    );
}

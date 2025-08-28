package com.vet.manadawoof.repository;

import com.vet.manadawoof.entity.EntidadEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EntidadRepository extends JpaRepository<EntidadEntity, Long> {

    @Procedure(name = "registrar_entidad_base")
    String registrarEntidad(
            @Param("p_id_tipo_entidad") Long idTipoEntidad,
            @Param("p_id_tipo_persona_juridica") Long idTipoPersonaJuridica,
            @Param("p_nombre") String nombre,
            @Param("p_sexo") String sexo,
            @Param("p_documento") String documento,
            @Param("p_id_tipo_documento") Long idTipoDocumento,
            @Param("p_correo") String correo,
            @Param("p_telefono") String telefono,
            @Param("p_direccion") String direccion,
            @Param("p_ciudad") String ciudad,
            @Param("p_distrito") String distrito,
            @Param("p_representante") String representante
    );

    @Procedure(name = "actualizar_entidad_base")
    String actualizarEntidad(
            @Param("p_id_entidad") Long idEntidad,
            @Param("p_id_tipo_persona_juridica") Long idTipoPersonaJuridica,
            @Param("p_nombre") String nombre,
            @Param("p_sexo") String sexo,
            @Param("p_documento") String documento,
            @Param("p_id_tipo_documento") Long idTipoDocumento,
            @Param("p_correo") String correo,
            @Param("p_telefono") String telefono,
            @Param("p_direccion") String direccion,
            @Param("p_ciudad") String ciudad,
            @Param("p_distrito") String distrito,
            @Param("p_representante") String representante,
            @Param("p_activo") Boolean activo
    );
}

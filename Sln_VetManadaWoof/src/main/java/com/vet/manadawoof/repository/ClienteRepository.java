package com.vet.manadawoof.repository;

import com.vet.manadawoof.entity.ClienteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepository extends JpaRepository<ClienteEntity, Long> {

    @Procedure(name = "ClienteEntity.registrarCliente")
    String registrarCliente(
            @Param("p_id_tipo_persona_juridica") Integer tipoPersonaJuridica,
            @Param("p_nombre") String nombre,
            @Param("p_sexo") String sexo,
            @Param("p_documento") String documento,
            @Param("p_id_tipo_documento") Integer tipoDocumento,
            @Param("p_correo") String correo,
            @Param("p_telefono") String telefono,
            @Param("p_direccion") String direccion,
            @Param("p_ciudad") String ciudad,
            @Param("p_distrito") String distrito,
            @Param("p_codigo_entidad") String codigoEntidad,
            @Param("p_codigo_cliente") String codigoCliente
    );

    @Procedure(name = "ClienteEntity.actualizarCliente")
    String actualizarCliente(
            @Param("p_id_entidad") Long idEntidad,
            @Param("p_id_tipo_persona_juridica") Integer tipoPersonaJuridica,
            @Param("p_nombre") String nombre,
            @Param("p_sexo") String sexo,
            @Param("p_documento") String documento,
            @Param("p_id_tipo_documento") Integer tipoDocumento,
            @Param("p_correo") String correo,
            @Param("p_telefono") String telefono,
            @Param("p_direccion") String direccion,
            @Param("p_ciudad") String ciudad,
            @Param("p_distrito") String distrito,
            @Param("p_activo") Boolean activo
    );
}

package com.vet.manadawoof.repository;

import com.vet.manadawoof.entity.VeterinarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VeterinarioRepository extends JpaRepository<VeterinarioEntity, Long> {

    @Procedure(name = "VeterinarioEntity.registrarVeterinario")
    String registrarVeterinario(
            @Param("p_id_entidad") Long idEntidad,
            @Param("p_id_tipo_persona_juridica") Long idTipoPersona,
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
            @Param("p_id_especialidad") Long idEspecialidad,
            @Param("p_cmp") String cmp
    );

    @Procedure(name = "VeterinarioEntity.actualizarVeterinario")
    String actualizarVeterinario(
            @Param("p_id_entidad") Long idEntidad,
            @Param("p_id_tipo_persona_juridica") Long idTipoPersona,
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
            @Param("p_id_usuario") Long idUsuario,
            @Param("p_foto") String foto,
            @Param("p_id_especialidad") Long idEspecialidad,
            @Param("p_cmp") String cmp,
            @Param("p_activo") Boolean activo
    );
}

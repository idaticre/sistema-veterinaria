package com.vet.manadawoof.repository;

import com.vet.manadawoof.entity.TipoPersonaJuridicaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoPersonaJuridicaRepository extends JpaRepository<TipoPersonaJuridicaEntity, Long> {

    @Procedure(name = "sp_tipo_persona_juridica")
    String spTipoPersonaJuridica(
            @Param("p_accion") String accion,
            @Param("p_id") Long id,
            @Param("p_nombre") String nombre,
            @Param("p_descripcion") String descripcion,
            @Param("p_activo") Boolean activo
    );
}

package com.vet.manadawoof.repository;

import com.vet.manadawoof.entity.UsuarioRolEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRolRepository extends JpaRepository<UsuarioRolEntity, Long> {

    @Procedure(name = "UsuarioRolEntity.spUsuariosRoles")
    String spUsuariosRoles(
            @Param("p_accion") String accion,
            @Param("p_id") Long id,
            @Param("p_usuario_id") Long usuarioId,
            @Param("p_rol_id") Long rolId
    );
}

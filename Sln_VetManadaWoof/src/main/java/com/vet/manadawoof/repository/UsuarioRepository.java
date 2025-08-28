package com.vet.manadawoof.repository;

import com.vet.manadawoof.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Long> {

    @Procedure(name = "UsuarioEntity.sp_usuarios")
    String callSpUsuarios(
            @Param("p_accion") String accion,
            @Param("p_id") Integer id,
            @Param("p_usuario") String usuario,
            @Param("p_clave") String clave,
            @Param("p_activo") Integer activo
    );
}

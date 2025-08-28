package com.vet.manadawoof.repository;

import com.vet.manadawoof.entity.RolEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RolRepository extends JpaRepository<RolEntity, Long> {

    @Procedure(name = "RolEntity.sp_roles")
    String spRoles(
            @Param("p_accion") String accion,
            @Param("p_id") Integer id,
            @Param("p_nombre") String nombre,
            @Param("p_descripcion") String descripcion,
            @Param("p_activo") Integer activo
    );
}

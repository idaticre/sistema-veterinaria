package com.vet.manadawoof.repository;

import com.vet.manadawoof.entity.RolEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.stereotype.Repository;

@Repository
public interface RolRepository extends JpaRepository<RolEntity, Long> {

    @Procedure(name = "RolEntity.spRoles")
    String spRoles(String accion, Integer id, String nombre, String descripcion, Integer activo);
}

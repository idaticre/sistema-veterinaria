package com.vet.manadawoof.repository;

import com.vet.manadawoof.entity.UsuarioRolEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UsuarioRolRepository extends JpaRepository<UsuarioRolEntity, Integer> {

}

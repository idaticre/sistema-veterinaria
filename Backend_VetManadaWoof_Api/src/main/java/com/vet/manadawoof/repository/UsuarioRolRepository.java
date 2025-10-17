package com.vet.manadawoof.repository;

import com.vet.manadawoof.entity.UsuarioRolEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsuarioRolRepository extends JpaRepository<UsuarioRolEntity, Integer> {

    List<UsuarioRolEntity> findByUsuarioId(Integer usuarioId);

    void deleteByUsuarioIdAndRolId(Integer usuarioId, Integer rolId);
}

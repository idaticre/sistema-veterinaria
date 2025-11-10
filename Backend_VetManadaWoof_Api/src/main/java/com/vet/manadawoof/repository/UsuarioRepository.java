// repository/UsuarioRepository.java
package com.vet.manadawoof.repository;

import com.vet.manadawoof.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Integer> {

    Optional<UsuarioEntity> findByUsername(String username);

    @Query("SELECT u FROM UsuarioEntity u WHERE u.username = :username AND u.activo = true")
    Optional<UsuarioEntity> findByUsernameAndActivoTrue(@Param("username") String username);

    Boolean existsByUsername(String username);
}
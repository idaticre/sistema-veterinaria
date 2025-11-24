package com.vet.manadawoof.repository;

import com.vet.manadawoof.entity.HorarioBaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para gestionar la entidad HorarioTrabajoEntity.
 * Permite realizar operaciones CRUD con Spring Data JPA.
 */

@Repository
public interface HorarioBaseRepository extends JpaRepository<HorarioBaseEntity, Integer> {
    Optional<HorarioBaseEntity> findByNombreIgnoreCase(String nombre);
    
    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Integer id);
}

package com.vet.manadawoof.repository;

import com.vet.manadawoof.entity.HorarioTrabajoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para gestionar la entidad HorarioTrabajoEntity.
 * Permite realizar operaciones CRUD con Spring Data JPA.
 */

@Repository
public interface HorarioTrabajoRepository extends JpaRepository<HorarioTrabajoEntity, Integer> {

}

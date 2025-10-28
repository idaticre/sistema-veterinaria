package com.vet.manadawoof.repository;

import com.vet.manadawoof.entity.AplicacionViaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio JPA para la entidad AplicacionVia.
 * Proporciona operaciones CRUD básicas.
 */
@Repository
public interface AplicacionViaRepository extends JpaRepository<AplicacionViaEntity, Integer> {
}

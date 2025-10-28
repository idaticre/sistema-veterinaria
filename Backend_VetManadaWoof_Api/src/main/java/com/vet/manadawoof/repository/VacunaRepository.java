package com.vet.manadawoof.repository;

import com.vet.manadawoof.entity.VacunaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Repositorio JPA para la entidad Vacuna
@Repository
public interface VacunaRepository extends JpaRepository<VacunaEntity, Integer> {
    // Hereda operaciones CRUD básicas de JpaRepository
}

package com.vet.manadawoof.repository;

import com.vet.manadawoof.entity.MedicamentoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Repositorio JPA para medicamentos
@Repository
public interface MedicamentoRepository extends JpaRepository<MedicamentoEntity, Integer> {
    // CRUD base heredado de JpaRepository
}

package com.vet.manadawoof.repository;

import com.vet.manadawoof.entity.EstadoMascotaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Repositorio JPA para estados de mascota
@Repository
public interface EstadoMascotaRepository extends JpaRepository<EstadoMascotaEntity, Integer> {
    // CRUD base heredado de JpaRepository
}

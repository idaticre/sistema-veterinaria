package com.vet.manadawoof.repository;

import com.vet.manadawoof.entity.MascotaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

// Repositorio JPA para la entidad Mascota
public interface MascotaRepository extends JpaRepository<MascotaEntity, Long> {
    // JpaRepository ya provee CRUD y búsquedas básicas por ID
}

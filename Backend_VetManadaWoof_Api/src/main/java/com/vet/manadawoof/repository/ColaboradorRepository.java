package com.vet.manadawoof.repository;

import com.vet.manadawoof.entity.ColaboradorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ColaboradorRepository extends JpaRepository<ColaboradorEntity, Long> {
    // Repositorio vacío siguiendo patrón ENTIDAD
}

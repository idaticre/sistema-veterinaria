package com.vet.manadawoof.repository;

import com.vet.manadawoof.entity.AgendaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AgendaRepository extends JpaRepository<AgendaEntity, Long> {
    Optional<AgendaEntity> findByCodigo(String codigo);
}

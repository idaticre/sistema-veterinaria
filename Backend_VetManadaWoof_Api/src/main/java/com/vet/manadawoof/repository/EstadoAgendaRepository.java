package com.vet.manadawoof.repository;

import com.vet.manadawoof.entity.EstadoAgendaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstadoAgendaRepository extends JpaRepository<EstadoAgendaEntity, Integer> {
}

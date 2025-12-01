package com.vet.manadawoof.repository;

import com.vet.manadawoof.entity.AgendaPagoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PagoAgendaRepository extends JpaRepository<AgendaPagoEntity, Long> {
    
    List<AgendaPagoEntity> findByAgendaId(Long idAgenda);
    
    Optional<AgendaPagoEntity> findByCodigo(String codigo);
}

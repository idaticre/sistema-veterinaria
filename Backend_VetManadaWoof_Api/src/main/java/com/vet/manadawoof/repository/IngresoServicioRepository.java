package com.vet.manadawoof.repository;

import com.vet.manadawoof.entity.IngresoServicioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IngresoServicioRepository extends JpaRepository<IngresoServicioEntity, Long> {
    
    List<IngresoServicioEntity> findByAgendaId(Long idAgenda);
    
    Optional<IngresoServicioEntity> findByCodigo(String codigo);
}

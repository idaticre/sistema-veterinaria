package com.vet.manadawoof.repository;

import com.vet.manadawoof.entity.HistoriaClinicaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HistoriaClinicaRepository extends JpaRepository<HistoriaClinicaEntity, Long> {
    
    Optional<HistoriaClinicaEntity> findByMascotaId(Long idMascota);
    
    Optional<HistoriaClinicaEntity> findByCodigo(String codigo);
    
    Page<HistoriaClinicaEntity> findByActivoTrue(Pageable pageable);
}

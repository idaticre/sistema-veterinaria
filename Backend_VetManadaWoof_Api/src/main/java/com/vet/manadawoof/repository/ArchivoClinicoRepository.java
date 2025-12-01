package com.vet.manadawoof.repository;

import com.vet.manadawoof.entity.HistoriaClinicaArchivoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArchivoClinicoRepository extends JpaRepository<HistoriaClinicaArchivoEntity, Long> {
    
    List<HistoriaClinicaArchivoEntity> findByRegistroAtencionId(Long idRegistroAtencion);
    
    Page<HistoriaClinicaArchivoEntity> findByRegistroAtencionId(Long idRegistroAtencion, Pageable pageable);
    
    Optional<HistoriaClinicaArchivoEntity> findByCodigo(String codigo);
}

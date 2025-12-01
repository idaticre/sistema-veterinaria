package com.vet.manadawoof.repository;

import com.vet.manadawoof.entity.HistoriaClinicaRegistroEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AtencionMedicaRepository extends JpaRepository<HistoriaClinicaRegistroEntity, Long> {
    
    List<HistoriaClinicaRegistroEntity> findByHistoriaClinicaId(Long idHistoriaClinica);
    
    Page<HistoriaClinicaRegistroEntity> findByHistoriaClinicaId(Long idHistoriaClinica, Pageable pageable);
    
    List<HistoriaClinicaRegistroEntity> findByVeterinarioId(Long idVeterinario);
}

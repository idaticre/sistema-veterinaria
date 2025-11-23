package com.vet.manadawoof.repository;

import com.vet.manadawoof.entity.AsignacionHorarioDetalleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface AsignacionHorarioDetalleRepository extends JpaRepository<AsignacionHorarioDetalleEntity, Long> {
    // Solo métodos simples que Spring Data JPA puede derivar
    Optional<AsignacionHorarioDetalleEntity> findByAsignacionIdAndFecha(Long idAsignacion, LocalDate fecha);
}

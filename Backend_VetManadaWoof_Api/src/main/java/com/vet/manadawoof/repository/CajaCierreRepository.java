package com.vet.manadawoof.repository;

import com.vet.manadawoof.entity.CajaCierreEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface CajaCierreRepository extends JpaRepository<CajaCierreEntity, Long> {

    Optional<CajaCierreEntity> findByCajaDiaria_Id(Long cajaDiariaId);

    Optional<CajaCierreEntity> findTopByCajaDiaria_FechaLessThanOrderByCajaDiaria_FechaDesc(LocalDate fecha);
}

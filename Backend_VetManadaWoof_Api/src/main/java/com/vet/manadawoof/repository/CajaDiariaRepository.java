package com.vet.manadawoof.repository;

import com.vet.manadawoof.entity.CajaDiariaEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface CajaDiariaRepository extends JpaRepository<CajaDiariaEntity, Long> {

    Optional<CajaDiariaEntity> findByFecha(LocalDate fecha);

    Optional<CajaDiariaEntity> findByFechaAndEstado(LocalDate fecha, String estado);

    Optional<CajaDiariaEntity> findTopByFechaLessThanOrderByFechaDesc(LocalDate fecha);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM CajaDiariaEntity c WHERE c.fecha = :fecha")
    Optional<CajaDiariaEntity> findByFechaForUpdate(@Param("fecha") LocalDate fecha);
}

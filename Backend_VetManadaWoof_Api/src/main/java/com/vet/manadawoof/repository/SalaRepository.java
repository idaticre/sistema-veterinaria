package com.vet.manadawoof.repository;

import com.vet.manadawoof.entity.SalaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface SalaRepository extends JpaRepository<SalaEntity, Integer> {

    @Query("""
        SELECT s
        FROM SalaEntity s
        WHERE s.activo = true
        AND s.id NOT IN (
            SELECT a.sala.id
            FROM AgendaEntity a
            WHERE a.fecha = :fecha
            AND a.hora = :hora
            AND a.sala IS NOT NULL
        )
    """)
    List<SalaEntity> buscarSalasDisponibles(
            @Param("fecha") LocalDate fecha,
            @Param("hora") LocalTime hora
    );
}
package com.vet.manadawoof.repository;

import com.vet.manadawoof.entity.RegistroAsistenciaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface RegistroAsistenciaRepository extends JpaRepository<RegistroAsistenciaEntity, Integer> {
    @Query("SELECT r FROM RegistroAsistenciaEntity r " +
            "WHERE r.colaborador.id = :idColaborador " +
            "AND r.fecha = :fecha")
    Optional<RegistroAsistenciaEntity> findByColaboradorIdAndFecha(
            Long idColaborador,
            LocalDate fecha
    );
    
    boolean existsByColaboradorIdAndFecha(Long idColaborador, LocalDate fecha);
}

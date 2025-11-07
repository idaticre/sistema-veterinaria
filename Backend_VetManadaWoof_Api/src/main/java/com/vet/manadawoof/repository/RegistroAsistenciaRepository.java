package com.vet.manadawoof.repository;

import com.vet.manadawoof.entity.RegistroAsistenciaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface RegistroAsistenciaRepository extends JpaRepository<RegistroAsistenciaEntity, Integer> {
    // Repositorio sin métodos JPA personalizados.
    // Operaciones se realizan vía procedimientos almacenados.
    Optional<RegistroAsistenciaEntity> findTopByColaborador_IdAndFechaOrderByIdDesc(Integer idColaborador, LocalDate fecha);
    
}

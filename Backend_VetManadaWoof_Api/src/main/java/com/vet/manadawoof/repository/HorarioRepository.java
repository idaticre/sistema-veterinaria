package com.vet.manadawoof.repository;

import com.vet.manadawoof.entity.HorarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface HorarioRepository extends JpaRepository<HorarioEntity, Long> {

    // Usado por el mapper para verificar duplicados y para DELETE
    Optional<HorarioEntity> findByColaboradorIdAndDiaId(Long trabajadorId, Integer diaId);

    // Usado por DELETE para obtener los registros antes de borrarlos
    List<HorarioEntity> findByColaboradorId(Long trabajadorId);
}

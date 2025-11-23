package com.vet.manadawoof.repository;

import com.vet.manadawoof.entity.HorarioBaseRolEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HorarioBaseRolRepository extends JpaRepository<HorarioBaseRolEntity, Long> {
    
    // Verifica si existe la combinación rol-horario-día
    boolean existsByRolIdAndHorarioBaseIdAndDiaId(Integer idRol, Integer idHorarioBase, Integer idDiaSemana);
    
    // Busca por la combinación única
    Optional<HorarioBaseRolEntity> findByRolIdAndHorarioBaseIdAndDiaId(
            Integer idRol, Integer idHorarioBase, Integer idDiaSemana);
    
    // Lista todos los horarios de un rol específico
    @Query("SELECT hbr FROM HorarioBaseRolEntity hbr " +
            "JOIN FETCH hbr.rol " +
            "JOIN FETCH hbr.horarioBase " +
            "JOIN FETCH hbr.dia " +
            "WHERE hbr.rol.id = :idRol " +
            "ORDER BY hbr.dia.orden")
    List<HorarioBaseRolEntity> findByRolIdWithDetails(@Param("idRol") Integer idRol);
    
    // Lista todos los roles asignados a un horario base
    @Query("SELECT hbr FROM HorarioBaseRolEntity hbr " +
            "JOIN FETCH hbr.rol " +
            "JOIN FETCH hbr.horarioBase " +
            "JOIN FETCH hbr.dia " +
            "WHERE hbr.horarioBase.id = :idHorarioBase " +
            "ORDER BY hbr.dia.orden")
    List<HorarioBaseRolEntity> findByHorarioBaseIdWithDetails(@Param("idHorarioBase") Integer idHorarioBase);
    
    // Lista todos los horarios de un día específico
    @Query("SELECT hbr FROM HorarioBaseRolEntity hbr " +
            "JOIN FETCH hbr.rol " +
            "JOIN FETCH hbr.horarioBase " +
            "JOIN FETCH hbr.dia " +
            "WHERE hbr.dia.id = :idDiaSemana " +
            "ORDER BY hbr.rol.nombre")
    List<HorarioBaseRolEntity> findByDiaIdWithDetails(@Param("idDiaSemana") Integer idDiaSemana);
    
    // Lista todos con información completa
    @Query("SELECT hbr FROM HorarioBaseRolEntity hbr " +
            "JOIN FETCH hbr.rol " +
            "JOIN FETCH hbr.horarioBase " +
            "JOIN FETCH hbr.dia " +
            "ORDER BY hbr.rol.nombre, hbr.dia.orden")
    List<HorarioBaseRolEntity> findAllWithDetails();
}

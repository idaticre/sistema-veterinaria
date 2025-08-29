package com.vet.manadawoof.repository;

import com.vet.manadawoof.entity.DiaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.stereotype.Repository;

@Repository
public interface DiaRepository extends JpaRepository<DiaEntity, Long> {

    @Procedure(name = "DiaEntity.spDiasSemana")
    String spDiasSemana(String accion, Long id, String nombre, Boolean activo);
}

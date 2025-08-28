package com.vet.manadawoof.repository;

import com.vet.manadawoof.entity.DiaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DiaRepository extends JpaRepository<DiaEntity, Long> {

    @Procedure(name = "sp_dias_semana")
    String spDiasSemana(
            @Param("p_accion") String accion,
            @Param("p_id") Long id,
            @Param("p_nombre") String nombre,
            @Param("p_activo") Boolean activo
    );
}

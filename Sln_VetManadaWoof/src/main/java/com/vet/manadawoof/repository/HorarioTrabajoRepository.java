package com.vet.manadawoof.repository;

import com.vet.manadawoof.entity.HorarioTrabajoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface HorarioTrabajoRepository extends JpaRepository<HorarioTrabajoEntity, Long> {

    @Procedure(name = "sp_horarios_trabajo")
    String spHorariosTrabajo(
            @Param("p_accion") String accion,
            @Param("p_id") Long id,
            @Param("p_id_colaborador") Long idColaborador,
            @Param("p_id_dia") Long idDia,
            @Param("p_id_tipo_dia") Long idTipoDia,
            @Param("p_hora_inicio") String horaInicio,
            @Param("p_hora_fin") String horaFin
    );
}

package com.vet.manadawoof.repository;

import com.vet.manadawoof.entity.RegistroAsistenciaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RegistroAsistenciaRepository extends JpaRepository<RegistroAsistenciaEntity, Long> {

    @Procedure(name = "sp_registro_asistencia")
    String spRegistroAsistencia(
            @Param("p_accion") String accion,
            @Param("p_id") Long id,
            @Param("p_id_colaborador") Long idColaborador,
            @Param("p_fecha_hora_entrada") String fechaHoraEntrada,
            @Param("p_fecha_hora_salida") String fechaHoraSalida,
            @Param("p_activo") Boolean activo
    );
}

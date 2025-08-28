package com.vet.manadawoof.repository;

import com.vet.manadawoof.entity.ColaboradorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Date;

@Repository
public interface ColaboradorRepository extends JpaRepository<ColaboradorEntity, Long> {

    @Procedure(name = "sp_registrar_colaborador")
    String registrarColaborador(
            @Param("p_id_entidad") Long idEntidad,
            @Param("p_fecha_ingreso") Date fechaIngreso,
            @Param("p_foto") String foto,
            @Param("p_activo") Boolean activo
    );

    @Procedure(name = "sp_actualizar_colaborador")
    String actualizarColaborador(
            @Param("p_id") Long id,
            @Param("p_id_entidad") Long idEntidad,
            @Param("p_fecha_ingreso") Date fechaIngreso,
            @Param("p_foto") String foto,
            @Param("p_activo") Boolean activo
    );
}

package com.vet.manadawoof.repository;

import com.vet.manadawoof.entity.VeterinarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VeterinarioRepository extends JpaRepository<VeterinarioEntity, Long> {

    @Procedure(name = "registrar_veterinario")
    String registrarVeterinario(
            @Param("p_id_colaborador") Long idColaborador,
            @Param("p_id_especialidad") Long idEspecialidad,
            @Param("p_cmp") String cmp,
            @Param("p_activo") Boolean activo
    );

    @Procedure(name = "actualizar_veterinario")
    String actualizarVeterinario(
            @Param("p_id") Long idVeterinario,
            @Param("p_id_colaborador") Long idColaborador,
            @Param("p_id_especialidad") Long idEspecialidad,
            @Param("p_cmp") String cmp,
            @Param("p_activo") Boolean activo
    );
}

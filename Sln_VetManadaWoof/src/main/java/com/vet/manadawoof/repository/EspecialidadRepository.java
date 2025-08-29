package com.vet.manadawoof.repository;

import com.vet.manadawoof.entity.EspecialidadEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.stereotype.Repository;

@Repository
public interface EspecialidadRepository extends JpaRepository<EspecialidadEntity, Long> {

    @Procedure(name = "EspecialidadEntity.spEspecialidades")
    String spEspecialidades(String accion, Long id, String nombre, Boolean activo);
}
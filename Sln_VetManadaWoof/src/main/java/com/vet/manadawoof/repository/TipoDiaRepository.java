package com.vet.manadawoof.repository;

import com.vet.manadawoof.entity.TipoDiaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoDiaRepository extends JpaRepository<TipoDiaEntity, Long> {

    @Procedure(name = "TipoDiaEntity.spTiposDia")
    String spTiposDia(String accion, Long id, String nombre, Boolean activo);
}

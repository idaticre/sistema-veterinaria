package com.vet.manadawoof.repository;

import com.vet.manadawoof.entity.TipoPersonaJuridicaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoPersonaJuridicaRepository extends JpaRepository<TipoPersonaJuridicaEntity, Integer> {

}

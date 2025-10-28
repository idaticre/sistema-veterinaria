package com.vet.manadawoof.repository;

import com.vet.manadawoof.entity.EtapaVidaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EtapaVidaRepository extends JpaRepository<EtapaVidaEntity, Integer> {
}

package com.vet.manadawoof.repository;

import com.vet.manadawoof.entity.TipoRecordatorioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoRecordatorioRepository extends JpaRepository<TipoRecordatorioEntity, Integer> {
}

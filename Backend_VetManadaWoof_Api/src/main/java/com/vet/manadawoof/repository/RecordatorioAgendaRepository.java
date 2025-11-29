package com.vet.manadawoof.repository;

import com.vet.manadawoof.entity.RecordatorioAgendaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecordatorioAgendaRepository extends JpaRepository<RecordatorioAgendaEntity, Long> {
}

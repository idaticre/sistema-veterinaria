package com.vet.manadawoof.service;

import com.vet.manadawoof.entity.RecordatorioAgendaEntity;

import java.util.List;

public interface RecordatorioAgendaService {
    RecordatorioAgendaEntity crear(RecordatorioAgendaEntity entity);
    
    RecordatorioAgendaEntity actualizar(RecordatorioAgendaEntity entity);
    
    String eliminar(Long id);
    
    List<RecordatorioAgendaEntity> listar();
    
    RecordatorioAgendaEntity obtenerPorId(Long id);
}

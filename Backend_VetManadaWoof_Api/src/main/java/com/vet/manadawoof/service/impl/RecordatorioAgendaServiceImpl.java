package com.vet.manadawoof.service.impl;

import com.vet.manadawoof.entity.RecordatorioAgendaEntity;
import com.vet.manadawoof.repository.RecordatorioAgendaRepository;
import com.vet.manadawoof.service.RecordatorioAgendaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecordatorioAgendaServiceImpl implements RecordatorioAgendaService {
    
    private final RecordatorioAgendaRepository repository;
    
    @Override
    @Transactional
    public RecordatorioAgendaEntity crear(RecordatorioAgendaEntity entity) {
        // Validación única por código
        repository.findAll().stream().filter(r -> r.getCodigo().equals(entity.getCodigo())).findFirst().ifPresent(r -> {
            throw new RuntimeException("Ya existe un recordatorio con ese código");
        });
        
        entity.setEnviado(false); return repository.save(entity);
    }
    
    @Override
    @Transactional
    public RecordatorioAgendaEntity actualizar(RecordatorioAgendaEntity entity) {
        RecordatorioAgendaEntity existente = repository.findById(entity.getId()).orElseThrow(() -> new RuntimeException("Recordatorio no encontrado"));
        
        // Validar código único (excepto sí mismo)
        repository.findAll().stream().filter(r -> r.getCodigo().equals(entity.getCodigo()) && ! r.getId().equals(entity.getId())).findFirst().ifPresent(r -> {
            throw new RuntimeException("Otro recordatorio ya usa ese código");
        });
        
        // Actualizar campos
        existente.setCodigo(entity.getCodigo()); existente.setAgenda(entity.getAgenda());
        existente.setTipoRecordatorio(entity.getTipoRecordatorio());
        existente.setFechaRecordatorio(entity.getFechaRecordatorio()); existente.setHora(entity.getHora());
        existente.setMensaje(entity.getMensaje()); existente.setCanalComunicacion(entity.getCanalComunicacion());
        existente.setEnviado(entity.getEnviado()); existente.setFechaEnvio(entity.getFechaEnvio());
        
        return repository.save(existente);
    }
    
    @Override
    @Transactional
    public String eliminar(Long id) {
        if(! repository.existsById(id)) {
            throw new RuntimeException("Recordatorio no encontrado");
        } repository.deleteById(id); return "Recordatorio eliminado correctamente";
    }
    
    @Override
    @Transactional
    public List<RecordatorioAgendaEntity> listar() {
        return repository.findAll();
    }
    
    @Override
    @Transactional
    public RecordatorioAgendaEntity obtenerPorId(Long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Recordatorio no encontrado"));
    }
}

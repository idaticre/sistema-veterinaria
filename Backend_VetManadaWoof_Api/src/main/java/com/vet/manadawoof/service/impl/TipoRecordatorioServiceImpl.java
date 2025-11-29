package com.vet.manadawoof.service.impl;

import com.vet.manadawoof.entity.TipoRecordatorioEntity;
import com.vet.manadawoof.repository.TipoRecordatorioRepository;
import com.vet.manadawoof.service.TipoRecordatorioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class TipoRecordatorioServiceImpl implements TipoRecordatorioService {
    
    private final TipoRecordatorioRepository repository;
    
    // Crea un nuevo tipo de recordatorio.
    @Override
    @Transactional
    public TipoRecordatorioEntity crear(TipoRecordatorioEntity entity) {
        repository.findAll().stream()
                .filter(e -> e.getNombre().equalsIgnoreCase(entity.getNombre()))
                .findFirst()
                .ifPresent(e -> {
                    throw new RuntimeException("Tipo de recordatorio ya existe");
                });
        return repository.save(entity);
    }
    
    // Actualiza los datos de un tipo de recordatorio existente.
    @Override
    @Transactional
    public TipoRecordatorioEntity actualizar(TipoRecordatorioEntity entity) {
        TipoRecordatorioEntity existente = repository.findById(entity.getId())
                .orElseThrow(() -> new RuntimeException("Tipo de recordatorio no encontrado"));
        
        repository.findAll().stream()
                .filter(e -> e.getNombre().equalsIgnoreCase(entity.getNombre()) && ! e.getId().equals(entity.getId()))
                .findFirst()
                .ifPresent(e -> {
                    throw new RuntimeException("Otro tipo de recordatorio con ese nombre ya existe");
                });
        
        existente.setNombre(entity.getNombre());
        existente.setDescripcion(entity.getDescripcion());
        return repository.save(existente);
    }
    
    // Elimina un tipo de recordatorio de la base de datos por su ID.
    @Override
    @Transactional
    public String eliminar(Integer id) {
        if(! repository.existsById(id)) {
            throw new RuntimeException("Tipo de recordatorio no encontrado");
        }
        repository.deleteById(id);
        return "Tipo de recordatorio eliminado correctamente";
    }
    
    // Lista todos los tipos de recordatorio registrados.
    @Override
    @Transactional
    public List<TipoRecordatorioEntity> listar() {
        return repository.findAll();
    }
    
    // Obtiene un tipo de recordatorio específico por su ID.
    @Override
    @Transactional
    public TipoRecordatorioEntity obtenerPorId(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tipo de recordatorio no encontrado"));
    }
}

package com.vet.manadawoof.service.impl;

import com.vet.manadawoof.entity.*;
import com.vet.manadawoof.repository.*;
import com.vet.manadawoof.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class EstadoAgendaServiceImpl implements EstadoAgendaService {
    
    private final EstadoAgendaRepository repository;
    
    // Crea un nuevo estado de agenda.
    @Override
    @Transactional
    public EstadoAgendaEntity crear(EstadoAgendaEntity entity) {
        repository.findAll().stream()
                .filter(e -> e.getNombre().equalsIgnoreCase(entity.getNombre()))
                .findFirst()
                .ifPresent(e -> {
                    throw new RuntimeException("Estado de agenda ya existe");
                });
        return repository.save(entity);
    }
    
    // Actualiza los datos de un estado de agenda existente.
    @Override
    @Transactional
    public EstadoAgendaEntity actualizar(EstadoAgendaEntity entity) {
        EstadoAgendaEntity existente = repository.findById(entity.getId())
                .orElseThrow(() -> new RuntimeException("Estado de agenda no encontrado"));
        
        repository.findAll().stream()
                .filter(e -> e.getNombre().equalsIgnoreCase(entity.getNombre()) && ! e.getId().equals(entity.getId()))
                .findFirst()
                .ifPresent(e -> {
                    throw new RuntimeException("Otro estado de agenda con ese nombre ya existe");
                });
        
        existente.setNombre(entity.getNombre());
        existente.setDescripcion(entity.getDescripcion());
        return repository.save(existente);
    }
    
    // Elimina un estado de agenda de la base de datos por su ID.
    @Override
    @Transactional
    public String eliminar(Integer id) {
        if(! repository.existsById(id)) {
            throw new RuntimeException("Estado de agenda no encontrado");
        }
        repository.deleteById(id);
        return "Estado de agenda eliminado correctamente";
    }
    
    // Lista todos los estados de agenda registrados.
    @Override
    @Transactional
    public List<EstadoAgendaEntity> listar() {
        return repository.findAll();
    }
    
    // Obtiene un estado de agenda específico por su ID.
    @Override
    @Transactional
    public EstadoAgendaEntity obtenerPorId(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Estado de agenda no encontrado"));
    }
}

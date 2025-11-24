package com.vet.manadawoof.service.impl;

import com.vet.manadawoof.entity.DiaEntity;
import com.vet.manadawoof.repository.DiaRepository;
import com.vet.manadawoof.service.DiaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DiaServiceImpl implements DiaService {
    
    private final DiaRepository repository;
    
    @Override
    @Transactional
    public DiaEntity crearDia(DiaEntity entity) {
        repository.findAll().stream()
                .filter(d -> d.getNombre().equalsIgnoreCase(entity.getNombre()))
                .findAny()
                .ifPresent(d -> {
                    throw new RuntimeException("Día ya existe");
                });
        return repository.save(entity);
    }
    
    @Override
    @Transactional
    public DiaEntity actualizarDia(DiaEntity entity) {
        DiaEntity existente = repository.findById(entity.getId())
                .orElseThrow(() -> new RuntimeException("Día no encontrado"));
        
        repository.findAll().stream()
                .filter(d -> d.getNombre().equalsIgnoreCase(entity.getNombre()) && ! d.getId().equals(entity.getId()))
                .findAny()
                .ifPresent(d -> {
                    throw new RuntimeException("Otro día con ese nombre ya existe");
                });
        
        existente.setNombre(entity.getNombre());
        existente.setOrden(entity.getOrden());
        return repository.save(existente);
    }
    
    @Override
    @Transactional
    public String eliminarDia(Integer id) {
        DiaEntity dia = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Día no encontrado"));
        repository.delete(dia);
        return "Día eliminado correctamente";
    }
    
    @Override
    @Transactional
    public List<DiaEntity> listarDias() {
        return repository.findAll();
    }
    
    @Override
    @Transactional
    public DiaEntity obtenerPorId(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Día no encontrado"));
    }
}

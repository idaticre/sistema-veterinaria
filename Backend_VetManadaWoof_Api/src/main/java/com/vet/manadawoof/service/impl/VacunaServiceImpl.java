package com.vet.manadawoof.service.impl;

import com.vet.manadawoof.entity.VacunaEntity;
import com.vet.manadawoof.repository.VacunaRepository;
import com.vet.manadawoof.service.VacunaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VacunaServiceImpl implements VacunaService {
    
    private final VacunaRepository repository;
    
    @Override
    @Transactional
    public VacunaEntity crear(VacunaEntity entity) {
        repository.findAll().stream()
                .filter(e -> e.getNombre().equalsIgnoreCase(entity.getNombre()))
                .findFirst()
                .ifPresent(e -> {
                    throw new RuntimeException("Vacuna ya existe");
                });
        return repository.save(entity);
    }
    
    @Override
    @Transactional
    public VacunaEntity actualizar(VacunaEntity entity) {
        VacunaEntity existente = repository.findById(entity.getId())
                .orElseThrow(() -> new RuntimeException("Vacuna no encontrada"));
        
        repository.findAll().stream()
                .filter(e -> e.getNombre().equalsIgnoreCase(entity.getNombre()) && ! e.getId().equals(entity.getId()))
                .findFirst()
                .ifPresent(e -> {
                    throw new RuntimeException("Otra vacuna con ese nombre ya existe");
                });
        
        existente.setNombre(entity.getNombre());
        existente.setActivo(entity.getActivo());
        return repository.save(existente);
    }
    
    @Override
    @Transactional
    public String eliminar(Integer id) {
        if(! repository.existsById(id)) {
            throw new RuntimeException("Vacuna no encontrada");
        }
        repository.deleteById(id);
        return "Vacuna eliminada correctamente";
    }
    
    @Override
    @Transactional
    public List<VacunaEntity> listar() {
        return repository.findAll();
    }
    
    @Override
    @Transactional
    public VacunaEntity obtenerPorId(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vacuna no encontrada"));
    }
}

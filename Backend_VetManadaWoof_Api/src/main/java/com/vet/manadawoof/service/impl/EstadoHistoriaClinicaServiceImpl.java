package com.vet.manadawoof.service.impl;

import com.vet.manadawoof.entity.EstadoHistoriaClinicaEntity;
import com.vet.manadawoof.repository.EstadoHistoriaClinicaRepository;
import com.vet.manadawoof.service.EstadoHistoriaClinicaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EstadoHistoriaClinicaServiceImpl implements EstadoHistoriaClinicaService {
    
    private final EstadoHistoriaClinicaRepository repository;
    
    @Override
    @Transactional
    public EstadoHistoriaClinicaEntity crear(EstadoHistoriaClinicaEntity entity) {
        repository.findAll().stream().filter(e -> e.getNombre().equalsIgnoreCase(entity.getNombre())).findFirst().ifPresent(e -> {
            throw new RuntimeException("Estado de historia clínica ya existe");
        }); return repository.save(entity);
    }
    
    @Override
    @Transactional
    public EstadoHistoriaClinicaEntity actualizar(EstadoHistoriaClinicaEntity entity) {
        EstadoHistoriaClinicaEntity existente = repository.findById(entity.getId()).orElseThrow(() -> new RuntimeException("Estado de historia clínica no encontrado"));
        
        repository.findAll().stream().filter(e -> e.getNombre().equalsIgnoreCase(entity.getNombre()) && ! e.getId().equals(entity.getId())).findFirst().ifPresent(e -> {
            throw new RuntimeException("Otro estado de historia clínica con ese nombre ya existe");
        });
        
        existente.setNombre(entity.getNombre()); existente.setDescripcion(entity.getDescripcion());
        return repository.save(existente);
    }
    
    @Override
    @Transactional
    public String eliminar(Integer id) {
        if(! repository.existsById(id)) {
            throw new RuntimeException("Estado de historia clínica no encontrado");
        } repository.deleteById(id); return "Estado de historia clínica eliminado correctamente";
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<EstadoHistoriaClinicaEntity> listar() {
        return repository.findAll();
    }
    
    @Override
    @Transactional
    public EstadoHistoriaClinicaEntity obtenerPorId(Integer id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Estado de historia clínica no encontrado"));
    }
}

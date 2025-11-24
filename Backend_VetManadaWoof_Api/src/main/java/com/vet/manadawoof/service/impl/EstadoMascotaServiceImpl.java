package com.vet.manadawoof.service.impl;

import com.vet.manadawoof.entity.EstadoMascotaEntity;
import com.vet.manadawoof.repository.EstadoMascotaRepository;
import com.vet.manadawoof.service.EstadoMascotaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// Implementación del servicio de estados de mascota
@Service
@RequiredArgsConstructor
public class EstadoMascotaServiceImpl implements EstadoMascotaService {
    
    // Repositorio inyectado
    private final EstadoMascotaRepository repository;
    
    // Crear estado con validación de nombre único
    @Override
    @Transactional
    public EstadoMascotaEntity crear(EstadoMascotaEntity entity) {
        repository.findAll().stream().filter(e -> e.getNombre().equalsIgnoreCase(entity.getNombre())).findFirst().ifPresent(e -> {
            throw new RuntimeException("Vacuna ya existe");
        }); return repository.save(entity);
    }
    
    // Actualizar estado existente
    @Override
    @Transactional
    public EstadoMascotaEntity actualizar(EstadoMascotaEntity entity) {
        EstadoMascotaEntity existente = repository.findById(entity.getId()).orElseThrow(() -> new RuntimeException("Vacuna no encontrada"));
        
        repository.findAll().stream().filter(e -> e.getNombre().equalsIgnoreCase(entity.getNombre()) && ! e.getId().equals(entity.getId())).findFirst().ifPresent(e -> {
            throw new RuntimeException("Otra vacuna con ese nombre ya existe");
        });
        
        existente.setNombre(entity.getNombre()); existente.setActivo(entity.getActivo());
        return repository.save(existente);
    }
    
    // Eliminar estado por ID
    @Override
    @Transactional
    public String eliminar(Integer id) {
        if(! repository.existsById(id)) throw new RuntimeException("Vacuna no encontrada"); repository.deleteById(id);
        return "Vacuna eliminada correctamente";
    }
    
    // Listar estados
    @Override
    @Transactional
    public List<EstadoMascotaEntity> listar() {
        return repository.findAll();
    }
    
    // Obtener estado por ID
    @Override
    @Transactional
    public EstadoMascotaEntity obtenerPorId(Integer id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Vacuna no encontrada"));
    }
}

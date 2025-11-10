package com.vet.manadawoof.service.impl;

import com.vet.manadawoof.entity.ServicioEntity;
import com.vet.manadawoof.repository.ServicioRepository;
import com.vet.manadawoof.service.ServicioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementación del servicio para la gestión de servicios veterinarios.
 * Permite crear, actualizar, eliminar y listar los servicios registrados.
 */
@Service
@RequiredArgsConstructor
public class ServicioServiceImpl implements ServicioService {
    
    private final ServicioRepository repository;
    
    // Crea un nuevo servicio.
    @Override
    @Transactional
    public ServicioEntity crear(ServicioEntity entity) {
        repository.findAll().stream()
                .filter(e -> e.getNombre().equalsIgnoreCase(entity.getNombre()))
                .findFirst()
                .ifPresent(e -> {
                    throw new RuntimeException("Servicio ya existe");
                });
        return repository.save(entity);
    }
    
    // Actualiza los datos de un servicio existente.
    @Override
    @Transactional
    public ServicioEntity actualizar(ServicioEntity entity) {
        ServicioEntity existente = repository.findById(entity.getId())
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));
        
        repository.findAll().stream()
                .filter(e -> e.getNombre().equalsIgnoreCase(entity.getNombre()) && ! e.getId().equals(entity.getId()))
                .findFirst()
                .ifPresent(e -> {
                    throw new RuntimeException("Otro servicio con ese nombre ya existe");
                });
        
        existente.setNombre(entity.getNombre());
        existente.setDescripcion(entity.getDescripcion());
        return repository.save(existente);
    }
    
    // Elimina un servicio de la base de datos por su ID.
    @Override
    @Transactional
    public String eliminar(Integer id) {
        if(! repository.existsById(id)) {
            throw new RuntimeException("Servicio no encontrado");
        }
        repository.deleteById(id);
        return "Servicio eliminado correctamente";
    }
    
    // Lista todos los servicios registrados.
    @Override
    @Transactional
    public List<ServicioEntity> listar() {
        return repository.findAll();
    }
    
    // Obtiene un servicio específico por su ID.
    @Override
    @Transactional
    public ServicioEntity obtenerPorId(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));
    }
}

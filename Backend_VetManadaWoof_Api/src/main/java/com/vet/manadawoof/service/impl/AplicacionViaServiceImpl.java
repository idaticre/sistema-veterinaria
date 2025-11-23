package com.vet.manadawoof.service.impl;

import com.vet.manadawoof.entity.AplicacionViaEntity;
import com.vet.manadawoof.repository.AplicacionViaRepository;
import com.vet.manadawoof.service.AplicacionViaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementación del servicio para gestionar vías de aplicación.
 */
@Service
@RequiredArgsConstructor
public class AplicacionViaServiceImpl implements AplicacionViaService {
    
    // Repositorio de vías de aplicación
    private final AplicacionViaRepository repository;
    
    // Crea una nueva vía de aplicación con validación de nombre único
    @Override
    @Transactional
    public AplicacionViaEntity crear(AplicacionViaEntity entity) {
        repository.findAll().stream()
                .filter(e -> e.getNombre().equalsIgnoreCase(entity.getNombre()))
                .findFirst()
                .ifPresent(e -> {
                    throw new RuntimeException("La vía de aplicación ya existe");
                });
        return repository.save(entity);
    }
    
    // Actualiza una vía de aplicación existente con validaciones
    @Override
    @Transactional
    public AplicacionViaEntity actualizar(AplicacionViaEntity entity) {
        AplicacionViaEntity existente = repository.findById(entity.getId())
                .orElseThrow(() -> new RuntimeException("Vía de aplicación no encontrada"));
        
        repository.findAll().stream()
                .filter(e -> e.getNombre().equalsIgnoreCase(entity.getNombre()) && ! e.getId().equals(entity.getId()))
                .findFirst()
                .ifPresent(e -> {
                    throw new RuntimeException("Otra vía de aplicación con ese nombre ya existe");
                });
        
        existente.setNombre(entity.getNombre());
        existente.setActivo(entity.getActivo());
        return repository.save(existente);
    }
    
    // Elimina una vía de aplicación por su ID
    @Override
    @Transactional
    public String eliminar(Integer id) {
        if(! repository.existsById(id)) {
            throw new RuntimeException("Vía de aplicación no encontrada");
        }
        repository.deleteById(id);
        return "Vía de aplicación eliminada correctamente";
    }
    
    // Lista todas las vías de aplicación
    @Override
    @Transactional
    public List<AplicacionViaEntity> listar() {
        return repository.findAll();
    }
    
    // Obtiene una vía de aplicación por su ID
    @Override
    @Transactional
    public AplicacionViaEntity obtenerPorId(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vía de aplicación no encontrada"));
    }
}

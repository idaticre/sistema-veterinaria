package com.vet.manadawoof.service.impl;

import com.vet.manadawoof.entity.*;
import com.vet.manadawoof.repository.*;
import com.vet.manadawoof.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// Implementación del servicio para la gestión de canales de comunicación.
@Service
@RequiredArgsConstructor
public class CanalComunicacionServiceImpl implements CanalComunicacionService {
    
    private final CanalComunicacionRepository repository;
    
    // Crea un nuevo canal de comunicación.
    // Verifica que no exista otro canal con el mismo nombre antes de registrarlo.
    
    @Override
    @Transactional
    public CanalComunicacionEntity crear(CanalComunicacionEntity entity) {
        repository.findAll().stream()
                .filter(e -> e.getNombre().equalsIgnoreCase(entity.getNombre()))
                .findFirst()
                .ifPresent(e -> {
                    throw new RuntimeException("Canal de comunicación ya existe");
                });
        return repository.save(entity);
    }
    
    // Actualiza los datos de un canal de comunicación existente.
    @Override
    @Transactional
    public CanalComunicacionEntity actualizar(CanalComunicacionEntity entity) {
        CanalComunicacionEntity existente = repository.findById(entity.getId())
                .orElseThrow(() -> new RuntimeException("Canal de comunicación no encontrado"));
        
        repository.findAll().stream()
                .filter(e -> e.getNombre().equalsIgnoreCase(entity.getNombre()) && ! e.getId().equals(entity.getId()))
                .findFirst()
                .ifPresent(e -> {
                    throw new RuntimeException("Otro canal de comunicación con ese nombre ya existe");
                });
        
        existente.setNombre(entity.getNombre());
        return repository.save(existente);
    }
    
    // Elimina un canal de comunicación de la base de datos por su ID.
    @Override
    @Transactional
    public String eliminar(Integer id) {
        if(! repository.existsById(id)) {
            throw new RuntimeException("Canal de comunicación no encontrado");
        }
        repository.deleteById(id);
        return "Canal de comunicación eliminado correctamente";
    }
    
    // Lista todos los canales de comunicación registrados.
    
    @Override
    @Transactional
    public List<CanalComunicacionEntity> listar() {
        return repository.findAll();
    }
    
    // Obtiene un canal de comunicación específico por su ID.
    @Override
    @Transactional
    public CanalComunicacionEntity obtenerPorId(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Canal de comunicación no encontrado"));
    }
}

package com.vet.manadawoof.service.impl;

import com.vet.manadawoof.entity.MedioSolicitudEntity;
import com.vet.manadawoof.repository.MedioSolicitudRepository;
import com.vet.manadawoof.service.MedioSolicitudService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementación del servicio para la gestión de medios de solicitud.
 * Permite crear, actualizar, eliminar y listar los medios registrados.
 */
@Service
@RequiredArgsConstructor
public class MedioSolicitudServiceImpl implements MedioSolicitudService {
    
    private final MedioSolicitudRepository repository;
    
    // Crea un nuevo medio de solicitud.
    @Override
    @Transactional
    public MedioSolicitudEntity crear(MedioSolicitudEntity entity) {
        repository.findAll().stream()
                .filter(e -> e.getNombre().equalsIgnoreCase(entity.getNombre()))
                .findFirst()
                .ifPresent(e -> {
                    throw new RuntimeException("Medio de solicitud ya existe");
                });
        return repository.save(entity);
    }
    
    // Actualiza los datos de un medio de solicitud existente.
    @Override
    @Transactional
    public MedioSolicitudEntity actualizar(MedioSolicitudEntity entity) {
        MedioSolicitudEntity existente = repository.findById(entity.getId())
                .orElseThrow(() -> new RuntimeException("Medio de solicitud no encontrado"));
        
        repository.findAll().stream()
                .filter(e -> e.getNombre().equalsIgnoreCase(entity.getNombre()) && ! e.getId().equals(entity.getId()))
                .findFirst()
                .ifPresent(e -> {
                    throw new RuntimeException("Otro medio de solicitud con ese nombre ya existe");
                });
        
        existente.setNombre(entity.getNombre());
        existente.setDescripcion(entity.getDescripcion());
        return repository.save(existente);
    }
    
    // Elimina un medio de solicitud de la base de datos por su ID.
    @Override
    @Transactional
    public String eliminar(Integer id) {
        if(! repository.existsById(id)) {
            throw new RuntimeException("Medio de solicitud no encontrado");
        }
        repository.deleteById(id);
        return "Medio de solicitud eliminado correctamente";
    }
    
    // Lista todos los medios de solicitud registrados.
    @Override
    @Transactional
    public List<MedioSolicitudEntity> listar() {
        return repository.findAll();
    }
    
    // Obtiene un medio de solicitud específico por su ID.
    @Override
    @Transactional
    public MedioSolicitudEntity obtenerPorId(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Medio de solicitud no encontrado"));
    }
}

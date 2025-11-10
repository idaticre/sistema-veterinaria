package com.vet.manadawoof.service.impl;


import com.vet.manadawoof.entity.MedioPagoEntity;
import com.vet.manadawoof.repository.MedioPagoRepository;
import com.vet.manadawoof.service.MedioPagoService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

// Implementación del servicio para la gestión de medios de pago.
@Service
@RequiredArgsConstructor
public class MedioPagoServiceImpl implements MedioPagoService {
    
    private final MedioPagoRepository repository;
    
    // Crea un nuevo medio de pago.
    // Verifica que no exista otro medio de pago con el mismo nombre antes de registrarlo.
    
    @Override
    @Transactional
    public MedioPagoEntity crear(MedioPagoEntity entity) {
        repository.findAll().stream()
                .filter(e -> e.getNombre().equalsIgnoreCase(entity.getNombre()))
                .findFirst()
                .ifPresent(e -> {
                    throw new RuntimeException("Medio de pago ya existe");
                });
        return repository.save(entity);
    }
    
    // Actualiza los datos de un medio de pago existente.
    @Override
    @Transactional
    public MedioPagoEntity actualizar(MedioPagoEntity entity) {
        MedioPagoEntity existente = repository.findById(entity.getId())
                .orElseThrow(() -> new RuntimeException("Medio de pago no encontrado"));
        
        repository.findAll().stream()
                .filter(e -> e.getNombre().equalsIgnoreCase(entity.getNombre()) && ! e.getId().equals(entity.getId()))
                .findFirst()
                .ifPresent(e -> {
                    throw new RuntimeException("Otro medio de pago con ese nombre ya existe");
                });
        
        existente.setNombre(entity.getNombre());
        existente.setDescripcion(entity.getDescripcion());
        return repository.save(existente);
    }
    
    //  Elimina un medio de pago de la base de datos por su ID.
    @Override
    @Transactional
    public String eliminar(Integer id) {
        if(! repository.existsById(id)) {
            throw new RuntimeException("Medio de pago no encontrado");
        }
        repository.deleteById(id);
        return "Medio de pago eliminado correctamente";
    }
    
    // Lista todos los medios de pago registrados.
    @Override
    @Transactional
    public List<MedioPagoEntity> listar() {
        return repository.findAll();
    }
    
    // Obtiene un medio de pago específico por su ID.
    @Override
    @Transactional
    public MedioPagoEntity obtenerPorId(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Medio de pago no encontrado"));
    }
}

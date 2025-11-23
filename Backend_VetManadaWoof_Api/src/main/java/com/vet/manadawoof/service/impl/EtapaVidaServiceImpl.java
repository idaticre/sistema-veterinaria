package com.vet.manadawoof.service.impl;

import com.vet.manadawoof.entity.EtapaVidaEntity;
import com.vet.manadawoof.repository.EtapaVidaRepository;
import com.vet.manadawoof.service.EtapaVidaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EtapaVidaServiceImpl implements EtapaVidaService {
    
    private final EtapaVidaRepository repository; // Repositorio JPA para etapas de vida
    
    // Crear nueva etapa, validando duplicados por descripción
    @Override
    @Transactional
    public EtapaVidaEntity crear(EtapaVidaEntity entity) {
        repository.findAll().stream()
                .filter(e -> e.getDescripcion().equalsIgnoreCase(entity.getDescripcion()))
                .findFirst()
                .ifPresent(e -> {
                    throw new RuntimeException("Etapa de vida ya existe");
                });
        return repository.save(entity);
    }
    
    // Actualizar etapa existente, validando duplicados y existencia
    @Override
    @Transactional
    public EtapaVidaEntity actualizar(EtapaVidaEntity entity) {
        EtapaVidaEntity existente = repository.findById(entity.getId())
                .orElseThrow(() -> new RuntimeException("Etapa no encontrada"));
        
        repository.findAll().stream()
                .filter(e -> e.getDescripcion().equalsIgnoreCase(entity.getDescripcion()) && ! e.getId().equals(entity.getId()))
                .findFirst()
                .ifPresent(e -> {
                    throw new RuntimeException("Otra etapa con ese nombre ya existe");
                });
        
        existente.setDescripcion(entity.getDescripcion());
        existente.setActivo(entity.getActivo());
        return repository.save(existente);
    }
    
    // Eliminar etapa por ID, con validación previa
    @Override
    @Transactional
    public String eliminar(Integer id) {
        if(! repository.existsById(id)) {
            throw new RuntimeException("Etapa no encontrada");
        }
        repository.deleteById(id);
        return "Etapa eliminada correctamente";
    }
    
    // Listar todas las etapas registradas
    @Override
    @Transactional
    public List<EtapaVidaEntity> listar() {
        return repository.findAll();
    }
    
    // Obtener una etapa por su ID
    @Override
    @Transactional
    public EtapaVidaEntity obtenerPorId(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Etapa no encontrada"));
    }
}

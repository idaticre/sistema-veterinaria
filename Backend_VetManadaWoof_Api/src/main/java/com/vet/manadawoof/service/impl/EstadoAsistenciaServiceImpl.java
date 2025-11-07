package com.vet.manadawoof.service.impl;

import com.vet.manadawoof.entity.EstadoAsistenciaEntity;
import com.vet.manadawoof.repository.EstadoAsistenciaRepository;
import com.vet.manadawoof.service.EstadoAsistenciaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// Implementación del servicio de estados de asistencia

@Service
@RequiredArgsConstructor
public class EstadoAsistenciaServiceImpl implements EstadoAsistenciaService {
    // Repositorio inyectado
    private final EstadoAsistenciaRepository repository;
    
    // Crear estado con validación de nombre único
    @Override
    @Transactional
    public EstadoAsistenciaEntity crear(EstadoAsistenciaEntity entity) {
        repository.findAll().stream().filter(e -> e.getNombre().equalsIgnoreCase(entity.getNombre())).findFirst().ifPresent(e -> {
            throw new RuntimeException("Estado ya existe");
        }); return repository.save(entity);
    }
    
    // Actualizar estado existente
    @Override
    @Transactional
    public EstadoAsistenciaEntity actualizar(EstadoAsistenciaEntity entity) {
        EstadoAsistenciaEntity existente = repository.findById(entity.getId()).orElseThrow(() -> new RuntimeException("Estado no encontrado"));
        
        repository.findAll().stream().filter(e -> e.getNombre().equalsIgnoreCase(entity.getNombre()) && ! e.getId().equals(entity.getId())).findFirst().ifPresent(e -> {
            throw new RuntimeException("Otro estado con ese nombre ya existe");
        });
        
        existente.setNombre(entity.getNombre());
        return repository.save(existente);
    }
    
    // Eliminar estado por ID
    @Override
    @Transactional
    public String eliminar(Integer id) {
        if(! repository.existsById(id)) throw new RuntimeException("Estado no encontrado"); repository.deleteById(id);
        return "Estado eliminado correctamente";
    }
    
    // Listar estados
    @Override
    @Transactional(readOnly = true)
    public List<EstadoAsistenciaEntity> listar() {
        return repository.findAll();
    }
    
    // Obtener estado por ID
    @Override
    @Transactional(readOnly = true)
    public EstadoAsistenciaEntity obtenerPorId(Integer id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Estado no encontrado"));
    }
}

package com.vet.manadawoof.service.impl;

import com.vet.manadawoof.entity.MedicamentoEntity;
import com.vet.manadawoof.repository.MedicamentoRepository;
import com.vet.manadawoof.service.MedicamentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// Implementación del servicio de medicamentos
@Service
@RequiredArgsConstructor
public class MedicamentoServiceImpl implements MedicamentoService {
    
    // Repositorio inyectado
    private final MedicamentoRepository repository;
    
    // Crear medicamento con validación de nombre único
    @Override
    @Transactional
    public MedicamentoEntity crear(MedicamentoEntity entity) {
        repository.findAll().stream()
                .filter(e -> e.getNombre().equalsIgnoreCase(entity.getNombre()))
                .findFirst()
                .ifPresent(e -> {
                    throw new RuntimeException("Vacuna ya existe");
                });
        return repository.save(entity);
    }
    
    // Actualizar medicamento existente
    @Override
    @Transactional
    public MedicamentoEntity actualizar(MedicamentoEntity entity) {
        MedicamentoEntity existente = repository.findById(entity.getId())
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
    
    // Eliminar medicamento por ID
    @Override
    @Transactional
    public String eliminar(Integer id) {
        if(! repository.existsById(id)) throw new RuntimeException("Vacuna no encontrada");
        repository.deleteById(id);
        return "Vacuna eliminada correctamente";
    }
    
    // Listar medicamentos
    @Override
    @Transactional
    public List<MedicamentoEntity> listar() {
        return repository.findAll();
    }
    
    // Obtener medicamento por ID
    @Override
    @Transactional
    public MedicamentoEntity obtenerPorId(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vacuna no encontrada"));
    }
}

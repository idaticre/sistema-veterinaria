package com.vet.manadawoof.service.impl;

import com.vet.manadawoof.entity.TipoMedicamentoEntity;
import com.vet.manadawoof.repository.TipoMedicamentoRepository;
import com.vet.manadawoof.service.TipoMedicamentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementación del servicio para gestionar tipos de medicamentos.
 */
@Service
@RequiredArgsConstructor
public class TipoMedicamentoServiceImpl implements TipoMedicamentoService {
    
    // Inyección del repositorio de tipos de medicamentos
    private final TipoMedicamentoRepository repository;
    
    // Crea un nuevo tipo de medicamento con validación de nombre único
    @Override
    @Transactional
    public TipoMedicamentoEntity crear(TipoMedicamentoEntity entity) {
        repository.findAll().stream().filter(e -> e.getNombre().equalsIgnoreCase(entity.getNombre())).findFirst().ifPresent(e -> {
            throw new RuntimeException("Tipo de medicamento ya existe");
        }); return repository.save(entity);
    }
    
    // Actualiza un tipo de medicamento existente con validaciones
    @Override
    @Transactional
    public TipoMedicamentoEntity actualizar(TipoMedicamentoEntity entity) {
        TipoMedicamentoEntity existente = repository.findById(entity.getId()).orElseThrow(() -> new RuntimeException("Tipo de medicamento no encontrado"));
        
        repository.findAll().stream().filter(e -> e.getNombre().equalsIgnoreCase(entity.getNombre()) && ! e.getId().equals(entity.getId())).findFirst().ifPresent(e -> {
            throw new RuntimeException("Otro tipo de medicamento con ese nombre ya existe");
        });
        
        existente.setNombre(entity.getNombre()); existente.setActivo(entity.getActivo());
        return repository.save(existente);
    }
    
    // Elimina un tipo de medicamento por su ID
    @Override
    @Transactional
    public String eliminar(Integer id) {
        if(! repository.existsById(id)) {
            throw new RuntimeException("Tipo de medicamento no encontrado");
        } repository.deleteById(id); return "Tipo de medicamento eliminado correctamente";
    }
    
    // Lista todos los tipos de medicamentos
    @Override
    @Transactional
    public List<TipoMedicamentoEntity> listar() {
        return repository.findAll();
    }
    
    // Obtiene un tipo de medicamento por su ID
    @Override
    @Transactional
    public TipoMedicamentoEntity obtenerPorId(Integer id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Tipo de medicamento no encontrado"));
    }
}

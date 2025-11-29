package com.vet.manadawoof.service.impl;

import com.vet.manadawoof.entity.TipoArchivoClinicoEntity;
import com.vet.manadawoof.repository.TipoArchivoClinicoRepository;
import com.vet.manadawoof.service.TipoArchivoClinicoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TipoArchivoClinicoServiceImpl implements TipoArchivoClinicoService {
    
    private final TipoArchivoClinicoRepository repository;
    
    @Override
    @Transactional
    public TipoArchivoClinicoEntity crear(TipoArchivoClinicoEntity entity) {
        repository.findAll().stream().filter(e -> e.getNombre().equalsIgnoreCase(entity.getNombre())).findFirst().ifPresent(e -> {
            throw new RuntimeException("Tipo de archivo clínico ya existe");
        }); return repository.save(entity);
    }
    
    @Override
    @Transactional
    public TipoArchivoClinicoEntity actualizar(TipoArchivoClinicoEntity entity) {
        TipoArchivoClinicoEntity existente = repository.findById(entity.getId()).orElseThrow(() -> new RuntimeException("Tipo de archivo clínico no encontrado"));
        
        repository.findAll().stream().filter(e -> e.getNombre().equalsIgnoreCase(entity.getNombre()) && ! e.getId().equals(entity.getId())).findFirst().ifPresent(e -> {
            throw new RuntimeException("Otro tipo de archivo clínico con ese nombre ya existe");
        });
        
        existente.setNombre(entity.getNombre()); existente.setDescripcion(entity.getDescripcion());
        return repository.save(existente);
    }
    
    @Override
    @Transactional
    public String eliminar(Integer id) {
        if(! repository.existsById(id)) {
            throw new RuntimeException("Tipo de archivo clínico no encontrado");
        } repository.deleteById(id); return "Tipo de archivo clínico eliminado correctamente";
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TipoArchivoClinicoEntity> listar() {
        return repository.findAll();
    }
    
    @Override
    @Transactional
    public TipoArchivoClinicoEntity obtenerPorId(Integer id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Tipo de archivo clínico no encontrado"));
    }
}

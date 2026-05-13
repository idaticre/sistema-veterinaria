package com.vet.manadawoof.service.impl;

import com.vet.manadawoof.entity.TipoIGVEntity;
import com.vet.manadawoof.repository.TipoIGVRepository;
import com.vet.manadawoof.service.TipoIGVService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TipoIGVServiceImpl implements TipoIGVService{
    
    private final TipoIGVRepository repository;

    @Override
    @Transactional
    public List<TipoIGVEntity> Listar(){
        return repository.findAll();
    }
    
    @Override
    @Transactional
    public TipoIGVEntity ObtenerPorId(Integer id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Tipo de IGV no encontrado"));
    }
    
    @Override
    @Transactional
    public TipoIGVEntity Añadir(TipoIGVEntity entity) {
        repository.findAll().stream().filter(
                e -> e.getDescripcion().equalsIgnoreCase(entity.getDescripcion())).findFirst().ifPresent(
                e -> {
                    throw new RuntimeException("Tipo de IGV ya existe");
                });
        return repository.save(entity);
    }
    
    @Override
    @Transactional
    public void EliminarTIGV(Integer id) {
        TipoIGVEntity existente = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tipo de IGV no encontrado con id " + id));
        repository.delete(existente);
    }
    
    @Override
    @Transactional
    public TipoIGVEntity Actualizar(Integer id, TipoIGVEntity tipoDocumento) {
        TipoIGVEntity existente = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tipo de IGV no encontrado con id " + id));
        
        existente.setDescripcion(tipoDocumento.getDescripcion());
        
        return repository.save(existente);
    }
}


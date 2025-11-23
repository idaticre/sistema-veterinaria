package com.vet.manadawoof.service.impl;

import com.vet.manadawoof.entity.TipoPersonaJuridicaEntity;
import com.vet.manadawoof.repository.TipoPersonaJuridicaRepository;
import com.vet.manadawoof.service.TipoPersonaJuridicaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TipoPersonaJuridicaServiceImpl implements TipoPersonaJuridicaService {
    
    private final TipoPersonaJuridicaRepository repository;
    
    @Override
    @Transactional
    public TipoPersonaJuridicaEntity crearTipoPersonaJuridica(TipoPersonaJuridicaEntity tipo) {
        return repository.save(tipo);
    }
    
    @Override
    @Transactional
    public TipoPersonaJuridicaEntity actualizarTipoPersonaJuridica(Integer id, TipoPersonaJuridicaEntity tipo) {
        TipoPersonaJuridicaEntity existente = repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Tipo persona jurídica no encontrado con id " + id));
        
        existente.setNombre(tipo.getNombre());
        existente.setDescripcion(tipo.getDescripcion());
        existente.setActivo(tipo.getActivo());
        
        return repository.save(existente);
    }
    
    @Override
    @Transactional
    public void eliminarTipoPersonaJuridica(Integer id) {
        TipoPersonaJuridicaEntity existente = repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Tipo persona jurídica no encontrado con id " + id));
        repository.delete(existente);
    }
    
    @Override
    @Transactional
    public List<TipoPersonaJuridicaEntity> listarTiposPersonaJuridica() {
        return repository.findAll();
    }
    
    @Override
    @Transactional
    public TipoPersonaJuridicaEntity obtenerTipoPersonaJuridicaPorId(Integer id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Tipo persona jurídica no encontrado con id " + id));
    }
}

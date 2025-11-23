package com.vet.manadawoof.service.impl;

import com.vet.manadawoof.entity.TipoDocumentoEntity;
import com.vet.manadawoof.repository.TipoDocumentoRepository;
import com.vet.manadawoof.service.TipoDocumentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TipoDocumentoServiceImpl implements TipoDocumentoService {
    
    private final TipoDocumentoRepository repository;
    
    @Override
    @Transactional
    public TipoDocumentoEntity crearTdoc(TipoDocumentoEntity tipoDocumento) {
        return repository.save(tipoDocumento);
    }
    
    @Override
    @Transactional
    public TipoDocumentoEntity actualizarTdoc(Integer id, TipoDocumentoEntity tipoDocumento) {
        TipoDocumentoEntity existente = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tipo de documento no encontrado con id " + id));
        
        existente.setDescripcion(tipoDocumento.getDescripcion());
        existente.setActivo(tipoDocumento.getActivo());
        
        return repository.save(existente);
    }
    
    @Override
    @Transactional
    public void eliminarTdoc(Integer id) {
        TipoDocumentoEntity existente = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tipo de documento no encontrado con id " + id));
        repository.delete(existente);
    }
    
    @Override
    @Transactional
    public List<TipoDocumentoEntity> listarTdoc() {
        return repository.findAll();
    }
    
    @Override
    @Transactional
    public TipoDocumentoEntity obtenerTdocPorId(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tipo de documento no encontrado con id " + id));
    }
}

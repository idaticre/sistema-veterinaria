package com.vet.manadawoof.service;

import com.vet.manadawoof.entity.TipoDocumentoEntity;
import com.vet.manadawoof.repository.TipoDocumentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TipoDocumentoServiceImpl implements TipoDocumentoService {

    private final TipoDocumentoRepository tipoDocumentoRepository;

    @Override
    public List<TipoDocumentoEntity> listarTiposDocumento() {
        tipoDocumentoRepository.spTipoDocumento("READ", null, null, null);
        return tipoDocumentoRepository.findAll();
    }

    @Override
    public TipoDocumentoEntity obtenerTipoDocumento(Long id) {
        tipoDocumentoRepository.spTipoDocumento("READ", id, null, null);
        return tipoDocumentoRepository.findById(id).orElse(null);
    }

    @Override
    public String guardarTipoDocumento(TipoDocumentoEntity tipoDocumento) {
        String accion = (tipoDocumento.getId() == null) ? "CREATE" : "UPDATE";
        return tipoDocumentoRepository.spTipoDocumento(accion, tipoDocumento.getId(), tipoDocumento.getNombre(), tipoDocumento.getActivo());
    }

    @Override
    public String eliminarTipoDocumento(Long id) {
        return tipoDocumentoRepository.spTipoDocumento("DELETE", id, null, null);
    }
}

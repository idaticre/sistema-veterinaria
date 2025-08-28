package com.vet.manadawoof.service;

import com.vet.manadawoof.entity.TipoDocumentoEntity;

import java.util.List;

public interface TipoDocumentoService {
    List<TipoDocumentoEntity> listarTiposDocumento();

    TipoDocumentoEntity obtenerTipoDocumento(Long id);

    String guardarTipoDocumento(TipoDocumentoEntity tipoDocumento);

    String eliminarTipoDocumento(Long id);
}

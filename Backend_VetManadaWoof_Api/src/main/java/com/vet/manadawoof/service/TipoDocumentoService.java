package com.vet.manadawoof.service;

import com.vet.manadawoof.entity.TipoDocumentoEntity;

import java.util.List;

public interface TipoDocumentoService {

    TipoDocumentoEntity crearTdoc(TipoDocumentoEntity tipoDocumento);

    TipoDocumentoEntity actualizarTdoc(Integer id, TipoDocumentoEntity tipoDocumento);

    void eliminarTdoc(Integer id);

    List<TipoDocumentoEntity> listarTdoc();

    TipoDocumentoEntity obtenerTdocPorId(Integer id);
}

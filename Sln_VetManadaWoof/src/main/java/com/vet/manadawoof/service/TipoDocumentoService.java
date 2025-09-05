package com.vet.manadawoof.service;

import com.vet.manadawoof.entity.TipoDocumentoEntity;
import java.util.List;

public interface TipoDocumentoService {

    String crearTipoDocumento(TipoDocumentoEntity tipoDocumento);

    String actualizarTipoDocumento(TipoDocumentoEntity tipoDocumento);

    String eliminarTipoDocumento(Integer id);

    List<TipoDocumentoEntity> listarTiposDocumento();
}

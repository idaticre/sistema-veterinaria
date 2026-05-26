package com.vet.manadawoof.service;

import com.vet.manadawoof.dtos.response.TipoIGVResponseDTO;
import com.vet.manadawoof.dtos.response.TipoMonedaResponseDTO;
import com.vet.manadawoof.dtos.response.TiposComprobantesResponseDTO;
import com.vet.manadawoof.dtos.response.TiposPercepcionesResponseDTO;
import java.util.List;

public interface CatalogoService {
    List<TipoMonedaResponseDTO> listarMonedas();
    List<TiposComprobantesResponseDTO> listarTiposComprobantes();
    List<TiposPercepcionesResponseDTO> listarTiposPercepciones();
    List<TipoIGVResponseDTO> listarTiposIgv();
}

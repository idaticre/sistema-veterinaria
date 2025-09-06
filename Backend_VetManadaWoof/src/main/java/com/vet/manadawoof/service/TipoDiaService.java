package com.vet.manadawoof.service;

import com.vet.manadawoof.dtos.response.TipoDiaResponseDTO;
import com.vet.manadawoof.entity.TipoDiaEntity;

import java.util.List;

public interface TipoDiaService {

    TipoDiaResponseDTO crearTipoDia(TipoDiaEntity tipoDia);

    TipoDiaResponseDTO actualizarTipoDia(TipoDiaEntity tipoDia);

    TipoDiaResponseDTO eliminarTipoDia(Integer id);

    List<TipoDiaResponseDTO> listarTiposDia();
}

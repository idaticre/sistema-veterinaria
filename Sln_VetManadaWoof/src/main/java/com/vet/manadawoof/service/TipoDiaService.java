package com.vet.manadawoof.service;

import com.vet.manadawoof.entity.TipoDiaEntity;

import java.util.List;

public interface TipoDiaService {

    String crearTipoDia(TipoDiaEntity tipoDia);

    String actualizarTipoDia(TipoDiaEntity tipoDia);

    String eliminarTipoDia(Long id);

    List<TipoDiaEntity> listarTiposDia();
}

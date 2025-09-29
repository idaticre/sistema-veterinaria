package com.vet.manadawoof.service;

import com.vet.manadawoof.entity.TipoDiaEntity;

import java.util.List;

public interface TipoDiaService {

    TipoDiaEntity crearTipoDia(TipoDiaEntity entity);

    TipoDiaEntity actualizarTipoDia(TipoDiaEntity entity);

    String eliminarTipoDia(Integer id);

    List<TipoDiaEntity> listarTiposDia();

    TipoDiaEntity obtenerPorId(Integer id);
}

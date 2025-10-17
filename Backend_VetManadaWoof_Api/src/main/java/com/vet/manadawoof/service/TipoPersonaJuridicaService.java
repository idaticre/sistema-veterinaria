package com.vet.manadawoof.service;

import com.vet.manadawoof.entity.TipoPersonaJuridicaEntity;
import java.util.List;

public interface TipoPersonaJuridicaService {

    TipoPersonaJuridicaEntity crearTipoPersonaJuridica(TipoPersonaJuridicaEntity tipo);

    TipoPersonaJuridicaEntity actualizarTipoPersonaJuridica(Integer id, TipoPersonaJuridicaEntity tipo);

    void eliminarTipoPersonaJuridica(Integer id);

    List<TipoPersonaJuridicaEntity> listarTiposPersonaJuridica();

    TipoPersonaJuridicaEntity obtenerTipoPersonaJuridicaPorId(Integer id);
}

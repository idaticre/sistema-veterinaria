package com.vet.manadawoof.service;

import com.vet.manadawoof.entity.TipoPersonaJuridicaEntity;
import java.util.List;

public interface TipoPersonaJuridicaService {

    List<TipoPersonaJuridicaEntity> listarTiposPersonaJuridica();

    String registrarTipoPersonaJuridica(TipoPersonaJuridicaEntity entity);

    String actualizarTipoPersonaJuridica(TipoPersonaJuridicaEntity entity);

    String eliminarTipoPersonaJuridica(Integer id);
}

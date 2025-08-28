package com.vet.manadawoof.service;

import com.vet.manadawoof.entity.TipoPersonaJuridicaEntity;
import com.vet.manadawoof.repository.TipoPersonaJuridicaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TipoPersonaJuridicaServiceImpl implements TipoPersonaJuridicaService {

    private final TipoPersonaJuridicaRepository repository;

    @Override
    public List<TipoPersonaJuridicaEntity> listarTiposPersonaJuridica() {
        return repository.findAll();
    }

    @Override
    public String registrarTipoPersonaJuridica(TipoPersonaJuridicaEntity entity) {
        return repository.spTipoPersonaJuridica(
                "CREATE", null, entity.getNombre(), entity.getDescripcion(), entity.getActivo());
    }

    @Override
    public String actualizarTipoPersonaJuridica(TipoPersonaJuridicaEntity entity) {
        return repository.spTipoPersonaJuridica(
                "UPDATE", entity.getId(), entity.getNombre(), entity.getDescripcion(), entity.getActivo());
    }

    @Override
    public String eliminarTipoPersonaJuridica(Long id) {
        return repository.spTipoPersonaJuridica(
                "DELETE", id, null, null, null);
    }
}

package com.vet.manadawoof.service;

import com.vet.manadawoof.entity.TipoDiaEntity;
import com.vet.manadawoof.repository.TipoDiaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TipoDiaServiceImpl implements TipoDiaService {

    private final TipoDiaRepository repository;

    @Override
    public String crearTipoDia(TipoDiaEntity tipoDia) {
        return repository.spTiposDia("CREATE", null, tipoDia.getNombre(), tipoDia.getActivo());
    }

    @Override
    public String actualizarTipoDia(TipoDiaEntity tipoDia) {
        return repository.spTiposDia("UPDATE", tipoDia.getId(), tipoDia.getNombre(), tipoDia.getActivo());
    }

    @Override
    public String eliminarTipoDia(Long id) {
        return repository.spTiposDia("DELETE", id, null, null);
    }

    @Override
    public List<TipoDiaEntity> listarTiposDia() {
        repository.spTiposDia("READ", null, null, null);
        // Retornamos findAll como referencia
        return repository.findAll();
    }
}

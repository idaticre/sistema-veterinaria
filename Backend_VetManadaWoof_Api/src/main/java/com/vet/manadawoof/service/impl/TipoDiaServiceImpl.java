package com.vet.manadawoof.service.impl;

import com.vet.manadawoof.entity.TipoDiaEntity;
import com.vet.manadawoof.repository.TipoDiaRepository;
import com.vet.manadawoof.service.TipoDiaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TipoDiaServiceImpl implements TipoDiaService {

    private final TipoDiaRepository repository;

    @Override
    @Transactional
    public TipoDiaEntity crearTipoDia(TipoDiaEntity entity) {
        repository.findAll().stream()
                .filter(d -> d.getNombre().equalsIgnoreCase(entity.getNombre()))
                .findAny()
                .ifPresent(d -> { throw new RuntimeException("Tipo de día ya existe"); });
        return repository.save(entity);
    }

    @Override
    @Transactional
    public TipoDiaEntity actualizarTipoDia(TipoDiaEntity entity) {
        TipoDiaEntity existente = repository.findById(entity.getId())
                .orElseThrow(() -> new RuntimeException("Tipo de día no encontrado"));

        repository.findAll().stream()
                .filter(d -> d.getNombre().equalsIgnoreCase(entity.getNombre()) && !d.getId().equals(entity.getId()))
                .findAny()
                .ifPresent(d -> { throw new RuntimeException("Otro tipo de día con ese nombre ya existe"); });

        existente.setNombre(entity.getNombre());
        existente.setActivo(entity.getActivo());
        return repository.save(existente);
    }

    @Override
    @Transactional
    public String eliminarTipoDia(Integer id) {
        TipoDiaEntity tipo = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tipo de día no encontrado"));
        repository.delete(tipo);
        return "Tipo de día eliminado correctamente";
    }

    @Override
    @Transactional(readOnly = true)
    public List<TipoDiaEntity> listarTiposDia() {
        return repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public TipoDiaEntity obtenerPorId(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tipo de día no encontrado"));
    }
}

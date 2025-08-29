package com.vet.manadawoof.service;

import com.vet.manadawoof.entity.DiaEntity;
import com.vet.manadawoof.repository.DiaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DiaServiceImpl implements DiaService {

    private final DiaRepository repository;

    @Override
    public String crearDia(DiaEntity dia) {
        return repository.spDiasSemana("CREATE", null, dia.getNombre(), dia.getActivo());
    }

    @Override
    public String actualizarDia(DiaEntity dia) {
        return repository.spDiasSemana("UPDATE", dia.getId(), dia.getNombre(), dia.getActivo());
    }

    @Override
    public String eliminarDia(Long id) {
        return repository.spDiasSemana("DELETE", id, null, null);
    }

    @Override
    public List<DiaEntity> listarDias() {
        repository.spDiasSemana("READ", null, null, null);
        // Retornamos findAll solo como referencia
        return repository.findAll();
    }
}

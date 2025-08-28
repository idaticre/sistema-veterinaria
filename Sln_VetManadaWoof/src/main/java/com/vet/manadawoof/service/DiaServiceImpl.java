package com.vet.manadawoof.service;

import com.vet.manadawoof.entity.DiaEntity;
import com.vet.manadawoof.repository.DiaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DiaServiceImpl implements DiaService {

    private final DiaRepository diaRepository;

    @Override
    public String spDiasSemana(String accion, Long id, String nombre, Boolean activo) {
        return diaRepository.spDiasSemana(accion, id, nombre, activo);
    }

    @Override
    public List<DiaEntity> findAll() {
        return diaRepository.findAll();
    }

    @Override
    public DiaEntity findById(Long id) {
        return diaRepository.findById(id).orElse(null);
    }
}

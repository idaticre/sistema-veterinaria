package com.vet.manadawoof.service.impl;

import com.vet.manadawoof.entity.SalaEntity;
import com.vet.manadawoof.repository.SalaRepository;
import com.vet.manadawoof.service.SalaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SalaServiceImpl implements SalaService {

    private final SalaRepository salaRepository;

    @Override
    public List<SalaEntity> listar() {
        return salaRepository.findAll();
    }

    @Override
    public List<SalaEntity> buscarSalasDisponibles(LocalDate fecha, LocalTime hora) {
        return salaRepository.buscarSalasDisponibles(fecha, hora);
    }
}
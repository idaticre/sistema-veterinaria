package com.vet.manadawoof.service;

import com.vet.manadawoof.entity.HorarioTrabajoEntity;
import com.vet.manadawoof.repository.HorarioTrabajoRepository;
import com.vet.manadawoof.service.HorarioTrabajoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HorarioTrabajoServiceImpl implements HorarioTrabajoService {

    private final HorarioTrabajoRepository repository;

    @Override
    public String ejecutarSP(HorarioTrabajoEntity horario, String accion) {
        return repository.spHorariosTrabajo(
                accion,
                horario.getId(),
                horario.getColaborador().getId(),
                horario.getDia().getId(),
                horario.getTipoDia().getId(),
                horario.getHoraInicio(),
                horario.getHoraFin(),
                true
        );
    }

    @Override
    public HorarioTrabajoEntity findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public List<HorarioTrabajoEntity> findAll() {
        return repository.findAll();
    }
}

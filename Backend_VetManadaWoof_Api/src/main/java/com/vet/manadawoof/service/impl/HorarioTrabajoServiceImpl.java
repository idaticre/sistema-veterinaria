package com.vet.manadawoof.service.impl;

import com.vet.manadawoof.entity.HorarioTrabajoEntity;
import com.vet.manadawoof.repository.HorarioTrabajoRepository;
import com.vet.manadawoof.service.HorarioTrabajoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HorarioTrabajoServiceImpl implements HorarioTrabajoService {

    private final HorarioTrabajoRepository repository;

    @Override
    @Transactional
    public HorarioTrabajoEntity crearHorario(HorarioTrabajoEntity entity) {
        return repository.save(entity);
    }

    @Override
    @Transactional
    public HorarioTrabajoEntity actualizarHorario(HorarioTrabajoEntity entity) {
        HorarioTrabajoEntity existente = repository.findById(entity.getId())
                .orElseThrow(() -> new RuntimeException("Horario no encontrado"));

        existente.setCodigo(entity.getCodigo());
        existente.setHoraInicio(entity.getHoraInicio());
        existente.setHoraFin(entity.getHoraFin());
        existente.setColaborador(entity.getColaborador());
        existente.setDia(entity.getDia());
        existente.setTipoDia(entity.getTipoDia());

        return repository.save(existente);
    }

    @Override
    @Transactional
    public String eliminarHorario(Integer id) {
        HorarioTrabajoEntity entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Horario no encontrado"));
        repository.delete(entity);
        return "Horario eliminado correctamente";
    }

    @Override
    @Transactional(readOnly = true)
    public List<HorarioTrabajoEntity> listarHorarios() {
        return repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public HorarioTrabajoEntity obtenerPorId(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Horario no encontrado"));
    }
}

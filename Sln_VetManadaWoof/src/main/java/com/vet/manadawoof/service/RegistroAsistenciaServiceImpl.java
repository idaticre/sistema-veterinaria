package com.vet.manadawoof.service;

import com.vet.manadawoof.entity.RegistroAsistenciaEntity;
import com.vet.manadawoof.repository.RegistroAsistenciaRepository;
import com.vet.manadawoof.service.RegistroAsistenciaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RegistroAsistenciaServiceImpl implements RegistroAsistenciaService {

    private final RegistroAsistenciaRepository repository;

    @Override
    public String ejecutarSP(RegistroAsistenciaEntity registro, String accion) {
        return repository.spRegistroAsistencia(
                accion,
                registro.getId(),
                registro.getColaborador().getId(),
                registro.getFechaHoraEntrada().toString(),
                registro.getFechaHoraSalida() != null ? registro.getFechaHoraSalida().toString() : null,
                registro.getActivo()
        );
    }

    @Override
    public RegistroAsistenciaEntity findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public List<RegistroAsistenciaEntity> findAll() {
        return repository.findAll();
    }
}

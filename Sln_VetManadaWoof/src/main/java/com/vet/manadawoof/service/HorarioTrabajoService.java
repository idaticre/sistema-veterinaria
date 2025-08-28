package com.vet.manadawoof.service;

import com.vet.manadawoof.entity.HorarioTrabajoEntity;

import java.util.List;

public interface HorarioTrabajoService {
    String ejecutarSP(HorarioTrabajoEntity horario, String accion);

    HorarioTrabajoEntity findById(Long id);

    List<HorarioTrabajoEntity> findAll();
}

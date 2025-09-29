package com.vet.manadawoof.service;

import com.vet.manadawoof.entity.HorarioTrabajoEntity;

import java.util.List;

public interface HorarioTrabajoService {

    HorarioTrabajoEntity crearHorario(HorarioTrabajoEntity entity);

    HorarioTrabajoEntity actualizarHorario(HorarioTrabajoEntity entity);

    String eliminarHorario(Integer id);

    List<HorarioTrabajoEntity> listarHorarios();

    HorarioTrabajoEntity obtenerPorId(Integer id);
}

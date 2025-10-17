package com.vet.manadawoof.service;

import com.vet.manadawoof.entity.HorarioTrabajoEntity;

import java.util.List;


// Servicio para manejar los horarios de trabajo.
public interface HorarioTrabajoService {
    
    // Crea un nuevo horario.
    HorarioTrabajoEntity crearHorario(HorarioTrabajoEntity entity);
    
    // Actualiza un horario existente.
    HorarioTrabajoEntity actualizarHorario(HorarioTrabajoEntity entity);
    
    // Elimina un horario por su ID.
    String eliminarHorario(Integer id);
    
    // Lista todos los horarios
    List<HorarioTrabajoEntity> listarHorarios();
    
    // Busca un horario por su ID
    HorarioTrabajoEntity obtenerPorId(Integer id);
}

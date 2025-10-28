package com.vet.manadawoof.service;

import com.vet.manadawoof.entity.EspecialidadEntity;

import java.util.List;

// Servicio para gestionar las especialidades de los veterinarios

public interface EspecialidadService {
    
    // Crea una nueva especialidad en la base de datos
    EspecialidadEntity crearEspecialidad(EspecialidadEntity entity);
    
    // Actualiza los datos de una especialidad existente.
    EspecialidadEntity actualizarEspecialidad(EspecialidadEntity entity);
    
    // Elimina una especialidad
    String eliminarEspecialidad(Integer id);
    
    // Lista todas las especialidades registradas.
    List<EspecialidadEntity> listarEspecialidades();
    
    // Obtiene una especialidad específica por su ID.
    EspecialidadEntity obtenerPorId(Integer id);
}

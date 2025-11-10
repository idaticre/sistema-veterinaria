package com.vet.manadawoof.service;

import com.vet.manadawoof.entity.EstadoAgendaEntity;

import java.util.List;

public interface EstadoAgendaService {
    // Crea un nuevo estado de agenda en la base de datos
    EstadoAgendaEntity crear(EstadoAgendaEntity entity);
    
    // Actualiza los datos de un estado de agenda existente
    EstadoAgendaEntity actualizar(EstadoAgendaEntity entity);
    
    // Elimina un estado de agenda
    String eliminar(Integer id);
    
    // Lista todos los estados de agenda registrados
    List<EstadoAgendaEntity> listar();
    
    // Obtiene un estado de agenda específico por su ID
    EstadoAgendaEntity obtenerPorId(Integer id);
}

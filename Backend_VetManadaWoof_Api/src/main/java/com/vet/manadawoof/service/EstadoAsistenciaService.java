package com.vet.manadawoof.service;

import com.vet.manadawoof.entity.EstadoAsistenciaEntity;

import java.util.List;

public interface EstadoAsistenciaService {
    // Crear estado
    EstadoAsistenciaEntity crear(EstadoAsistenciaEntity entity);
    
    // Actualizar estado
    EstadoAsistenciaEntity actualizar(EstadoAsistenciaEntity entity);
    
    // Eliminar estado
    String eliminar(Integer id);
    
    // Listar estados
    List<EstadoAsistenciaEntity> listar();
    
    // Obtener estado por ID
    EstadoAsistenciaEntity obtenerPorId(Integer id);
    
}

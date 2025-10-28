package com.vet.manadawoof.service;

import com.vet.manadawoof.entity.EstadoMascotaEntity;

import java.util.List;

// Servicio para estados de mascota
public interface EstadoMascotaService {
    
    // Crear estado
    EstadoMascotaEntity crear(EstadoMascotaEntity entity);
    
    // Actualizar estado
    EstadoMascotaEntity actualizar(EstadoMascotaEntity entity);
    
    // Eliminar estado
    String eliminar(Integer id);
    
    // Listar estados
    List<EstadoMascotaEntity> listar();
    
    // Obtener estado por ID
    EstadoMascotaEntity obtenerPorId(Integer id);
}

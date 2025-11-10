package com.vet.manadawoof.service;

import com.vet.manadawoof.entity.CanalComunicacionEntity;

import java.util.List;

// Servicio para gestionar los canales de comunicación

public interface CanalComunicacionService {
    // Crea un nuevo canal de comunicación en la base de datos
    CanalComunicacionEntity crear(CanalComunicacionEntity entity);
    
    // Actualiza los datos de un canal de comunicación existente
    CanalComunicacionEntity actualizar(CanalComunicacionEntity entity);
    
    // Elimina un canal de comunicación
    String eliminar(Integer id);
    
    // Lista todos los canales de comunicación registrados
    List<CanalComunicacionEntity> listar();
    
    // Obtiene un canal de comunicación específico por su ID
    CanalComunicacionEntity obtenerPorId(Integer id);
    
}

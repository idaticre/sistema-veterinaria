package com.vet.manadawoof.service;


import com.vet.manadawoof.entity.ServicioEntity;

import java.util.List;

// Servicio para gestionar los servicios veterinarios
public interface ServicioService {
    // Crea un nuevo servicio en la base de datos
    ServicioEntity crear(ServicioEntity entity);
    
    // Actualiza los datos de un servicio existente
    ServicioEntity actualizar(ServicioEntity entity);
    
    // Elimina un servicio
    String eliminar(Integer id);
    
    // Lista todos los servicios registrados
    List<ServicioEntity> listar();
    
    // Obtiene un servicio específico por su ID
    ServicioEntity obtenerPorId(Integer id);
}

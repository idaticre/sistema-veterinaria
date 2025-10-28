package com.vet.manadawoof.service;

import com.vet.manadawoof.entity.AplicacionViaEntity;

import java.util.List;

/**
 * Servicio para gestionar las vías de aplicación de medicamentos.
 */
public interface AplicacionViaService {
    
    // Crea una nueva vía de aplicación
    AplicacionViaEntity crear(AplicacionViaEntity entity);
    
    // Actualiza una vía de aplicación existente
    AplicacionViaEntity actualizar(AplicacionViaEntity entity);
    
    // Elimina una vía de aplicación por su ID
    String eliminar(Integer id);
    
    // Lista todas las vías de aplicación
    List<AplicacionViaEntity> listar();
    
    // Obtiene una vía de aplicación por su ID
    AplicacionViaEntity obtenerPorId(Integer id);
}

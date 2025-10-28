package com.vet.manadawoof.service;

import com.vet.manadawoof.entity.EspecieEntity;

import java.util.List;

// Servicio para gestionar las especies de mascotas.

public interface EspecieService {
    
    // Crea una nueva especie en la base de datos.
    EspecieEntity crear(EspecieEntity entity);
    
    // Actualiza los datos de una especie existente.
    EspecieEntity actualizar(EspecieEntity entity);
    
    // Elimina una especie por su ID
    String eliminar(Integer id);
    
    // Lista todas las especies registradas.
    List<EspecieEntity> listar();
    
    // Obtiene una especie específica por su ID.
    EspecieEntity obtenerPorId(Integer id);
}

package com.vet.manadawoof.service;

import com.vet.manadawoof.entity.TamanoMascEntity;

import java.util.List;

public interface TamanoMascService {
    
    // Crea un nuevo tamaño en la base de datos
    TamanoMascEntity crear(TamanoMascEntity entity);
    
    // Actualiza los datos de un tamaño existente.
    TamanoMascEntity actualizar(TamanoMascEntity entity);
    
    // Elimina una raza
    String eliminar(Integer id);
    
    // Lista todos los tamaños registrados.
    List<TamanoMascEntity> listar();
    
    // Obtiene un tamaño específica por su ID.
    TamanoMascEntity obtenerPorId(Integer id);
}

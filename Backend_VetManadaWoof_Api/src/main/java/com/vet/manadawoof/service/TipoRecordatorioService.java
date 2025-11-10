package com.vet.manadawoof.service;

import com.vet.manadawoof.entity.TipoRecordatorioEntity;

import java.util.List;

public interface TipoRecordatorioService {
    
    // Crea un nuevo tipo de recordatorio en la base de datos
    TipoRecordatorioEntity crear(TipoRecordatorioEntity entity);
    
    // Actualiza los datos de un tipo de recordatorio existente
    TipoRecordatorioEntity actualizar(TipoRecordatorioEntity entity);
    
    // Elimina un tipo de recordatorio
    String eliminar(Integer id);
    
    // Lista todos los tipos de recordatorio registrados
    List<TipoRecordatorioEntity> listar();
    
    // Obtiene un tipo de recordatorio específico por su ID
    TipoRecordatorioEntity obtenerPorId(Integer id);
}

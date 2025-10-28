package com.vet.manadawoof.service;

import com.vet.manadawoof.entity.EtapaVidaEntity;

import java.util.List;

// Servicio para operaciones CRUD de etapas de vida
public interface EtapaVidaService {
    
    // Crear nueva etapa
    EtapaVidaEntity crear(EtapaVidaEntity entity);
    
    // Actualizar etapa existente
    EtapaVidaEntity actualizar(EtapaVidaEntity entity);
    
    // Eliminar por ID
    String eliminar(Integer id);
    
    // Listar todas
    List<EtapaVidaEntity> listar();
    
    // Obtener por ID
    EtapaVidaEntity obtenerPorId(Integer id);
}

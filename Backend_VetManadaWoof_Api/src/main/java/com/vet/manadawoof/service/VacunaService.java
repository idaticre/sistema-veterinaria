package com.vet.manadawoof.service;

import com.vet.manadawoof.entity.VacunaEntity;

import java.util.List;

// Servicio para operaciones CRUD de vacunas
public interface VacunaService {
    
    // Crear nueva vacuna
    VacunaEntity crear(VacunaEntity entity);
    
    // Actualizar vacuna existente
    VacunaEntity actualizar(VacunaEntity entity);
    
    // Eliminar vacuna por ID
    String eliminar(Integer id);
    
    // Listar todas las vacunas
    List<VacunaEntity> listar();
    
    // Obtener vacuna por ID
    VacunaEntity obtenerPorId(Integer id);
}

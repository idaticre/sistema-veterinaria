package com.vet.manadawoof.service;

import com.vet.manadawoof.entity.MedioSolicitudEntity;

import java.util.List;

// Servicio para gestionar los medios de solicitud

public interface MedioSolicitudService {
    
    // Crea un nuevo medio de solicitud en la base de datos
    MedioSolicitudEntity crear(MedioSolicitudEntity entity);
    
    // Actualiza los datos de un medio de solicitud existente
    MedioSolicitudEntity actualizar(MedioSolicitudEntity entity);
    
    // Elimina un medio de solicitud
    String eliminar(Integer id);
    
    // Lista todos los medios de solicitud registrados
    List<MedioSolicitudEntity> listar();
    
    // Obtiene un medio de solicitud específico por su ID
    MedioSolicitudEntity obtenerPorId(Integer id);
}

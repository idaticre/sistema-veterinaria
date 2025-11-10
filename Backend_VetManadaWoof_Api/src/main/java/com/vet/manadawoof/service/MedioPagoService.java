package com.vet.manadawoof.service;

import com.vet.manadawoof.entity.MedioPagoEntity;

import java.util.List;

public interface MedioPagoService {
    // Crea un nuevo medio de pago en la base de datos
    MedioPagoEntity crear(MedioPagoEntity entity);
    
    // Actualiza los datos de un medio de pago existente
    MedioPagoEntity actualizar(MedioPagoEntity entity);
    
    // Elimina un medio de pago
    String eliminar(Integer id);
    
    // Lista todos los medios de pago registrados
    List<MedioPagoEntity> listar();
    
    // Obtiene un medio de pago específico por su ID
    MedioPagoEntity obtenerPorId(Integer id);
}

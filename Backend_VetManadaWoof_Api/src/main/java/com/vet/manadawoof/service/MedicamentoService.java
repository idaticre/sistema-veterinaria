package com.vet.manadawoof.service;

import com.vet.manadawoof.entity.MedicamentoEntity;

import java.util.List;

// Servicio para medicamentos
public interface MedicamentoService {
    
    // Crear medicamento
    MedicamentoEntity crear(MedicamentoEntity entity);
    
    // Actualizar medicamento
    MedicamentoEntity actualizar(MedicamentoEntity entity);
    
    // Eliminar medicamento
    String eliminar(Integer id);
    
    // Listar medicamentos
    List<MedicamentoEntity> listar();
    
    // Obtener medicamento
    MedicamentoEntity obtenerPorId(Integer id);
}

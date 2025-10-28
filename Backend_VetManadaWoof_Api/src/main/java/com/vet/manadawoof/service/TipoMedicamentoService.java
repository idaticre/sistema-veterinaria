package com.vet.manadawoof.service;

import com.vet.manadawoof.entity.TipoMedicamentoEntity;

import java.util.List;

/**
 * Servicio para gestionar los tipos de medicamentos.
 */
public interface TipoMedicamentoService {
    
    // Crea un nuevo tipo de medicamento
    TipoMedicamentoEntity crear(TipoMedicamentoEntity entity);
    
    // Actualiza un tipo de medicamento existente
    TipoMedicamentoEntity actualizar(TipoMedicamentoEntity entity);
    
    // Elimina un tipo de medicamento por ID
    String eliminar(Integer id);
    
    // Lista todos los tipos de medicamento
    List<TipoMedicamentoEntity> listar();
    
    // Obtiene un tipo de medicamento por ID
    TipoMedicamentoEntity obtenerPorId(Integer id);
}

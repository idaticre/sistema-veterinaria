package com.vet.manadawoof.service;

import com.vet.manadawoof.entity.EspecialidadEntity;
import com.vet.manadawoof.entity.RazaEntity;

import java.util.List;

// Servicio para gestionar las razas
public interface RazaService {
    
    // Crea una nueva raza en la base de datos
    RazaEntity crear(RazaEntity entity);
    
    // Actualiza los datos de una raza existente.
    RazaEntity actualizar(RazaEntity entity);
    
    // Elimina una raza
    String eliminar(Integer id);
    
    // Lista todas las razas registradas.
    List<RazaEntity> listar();
    
    // Obtiene una raza específica por su ID.
    RazaEntity obtenerPorId(Integer id);
    
}

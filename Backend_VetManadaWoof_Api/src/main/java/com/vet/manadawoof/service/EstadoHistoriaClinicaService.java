package com.vet.manadawoof.service;

import com.vet.manadawoof.entity.EstadoHistoriaClinicaEntity;

import java.util.List;

public interface EstadoHistoriaClinicaService {
    EstadoHistoriaClinicaEntity crear(EstadoHistoriaClinicaEntity entity);
    
    EstadoHistoriaClinicaEntity actualizar(EstadoHistoriaClinicaEntity entity);
    
    String eliminar(Integer id);
    
    List<EstadoHistoriaClinicaEntity> listar();
    
    EstadoHistoriaClinicaEntity obtenerPorId(Integer id);
}

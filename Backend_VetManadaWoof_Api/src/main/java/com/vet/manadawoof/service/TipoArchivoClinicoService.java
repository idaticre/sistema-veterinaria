package com.vet.manadawoof.service;

import com.vet.manadawoof.entity.TipoArchivoClinicoEntity;

import java.util.List;

public interface TipoArchivoClinicoService {
    TipoArchivoClinicoEntity crear(TipoArchivoClinicoEntity entity);
    
    TipoArchivoClinicoEntity actualizar(TipoArchivoClinicoEntity entity);
    
    String eliminar(Integer id);
    
    List<TipoArchivoClinicoEntity> listar();
    
    TipoArchivoClinicoEntity obtenerPorId(Integer id);
}

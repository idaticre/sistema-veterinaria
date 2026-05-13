package com.vet.manadawoof.service;

import com.vet.manadawoof.entity.TipoIGVEntity;
import java.util.List;


public interface TipoIGVService {
    
    List<TipoIGVEntity> Listar();
    
    TipoIGVEntity Añadir(TipoIGVEntity tipoIGV);
    
    TipoIGVEntity Actualizar(Integer id, TipoIGVEntity tipoIGV);
    
    void EliminarTIGV (Integer id);
    
    TipoIGVEntity ObtenerPorId(Integer id);
}

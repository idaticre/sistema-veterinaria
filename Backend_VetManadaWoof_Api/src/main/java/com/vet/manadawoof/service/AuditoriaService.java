package com.vet.manadawoof.service;

import com.vet.manadawoof.entity.AuditoriaEntity;
import java.util.List;

public interface AuditoriaService {
    
    void crear(
            Integer idTipoAccion,
            String entidad,
            String descripcion
    );
    
    List<AuditoriaEntity> listar();
}

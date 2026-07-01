package com.vet.manadawoof.mapper;

import com.vet.manadawoof.entity.AuditoriaEntity;
import com.vet.manadawoof.entity.TipoAccionEntity;
import com.vet.manadawoof.entity.UsuarioEntity;
import org.springframework.stereotype.Component;

@Component
public class AuditoriaMapper {
    
    public AuditoriaEntity toResponse(AuditoriaEntity entity){
        
        if (entity == null) {
            return null;
        }
        
        return AuditoriaEntity.builder()
                .id(entity.getId())
                .usuario(
                        entity.getUsuario() != null ? UsuarioEntity.builder()
                        .id(entity.getId())
                        .username(entity.getUsuario().getUsername())
                        .build() : null
                    )
                .tipoAccion(entity.getTipoAccion() != null ? TipoAccionEntity.builder()
                        .id(entity.getId())
                        .nombre(entity.getTipoAccion().getNombre())
                        .build() : null
                    )
                .entidad(entity.getEntidad())
                .descripcion(entity.getDescripcion())
                .fecha(entity.getFecha())
                .build();
    }
    
}

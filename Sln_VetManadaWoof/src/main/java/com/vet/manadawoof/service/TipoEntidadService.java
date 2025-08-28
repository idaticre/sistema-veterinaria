package com.vet.manadawoof.service;

import com.vet.manadawoof.entity.TipoEntidadEntity;
import java.util.List;

public interface TipoEntidadService {

    String crearTipoEntidad(String nombre, Boolean activo);

    String actualizarTipoEntidad(Long id, String nombre, Boolean activo);

    String eliminarTipoEntidad(Long id);

    List<TipoEntidadEntity> listarTipoEntidad(Long id);
}

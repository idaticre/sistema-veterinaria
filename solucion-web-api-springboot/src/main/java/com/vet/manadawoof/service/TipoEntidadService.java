package com.vet.manadawoof.service;

import com.vet.manadawoof.entity.TipoEntidadEntity;

import java.util.List;

public interface TipoEntidadService {

    String crearTipoEntidad(String nombre, Boolean activo);

    String actualizarTipoEntidad(Integer id, String nombre, Boolean activo);

    String eliminarTipoEntidad(Integer id);

    List<TipoEntidadEntity> listarTipoEntidad(Integer id);
}

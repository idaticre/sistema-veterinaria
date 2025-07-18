package com.veterinariawoof.services;

import com.veterinariawoof.models.CanalComunicacion;

import java.util.List;

public interface CanalComunicacionService {
    List<CanalComunicacion> obtenerTodos();
    CanalComunicacion obtenerPorId(Long id);
    CanalComunicacion guardar(CanalComunicacion canal);
    void eliminar(Long id);
}

package com.veterinariawoof.services;

import com.veterinariawoof.models.IngresoServicio;
import java.util.List;

public interface IngresoServicioService {
    List<IngresoServicio> obtenerTodos();
    IngresoServicio obtenerPorId(Long id);
    IngresoServicio guardar(IngresoServicio ingreso);
    void eliminar(Long id);
}

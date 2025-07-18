package com.veterinariawoof.services;

import com.veterinariawoof.models.Servicio;
import java.util.List;

public interface ServicioService {
    List<Servicio> obtenerTodos();
    Servicio obtenerPorId(Long id);
    Servicio guardar(Servicio servicio);
    void eliminar(Long id);
}

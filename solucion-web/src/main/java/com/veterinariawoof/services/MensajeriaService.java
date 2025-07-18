package com.veterinariawoof.services;

import com.veterinariawoof.models.Mensaje;
import java.util.List;

public interface MensajeriaService {
    List<Mensaje> obtenerTodos();
    Mensaje obtenerPorId(Long id);
    void guardar(Mensaje mensaje);
    void eliminar(Long id);
}

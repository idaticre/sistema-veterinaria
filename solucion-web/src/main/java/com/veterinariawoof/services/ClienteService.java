package com.veterinariawoof.services;

import com.veterinariawoof.models.Cliente;
import java.util.List;

public interface ClienteService {
    List<Cliente> obtenerTodos();
    Cliente obtenerPorId(Long id);
    Cliente guardar(Cliente cliente);
    void eliminar(Long id);
}

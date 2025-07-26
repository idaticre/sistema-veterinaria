package com.veterinariawoof.services;

import com.veterinariawoof.models.Inventario;
import java.util.List;

public interface InventarioService {
    List<Inventario> listar();
    Inventario obtenerPorId(int id);
    Inventario guardar(Inventario inventario);
    void eliminar(int id);
}

package com.veterinariawoof.services;

import com.veterinariawoof.models.Especie;
import java.util.List;

public interface EspecieService {
    List<Especie> obtenerTodas();
    void guardar(Especie especie);
    Especie obtenerPorId(Long id);
    void eliminar(Long id);
}

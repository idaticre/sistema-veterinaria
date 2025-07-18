package com.veterinariawoof.services;

import com.veterinariawoof.models.Mascota;
import java.util.List;

public interface MascotaService {
    List<Mascota> obtenerTodas();
    Mascota obtenerPorId(Long id);
    Mascota guardar(Mascota mascota);
    void eliminar(Long id);
}

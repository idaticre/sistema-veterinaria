package com.veterinariawoof.services;

import com.veterinariawoof.models.Agenda;
import java.util.List;

public interface AgendaService {
    List<Agenda> listarTodas();
    void guardar(Agenda agenda);
    Agenda obtenerPorId(Long id);
    void eliminar(Long id);
}

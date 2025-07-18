package com.veterinariawoof.services;

import com.veterinariawoof.models.Agenda;

import java.util.List;

public interface AgendaService {
    List<Agenda> obtenerTodas();
    Agenda obtenerPorId(Long id);
    Agenda guardar(Agenda agenda);
    void eliminar(Long id);
}

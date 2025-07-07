package com.veterinariawoof.services;

import com.veterinariawoof.dto.CitaDTO;
import com.veterinariawoof.models.Agenda;

import java.util.List;

public interface CitaService {
    void guardar(CitaDTO cita);
    List<Agenda> listarTodas();
}


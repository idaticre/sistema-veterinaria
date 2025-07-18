package com.veterinariawoof.services;

import com.veterinariawoof.models.HistoriaClinica;
import java.util.List;

public interface HistoriaClinicaService {
    List<HistoriaClinica> obtenerTodas();
    HistoriaClinica obtenerPorId(Long id);
    HistoriaClinica guardar(HistoriaClinica historia);
    void eliminar(Long id);
}

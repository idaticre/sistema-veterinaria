package com.veterinariawoof.services.impl;

import com.veterinariawoof.models.HistoriaClinica;
import com.veterinariawoof.repositories.HistoriaClinicaRepository;
import com.veterinariawoof.services.HistoriaClinicaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HistoriaClinicaServiceImpl implements HistoriaClinicaService {

    @Autowired
    private HistoriaClinicaRepository historiaRepo;

    @Override
    public List<HistoriaClinica> obtenerTodas() {
        return historiaRepo.findAll();
    }

    @Override
    public HistoriaClinica obtenerPorId(Long id) {
        return historiaRepo.findById(id).orElse(null);
    }

    @Override
    public HistoriaClinica guardar(HistoriaClinica historia) {
        return historiaRepo.save(historia);
    }

    @Override
    public void eliminar(Long id) {
        historiaRepo.deleteById(id);
    }
}

package com.veterinariawoof.services.impl;

import com.veterinariawoof.models.Servicio;
import com.veterinariawoof.repositories.ServicioRepository;
import com.veterinariawoof.services.ServicioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServicioServiceImpl implements ServicioService {

    @Autowired
    private ServicioRepository servicioRepo;

    @Override
    public List<Servicio> obtenerTodos() {
        return servicioRepo.findAll();
    }

    @Override
    public Servicio obtenerPorId(Long id) {
        return servicioRepo.findById(id).orElse(null);
    }

    @Override
    public Servicio guardar(Servicio servicio) {
        return servicioRepo.save(servicio);
    }

    @Override
    public void eliminar(Long id) {
        servicioRepo.deleteById(id);
    }
}

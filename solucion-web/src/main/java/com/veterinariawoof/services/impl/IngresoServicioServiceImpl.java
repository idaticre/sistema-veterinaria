package com.veterinariawoof.services.impl;

import com.veterinariawoof.models.IngresoServicio;
import com.veterinariawoof.repositories.IngresoServicioRepository;
import com.veterinariawoof.services.IngresoServicioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IngresoServicioServiceImpl implements IngresoServicioService {

    @Autowired
    private IngresoServicioRepository ingresoRepo;

    @Override
    public List<IngresoServicio> obtenerTodos() {
        return ingresoRepo.findAll();
    }

    @Override
    public IngresoServicio obtenerPorId(Long id) {
        return ingresoRepo.findById(id).orElse(null);
    }

    @Override
    public IngresoServicio guardar(IngresoServicio ingreso) {
        return ingresoRepo.save(ingreso);
    }

    @Override
    public void eliminar(Long id) {
        ingresoRepo.deleteById(id);
    }
}

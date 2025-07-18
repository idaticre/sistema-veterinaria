package com.veterinariawoof.services.impl;

import com.veterinariawoof.models.Mascota;
import com.veterinariawoof.repositories.MascotaRepository;
import com.veterinariawoof.services.MascotaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MascotaServiceImpl implements MascotaService {

    @Autowired
    private MascotaRepository mascotaRepo;

    @Override
    public List<Mascota> obtenerTodas() {
        return mascotaRepo.findAll();
    }

    @Override
    public Mascota obtenerPorId(Long id) {
        return mascotaRepo.findById(id).orElse(null);
    }

    @Override
    public Mascota guardar(Mascota mascota) {
        return mascotaRepo.save(mascota);
    }

    @Override
    public void eliminar(Long id) {
        mascotaRepo.deleteById(id);
    }
}

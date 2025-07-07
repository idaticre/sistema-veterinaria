package com.veterinariawoof.services;

import com.veterinariawoof.models.Mascota;
import com.veterinariawoof.repositories.MascotaRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MascotaService {

    private final MascotaRepository repository;

    public MascotaService(MascotaRepository repository) {
        this.repository = repository;
    }

    public List<Mascota> findAll() {
        return repository.findAll();
    }

    public Mascota save(Mascota mascota) {
        return repository.save(mascota);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Mascota findById(Long id) {
        return repository.findById(id).orElse(null);
    }
}

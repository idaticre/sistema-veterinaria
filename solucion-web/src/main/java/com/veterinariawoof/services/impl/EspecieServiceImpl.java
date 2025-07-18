package com.veterinariawoof.services.impl;

import com.veterinariawoof.models.Especie;
import com.veterinariawoof.repositories.EspecieRepository;
import com.veterinariawoof.services.EspecieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EspecieServiceImpl implements EspecieService {

    @Autowired
    private EspecieRepository especieRepository;

    @Override
    public List<Especie> obtenerTodas() {
        return especieRepository.findAll();
    }

    @Override
    public void guardar(Especie especie) {
        especieRepository.save(especie);
    }

    @Override
    public Especie obtenerPorId(Long id) {
        return especieRepository.findById(id).orElse(null);
    }

    @Override
    public void eliminar(Long id) {
        especieRepository.deleteById(id);
    }
}

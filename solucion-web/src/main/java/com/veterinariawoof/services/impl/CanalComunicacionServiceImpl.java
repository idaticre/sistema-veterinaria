package com.veterinariawoof.services.impl;

import com.veterinariawoof.models.CanalComunicacion;
import com.veterinariawoof.repositories.CanalComunicacionRepository;
import com.veterinariawoof.services.CanalComunicacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CanalComunicacionServiceImpl implements CanalComunicacionService {

    @Autowired
    private CanalComunicacionRepository canalRepository;

    @Override
    public List<CanalComunicacion> obtenerTodos() {
        return canalRepository.findAll();
    }

    @Override
    public CanalComunicacion obtenerPorId(Long id) {
        Optional<CanalComunicacion> canal = canalRepository.findById(id);
        return canal.orElse(null);
    }

    @Override
    public CanalComunicacion guardar(CanalComunicacion canal) {
        return canalRepository.save(canal);
    }

    @Override
    public void eliminar(Long id) {
        canalRepository.deleteById(id);
    }
}

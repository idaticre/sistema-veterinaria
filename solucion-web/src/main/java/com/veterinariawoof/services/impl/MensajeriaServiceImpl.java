package com.veterinariawoof.services.impl;

import com.veterinariawoof.models.Mensaje;
import com.veterinariawoof.repositories.MensajeRepository;
import com.veterinariawoof.services.MensajeriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MensajeriaServiceImpl implements MensajeriaService {

    @Autowired
    private MensajeRepository mensajeRepository;

    @Override
    public List<Mensaje> obtenerTodos() {
        return mensajeRepository.findAll();
    }

    @Override
    public Mensaje obtenerPorId(Long id) {
        return mensajeRepository.findById(id).orElse(null);
    }

    @Override
    public void guardar(Mensaje mensaje) {
        mensajeRepository.save(mensaje);
    }

    @Override
    public void eliminar(Long id) {
        mensajeRepository.deleteById(id);
    }
}

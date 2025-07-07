package com.veterinariawoof.services.impl;

import com.veterinariawoof.models.Agenda;
import com.veterinariawoof.repositories.AgendaRepository;
import com.veterinariawoof.services.AgendaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AgendaServiceImpl implements AgendaService {

    @Autowired
    private AgendaRepository repo;

    @Override
    public List<Agenda> listarTodas() {
        return repo.findAll();
    }

    @Override
    public void guardar(Agenda agenda) {
        repo.save(agenda);
    }

    @Override
    public Agenda obtenerPorId(Long id) {
        return repo.findById(id).orElse(null);
    }

    @Override
    public void eliminar(Long id) {
        repo.deleteById(id);
    }
}

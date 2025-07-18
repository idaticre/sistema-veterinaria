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
    private AgendaRepository agendaRepository;

    @Override
    public List<Agenda> obtenerTodas() {
        return agendaRepository.findAll();
    }

    @Override
    public Agenda obtenerPorId(Long id) {
        return agendaRepository.findById(id).orElse(null);
    }

    @Override
    public Agenda guardar(Agenda agenda) {
        return agendaRepository.save(agenda);
    }

    @Override
    public void eliminar(Long id) {
        agendaRepository.deleteById(id);
    }
}

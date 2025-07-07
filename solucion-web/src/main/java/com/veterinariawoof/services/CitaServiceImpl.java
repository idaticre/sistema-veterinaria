package com.veterinariawoof.services;

import com.veterinariawoof.dto.CitaDTO;
import com.veterinariawoof.models.*;
import com.veterinariawoof.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CitaServiceImpl implements CitaService {

    @Autowired private AgendaRepository agendaRepository;
    @Autowired private ClienteRepository clienteRepository;
    @Autowired private MascotaRepository mascotaRepository;
    @Autowired private TipoServicioRepository tipoServicioRepository;

    @Override
    public void guardar(CitaDTO dto) {
        Agenda cita = new Agenda();
        cita.setCliente(clienteRepository.findById(dto.getIdCliente()).orElseThrow());
        cita.setMascota(mascotaRepository.findById(dto.getIdMascota()).orElseThrow());
        cita.setServicio(tipoServicioRepository.findById(dto.getIdServicio()).orElseThrow());
        cita.setFecha(dto.getFecha());
        cita.setHora(dto.getHora());
        cita.setDuracionEstimada(dto.getDuracionEstimada());
        cita.setEstado(EstadoCita.PENDIENTE);
        cita.setObservaciones(dto.getObservaciones());
        cita.setFechaRegistro(LocalDateTime.now());

        agendaRepository.save(cita);
    }

    @Override
    public List<Agenda> listarTodas() {
        return agendaRepository.findAll();
    }
}
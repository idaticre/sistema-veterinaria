package com.vet.manadawoof.service;

import com.vet.manadawoof.entity.SalaEntity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface SalaService {

    List<SalaEntity> listar();

    List<SalaEntity> buscarSalasDisponibles(LocalDate fecha, LocalTime hora);

}
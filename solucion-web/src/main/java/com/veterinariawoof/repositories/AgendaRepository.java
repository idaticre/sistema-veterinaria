package com.veterinariawoof.repositories;

import com.veterinariawoof.models.Agenda;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AgendaRepository extends JpaRepository<Agenda, Long> {
    List<Agenda> findByFecha(LocalDate fecha);
}
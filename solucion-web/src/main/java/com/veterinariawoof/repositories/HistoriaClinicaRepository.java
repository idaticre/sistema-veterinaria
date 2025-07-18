package com.veterinariawoof.repositories;

import com.veterinariawoof.models.HistoriaClinica;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistoriaClinicaRepository extends JpaRepository<HistoriaClinica, Long> {
    List<HistoriaClinica> findByMascotaId(Long mascotaId);
}

package com.veterinariawoof.repositories;

import com.veterinariawoof.models.Mascota;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MascotaRepository extends JpaRepository<Mascota, Long> {}

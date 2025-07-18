package com.veterinariawoof.repositories;

import com.veterinariawoof.models.Mensaje;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MensajeRepository extends JpaRepository<Mensaje, Long> {
}

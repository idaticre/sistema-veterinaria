package com.veterinariawoof.repositories;

import com.veterinariawoof.models.IngresoServicio;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; // ✅ ESTA IMPORTACIÓN ES OBLIGATORIA

public interface IngresoServicioRepository extends JpaRepository<IngresoServicio, Long> {
    List<IngresoServicio> findByMascotaId(Long mascotaId);
}

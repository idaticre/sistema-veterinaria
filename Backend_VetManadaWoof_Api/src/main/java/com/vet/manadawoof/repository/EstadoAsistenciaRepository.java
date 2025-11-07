package com.vet.manadawoof.repository;

import com.vet.manadawoof.entity.EstadoAsistenciaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repositorio vacío de EstadoAsistenciaEntity.
 * Toda la lógica se maneja desde el servicio mediante SPs y mapeos.
 */
public interface EstadoAsistenciaRepository extends JpaRepository<EstadoAsistenciaEntity, Integer> {
    Optional<EstadoAsistenciaEntity> findByNombre(String nombre);
}

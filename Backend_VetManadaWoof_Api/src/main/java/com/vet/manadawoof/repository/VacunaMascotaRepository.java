package com.vet.manadawoof.repository;

import com.vet.manadawoof.entity.VacunaMascotaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository para VacunaMascotaEntity.
 * <p>
 * Actualmente vacío, solo extiende JpaRepository para contar con métodos CRUD básicos.
 * Se puede extender más adelante según necesidades de filtros o reportes.
 */
public interface VacunaMascotaRepository extends JpaRepository<VacunaMascotaEntity, Integer> {
}

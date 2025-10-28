package com.vet.manadawoof.repository;

import com.vet.manadawoof.entity.MedicamentoMascotaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository para MedicamentoMascotaEntity.
 * <p>
 * Actualmente vacío, solo extiende JpaRepository para contar con métodos CRUD básicos.
 * Se puede extender más adelante según necesidades de filtros o reportes.
 */
public interface MedicamentoMascotaRepository extends JpaRepository<MedicamentoMascotaEntity, Integer> {
}

package com.vet.manadawoof.repository;

import com.vet.manadawoof.entity.EstadoHistoriaClinicaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstadoHistoriaClinicaRepository extends JpaRepository<EstadoHistoriaClinicaEntity, Integer> {
}

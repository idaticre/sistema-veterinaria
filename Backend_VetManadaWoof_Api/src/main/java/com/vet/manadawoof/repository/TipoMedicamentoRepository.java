package com.vet.manadawoof.repository;

import com.vet.manadawoof.entity.TipoMedicamentoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Repositorio JPA para la entidad TipoMedicamento.
//  Proporciona operaciones CRUD básicas.

@Repository
public interface TipoMedicamentoRepository extends JpaRepository<TipoMedicamentoEntity, Integer> {
}

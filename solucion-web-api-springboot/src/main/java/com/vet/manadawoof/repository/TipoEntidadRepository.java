package com.vet.manadawoof.repository;

import com.vet.manadawoof.entity.TipoEntidadEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoEntidadRepository extends JpaRepository<TipoEntidadEntity, Integer> {
    // Solo métodos estándar JPA: findAll(), findById()
}

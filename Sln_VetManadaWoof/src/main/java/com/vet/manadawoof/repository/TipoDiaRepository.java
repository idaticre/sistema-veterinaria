package com.vet.manadawoof.repository;

import com.vet.manadawoof.entity.TipoDiaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoDiaRepository extends JpaRepository<TipoDiaEntity, Integer> {
    // Métodos JPA estándar: findAll(), findById()
}

package com.vet.manadawoof.repository;

import com.vet.manadawoof.entity.EntidadEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EntidadRepository extends JpaRepository<EntidadEntity, Long> {
    // Solo para consultas CRUD básicas
}

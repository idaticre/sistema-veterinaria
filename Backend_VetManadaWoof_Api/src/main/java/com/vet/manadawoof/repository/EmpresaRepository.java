package com.vet.manadawoof.repository;

import com.vet.manadawoof.entity.EmpresaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmpresaRepository extends JpaRepository<EmpresaEntity, Integer> {
    // Ya no necesitamos los @Procedure
}

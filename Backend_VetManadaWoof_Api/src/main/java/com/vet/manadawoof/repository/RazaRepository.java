package com.vet.manadawoof.repository;

import com.vet.manadawoof.entity.RazaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RazaRepository extends JpaRepository<RazaEntity, Integer> {
}

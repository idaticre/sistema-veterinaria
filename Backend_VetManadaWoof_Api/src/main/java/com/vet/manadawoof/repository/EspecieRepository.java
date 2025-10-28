package com.vet.manadawoof.repository;

import com.vet.manadawoof.entity.EspecieEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EspecieRepository extends JpaRepository<EspecieEntity, Integer> {
}

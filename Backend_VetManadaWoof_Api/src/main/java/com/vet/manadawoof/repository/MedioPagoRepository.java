package com.vet.manadawoof.repository;

import com.vet.manadawoof.entity.MedioPagoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedioPagoRepository extends JpaRepository<MedioPagoEntity, Integer> {

}

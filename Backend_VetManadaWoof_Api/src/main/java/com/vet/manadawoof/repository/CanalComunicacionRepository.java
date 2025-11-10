package com.vet.manadawoof.repository;

import com.vet.manadawoof.entity.CanalComunicacionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CanalComunicacionRepository extends JpaRepository<CanalComunicacionEntity, Integer> {

}

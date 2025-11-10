package com.vet.manadawoof.repository;

import com.vet.manadawoof.entity.MedioSolicitudEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedioSolicitudRepository extends JpaRepository<MedioSolicitudEntity, Integer> {

}

package com.vet.manadawoof.repository;

import com.vet.manadawoof.entity.AsignacionHorarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AsignacionHorarioRepository extends JpaRepository<AsignacionHorarioEntity, Long> {
    // Repository vacío, usamos SPs desde el service
}

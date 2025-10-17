package com.vet.manadawoof.repository;

import com.vet.manadawoof.entity.HorarioTrabajoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HorarioTrabajoRepository extends JpaRepository<HorarioTrabajoEntity, Integer> {

}

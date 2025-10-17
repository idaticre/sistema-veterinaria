package com.vet.manadawoof.repository;

import com.vet.manadawoof.entity.DiaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiaRepository extends JpaRepository<DiaEntity, Integer> {

}

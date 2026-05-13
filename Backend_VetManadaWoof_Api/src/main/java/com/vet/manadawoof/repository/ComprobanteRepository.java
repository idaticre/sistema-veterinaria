package com.vet.manadawoof.repository;

import com.vet.manadawoof.entity.ComprobanteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComprobanteRepository extends JpaRepository<ComprobanteEntity, Long>{
    
    
    ComprobanteEntity
    findTopByOrderByIdDesc();
}

package com.vet.manadawoof.repository;

import com.vet.manadawoof.entity.TipoArchivoClinicoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoArchivoClinicoRepository extends JpaRepository<TipoArchivoClinicoEntity, Integer> {
}

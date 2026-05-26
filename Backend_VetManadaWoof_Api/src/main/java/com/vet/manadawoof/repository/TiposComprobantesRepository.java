package com.vet.manadawoof.repository;

import com.vet.manadawoof.entity.TiposComprobantesEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TiposComprobantesRepository extends JpaRepository<TiposComprobantesEntity, Integer> {

    @Query(value = "CALL sp_listar_tipos_comprobantes()", nativeQuery = true)
    List<TiposComprobantesEntity> listarTiposComprobantes();
}

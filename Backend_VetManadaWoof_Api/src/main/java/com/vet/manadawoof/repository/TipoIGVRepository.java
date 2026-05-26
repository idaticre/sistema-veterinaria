package com.vet.manadawoof.repository;

import com.vet.manadawoof.entity.TipoIGVEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoIGVRepository extends JpaRepository<TipoIGVEntity, Integer> {

    @Query(value = "CALL sp_listar_tipos_igv()", nativeQuery = true)
    List<TipoIGVEntity> listarTiposIgv();
}
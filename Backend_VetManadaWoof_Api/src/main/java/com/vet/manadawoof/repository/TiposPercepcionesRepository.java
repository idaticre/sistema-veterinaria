package com.vet.manadawoof.repository;

import com.vet.manadawoof.entity.TiposPercepcionesEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TiposPercepcionesRepository extends JpaRepository<TiposPercepcionesEntity, Integer> {

    @Query(value = "CALL sp_listar_tipos_percepciones()", nativeQuery = true)
    List<TiposPercepcionesEntity> listarTiposPercepciones();
}

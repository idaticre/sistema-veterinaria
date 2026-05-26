package com.vet.manadawoof.repository;

import com.vet.manadawoof.entity.MonedasEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoMonedaRepository extends JpaRepository<MonedasEntity, Integer> {

    @Query(value = "CALL sp_listar_monedas()", nativeQuery = true)
    List<MonedasEntity> listarMonedas();
}

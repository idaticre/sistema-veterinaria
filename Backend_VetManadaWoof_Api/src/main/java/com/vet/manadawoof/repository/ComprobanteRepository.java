package com.vet.manadawoof.repository;

import com.vet.manadawoof.entity.ComprobanteEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ComprobanteRepository extends JpaRepository<ComprobanteEntity, Long>{
    
    
    ComprobanteEntity
    findTopByOrderByIdDesc();
    
    @Query(value = "CALL sp_get_comprobantes_por_tipo(:tipo)", nativeQuery = true)
    List<ComprobanteEntity> findByTipoComprobante(@Param("tipo") Integer tipo);

    @Query(value = "CALL sp_obtener_comprobantes_por_cliente(:clienteId)", nativeQuery = true)
    List<ComprobanteEntity> findByCliente(@Param("clienteId") Long clienteId);
}

package com.vet.manadawoof.repository;

import com.vet.manadawoof.entity.CajaMovimientoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CajaMovimientoRepository extends JpaRepository<CajaMovimientoEntity, Long> {

    List<CajaMovimientoEntity> findByCajaDiaria_IdOrderByFechaHoraDesc(Long cajaDiariaId);

    @Query("""
        SELECT m FROM CajaMovimientoEntity m
        WHERE m.cajaDiaria.fecha BETWEEN :desde AND :hasta
          AND (:medioPagoId IS NULL OR m.medioPago.id = :medioPagoId)
          AND (:tipo IS NULL OR m.tipo = :tipo)
        ORDER BY m.fechaHora DESC, m.id DESC
        """)
    List<CajaMovimientoEntity> buscarHistorial(
            @Param("desde") LocalDate desde,
            @Param("hasta") LocalDate hasta,
            @Param("medioPagoId") Integer medioPagoId,
            @Param("tipo") String tipo);

    boolean existsByOrigenAndReferenciaId(String origen, Long referenciaId);

    @Query(value = """
        SELECT COALESCE(SUM(CASE WHEN tipo IN ('INGRESO', 'REPOSICION') THEN monto ELSE -monto END), 0)
        FROM caja_movimientos
        WHERE medio_pago_id = :medioPagoId
        """, nativeQuery = true)
    java.math.BigDecimal calcularNetoHistoricoPorMedio(@Param("medioPagoId") Integer medioPagoId);

}

package com.vet.manadawoof.repository;

import com.vet.manadawoof.entity.AgendaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AgendaRepository
        extends JpaRepository<AgendaEntity, Long> {

    Optional<AgendaEntity> findByCodigo(String codigo);

    List<AgendaEntity> findByCliente_Id(Long idCliente);

    // 🔥 AGREGAR ESTO
    AgendaEntity findTopByOrderByIdDesc();

    // 🔥 NUEVO MÉTODO
    @Query("""
    SELECT a FROM AgendaEntity a 
    WHERE 
        (a.fecha < :hoy 
         OR (a.fecha = :hoy AND a.hora < :horaActual))
    AND a.estado.id IN :estados
    """)
    List<AgendaEntity> findCitasParaNoAsistido(
            @Param("hoy") LocalDate hoy,
            @Param("horaActual") java.time.LocalTime horaActual,
            @Param("estados") List<Integer> estados
    );
@Query("""
SELECT COUNT(i)
FROM IngresoServicioEntity i
JOIN i.agenda a
WHERE i.colaborador.id = :idColaborador
AND a.fecha = :fecha
AND a.hora = :hora
AND a.estado.id NOT IN (4,6)
""")
long contarColaboradorOcupado(
        @Param("idColaborador") Long idColaborador,
        @Param("fecha") LocalDate fecha,
        @Param("hora") LocalTime hora
);
}
package com.vet.manadawoof.repository;

import com.vet.manadawoof.entity.EmpresaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmpresaRepository extends JpaRepository<EmpresaEntity, Long> {

    // Llamar SP para listar o buscar
    @Query(value = "CALL sp_empresa(:accion, :id, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL)", nativeQuery = true)
    List<EmpresaEntity> spEmpresaList(
            @Param("accion") String accion,
            @Param("id") Long id
    );

    // Llamar SP para actualizar
    @Query(value = "CALL sp_empresa(:accion, :id, :razonSocial, :ruc, :direccion, :ciudad, :distrito, :telefono, :correo, :representante, :logoEmpresa)", nativeQuery = true)
    String spEmpresaUpdate(
            @Param("accion") String accion,
            @Param("id") Long id,
            @Param("razonSocial") String razonSocial,
            @Param("ruc") String ruc,
            @Param("direccion") String direccion,
            @Param("ciudad") String ciudad,
            @Param("distrito") String distrito,
            @Param("telefono") String telefono,
            @Param("correo") String correo,
            @Param("representante") String representante,
            @Param("logoEmpresa") String logoEmpresa
    );
}

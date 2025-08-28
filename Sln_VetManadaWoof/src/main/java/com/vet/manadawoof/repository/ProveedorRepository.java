package com.vet.manadawoof.repository;

import com.vet.manadawoof.entity.ProveedorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProveedorRepository extends JpaRepository<ProveedorEntity, Long> {

    @Procedure(name = "registrar_proveedor")
    String registrarProveedor(
            @Param("p_id_entidad") Long idEntidad,
            @Param("p_activo") Boolean activo
    );

    @Procedure(name = "actualizar_proveedor")
    String actualizarProveedor(
            @Param("p_id") Long idProveedor,
            @Param("p_id_entidad") Long idEntidad,
            @Param("p_activo") Boolean activo
    );
}

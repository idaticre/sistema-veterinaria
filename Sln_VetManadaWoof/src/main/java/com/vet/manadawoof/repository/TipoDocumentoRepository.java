package com.vet.manadawoof.repository;

import com.vet.manadawoof.entity.TipoDocumentoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoDocumentoRepository extends JpaRepository<TipoDocumentoEntity, Long> {

    @Procedure(name = "TipoDocumentoEntity.sp_tipo_documento")
    String spTipoDocumento(
            @Param("p_accion") String accion,
            @Param("p_id") Long id,
            @Param("p_nombre") String nombre,
            @Param("p_activo") Boolean activo
    );
}

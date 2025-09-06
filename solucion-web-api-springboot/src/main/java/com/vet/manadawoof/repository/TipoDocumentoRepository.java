package com.vet.manadawoof.repository;

import com.vet.manadawoof.entity.TipoDocumentoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoDocumentoRepository extends JpaRepository<TipoDocumentoEntity, Integer> {

    @Procedure(name = "TipoDocumentoEntity.spTipoDocumento")
    String spTipoDocumento(
            String p_accion,
            Integer p_id,
            String p_descripcion,
            Integer p_activo);
}

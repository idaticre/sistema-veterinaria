package com.vet.manadawoof.repository;

import com.vet.manadawoof.entity.EmpresaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmpresaRepository extends JpaRepository<EmpresaEntity, Integer> {

    @Procedure(name = "EmpresaEntity.spEmpresa")
    List<EmpresaEntity> spEmpresa(
            String p_accion,
            Integer p_id,
            String p_razon_social,
            String p_ruc,
            String p_direccion,
            String p_ciudad,
            String p_distrito,
            String p_telefono,
            String p_correo,
            String p_representante,
            String p_logo_empresa
    );
}

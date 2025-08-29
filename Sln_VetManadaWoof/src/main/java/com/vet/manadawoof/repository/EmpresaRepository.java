package com.vet.manadawoof.repository;

import com.vet.manadawoof.entity.EmpresaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmpresaRepository extends JpaRepository<EmpresaEntity, Long> {

    @Procedure(name = "EmpresaEntity.spEmpresa")
    List<EmpresaEntity> spEmpresaList(String accion, Long id, String razonSocial, String ruc, String direccion,
                                      String ciudad, String distrito, String telefono, String correo,
                                      String representante, String logoEmpresa);

    @Procedure(name = "EmpresaEntity.spEmpresa")
    String spEmpresaUpdate(String accion, Long id, String razonSocial, String ruc, String direccion,
                           String ciudad, String distrito, String telefono, String correo,
                           String representante, String logoEmpresa);
}

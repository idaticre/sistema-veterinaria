package com.vet.manadawoof.repository;

import com.vet.manadawoof.entity.EntidadEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EntidadRepository extends JpaRepository<EntidadEntity, Long> {

    @Procedure(name = "EntidadEntity.spRegistrarEntidadBase")
    String spRegistrarEntidad(Long idTipoEntidad,
                              Long idTipoPersonaJuridica,
                              String nombre,
                              String sexo,
                              String documento,
                              Long idTipoDocumento,
                              String correo,
                              String telefono,
                              String direccion,
                              String ciudad,
                              String distrito,
                              String representante);

    @Procedure(name = "EntidadEntity.spActualizarEntidadBase")
    String spActualizarEntidad(Long idEntidad,
                               Long idTipoPersonaJuridica,
                               String nombre,
                               String sexo,
                               String documento,
                               Long idTipoDocumento,
                               String correo,
                               String telefono,
                               String direccion,
                               String ciudad,
                               String distrito,
                               String representante,
                               Boolean activo);
}

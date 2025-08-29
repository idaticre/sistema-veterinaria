package com.vet.manadawoof.repository;

import com.vet.manadawoof.entity.ColaboradorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface ColaboradorRepository extends JpaRepository<ColaboradorEntity, Long> {

    @Procedure(name = "ColaboradorEntity.registrarColaborador")
    String spColaboradorRegistrar(Long idTipoPersona, String nombre, String sexo, String documento,
                                  Long idTipoDocumento, String correo, String telefono,
                                  String direccion, String ciudad, String distrito,
                                  Date fechaIngreso, Long idUsuario, String foto);

    @Procedure(name = "ColaboradorEntity.actualizarColaborador")
    String spColaboradorActualizar(Long idEntidad, Long idTipoPersona, String nombre, String sexo,
                                   String documento, Long idTipoDocumento, String correo,
                                   String telefono, String direccion, String ciudad, String distrito,
                                   Date fechaIngreso, Long idUsuario, String foto, Boolean activo);
}

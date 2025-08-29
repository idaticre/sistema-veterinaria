package com.vet.manadawoof.repository;

import com.vet.manadawoof.entity.ClienteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepository extends JpaRepository<ClienteEntity, Long> {

    @Procedure(name = "ClienteEntity.registrarCliente")
    String registrarCliente(
            Integer idTipoPersonaJuridica,
            String nombre,
            String sexo,
            String documento,
            Integer idTipoDocumento,
            String correo,
            String telefono,
            String direccion,
            String ciudad,
            String distrito,
            String codigoEntidad,
            String codigoCliente
    );

    @Procedure(name = "ClienteEntity.actualizarCliente")
    String actualizarCliente(
            Long idEntidad,
            Integer idTipoPersonaJuridica,
            String nombre,
            String sexo,
            String documento,
            Integer idTipoDocumento,
            String correo,
            String telefono,
            String direccion,
            String ciudad,
            String distrito,
            Boolean activo
    );
}

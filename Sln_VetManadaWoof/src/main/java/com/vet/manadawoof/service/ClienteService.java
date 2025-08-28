package com.vet.manadawoof.service;

import com.vet.manadawoof.entity.ClienteEntity;

import java.util.List;

public interface ClienteService {
    String registrarCliente(ClienteEntity cliente);

    String actualizarCliente(ClienteEntity cliente);

    ClienteEntity findById(Long id);

    List<ClienteEntity> findAll();
}

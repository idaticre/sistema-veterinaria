package com.vet.manadawoof.service;

import com.vet.manadawoof.dtos.request.ClienteRequestDTO;
import com.vet.manadawoof.dtos.response.ClienteResponseDTO;
import com.vet.manadawoof.entity.ClienteEntity;

import java.util.List;

public interface ClienteService {

    ClienteResponseDTO registrarCliente(ClienteRequestDTO request);

    ClienteResponseDTO actualizarCliente(Integer idCliente, ClienteRequestDTO request);

    ClienteEntity obtenerPorId(Integer id);

    List<ClienteEntity> listarClientes();
}

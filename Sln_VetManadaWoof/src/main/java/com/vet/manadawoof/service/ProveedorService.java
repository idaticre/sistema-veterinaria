package com.vet.manadawoof.service;

import com.vet.manadawoof.dtos.response.EntidadResponseDTO;
import com.vet.manadawoof.dtos.request.ProveedorRequestDTO;
import com.vet.manadawoof.entity.ProveedorEntity;

import java.util.List;

public interface ProveedorService {

    EntidadResponseDTO registrarProveedor(ProveedorRequestDTO dto);

    EntidadResponseDTO actualizarProveedor(ProveedorRequestDTO dto);

    ProveedorEntity findById(Integer id);

    List<ProveedorEntity> findAll();
}

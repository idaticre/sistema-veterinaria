package com.vet.manadawoof.service;

import com.vet.manadawoof.entity.ProveedorEntity;
import java.util.List;

public interface ProveedorService {

    String registrarProveedor(ProveedorEntity proveedor);

    String actualizarProveedor(ProveedorEntity proveedor);

    ProveedorEntity findById(Long id);

    List<ProveedorEntity> findAll();
}

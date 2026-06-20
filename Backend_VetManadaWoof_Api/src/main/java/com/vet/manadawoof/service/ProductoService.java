package com.vet.manadawoof.service;

import com.vet.manadawoof.dtos.request.ProductoRequestDTO;
import com.vet.manadawoof.dtos.response.ProductoResponseDTO;

import java.util.List;

public interface ProductoService {

    // GET /productos
    List<ProductoResponseDTO> listar();

    // GET /productos?proveedor={proveedorId}
    List<ProductoResponseDTO> listarPorProveedor(Long proveedorId);

    // POST /productos
    ProductoResponseDTO registrar(ProductoRequestDTO request);

    // PUT /productos/{id}
    ProductoResponseDTO actualizar(Long id, ProductoRequestDTO request);

    // DELETE /productos/{id} (lógico)
    ProductoResponseDTO eliminar(Long id);
}

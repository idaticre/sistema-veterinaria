package com.vet.manadawoof.service;

import com.vet.manadawoof.dtos.request.ProveedorRequestDTO;
import com.vet.manadawoof.dtos.response.ProveedorResponseDTO;

import java.util.List;

// Servicio para gestionar proveedores
public interface ProveedorService {
    
    // Registra un nuevo proveedor
    ProveedorResponseDTO registrar(ProveedorRequestDTO dto);
    
    // Actualiza los datos de un proveedor existente
    ProveedorResponseDTO actualizar(ProveedorRequestDTO dto);
    
    // Obtiene un proveedor por su ID
    ProveedorResponseDTO obtenerPorId(Long id);
    
    // Lista todos los proveedores
    List<ProveedorResponseDTO> listar();
    
    // Realiza el borrado lógico de un proveedor
    ProveedorResponseDTO eliminar(Long idProveedor);
}

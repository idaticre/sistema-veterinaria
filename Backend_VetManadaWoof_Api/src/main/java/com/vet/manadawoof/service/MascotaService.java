package com.vet.manadawoof.service;

import com.vet.manadawoof.dtos.request.MascotaRequestDTO;
import com.vet.manadawoof.dtos.response.MascotaResponseDTO;

import java.util.List;

// Servicio de negocio para la entidad Mascota
public interface MascotaService {
    
    // Listar todas las mascotas
    List<MascotaResponseDTO> listarMascotas();
    
    // Obtener por id
    MascotaResponseDTO obtenerPorId(Long id);
    
    // Crear una nueva mascota
    MascotaResponseDTO crearMascota(MascotaRequestDTO request);
    
    // Actualizar una mascota existente (se debe pasar el id)
    MascotaResponseDTO actualizarMascota(Long id, MascotaRequestDTO request);
    
    // Eliminar (lógica) una mascota por ID
    MascotaResponseDTO eliminarMascota(Long id);
}

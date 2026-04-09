package com.vet.manadawoof.service;


import com.vet.manadawoof.dtos.request.VacunaMascotaRequestDTO;
import com.vet.manadawoof.dtos.response.VacunaMascotaResponseDTO;

import java.util.List;

// Servicio para la gestión de medicamentos aplicados a mascotas CRUD

public interface VacunaMascotaService {

    // Registrar una vacuna aplicada a una mascota
    VacunaMascotaResponseDTO crearVacunaMascota(VacunaMascotaRequestDTO request);

    // Actualizar un registro de medicamento aplicado
    // o realizar eliminación lógica mediante campo activo
    VacunaMascotaResponseDTO actualizarVacunaMascota(VacunaMascotaRequestDTO request);

    // Listar todos los registros de medicamentos aplicados
    List<VacunaMascotaResponseDTO> listarVacunasMascota();

    // Eliminar el medicamento mediante el sp de cambio lógico
    VacunaMascotaResponseDTO eliminarVacuna(Integer id);

}

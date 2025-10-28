package com.vet.manadawoof.service;


import com.vet.manadawoof.dtos.request.MedicamentoMascotaRequestDTO;
import com.vet.manadawoof.dtos.response.MedicamentoMascotaResponseDTO;

import java.util.List;

// Servicio para la gestión de medicamentos aplicados a mascotas CRUD

public interface MedicamentoMascotaService {
    
    // Registrar un medicamento aplicado a una mascota
    MedicamentoMascotaResponseDTO crearMedicamentoMascota(MedicamentoMascotaRequestDTO request);
    
    
    // Actualizar un registro de medicamento aplicado
    // o realizar eliminación lógica mediante campo activo
    MedicamentoMascotaResponseDTO actualizarMedicamentoMascota(MedicamentoMascotaRequestDTO request);
    
    
    // Listar todos los registros de medicamentos aplicados
    List<MedicamentoMascotaResponseDTO> listarMedicamentosMascota();
    
    // Eliminar el medicamento mediante el sp de cambio lógico
    MedicamentoMascotaResponseDTO eliminarMedicamento(Integer id);
    
}

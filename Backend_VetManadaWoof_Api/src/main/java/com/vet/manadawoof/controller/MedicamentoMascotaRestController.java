package com.vet.manadawoof.controller;

import com.vet.manadawoof.dtos.request.MedicamentoMascotaRequestDTO;
import com.vet.manadawoof.dtos.response.ApiResponse;
import com.vet.manadawoof.dtos.response.MedicamentoMascotaResponseDTO;
import com.vet.manadawoof.service.MedicamentoMascotaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Permitir llamadas desde frontend
@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/medicamentos-mascota")
@RequiredArgsConstructor
public class MedicamentoMascotaRestController {
    
    // Servicio inyectado
    private final MedicamentoMascotaService service;
    
    // Listar todos los medicamentos aplicados
    @GetMapping
    public ResponseEntity<ApiResponse<List<MedicamentoMascotaResponseDTO>>> listar() {
        List<MedicamentoMascotaResponseDTO> lista = service.listarMedicamentosMascota();
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse<>(true, "Lista de medicamentos obtenida correctamente", lista));
    }
    
    // Obtener un medicamento específico por su ID
    // GET /api/medicamentos-mascota/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MedicamentoMascotaResponseDTO>> obtenerPorId(@PathVariable("id") Integer id) {
        MedicamentoMascotaResponseDTO registro;
        try {
            registro = service.listarMedicamentosMascota()
                    .stream()
                    .filter(r -> r.getId().equals(id))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Registro de medicamento no encontrado"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage()));
        }
        
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse<>(true, "Registro obtenido correctamente", registro));
    }
    
    // Crear un nuevo registro de medicamento
    @PostMapping("/crear")
    public ResponseEntity<ApiResponse<MedicamentoMascotaResponseDTO>> crear(
            @Valid @RequestBody MedicamentoMascotaRequestDTO dto
    ) {
        // Llamar al servicio
        MedicamentoMascotaResponseDTO creado = service.crearMedicamentoMascota(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Medicamento registrado correctamente", creado));
    }
    
    // Actualizar un registro existente
    @PutMapping("/actualizar")
    public ResponseEntity<ApiResponse<MedicamentoMascotaResponseDTO>> actualizar(
            @Valid @RequestBody MedicamentoMascotaRequestDTO dto
    ) {
        MedicamentoMascotaResponseDTO actualizado = service.actualizarMedicamentoMascota(dto); // Llamar al servicio
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse<>(true, "Registro actualizado correctamente", actualizado));
    }
    
    // Eliminar (lógica) un registro por ID
    // Se realiza mediante cambio de estado activo = 0
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<ApiResponse<MedicamentoMascotaResponseDTO>> eliminar(@PathVariable Integer id) {
        MedicamentoMascotaResponseDTO eliminado = service.eliminarMedicamento(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Registro eliminado correctamente", eliminado));
    }
    
}

package com.vet.manadawoof.controller;

import com.vet.manadawoof.dtos.request.VacunaMascotaRequestDTO;
import com.vet.manadawoof.dtos.response.ApiResponse;
import com.vet.manadawoof.dtos.response.VacunaMascotaResponseDTO;
import com.vet.manadawoof.service.VacunaMascotaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/vacunas-mascota")
@RequiredArgsConstructor
public class VacunaMascotaRestController {
    
    // Servicio inyectado
    private final VacunaMascotaService service;
    
    // Listar todas las vacunas aplicadas
    @GetMapping
    public ResponseEntity<ApiResponse<List<VacunaMascotaResponseDTO>>> listar() {
        List<VacunaMascotaResponseDTO> lista = service.listarVacunasMascota();
        return ResponseEntity.ok(new ApiResponse<>(true, "Lista obtenida correctamente", lista));
    }
    
    // Crear un nuevo registro de vacuna
    @PostMapping("/crear")
    public ResponseEntity<ApiResponse<VacunaMascotaResponseDTO>> crear(
            @Valid @RequestBody VacunaMascotaRequestDTO dto
    ) {
        VacunaMascotaResponseDTO creado = service.crearVacunaMascota(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Vacuna registrada correctamente", creado));
    }
    
    // Actualizar un registro existente
    @PutMapping("/actualizar")
    public ResponseEntity<ApiResponse<VacunaMascotaResponseDTO>> actualizar(
            @Valid @RequestBody VacunaMascotaRequestDTO dto
    ) {
        VacunaMascotaResponseDTO actualizado = service.actualizarVacunaMascota(dto);
        return ResponseEntity.ok(new ApiResponse<>(true, "Registro actualizado", actualizado));
    }
    
    // Eliminar (lógica) un registro por ID
    // Se realiza mediante cambio de estado activo = 0
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<ApiResponse<VacunaMascotaResponseDTO>> eliminar(@PathVariable Integer id) {
        VacunaMascotaResponseDTO eliminado = service.eliminarVacuna(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Registro eliminado lógicamente", eliminado));
    }
}

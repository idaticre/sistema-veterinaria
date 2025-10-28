package com.vet.manadawoof.controller;

import com.vet.manadawoof.dtos.request.MascotaRequestDTO;
import com.vet.manadawoof.dtos.response.ApiResponse;
import com.vet.manadawoof.dtos.response.MascotaResponseDTO;
import com.vet.manadawoof.service.MascotaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173") // Permitir llamadas desde frontend
@RestController
@RequestMapping("/api/mascotas") // Ruta base de la API de mascotas
@RequiredArgsConstructor
public class MascotaRestController {
    
    // Inyección de servicio de negocio de mascotas
    private final MascotaService mascotaService;
    
    // Listar todas las mascotas registradas
    // GET /api/mascotas
    @GetMapping
    public ResponseEntity<ApiResponse<List<MascotaResponseDTO>>> listar() {
        List<MascotaResponseDTO> lista = mascotaService.listarMascotas(); // Obtener lista de mascotas
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse<>(true, "Lista de mascotas obtenida correctamente", lista));
    }
    
    // Obtener una mascota específica por su ID
    // GET /api/mascotas/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MascotaResponseDTO>> obtenerPorId(@PathVariable("id") Long id) {
        MascotaResponseDTO mascota;
        try {
            // Buscar en la lista filtrando por ID
            mascota = mascotaService.listarMascotas()
                    .stream()
                    .filter(m -> m.getId().equals(id))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Mascota no encontrada"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage()));
        }
        
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse<>(true, "Mascota obtenida correctamente", mascota));
    }
    
    // Crear una nueva mascota
    // POST /api/mascotas/crear
    @PostMapping("/crear")
    public ResponseEntity<ApiResponse<MascotaResponseDTO>> crear(@Valid @RequestBody MascotaRequestDTO dto) {
        MascotaResponseDTO creada = mascotaService.crearMascota(dto); // Llamar al servicio para crear
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Mascota creada correctamente", creada));
    }
    
    // Actualizar los datos de una mascota existente
    // PUT /api/mascotas/actualizar
    @PutMapping("/actualizar")
    public ResponseEntity<ApiResponse<MascotaResponseDTO>> actualizar(@Valid @RequestBody MascotaRequestDTO dto) {
        MascotaResponseDTO actualizada = mascotaService.actualizarMascota(dto); // Llamar al servicio para actualizar
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse<>(true, "Mascota actualizada correctamente", actualizada));
    }
    
    // Eliminar (lógica) una mascota por ID
    // DELETE /api/mascotas/eliminar/{id}
    // Se realiza mediante cambio de estado a "INACTIVA"
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<ApiResponse<MascotaResponseDTO>> eliminar(@PathVariable("id") Long id) {
        MascotaResponseDTO eliminada = mascotaService.eliminarMascota(id); // Llamar al servicio para "eliminar"
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse<>(true, "Mascota desactivada correctamente", eliminada));
    }
}

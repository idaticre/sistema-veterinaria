package com.vet.manadawoof.controller;

import com.vet.manadawoof.dtos.request.MascotaRequestDTO;
import com.vet.manadawoof.dtos.response.ApiResponse;
import com.vet.manadawoof.dtos.response.MascotaResponseDTO;
import com.vet.manadawoof.service.MascotaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/mascotas")
@RequiredArgsConstructor
public class MascotaRestController {
    
    private final MascotaService mascotaService;
    
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<MascotaResponseDTO>>> listar() {
        List<MascotaResponseDTO> lista = mascotaService.listarMascotas(); // Obtener lista de mascotas
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse<>(true, "Lista de mascotas obtenida correctamente", lista));
    }
    
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MascotaResponseDTO>> obtenerPorId(@PathVariable("id") Long id) {
        MascotaResponseDTO mascota;
        try {
            
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
// PUT /api/mascotas/actualizar/{id}
    @PutMapping("/actualizar/{id}")
    public ResponseEntity<ApiResponse<MascotaResponseDTO>> actualizar(
            @PathVariable("id") Long id,
            @Valid @RequestBody MascotaRequestDTO dto
    ) {
        try {
            // Llamamos al servicio para actualizar
            MascotaResponseDTO actualizada = mascotaService.actualizarMascota(id, dto);
            
            // Devolvemos la respuesta exitosa
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResponse<>(true, "Mascota actualizada correctamente", actualizada));
            
        } catch (RuntimeException e) {
            // Si ocurre un error (ej: no encontrada, datos inválidos)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage()));
        } catch (Exception e) {
            // Error inesperado (para debugging o casos extremos)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Error interno al actualizar la mascota: " + e.getMessage()));
        }
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

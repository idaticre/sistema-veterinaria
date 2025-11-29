package com.vet.manadawoof.controller;

import com.vet.manadawoof.dtos.request.EntidadRequestDTO;
import com.vet.manadawoof.dtos.response.ApiResponse;
import com.vet.manadawoof.dtos.response.EntidadResponseDTO;
import com.vet.manadawoof.service.EntidadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/entidades")
@RequiredArgsConstructor
public class EntidadRestController {
    
    private final EntidadService service;
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<EntidadResponseDTO>>> listar() {
        List<EntidadResponseDTO> lista = service.listarEntidades();
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse<>(
                        true, "Lista obtenida correctamente", lista));
    }
    
    @PostMapping("/crear")
    public ResponseEntity<ApiResponse<EntidadResponseDTO>> crear(
            @Valid @RequestBody EntidadRequestDTO dto
    ) {
        EntidadResponseDTO creada = service.crearEntidad(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(
                        true, "Entidad creada correctamente", creada));
    }
    
    @PutMapping("/actualizar")
    public ResponseEntity<ApiResponse<EntidadResponseDTO>> actualizar(
            @Valid @RequestBody EntidadRequestDTO dto
    ) {
        EntidadResponseDTO actualizada = service.actualizarEntidad(dto);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse<>(
                        true, "Entidad actualizada correctamente", actualizada));
    }
    
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<ApiResponse<EntidadResponseDTO>> eliminar(
            @PathVariable("id") Long id
    ) {
        EntidadResponseDTO eliminada = service.eliminarEntidad(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse<>(
                        true, "Entidad desactivada correctamente", eliminada));
    }
}

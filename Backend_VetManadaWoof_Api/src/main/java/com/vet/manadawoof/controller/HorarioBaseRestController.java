package com.vet.manadawoof.controller;

import com.vet.manadawoof.dtos.request.HorarioBaseRequestDTO;
import com.vet.manadawoof.dtos.response.ApiResponse;
import com.vet.manadawoof.dtos.response.HorarioBaseResponseDTO;
import com.vet.manadawoof.service.HorarioBaseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/horarios-base")
@RequiredArgsConstructor
public class HorarioBaseRestController {
    
    private final HorarioBaseService service;
    
    @PostMapping
    public ResponseEntity<ApiResponse<HorarioBaseResponseDTO>> crear(
            @Valid @RequestBody HorarioBaseRequestDTO request
    ) {
        try {
            HorarioBaseResponseDTO creado = service.crearHorario(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(true, "Horario creado correctamente", creado));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<HorarioBaseResponseDTO>> actualizar(
            @PathVariable Integer id,
            @Valid @RequestBody HorarioBaseRequestDTO request
    ) {
        try {
            HorarioBaseResponseDTO actualizado = service.actualizarHorario(id, request);
            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Horario actualizado correctamente", actualizado));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Integer id) {
        try {
            service.eliminarHorario(id);
            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Horario eliminado correctamente", null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<HorarioBaseResponseDTO>>> listar() {
        List<HorarioBaseResponseDTO> lista = service.listarHorarios();
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Lista de horarios obtenida correctamente", lista));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<HorarioBaseResponseDTO>> obtener(@PathVariable Integer id) {
        try {
            HorarioBaseResponseDTO encontrado = service.obtenerPorId(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Horario encontrado", encontrado));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }
}

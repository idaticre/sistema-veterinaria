package com.vet.manadawoof.controller;

import com.vet.manadawoof.dtos.request.VeterinarioRequestDTO;
import com.vet.manadawoof.dtos.response.ApiResponse;
import com.vet.manadawoof.dtos.response.VeterinarioResponseDTO;
import com.vet.manadawoof.service.VeterinarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/veterinarios")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class VeterinarioRestController {
    
    private final VeterinarioService service;
    
    @PostMapping("/registrar")
    public ResponseEntity<ApiResponse<VeterinarioResponseDTO>> registrar(@RequestBody VeterinarioRequestDTO dto) {
        VeterinarioResponseDTO response = service.registrar(dto);
        if(response.getMensaje() != null && response.getMensaje().startsWith("ERROR:")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, response.getMensaje(), null));
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, response.getMensaje(), response));
    }
    
    @PutMapping("/actualizar")
    public ResponseEntity<ApiResponse<VeterinarioResponseDTO>> actualizar(@RequestBody VeterinarioRequestDTO dto) {
        if(dto.getId() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, "ID de veterinario requerido para actualizar", null));
        }
        
        VeterinarioResponseDTO response = service.actualizar(dto.getId(), dto);
        
        String mensaje = response.getMensaje() != null ? response.getMensaje() : "Operación fallida";
        
        if(mensaje.startsWith("ERROR:")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, mensaje, null));
        }
        return ResponseEntity.ok(new ApiResponse<>(true, mensaje, response));
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<VeterinarioResponseDTO>>> listar() {
        List<VeterinarioResponseDTO> lista = service.listar();
        return ResponseEntity.ok(new ApiResponse<>(true, "Lista de veterinarios", lista));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<VeterinarioResponseDTO>> obtenerPorId(@PathVariable Long id) {
        VeterinarioResponseDTO response = service.obtenerPorId(id);
        if(response == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, "Veterinario no encontrado", null));
        }
        return ResponseEntity.ok(new ApiResponse<>(true, "Veterinario encontrado", response));
    }
}

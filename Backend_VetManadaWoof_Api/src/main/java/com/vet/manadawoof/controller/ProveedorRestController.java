package com.vet.manadawoof.controller;

import com.vet.manadawoof.dtos.request.ProveedorRequestDTO;
import com.vet.manadawoof.dtos.response.ApiResponse;
import com.vet.manadawoof.dtos.response.ProveedorResponseDTO;
import com.vet.manadawoof.service.ProveedorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/proveedores")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class ProveedorRestController {

    private final ProveedorService service;

    @PostMapping("/registrar")
    public ResponseEntity<ApiResponse<ProveedorResponseDTO>> registrar(@RequestBody ProveedorRequestDTO dto) {
        ProveedorResponseDTO data = service.registrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, data.getMensaje(), data));
    }

    @PutMapping("/actualizar")
    public ResponseEntity<ApiResponse<ProveedorResponseDTO>> actualizar(@RequestBody ProveedorRequestDTO dto) {
        ProveedorResponseDTO data = service.actualizar(dto);
        return ResponseEntity.ok(new ApiResponse<>(true, data.getMensaje(), data));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProveedorResponseDTO>> obtenerPorId(@PathVariable Long id) {
        ProveedorResponseDTO data = service.obtenerPorId(id);
        if(data == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, "Proveedor no encontrado", null));
        return ResponseEntity.ok(new ApiResponse<>(true, "Proveedor encontrado", data));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProveedorResponseDTO>>> listar() {
        List<ProveedorResponseDTO> data = service.listar();
        return ResponseEntity.ok(new ApiResponse<>(true, "Lista de proveedores", data));
    }
}

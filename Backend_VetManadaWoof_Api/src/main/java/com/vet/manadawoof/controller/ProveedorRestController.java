package com.vet.manadawoof.controller;

import com.vet.manadawoof.dtos.request.ProveedorRequestDTO;
import com.vet.manadawoof.dtos.response.ApiResponse;
import com.vet.manadawoof.dtos.response.ProveedorResponseDTO;
import com.vet.manadawoof.service.ProveedorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/proveedores")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
// Controlador REST que expone los endpoints para la gestión de proveedores.
// Maneja operaciones CRUD básicas e interactúa con el servicio correspondiente.
public class ProveedorRestController {
    
    // Inyección del servicio de proveedores mediante constructor (gracias a @RequiredArgsConstructor)
    private final ProveedorService service;
    
    /**
     * Endpoint para registrar un nuevo proveedor.
     * Valida si la respuesta del servicio contiene un error y devuelve el estado HTTP apropiado.
     */
    @PostMapping("/registrar")
    public ResponseEntity<ApiResponse<ProveedorResponseDTO>> registrar(@RequestBody ProveedorRequestDTO dto) {
        ProveedorResponseDTO response = service.registrar(dto);
        if(response.getMensaje() != null && response.getMensaje().startsWith("ERROR:")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, response.getMensaje(), null));
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, response.getMensaje(), response));
    }
    
    /**
     * Endpoint para actualizar los datos de un proveedor existente.
     * Si se detecta un error en la respuesta, se devuelve un código HTTP 400.
     */
    @PutMapping("/actualizar")
    public ResponseEntity<ApiResponse<ProveedorResponseDTO>> actualizar(@RequestBody ProveedorRequestDTO dto) {
        ProveedorResponseDTO response = service.actualizar(dto);
        if(response.getMensaje() != null && response.getMensaje().startsWith("ERROR:")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, response.getMensaje(), null));
        }
        return ResponseEntity.ok(new ApiResponse<>(true, response.getMensaje(), response));
    }
    
    /**
     * Devuelve la lista completa de proveedores registrados.
     * Ideal para mostrar en tablas o selectores dentro del frontend.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<ProveedorResponseDTO>>> listar() {
        List<ProveedorResponseDTO> proveedores = service.listar();
        return ResponseEntity.ok(new ApiResponse<>(true, "Lista de proveedores", proveedores));
    }
    
    /**
     * Busca y devuelve la información de un proveedor específico por su ID.
     * Si no existe, responde con un estado HTTP 404 (Not Found).
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProveedorResponseDTO>> obtenerPorId(@PathVariable Long id) {
        ProveedorResponseDTO proveedor = service.obtenerPorId(id);
        if(proveedor == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, "Proveedor no encontrado", null));
        return ResponseEntity.ok(new ApiResponse<>(true, "Proveedor encontrado", proveedor));
    }
    
    /**
     * Realiza el borrado lógico de un proveedor, sin eliminar físicamente el registro.
     * Si ocurre un error durante el proceso, devuelve un mensaje detallado y el código correspondiente.
     */
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<ApiResponse<ProveedorResponseDTO>> eliminar(@PathVariable Long id) {
        try {
            ProveedorResponseDTO response = service.eliminar(id);
            if(response.getMensaje() != null && response.getMensaje().startsWith("ERROR:")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, response.getMensaje(), null));
            }
            return ResponseEntity.ok(new ApiResponse<>(true, response.getMensaje(), response));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Error en la operación: " + e.getMessage(), null));
        }
    }
}

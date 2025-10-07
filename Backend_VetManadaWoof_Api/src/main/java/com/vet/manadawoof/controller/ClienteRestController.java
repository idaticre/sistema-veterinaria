package com.vet.manadawoof.controller;

import com.vet.manadawoof.dtos.request.ClienteRequestDTO;
import com.vet.manadawoof.dtos.response.ApiResponse;
import com.vet.manadawoof.dtos.response.ClienteResponseDTO;
import com.vet.manadawoof.service.ClienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class ClienteRestController {

    private final ClienteService service;

    @PostMapping("/registrar")
    public ResponseEntity<ApiResponse<ClienteResponseDTO>> registrar(@RequestBody ClienteRequestDTO dto) {
        ClienteResponseDTO response = service.registrar(dto);
        if (response.getMensaje() != null && response.getMensaje().startsWith("ERROR:")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, response.getMensaje(), null));
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, response.getMensaje(), response));
    }

    @PutMapping("/actualizar")
    public ResponseEntity<ApiResponse<ClienteResponseDTO>> actualizar(@RequestBody ClienteRequestDTO dto) {
        ClienteResponseDTO response = service.actualizar(dto);
        if (response.getMensaje() != null && response.getMensaje().startsWith("ERROR:")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, response.getMensaje(), null));
        }
        return ResponseEntity.ok(new ApiResponse<>(true, response.getMensaje(), response));
    }

    // Listar todos los clientes
    @GetMapping
    public ResponseEntity<ApiResponse<List<ClienteResponseDTO>>> listar() {
        List<ClienteResponseDTO> clientes = service.listar();
        return ResponseEntity.ok(new ApiResponse<>(
                true, "Lista de clientes", clientes));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ClienteResponseDTO>> obtenerPorId(@PathVariable Long id) {
        ClienteResponseDTO cliente = service.obtenerPorId(id);
        if (cliente == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, "Cliente no encontrado", null));
        }
        return ResponseEntity.ok(new ApiResponse<>(
                true, "Cliente encontrado", cliente));
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<ApiResponse<ClienteResponseDTO>> eliminar(@PathVariable Long id) {
        try {
            ClienteResponseDTO response = service.eliminar(id);
            if (response.getMensaje() != null && response.getMensaje().startsWith("ERROR:")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, response.getMensaje(), null));
            }
            return ResponseEntity.ok(
                    new ApiResponse<>(true, response.getMensaje(), response)
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Error en la operación: " + e.getMessage(), null));
        }
    }


}

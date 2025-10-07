package com.vet.manadawoof.controller;

import com.vet.manadawoof.dtos.request.ColaboradorRequestDTO;
import com.vet.manadawoof.dtos.response.ApiResponse;
import com.vet.manadawoof.dtos.response.ColaboradorResponseDTO;
import com.vet.manadawoof.service.ColaboradorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/colaboradores")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
// Controlador REST que gestiona las operaciones CRUD sobre los colaboradores.
// Interactúa con el servicio para registrar, actualizar, listar y eliminar colaboradores.
public class ColaboradorRestController {
    
    // Inyección del servicio encargado de la lógica de negocio de colaboradores
    private final ColaboradorService service;
    
    /**
     * Endpoint para registrar un nuevo colaborador.
     * Si el servicio devuelve un mensaje de error, responde con código HTTP 400.
     * En caso exitoso, devuelve el colaborador registrado con código HTTP 201 (Created).
     */
    @PostMapping("/registrar")
    public ResponseEntity<ApiResponse<ColaboradorResponseDTO>> registrar(@RequestBody ColaboradorRequestDTO dto) {
        ColaboradorResponseDTO response = service.registrar(dto);
        if(response.getMensaje() != null && response.getMensaje().startsWith("ERROR:")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, response.getMensaje(), null));
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, response.getMensaje(), response));
    }
    
    /**
     * Endpoint para actualizar los datos de un colaborador existente.
     * Valida la respuesta del servicio y retorna un código de error si algo falla.
     */
    @PutMapping("/actualizar")
    public ResponseEntity<ApiResponse<ColaboradorResponseDTO>> actualizar(@RequestBody ColaboradorRequestDTO dto) {
        ColaboradorResponseDTO response = service.actualizar(dto);
        if(response.getMensaje() != null && response.getMensaje().startsWith("ERROR:")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, response.getMensaje(), null));
        }
        return ResponseEntity.ok(new ApiResponse<>(true, response.getMensaje(), response));
    }
    
    /**
     * Devuelve la lista de todos los colaboradores activos o registrados.
     * Ideal para vistas administrativas o selección de responsables en otros módulos.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<ColaboradorResponseDTO>>> listar() {
        List<ColaboradorResponseDTO> colaboradores = service.listar();
        return ResponseEntity.ok(new ApiResponse<>(true, "Lista de colaboradores", colaboradores));
    }
    
    /**
     * Busca un colaborador por su identificador único.
     * Si no se encuentra, devuelve un código HTTP 404 con un mensaje claro.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ColaboradorResponseDTO>> obtenerPorId(@PathVariable Long id) {
        ColaboradorResponseDTO colaborador = service.obtenerPorId(id);
        if(colaborador == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, "Colaborador no encontrado", null));
        }
        return ResponseEntity.ok(new ApiResponse<>(true, "Colaborador encontrado", colaborador));
    }
    
    /**
     * Elimina (de forma lógica o permanente) un colaborador del sistema.
     * Captura errores inesperados y los devuelve con código HTTP 500.
     */
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<ApiResponse<ColaboradorResponseDTO>> eliminar(@PathVariable Long id) {
        try {
            ColaboradorResponseDTO response = service.eliminar(id);
            if(response.getMensaje() != null && response.getMensaje().startsWith("ERROR")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, response.getMensaje(), null));
            }
            return ResponseEntity.ok(new ApiResponse<>(true, response.getMensaje(), null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Error en la operación: " + e.getMessage(), null));
        }
    }
}

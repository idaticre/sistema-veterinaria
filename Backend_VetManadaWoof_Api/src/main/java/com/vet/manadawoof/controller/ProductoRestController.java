package com.vet.manadawoof.controller;

import com.vet.manadawoof.dtos.request.ProductoRequestDTO;
import com.vet.manadawoof.dtos.response.ApiResponse;
import com.vet.manadawoof.dtos.response.ProductoResponseDTO;
import com.vet.manadawoof.service.ProductoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoRestController {

    private final ProductoService service;

    // GET /api/productos
    // GET /api/productos?proveedor={proveedorId}
    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductoResponseDTO>>> listar(
            @RequestParam(required = false) Long proveedor
    ) {
        try {
            List<ProductoResponseDTO> lista = proveedor != null
                    ? service.listarPorProveedor(proveedor)
                    : service.listar();

            String mensaje = proveedor != null
                    ? "Productos del proveedor obtenidos correctamente"
                    : "Lista de productos obtenida correctamente";

            return ResponseEntity.ok(new ApiResponse<>(true, mensaje, lista));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // POST /api/productos
    @PostMapping
    public ResponseEntity<ApiResponse<ProductoResponseDTO>> registrar(
            @Valid @RequestBody ProductoRequestDTO request
    ) {
        ProductoResponseDTO response = service.registrar(request);
        if (response.getMensaje() != null && response.getMensaje().startsWith("ERROR")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, response.getMensaje(), null));
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, response.getMensaje(), response));
    }

    // PUT /api/productos/{id}
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductoResponseDTO>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ProductoRequestDTO request
    ) {
        ProductoResponseDTO response = service.actualizar(id, request);
        if (response.getMensaje() != null && response.getMensaje().startsWith("ERROR")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, response.getMensaje(), null));
        }
        return ResponseEntity.ok(new ApiResponse<>(true, response.getMensaje(), response));
    }

    // DELETE /api/productos/{id} (lógico)
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductoResponseDTO>> eliminar(@PathVariable Long id) {
        try {
            ProductoResponseDTO response = service.eliminar(id);
            if (response.getMensaje() != null && response.getMensaje().startsWith("ERROR")) {
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

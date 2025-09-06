package com.vet.manadawoof.controller;

import com.vet.manadawoof.dtos.response.EntidadResponseDTO;
import com.vet.manadawoof.dtos.request.ProveedorRequestDTO;
import com.vet.manadawoof.entity.ProveedorEntity;
import com.vet.manadawoof.service.ProveedorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/proveedores")
@RequiredArgsConstructor
public class ProveedorRestController {

    private final ProveedorService service;

    @PostMapping("/registrar")
    public ResponseEntity<EntidadResponseDTO> registrar(@RequestBody ProveedorRequestDTO dto) {
        EntidadResponseDTO response = service.registrarProveedor(dto);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/actualizar")
    public ResponseEntity<EntidadResponseDTO> actualizar(@RequestBody ProveedorRequestDTO dto) {
        EntidadResponseDTO response = service.actualizarProveedor(dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProveedorEntity> obtenerPorId(@PathVariable Integer id) {
        ProveedorEntity proveedor = service.findById(id);
        return proveedor != null ? ResponseEntity.ok(proveedor) :
                ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<List<ProveedorEntity>> listar() {
        return ResponseEntity.ok(service.findAll());
    }
}

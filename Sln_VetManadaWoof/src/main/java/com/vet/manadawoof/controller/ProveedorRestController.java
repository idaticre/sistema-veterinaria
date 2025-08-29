package com.vet.manadawoof.controller;

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
    public ResponseEntity<String> registrar(@RequestBody ProveedorEntity proveedor) {
        String mensaje = service.registrarProveedor(proveedor);
        return ResponseEntity.ok(mensaje);
    }

    @PutMapping("/actualizar")
    public ResponseEntity<String> actualizar(@RequestBody ProveedorEntity proveedor) {
        String mensaje = service.actualizarProveedor(proveedor);
        return ResponseEntity.ok(mensaje);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProveedorEntity> obtenerPorId(@PathVariable Long id) {
        ProveedorEntity proveedor = service.findById(id);
        return proveedor != null ? ResponseEntity.ok(proveedor) : ResponseEntity.notFound().build();
    }

    @GetMapping("/listar")
    public ResponseEntity<List<ProveedorEntity>> listar() {
        return ResponseEntity.ok(service.findAll());
    }
}
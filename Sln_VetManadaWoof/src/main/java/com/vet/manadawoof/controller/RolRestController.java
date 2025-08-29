package com.vet.manadawoof.controller;

import com.vet.manadawoof.entity.RolEntity;
import com.vet.manadawoof.service.RolService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RolRestController {

    private final RolService service;

    @PostMapping("/crear")
    public ResponseEntity<String> crear(@RequestBody RolEntity rol) {
        return ResponseEntity.ok(service.crearRol(rol));
    }

    @GetMapping("/listar")
    public ResponseEntity<List<RolEntity>> listar() {
        return ResponseEntity.ok(service.listarRoles());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RolEntity> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.listarRoles().stream()
                .filter(r -> r.getId().equals(id))
                .findFirst()
                .orElse(null));
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<String> actualizar(@PathVariable Long id, @RequestBody RolEntity rol) {
        rol.setId(id);
        return ResponseEntity.ok(service.actualizarRol(rol));
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        return ResponseEntity.ok(service.eliminarRol(id));
    }
}

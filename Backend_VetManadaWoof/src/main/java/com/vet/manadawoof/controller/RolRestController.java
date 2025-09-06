package com.vet.manadawoof.controller;

import com.vet.manadawoof.entity.RolEntity;
import com.vet.manadawoof.service.impl.RolServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RolRestController {

    private final RolServiceImpl service;

    @PostMapping("/crear")
    public ResponseEntity<String> crear(@RequestBody RolEntity rol) {
        String mensaje = service.crearRol(rol);
        return ResponseEntity.ok(mensaje);
    }

    @GetMapping
    public ResponseEntity<List<RolEntity>> listar() {
        List<RolEntity> roles = service.listarRoles();
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RolEntity> obtener(@PathVariable Integer id)
    {
        return service.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<String> actualizar(
            @PathVariable Integer id,
            @RequestBody RolEntity rol)
    {
        rol.setId(id);
        String mensaje = service.actualizarRol(rol);
        return ResponseEntity.ok(mensaje);
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Integer id) {
        String mensaje = service.eliminarRol(id);
        return ResponseEntity.ok(mensaje);
    }
}

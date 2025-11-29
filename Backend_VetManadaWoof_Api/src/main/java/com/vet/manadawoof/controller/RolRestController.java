package com.vet.manadawoof.controller;

import com.vet.manadawoof.entity.RolEntity;
import com.vet.manadawoof.service.RolService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RolRestController {
    
    private final RolService service;
    
    @PostMapping
    public ResponseEntity<RolEntity> crear(@RequestBody RolEntity request) {
        RolEntity creado = service.crearRol(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<RolEntity> actualizar(
            @PathVariable Integer id,
            @RequestBody RolEntity request
    ) {
        RolEntity actualizado = service.actualizarRol(id, request);
        return ResponseEntity.ok(actualizado);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Integer id) {
        service.eliminarRol(id);
        return ResponseEntity.ok("RolEntity eliminado correctamente");
    }
    
    @GetMapping
    public ResponseEntity<List<RolEntity>> listar() {
        List<RolEntity> lista = service.listarRoles();
        if(lista.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(lista);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<RolEntity> obtener(@PathVariable Integer id) {
        RolEntity rol = service.obtenerPorId(id);
        return ResponseEntity.ok(rol);
    }
}

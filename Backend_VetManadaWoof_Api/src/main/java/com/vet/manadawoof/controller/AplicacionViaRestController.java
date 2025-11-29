package com.vet.manadawoof.controller;

import com.vet.manadawoof.entity.AplicacionViaEntity;
import com.vet.manadawoof.service.AplicacionViaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/via-aplicacion")
@RequiredArgsConstructor
public class AplicacionViaRestController {
    
    private final AplicacionViaService service;
    
    // Crear una nueva vía de aplicación
    @PostMapping
    public ResponseEntity<AplicacionViaEntity> crear(@RequestBody AplicacionViaEntity entity) {
        AplicacionViaEntity creado = service.crear(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }
    
    // Actualizar una vía de aplicación existente
    @PutMapping
    public ResponseEntity<AplicacionViaEntity> actualizar(@RequestBody AplicacionViaEntity entity) {
        AplicacionViaEntity actualizado = service.actualizar(entity); return ResponseEntity.ok(actualizado);
    }
    
    // Eliminar una vía de aplicación por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Integer id) {
        String mensaje = service.eliminar(id); return ResponseEntity.ok(mensaje);
    }
    
    // Listar todas las vías de aplicación
    @GetMapping
    public ResponseEntity<List<AplicacionViaEntity>> listar() {
        List<AplicacionViaEntity> list = service.listar(); return ResponseEntity.ok(list);
    }
    
    // Obtener una vía de aplicación por su ID
    @GetMapping("/{id}")
    public ResponseEntity<AplicacionViaEntity> obtener(@PathVariable Integer id) {
        AplicacionViaEntity entity = service.obtenerPorId(id); return ResponseEntity.ok(entity);
    }
}

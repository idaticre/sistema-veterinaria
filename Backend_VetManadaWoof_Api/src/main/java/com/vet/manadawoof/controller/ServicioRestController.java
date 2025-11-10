package com.vet.manadawoof.controller;

import com.vet.manadawoof.entity.ServicioEntity;
import com.vet.manadawoof.service.ServicioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/servicios")
@RequiredArgsConstructor
public class ServicioRestController {
    private final ServicioService service;
    
    @PostMapping
    public ResponseEntity<ServicioEntity> crear(@RequestBody ServicioEntity entity) {
        ServicioEntity creado = service.crear(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }
    
    @PutMapping
    public ResponseEntity<ServicioEntity> actualizar(@RequestBody ServicioEntity entity) {
        ServicioEntity actualizado = service.actualizar(entity); return ResponseEntity.ok(actualizado);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Integer id) {
        String mensaje = service.eliminar(id); return ResponseEntity.ok(mensaje);
    }
    
    @GetMapping
    public ResponseEntity<List<ServicioEntity>> listar() {
        List<ServicioEntity> list = service.listar(); return ResponseEntity.ok(list);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ServicioEntity> obtener(@PathVariable Integer id) {
        ServicioEntity entity = service.obtenerPorId(id); return ResponseEntity.ok(entity);
    }
}

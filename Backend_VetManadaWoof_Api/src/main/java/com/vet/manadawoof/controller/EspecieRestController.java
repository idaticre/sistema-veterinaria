package com.vet.manadawoof.controller;

import com.vet.manadawoof.entity.EspecieEntity;
import com.vet.manadawoof.service.EspecieService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/especies")
@RequiredArgsConstructor
public class EspecieRestController {
    
    private final EspecieService service;
    
    @PostMapping
    public ResponseEntity<EspecieEntity> crear(@RequestBody EspecieEntity entity) {
        EspecieEntity creado = service.crear(entity); return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }
    
    @PutMapping
    public ResponseEntity<EspecieEntity> actualizar(@RequestBody EspecieEntity entity) {
        EspecieEntity actualizado = service.actualizar(entity); return ResponseEntity.ok(actualizado);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Integer id) {
        String mensaje = service.eliminar(id); return ResponseEntity.ok(mensaje);
    }
    
    @GetMapping
    public ResponseEntity<List<EspecieEntity>> listar() {
        List<EspecieEntity> list = service.listar(); return ResponseEntity.ok(list);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<EspecieEntity> obtener(@PathVariable Integer id) {
        EspecieEntity entity = service.obtenerPorId(id); return ResponseEntity.ok(entity);
    }
}

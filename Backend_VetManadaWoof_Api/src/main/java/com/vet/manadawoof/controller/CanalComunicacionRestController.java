package com.vet.manadawoof.controller;

import com.vet.manadawoof.entity.CanalComunicacionEntity;
import com.vet.manadawoof.service.CanalComunicacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/canales-comunicacion")
@RequiredArgsConstructor
public class CanalComunicacionRestController {
    
    private final CanalComunicacionService service;
    
    @PostMapping
    public ResponseEntity<CanalComunicacionEntity> crear(@RequestBody CanalComunicacionEntity entity) {
        CanalComunicacionEntity creado = service.crear(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }
    
    @PutMapping
    public ResponseEntity<CanalComunicacionEntity> actualizar(@RequestBody CanalComunicacionEntity entity) {
        CanalComunicacionEntity actualizado = service.actualizar(entity);
        return ResponseEntity.ok(actualizado);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Integer id) {
        String mensaje = service.eliminar(id);
        return ResponseEntity.ok(mensaje);
    }
    
    @GetMapping
    public ResponseEntity<List<CanalComunicacionEntity>> listar() {
        List<CanalComunicacionEntity> list = service.listar();
        return ResponseEntity.ok(list);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<CanalComunicacionEntity> obtenerPorId(@PathVariable Integer id) {
        CanalComunicacionEntity entity = service.obtenerPorId(id);
        return ResponseEntity.ok(entity);
    }
}

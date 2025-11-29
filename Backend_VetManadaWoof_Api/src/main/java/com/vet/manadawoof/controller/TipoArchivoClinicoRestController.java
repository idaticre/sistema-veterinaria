package com.vet.manadawoof.controller;

import com.vet.manadawoof.entity.TipoArchivoClinicoEntity;
import com.vet.manadawoof.service.TipoArchivoClinicoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/tipos-archivo-clinico")
@RequiredArgsConstructor
public class TipoArchivoClinicoRestController {
    
    private final TipoArchivoClinicoService service;
    
    @PostMapping
    public ResponseEntity<TipoArchivoClinicoEntity> crear(@RequestBody TipoArchivoClinicoEntity entity) {
        TipoArchivoClinicoEntity creado = service.crear(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }
    
    @PutMapping
    public ResponseEntity<TipoArchivoClinicoEntity> actualizar(@RequestBody TipoArchivoClinicoEntity entity) {
        TipoArchivoClinicoEntity actualizado = service.actualizar(entity); return ResponseEntity.ok(actualizado);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Integer id) {
        String mensaje = service.eliminar(id); return ResponseEntity.ok(mensaje);
    }
    
    @GetMapping
    public ResponseEntity<List<TipoArchivoClinicoEntity>> listar() {
        return ResponseEntity.ok(service.listar());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<TipoArchivoClinicoEntity> obtener(@PathVariable Integer id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }
}

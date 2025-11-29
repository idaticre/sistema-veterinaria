package com.vet.manadawoof.controller;

import com.vet.manadawoof.entity.EstadoHistoriaClinicaEntity;
import com.vet.manadawoof.service.EstadoHistoriaClinicaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/estados-historia-clinica")
@RequiredArgsConstructor
public class EstadoHistoriaClinicaRestController {
    
    private final EstadoHistoriaClinicaService service;
    
    @PostMapping
    public ResponseEntity<EstadoHistoriaClinicaEntity> crear(@RequestBody EstadoHistoriaClinicaEntity entity) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(entity));
    }
    
    @PutMapping
    public ResponseEntity<EstadoHistoriaClinicaEntity> actualizar(@RequestBody EstadoHistoriaClinicaEntity entity) {
        return ResponseEntity.ok(service.actualizar(entity));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Integer id) {
        return ResponseEntity.ok(service.eliminar(id));
    }
    
    @GetMapping
    public ResponseEntity<List<EstadoHistoriaClinicaEntity>> listar() {
        return ResponseEntity.ok(service.listar());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<EstadoHistoriaClinicaEntity> obtener(@PathVariable Integer id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }
}

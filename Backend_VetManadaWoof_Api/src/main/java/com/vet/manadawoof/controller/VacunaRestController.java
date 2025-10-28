package com.vet.manadawoof.controller;

import com.vet.manadawoof.entity.VacunaEntity;
import com.vet.manadawoof.service.VacunaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/vacunas")
@RequiredArgsConstructor
public class VacunaRestController {
    // Servicio con la lógica de negocio de vacunas
    private final VacunaService service;
    
    // Crear una nueva vacuna
    @PostMapping
    public ResponseEntity<VacunaEntity> crear(@RequestBody VacunaEntity entity) {
        VacunaEntity creado = service.crear(entity); return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }
    
    // Actualizar una vacuna existente
    @PutMapping
    public ResponseEntity<VacunaEntity> actuaizar(@RequestBody VacunaEntity entity) {
        VacunaEntity actualizado = service.actualizar(entity); return ResponseEntity.ok(actualizado);
    }
    
    // Eliminar una vacuna por su ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Integer id) {
        String mensaje = service.eliminar(id); return ResponseEntity.ok(mensaje);
    }
    
    // Listar todas las vacunas registradas
    @GetMapping
    public ResponseEntity<List<VacunaEntity>> listar() {
        List<VacunaEntity> list = service.listar(); return ResponseEntity.ok(list);
    }
    
    // Obtener una vacuna por su ID
    @GetMapping("/{id}")
    public ResponseEntity<VacunaEntity> obtener(@PathVariable Integer id) {
        VacunaEntity entity = service.obtenerPorId(id); return ResponseEntity.ok(entity);
    }
    
}

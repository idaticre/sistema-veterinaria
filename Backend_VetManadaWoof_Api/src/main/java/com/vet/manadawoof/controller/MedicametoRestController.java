package com.vet.manadawoof.controller;

import com.vet.manadawoof.entity.MedicamentoEntity;
import com.vet.manadawoof.service.MedicamentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Controlador REST para gestión de medicamentos
@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/medicamentos")
@RequiredArgsConstructor
public class MedicametoRestController {
    
    // Servicio con la lógica de negocio
    private final MedicamentoService service;
    
    // Crear medicamento
    @PostMapping
    public ResponseEntity<MedicamentoEntity> crear(@RequestBody MedicamentoEntity entity) {
        MedicamentoEntity creado = service.crear(entity); return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }
    
    // Actualizar medicamento
    @PutMapping
    public ResponseEntity<MedicamentoEntity> actuaizar(@RequestBody MedicamentoEntity entity) {
        MedicamentoEntity actualizado = service.actualizar(entity); return ResponseEntity.ok(actualizado);
    }
    
    // Eliminar medicamento por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Integer id) {
        String mensaje = service.eliminar(id); return ResponseEntity.ok(mensaje);
    }
    
    // Listar medicamentos
    @GetMapping
    public ResponseEntity<List<MedicamentoEntity>> listar() {
        List<MedicamentoEntity> list = service.listar(); return ResponseEntity.ok(list);
    }
    
    // Obtener medicamento por ID
    @GetMapping("/{id}")
    public ResponseEntity<MedicamentoEntity> obtener(@PathVariable Integer id) {
        MedicamentoEntity entity = service.obtenerPorId(id); return ResponseEntity.ok(entity);
    }
}

package com.vet.manadawoof.controller;

import com.vet.manadawoof.entity.EstadoAsistenciaEntity;
import com.vet.manadawoof.service.EstadoAsistenciaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Controlador REST para gestión de estados de asistencia
@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/estado-asistencia")
@RequiredArgsConstructor
public class EstadoAsistenciaRestController {
    
    // Servicio con la lógica de negocio
    private final EstadoAsistenciaService service;
    
    // Crear estado de asistencia
    @PostMapping
    public ResponseEntity<EstadoAsistenciaEntity> crear(@RequestBody EstadoAsistenciaEntity entity) {
        EstadoAsistenciaEntity creado = service.crear(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }
    
    // Actualizar estado de asistencia
    @PutMapping
    public ResponseEntity<EstadoAsistenciaEntity> actuaizar(@RequestBody EstadoAsistenciaEntity entity) {
        EstadoAsistenciaEntity actualizado = service.actualizar(entity); return ResponseEntity.ok(actualizado);
    }
    
    // Eliminar estado de estado por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Integer id) {
        String mensaje = service.eliminar(id); return ResponseEntity.ok(mensaje);
    }
    
    // Listar estados de asistencia
    @GetMapping
    public ResponseEntity<List<EstadoAsistenciaEntity>> listar() {
        List<EstadoAsistenciaEntity> list = service.listar(); return ResponseEntity.ok(list);
    }
    
    // Obtener estado de asistencia por ID
    @GetMapping("/{id}")
    public ResponseEntity<EstadoAsistenciaEntity> obtener(@PathVariable Integer id) {
        EstadoAsistenciaEntity entity = service.obtenerPorId(id); return ResponseEntity.ok(entity);
    }
}

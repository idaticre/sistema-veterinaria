package com.vet.manadawoof.controller;

import com.vet.manadawoof.entity.EstadoMascotaEntity;
import com.vet.manadawoof.service.EstadoMascotaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Controlador REST para gestión de estados de mascota
@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/estado-mascota")
@RequiredArgsConstructor
public class EstadoMascotaRestController {
    
    // Servicio con la lógica de negocio
    private final EstadoMascotaService service;
    
    // Crear estado de mascota
    @PostMapping
    public ResponseEntity<EstadoMascotaEntity> crear(@RequestBody EstadoMascotaEntity entity) {
        EstadoMascotaEntity creado = service.crear(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }
    
    // Actualizar estado de mascota
    @PutMapping
    public ResponseEntity<EstadoMascotaEntity> actuaizar(@RequestBody EstadoMascotaEntity entity) {
        EstadoMascotaEntity actualizado = service.actualizar(entity); return ResponseEntity.ok(actualizado);
    }
    
    // Eliminar estado de mascota por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Integer id) {
        String mensaje = service.eliminar(id); return ResponseEntity.ok(mensaje);
    }
    
    // Listar estados de mascota
    @GetMapping
    public ResponseEntity<List<EstadoMascotaEntity>> listar() {
        List<EstadoMascotaEntity> list = service.listar(); return ResponseEntity.ok(list);
    }
    
    // Obtener estado de mascota por ID
    @GetMapping("/{id}")
    public ResponseEntity<EstadoMascotaEntity> obtener(@PathVariable Integer id) {
        EstadoMascotaEntity entity = service.obtenerPorId(id); return ResponseEntity.ok(entity);
    }
}

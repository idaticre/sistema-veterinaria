package com.vet.manadawoof.controller;

import com.vet.manadawoof.entity.EstadoAgendaEntity;
import com.vet.manadawoof.service.EstadoAgendaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/estados-agenda")
@RequiredArgsConstructor
public class EstadoAgendaRestController {
    private final EstadoAgendaService service;
    
    @PostMapping
    public ResponseEntity<EstadoAgendaEntity> crear(@RequestBody EstadoAgendaEntity entity) {
        EstadoAgendaEntity creado = service.crear(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }
    
    @PutMapping
    public ResponseEntity<EstadoAgendaEntity> actualizar(@RequestBody EstadoAgendaEntity entity) {
        EstadoAgendaEntity actualizado = service.actualizar(entity);
        return ResponseEntity.ok(actualizado);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Integer id) {
        String mensaje = service.eliminar(id);
        return ResponseEntity.ok(mensaje);
    }
    
    @GetMapping
    public ResponseEntity<List<EstadoAgendaEntity>> listar() {
        List<EstadoAgendaEntity> list = service.listar();
        return ResponseEntity.ok(list);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<EstadoAgendaEntity> obtener(@PathVariable Integer id) {
        EstadoAgendaEntity entity = service.obtenerPorId(id);
        return ResponseEntity.ok(entity);
    }
}

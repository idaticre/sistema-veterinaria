package com.vet.manadawoof.controller;

import com.vet.manadawoof.entity.RecordatorioAgendaEntity;
import com.vet.manadawoof.service.RecordatorioAgendaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/recordatorios-agenda")
@RequiredArgsConstructor
public class RecordatorioAgendaRestController {
    
    private final RecordatorioAgendaService service;
    
    @PostMapping
    public ResponseEntity<RecordatorioAgendaEntity> crear(@RequestBody RecordatorioAgendaEntity entity) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(entity));
    }
    
    @PutMapping
    public ResponseEntity<RecordatorioAgendaEntity> actualizar(@RequestBody RecordatorioAgendaEntity entity) {
        return ResponseEntity.ok(service.actualizar(entity));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        return ResponseEntity.ok(service.eliminar(id));
    }
    
    @GetMapping
    public ResponseEntity<List<RecordatorioAgendaEntity>> listar() {
        return ResponseEntity.ok(service.listar());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<RecordatorioAgendaEntity> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }
}

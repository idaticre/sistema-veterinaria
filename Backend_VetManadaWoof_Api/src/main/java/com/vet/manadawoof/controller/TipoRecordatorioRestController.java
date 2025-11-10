package com.vet.manadawoof.controller;

import com.vet.manadawoof.entity.TipoRecordatorioEntity;
import com.vet.manadawoof.service.TipoRecordatorioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/tipos-recordatorio")
@RequiredArgsConstructor
public class TipoRecordatorioRestController {
    private final TipoRecordatorioService service;
    
    @PostMapping
    public ResponseEntity<TipoRecordatorioEntity> crear(@RequestBody TipoRecordatorioEntity entity) {
        TipoRecordatorioEntity creado = service.crear(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }
    
    @PutMapping
    public ResponseEntity<TipoRecordatorioEntity> actualizar(@RequestBody TipoRecordatorioEntity entity) {
        TipoRecordatorioEntity actualizado = service.actualizar(entity);
        return ResponseEntity.ok(actualizado);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Integer id) {
        String mensaje = service.eliminar(id);
        return ResponseEntity.ok(mensaje);
    }
    
    @GetMapping
    public ResponseEntity<List<TipoRecordatorioEntity>> listar() {
        List<TipoRecordatorioEntity> list = service.listar();
        return ResponseEntity.ok(list);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<TipoRecordatorioEntity> obtener(@PathVariable Integer id) {
        TipoRecordatorioEntity entity = service.obtenerPorId(id);
        return ResponseEntity.ok(entity);
    }
    
}

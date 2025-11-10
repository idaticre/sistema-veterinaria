package com.vet.manadawoof.controller;

import com.vet.manadawoof.entity.MedioSolicitudEntity;
import com.vet.manadawoof.service.MedioSolicitudService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/medios-solicitud")
@RequiredArgsConstructor
public class MedioSolicitudRestController {
    private final MedioSolicitudService service;
    
    @PostMapping
    public ResponseEntity<MedioSolicitudEntity> crear(@RequestBody MedioSolicitudEntity entity) {
        MedioSolicitudEntity creado = service.crear(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }
    
    @PutMapping
    public ResponseEntity<MedioSolicitudEntity> actualizar(@RequestBody MedioSolicitudEntity entity) {
        MedioSolicitudEntity actualizado = service.actualizar(entity); return ResponseEntity.ok(actualizado);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Integer id) {
        String mensaje = service.eliminar(id); return ResponseEntity.ok(mensaje);
    }
    
    @GetMapping
    public ResponseEntity<List<MedioSolicitudEntity>> listar() {
        List<MedioSolicitudEntity> list = service.listar(); return ResponseEntity.ok(list);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<MedioSolicitudEntity> obtener(@PathVariable Integer id) {
        MedioSolicitudEntity entity = service.obtenerPorId(id); return ResponseEntity.ok(entity);
    }
}

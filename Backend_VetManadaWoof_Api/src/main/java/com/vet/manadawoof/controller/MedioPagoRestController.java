package com.vet.manadawoof.controller;

import com.vet.manadawoof.entity.MedioPagoEntity;
import com.vet.manadawoof.service.MedioPagoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/medios-pago")
@RequiredArgsConstructor
public class MedioPagoRestController {
    private final MedioPagoService service;
    
    @PostMapping
    public ResponseEntity<MedioPagoEntity> crear(@RequestBody MedioPagoEntity entity) {
        MedioPagoEntity creado = service.crear(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }
    
    @PutMapping
    public ResponseEntity<MedioPagoEntity> actualizar(@RequestBody MedioPagoEntity entity) {
        MedioPagoEntity actualizado = service.actualizar(entity);
        return ResponseEntity.ok(actualizado);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Integer id) {
        String mensaje = service.eliminar(id);
        return ResponseEntity.ok(mensaje);
    }
    
    @GetMapping
    public ResponseEntity<List<MedioPagoEntity>> listar() {
        List<MedioPagoEntity> list = service.listar();
        return ResponseEntity.ok(list);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<MedioPagoEntity> obtener(@PathVariable Integer id) {
        MedioPagoEntity entity = service.obtenerPorId(id);
        return ResponseEntity.ok(entity);
    }
}

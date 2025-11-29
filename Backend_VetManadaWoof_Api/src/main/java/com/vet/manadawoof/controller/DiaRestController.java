package com.vet.manadawoof.controller;

import com.vet.manadawoof.entity.DiaEntity;
import com.vet.manadawoof.service.DiaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/dias")
@RequiredArgsConstructor
public class DiaRestController {
    
    private final DiaService service;
    
    @PostMapping
    public ResponseEntity<DiaEntity> crear(@RequestBody DiaEntity entity) {
        DiaEntity creado = service.crearDia(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }
    
    @PutMapping
    public ResponseEntity<DiaEntity> actualizar(@RequestBody DiaEntity entity) {
        DiaEntity actualizado = service.actualizarDia(entity);
        return ResponseEntity.ok(actualizado);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Integer id) {
        String mensaje = service.eliminarDia(id);
        return ResponseEntity.ok(mensaje);
    }
    
    @GetMapping
    public ResponseEntity<List<DiaEntity>> listar() {
        List<DiaEntity> dias = service.listarDias();
        return ResponseEntity.ok(dias);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<DiaEntity> obtener(@PathVariable Integer id) {
        DiaEntity dia = service.obtenerPorId(id);
        return ResponseEntity.ok(dia);
    }
}

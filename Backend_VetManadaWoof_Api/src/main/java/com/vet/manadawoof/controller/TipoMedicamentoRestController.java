package com.vet.manadawoof.controller;

import com.vet.manadawoof.entity.TipoMedicamentoEntity;
import com.vet.manadawoof.service.TipoMedicamentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/tipoMedicamento")
@RequiredArgsConstructor
public class TipoMedicamentoRestController {
    
    // Inyección del servicio de tipo de medicamento
    private final TipoMedicamentoService service;
    
    // Crear un nuevo tipo de medicamento
    @PostMapping
    public ResponseEntity<TipoMedicamentoEntity> crear(@RequestBody TipoMedicamentoEntity entity) {
        TipoMedicamentoEntity creado = service.crear(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }
    
    // Actualizar un tipo de medicamento existente
    @PutMapping
    public ResponseEntity<TipoMedicamentoEntity> actualizar(@RequestBody TipoMedicamentoEntity entity) {
        TipoMedicamentoEntity actualizado = service.actualizar(entity);
        return ResponseEntity.ok(actualizado);
    }
    
    // Eliminar un tipo de medicamento por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Integer id) {
        String mensaje = service.eliminar(id);
        return ResponseEntity.ok(mensaje);
    }
    
    // Listar todos los tipos de medicamento
    @GetMapping
    public ResponseEntity<List<TipoMedicamentoEntity>> listar() {
        List<TipoMedicamentoEntity> list = service.listar();
        return ResponseEntity.ok(list);
    }
    
    // Obtener un tipo de medicamento por su ID
    @GetMapping("/{id}")
    public ResponseEntity<TipoMedicamentoEntity> obtener(@PathVariable Integer id) {
        TipoMedicamentoEntity entity = service.obtenerPorId(id);
        return ResponseEntity.ok(entity);
    }
}

package com.vet.manadawoof.controller;

import com.vet.manadawoof.entity.TamanoMascEntity;
import com.vet.manadawoof.service.TamanoMascService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/tamanos")
@RestController
public class TamanoMascRestController {
    
    // Servicio que maneja la lógica de negocio relacionada con los tamaños
    private final TamanoMascService service;
    
    @PostMapping
    // Crea un nuevo tamaño de mascota y devuelve el registro creado
    public ResponseEntity<TamanoMascEntity> crear(@RequestBody TamanoMascEntity entity) {
        TamanoMascEntity creado = service.crear(entity); return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }
    
    @PutMapping
    // Actualiza la información de un tamaño existente
    public ResponseEntity<TamanoMascEntity> actualizar(@RequestBody TamanoMascEntity entity) {
        TamanoMascEntity actualizado = service.actualizar(entity); return ResponseEntity.ok(actualizado);
    }
    
    @DeleteMapping("/{id}")
    // Elimina un tamaño según su identificador
    public ResponseEntity<String> eliminar(@PathVariable Integer id) {
        String mensaje = service.eliminar(id); return ResponseEntity.ok(mensaje);
    }
    
    @GetMapping
    // Devuelve una lista con todos los tamaños registrados
    public ResponseEntity<List<TamanoMascEntity>> listar() {
        List<TamanoMascEntity> list = service.listar(); return ResponseEntity.ok(list);
    }
    
    @GetMapping("/{id}")
    // Obtiene un tamaño específico por su ID
    public ResponseEntity<TamanoMascEntity> obtener(@PathVariable Integer id) {
        TamanoMascEntity entity = service.obtenerPorId(id); return ResponseEntity.ok(entity);
    }
}

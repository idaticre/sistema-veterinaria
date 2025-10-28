package com.vet.manadawoof.controller;

import com.vet.manadawoof.entity.EtapaVidaEntity;
import com.vet.manadawoof.service.EtapaVidaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/etapasVida")
@RestController
public class EtapaRestController {
    
    // Servicio que maneja la lógica de negocio para etapas de vida
    private final EtapaVidaService service;
    
    // Crea una nueva etapa de vida
    @PostMapping
    public ResponseEntity<EtapaVidaEntity> crear(@RequestBody EtapaVidaEntity entity) {
        EtapaVidaEntity creado = service.crear(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }
    
    // Actualiza una etapa de vida existente
    @PutMapping
    public ResponseEntity<EtapaVidaEntity> actualizar(@RequestBody EtapaVidaEntity entity) {
        EtapaVidaEntity actualizado = service.actualizar(entity);
        return ResponseEntity.ok(actualizado);
    }
    
    // Elimina una etapa de vida por su ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Integer id) {
        String mensaje = service.eliminar(id);
        return ResponseEntity.ok(mensaje);
    }
    
    // Lista todas las etapas de vida registradas
    @GetMapping
    public ResponseEntity<List<EtapaVidaEntity>> listar() {
        List<EtapaVidaEntity> list = service.listar();
        return ResponseEntity.ok(list);
    }
    
    // Obtiene una etapa de vida específica por su ID
    @GetMapping("/{id}")
    public ResponseEntity<EtapaVidaEntity> obtener(@PathVariable Integer id) {
        EtapaVidaEntity entity = service.obtenerPorId(id);
        return ResponseEntity.ok(entity);
    }
}

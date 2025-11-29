package com.vet.manadawoof.controller;

import com.vet.manadawoof.entity.RazaEntity;
import com.vet.manadawoof.service.RazaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/razas")
@RequiredArgsConstructor
public class RazaRestController {
    
    // Servicio encargado de manejar la lógica relacionada con las razas
    private final RazaService service;
    
    @PostMapping
    // Crea una nueva raza y la devuelve con estado HTTP 201
    public ResponseEntity<RazaEntity> crear(@RequestBody RazaEntity entity) {
        RazaEntity creado = service.crear(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }
    
    @PutMapping
    // Actualiza los datos de una raza existente
    public ResponseEntity<RazaEntity> actualizar(@RequestBody RazaEntity entity) {
        RazaEntity actualizado = service.actualizar(entity);
        return ResponseEntity.ok(actualizado);
    }
    
    @DeleteMapping("/{id}")
    // Elimina una raza por su identificador y devuelve un mensaje de confirmación
    public ResponseEntity<String> eliminar(@PathVariable Integer id) {
        String mensaje = service.eliminar(id);
        return ResponseEntity.ok(mensaje);
    }
    
    @GetMapping
    // Devuelve la lista completa de razas registradas
    public ResponseEntity<List<RazaEntity>> listar() {
        List<RazaEntity> list = service.listar();
        return ResponseEntity.ok(list);
    }
    
    @GetMapping("/{id}")
    // Obtiene los datos de una raza específica según su ID
    public ResponseEntity<RazaEntity> obtener(@PathVariable Integer id) {
        RazaEntity entity = service.obtenerPorId(id);
        return ResponseEntity.ok(entity);
    }
}

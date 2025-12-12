package com.vet.manadawoof.controller;

import com.vet.manadawoof.entity.EspecialidadEntity;
import com.vet.manadawoof.service.EspecialidadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/especialidades")
@RequiredArgsConstructor
public class EspecialidadRestController {
    
    private final EspecialidadService service;
    
    @PostMapping
    public ResponseEntity<EspecialidadEntity> crear(@RequestBody EspecialidadEntity entity) {
        EspecialidadEntity creado = service.crearEspecialidad(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }
    
    @PutMapping
    public ResponseEntity<EspecialidadEntity> actualizar(@RequestBody EspecialidadEntity entity) {
        EspecialidadEntity actualizado = service.actualizarEspecialidad(entity);
        return ResponseEntity.ok(actualizado);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Integer id) {
        String mensaje = service.eliminarEspecialidad(id);
        return ResponseEntity.ok(mensaje);
    }
    
    @GetMapping
    public ResponseEntity<List<EspecialidadEntity>> listar() {
        List<EspecialidadEntity> list = service.listarEspecialidades();
        return ResponseEntity.ok(list);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<EspecialidadEntity> obtener(@PathVariable Integer id) {
        EspecialidadEntity entity = service.obtenerPorId(id);
        return ResponseEntity.ok(entity);
    }
}

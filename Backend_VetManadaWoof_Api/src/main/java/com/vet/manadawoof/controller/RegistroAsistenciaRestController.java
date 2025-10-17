package com.vet.manadawoof.controller;

import com.vet.manadawoof.entity.RegistroAsistenciaEntity;
import com.vet.manadawoof.service.RegistroAsistenciaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/registro-asistencia")
@RequiredArgsConstructor
public class RegistroAsistenciaRestController {

    private final RegistroAsistenciaService service;

    @PostMapping
    public ResponseEntity<RegistroAsistenciaEntity> crear(@RequestBody RegistroAsistenciaEntity entity) {
        RegistroAsistenciaEntity creado = service.crearRegistro(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping
    public ResponseEntity<RegistroAsistenciaEntity> actualizar(@RequestBody RegistroAsistenciaEntity entity) {
        RegistroAsistenciaEntity actualizado = service.actualizarRegistro(entity);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Integer id) {
        String mensaje = service.eliminarRegistro(id);
        return ResponseEntity.ok(mensaje);
    }

    @GetMapping
    public ResponseEntity<List<RegistroAsistenciaEntity>> listar() {
        return ResponseEntity.ok(service.listarRegistros());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RegistroAsistenciaEntity> obtener(@PathVariable Integer id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }
}

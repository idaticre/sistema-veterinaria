package com.vet.manadawoof.controller;

import com.vet.manadawoof.entity.HorarioTrabajoEntity;
import com.vet.manadawoof.service.HorarioTrabajoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/horarios-trabajo")
@RequiredArgsConstructor
public class HorarioTrabajoRestController {

    private final HorarioTrabajoService service;

    @PostMapping
    public ResponseEntity<HorarioTrabajoEntity> crear(@RequestBody HorarioTrabajoEntity entity) {
        HorarioTrabajoEntity creado = service.crearHorario(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping
    public ResponseEntity<HorarioTrabajoEntity> actualizar(@RequestBody HorarioTrabajoEntity entity) {
        HorarioTrabajoEntity actualizado = service.actualizarHorario(entity);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Integer id) {
        String mensaje = service.eliminarHorario(id);
        return ResponseEntity.ok(mensaje);
    }

    @GetMapping
    public ResponseEntity<List<HorarioTrabajoEntity>> listar() {
        return ResponseEntity.ok(service.listarHorarios());
    }

    @GetMapping("/{id}")
    public ResponseEntity<HorarioTrabajoEntity> obtener(@PathVariable Integer id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }
}

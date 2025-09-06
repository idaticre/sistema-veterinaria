package com.vet.manadawoof.controller;

import com.vet.manadawoof.entity.TipoEntidadEntity;
import com.vet.manadawoof.service.TipoEntidadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/tipo-entidad")
@RequiredArgsConstructor
public class TipoEntidadRestController {

    private final TipoEntidadService service;

    @PostMapping("/crear")
    public ResponseEntity<String> crear(
            @RequestParam String nombre, @RequestParam Boolean activo) {
        String mensaje = service.crearTipoEntidad(nombre, activo);
        return ResponseEntity.ok(mensaje);
    }

    @PutMapping("/actualizar")
    public ResponseEntity<String> actualizar(
            @RequestParam Integer id, @RequestParam String nombre, @RequestParam Boolean activo) {
        String mensaje = service.actualizarTipoEntidad(id, nombre, activo);
        return ResponseEntity.ok(mensaje);
    }

    @DeleteMapping("/eliminar")
    public ResponseEntity<String> eliminar(@RequestParam Integer id) {
        String mensaje = service.eliminarTipoEntidad(id);
        return ResponseEntity.ok(mensaje);
    }

    @GetMapping
    public ResponseEntity<List<TipoEntidadEntity>> listar(
            @RequestParam(required = false) Integer id) {
        List<TipoEntidadEntity> lista = service.listarTipoEntidad(id);
        return ResponseEntity.ok(lista);
    }
}

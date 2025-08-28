package com.vet.manadawoof.controller;

import com.vet.manadawoof.entity.TipoEntidadEntity;
import com.vet.manadawoof.service.TipoEntidadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tipo-entidad")
@RequiredArgsConstructor
public class TipoEntidadRestController {

    private final TipoEntidadService service;

    @PostMapping("/crear")
    public ResponseEntity<String> crear(@RequestParam String nombre, @RequestParam Boolean activo) {
        String mensaje = service.crearTipoEntidad(nombre, activo);
        return ResponseEntity.ok(mensaje);
    }

    @PutMapping("/actualizar")
    public ResponseEntity<String> actualizar(@RequestParam Long id, @RequestParam String nombre, @RequestParam Boolean activo) {
        String mensaje = service.actualizarTipoEntidad(id, nombre, activo);
        return ResponseEntity.ok(mensaje);
    }

    @DeleteMapping("/eliminar")
    public ResponseEntity<String> eliminar(@RequestParam Long id) {
        String mensaje = service.eliminarTipoEntidad(id);
        return ResponseEntity.ok(mensaje);
    }

    @GetMapping("/listar")
    public ResponseEntity<List<TipoEntidadEntity>> listar(@RequestParam(required = false) Long id) {
        List<TipoEntidadEntity> lista = service.listarTipoEntidad(id);
        return ResponseEntity.ok(lista);
    }
}

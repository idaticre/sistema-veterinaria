package com.vet.manadawoof.controller;

import com.vet.manadawoof.entity.TipoDiaEntity;
import com.vet.manadawoof.service.TipoDiaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/tipos-dia")
@RequiredArgsConstructor
public class TipoDiaRestController {

    private final TipoDiaService service;

    @PostMapping
    public ResponseEntity<TipoDiaEntity> crear(@RequestBody TipoDiaEntity entity) {
        TipoDiaEntity creado = service.crearTipoDia(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping
    public ResponseEntity<TipoDiaEntity> actualizar(@RequestBody TipoDiaEntity entity) {
        TipoDiaEntity actualizado = service.actualizarTipoDia(entity);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Integer id) {
        String mensaje = service.eliminarTipoDia(id);
        return ResponseEntity.ok(mensaje);
    }

    @GetMapping
    public ResponseEntity<List<TipoDiaEntity>> listar() {
        List<TipoDiaEntity> lista = service.listarTiposDia();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoDiaEntity> obtener(@PathVariable Integer id) {
        TipoDiaEntity tipo = service.obtenerPorId(id);
        return ResponseEntity.ok(tipo);
    }
}

package com.vet.manadawoof.controller;

import com.vet.manadawoof.entity.TipoDiaEntity;
import com.vet.manadawoof.service.TipoDiaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tipos-dia")
@RequiredArgsConstructor
public class TipoDiaRestController {

    private final TipoDiaService service;

    @PostMapping
    public ResponseEntity<String> crearTipoDia(@RequestBody TipoDiaEntity tipoDia) {
        return ResponseEntity.ok(service.crearTipoDia(tipoDia));
    }

    @PutMapping
    public ResponseEntity<String> actualizarTipoDia(@RequestBody TipoDiaEntity tipoDia) {
        return ResponseEntity.ok(service.actualizarTipoDia(tipoDia));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarTipoDia(@PathVariable Long id) {
        return ResponseEntity.ok(service.eliminarTipoDia(id));
    }

    @GetMapping
    public ResponseEntity<List<TipoDiaEntity>> listarTiposDia() {
        return ResponseEntity.ok(service.listarTiposDia());
    }
}

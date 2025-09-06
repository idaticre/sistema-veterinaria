package com.vet.manadawoof.controller;

import com.vet.manadawoof.dtos.response.TipoDiaResponseDTO;
import com.vet.manadawoof.entity.TipoDiaEntity;
import com.vet.manadawoof.service.TipoDiaService;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<TipoDiaResponseDTO> crear(@RequestBody TipoDiaEntity tipoDia) {
        return ResponseEntity.ok(service.crearTipoDia(tipoDia));
    }

    @PutMapping
    public ResponseEntity<TipoDiaResponseDTO> actualizar(@RequestBody TipoDiaEntity tipoDia) {
        return ResponseEntity.ok(service.actualizarTipoDia(tipoDia));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<TipoDiaResponseDTO> eliminar(@PathVariable Integer id) {
        return ResponseEntity.ok(service.eliminarTipoDia(id));
    }

    @GetMapping
    public ResponseEntity<List<TipoDiaResponseDTO>> listar() {
        return ResponseEntity.ok(service.listarTiposDia());
    }
}

package com.vet.manadawoof.controller;

import com.vet.manadawoof.dtos.request.DiaRequestDTO;
import com.vet.manadawoof.dtos.response.DiaResponseDTO;
import com.vet.manadawoof.service.DiaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/dias")
@RequiredArgsConstructor
public class DiaRestController {

    private final DiaService service;

    @PostMapping("/crear")
    public ResponseEntity<DiaResponseDTO> crear(@RequestBody DiaRequestDTO dto) {
        return ResponseEntity.ok(service.crearDia(dto));
    }

    @PutMapping("/actualizar")
    public ResponseEntity<DiaResponseDTO> actualizar(@RequestBody DiaRequestDTO dto) {
        return ResponseEntity.ok(service.actualizarDia(dto));
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<DiaResponseDTO> eliminar(@PathVariable Integer id) {
        return ResponseEntity.ok(service.eliminarDia(id));
    }

    @GetMapping
    public ResponseEntity<List<DiaResponseDTO>> listar() {
        return ResponseEntity.ok(service.listarDias());
    }
}

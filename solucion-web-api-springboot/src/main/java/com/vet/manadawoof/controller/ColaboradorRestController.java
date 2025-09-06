package com.vet.manadawoof.controller;

import com.vet.manadawoof.dtos.request.ColaboradorRequestDTO;
import com.vet.manadawoof.dtos.response.ColaboradorResponseDTO;
import com.vet.manadawoof.service.ColaboradorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/colaboradores")
@RequiredArgsConstructor

public class ColaboradorRestController {

    private final ColaboradorService service;

    @PostMapping("/registrar")
    public ResponseEntity<ColaboradorResponseDTO> registrar(@RequestBody ColaboradorRequestDTO dto) {
        return ResponseEntity.ok(service.registrarColaborador(dto));
    }

    @PutMapping("/actualizar")
    public ResponseEntity<ColaboradorResponseDTO> actualizar(@RequestBody ColaboradorRequestDTO dto) {
        return ResponseEntity.ok(service.actualizarColaborador(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ColaboradorResponseDTO> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping
    public ResponseEntity<List<ColaboradorResponseDTO>> listar() {
        return ResponseEntity.ok(service.listarColaboradores());
    }
}

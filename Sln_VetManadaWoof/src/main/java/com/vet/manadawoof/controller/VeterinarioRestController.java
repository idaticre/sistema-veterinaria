package com.vet.manadawoof.controller;

import com.vet.manadawoof.dtos.request.VeterinarioRequestDTO;
import com.vet.manadawoof.dtos.response.VeterinarioResponseDTO;
import com.vet.manadawoof.service.VeterinarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/veterinarios")
@RequiredArgsConstructor
public class VeterinarioRestController {

    private final VeterinarioService service;

    @PostMapping("/registrar")
    public ResponseEntity<VeterinarioResponseDTO> registrar(@RequestBody VeterinarioRequestDTO dto) {
        return ResponseEntity.ok(service.registrarVeterinario(dto));
    }

    @PutMapping("/actualizar")
    public ResponseEntity<VeterinarioResponseDTO> actualizar(@RequestBody VeterinarioRequestDTO dto) {
        return ResponseEntity.ok(service.actualizarVeterinario(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<VeterinarioResponseDTO> obtenerPorId(@PathVariable Integer id) {
        VeterinarioResponseDTO v = service.obtenerPorId(id);
        return v != null ? ResponseEntity.ok(v) : ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<List<VeterinarioResponseDTO>> listar() {
        return ResponseEntity.ok(service.listar());
    }
}

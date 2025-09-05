package com.vet.manadawoof.controller;

import com.vet.manadawoof.dtos.response.EspecialidadResponseDTO;
import com.vet.manadawoof.entity.EspecialidadEntity;
import com.vet.manadawoof.service.EspecialidadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/especialidades")
@RequiredArgsConstructor
public class EspecialidadRestController {

    private final EspecialidadService service;

    @PostMapping("/crear")
    public ResponseEntity<EspecialidadResponseDTO> crear(@RequestBody EspecialidadEntity especialidad)
    {
        return ResponseEntity.ok(service.crearEspecialidad(especialidad));
    }

    @PutMapping("/actualizar")
    public ResponseEntity<EspecialidadResponseDTO> actualizar(@RequestBody EspecialidadEntity especialidad)
    {
        return ResponseEntity.ok(service.actualizarEspecialidad(especialidad));
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<EspecialidadResponseDTO> eliminar(@PathVariable Integer id)
    {
        return ResponseEntity.ok(service.eliminarEspecialidad(id));
    }

    @GetMapping
    public ResponseEntity<List<EspecialidadResponseDTO>> listar()
    {
        return ResponseEntity.ok(service.listarEspecialidades());
    }
}

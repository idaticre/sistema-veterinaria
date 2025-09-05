package com.vet.manadawoof.controller;

import com.vet.manadawoof.dtos.request.EntidadRequestDTO;
import com.vet.manadawoof.dtos.response.EntidadResponseDTO;
import com.vet.manadawoof.entity.EntidadEntity;
import com.vet.manadawoof.service.EntidadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/entidades")
@RequiredArgsConstructor
public class EntidadRestController {

    private final EntidadService service;

    @PostMapping("/registrar")
    public ResponseEntity<EntidadResponseDTO> registrar(@RequestBody EntidadRequestDTO dto) {
        EntidadResponseDTO response = service.registrarEntidad(dto);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/actualizar")
    public ResponseEntity<EntidadResponseDTO> actualizar(@RequestBody EntidadRequestDTO dto) {
        EntidadResponseDTO response = service.actualizarEntidad(dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntidadResponseDTO> obtener(@PathVariable Integer id) {
        EntidadEntity entidad = service.findById(id);
        if (entidad == null) {
            return ResponseEntity.notFound().build();
        }
        EntidadResponseDTO response = EntidadResponseDTO.builder()
                .idEntidad(entidad.getId())
                .codigoEntidad(entidad.getCodigo())
                .nombre(entidad.getNombre())
                .correo(entidad.getCorreo())
                .telefono(entidad.getTelefono())
                .ciudad(entidad.getCiudad())
                .distrito(entidad.getDistrito())
                .representante(entidad.getRepresentante())
                .mensaje("Entidad encontrada")
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<EntidadResponseDTO>> listar() {
        List<EntidadResponseDTO> lista = service.findAll().stream()
                .map(entidad -> EntidadResponseDTO.builder()
                        .idEntidad(entidad.getId())
                        .codigoEntidad(entidad.getCodigo())
                        .nombre(entidad.getNombre())
                        .correo(entidad.getCorreo())
                        .telefono(entidad.getTelefono())
                        .ciudad(entidad.getCiudad())
                        .distrito(entidad.getDistrito())
                        .representante(entidad.getRepresentante())
                        .mensaje("Entidad listada")
                        .build())
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }
}

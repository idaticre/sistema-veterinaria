package com.vet.manadawoof.controller;

import com.vet.manadawoof.dtos.request.UsuarioRolRequestDTO;
import com.vet.manadawoof.dtos.response.ApiResponse;
import com.vet.manadawoof.dtos.response.UsuarioRolResponseDTO;
import com.vet.manadawoof.service.UsuarioRolService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios-roles")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class UsuarioRolRestController {
    
    private final UsuarioRolService service;
    
    @PostMapping("/asignar")
    public ResponseEntity<ApiResponse<UsuarioRolResponseDTO>> asignar(@RequestBody UsuarioRolRequestDTO dto) {
        dto.setAccion("ASIGNAR"); UsuarioRolResponseDTO response = service.ejecutarAccion(dto);
        
        if(response.getMensaje() != null && response.getMensaje().startsWith("ERROR:")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, response.getMensaje(), null));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(true, response.getMensaje(), response));
    }
    
    /**
     * Lista todos los usuarios con los roles asignados.
     * Ideal para vista administrativa (gestión interna).
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<UsuarioRolResponseDTO>>> listar() {
        List<UsuarioRolResponseDTO> lista = service.listar();
        return ResponseEntity.ok(new ApiResponse<>(true, "Lista de usuarios y sus roles", lista));
    }
    
    /**
     * Lista todos los roles asignados a un usuario específico.
     */
    @GetMapping("/{idUsuario}")
    public ResponseEntity<ApiResponse<List<UsuarioRolResponseDTO>>> listarPorUsuario(@PathVariable Integer idUsuario) {
        List<UsuarioRolResponseDTO> lista = service.listarPorUsuario(idUsuario);
        return ResponseEntity.ok(new ApiResponse<>(true, "Roles asignados al usuario", lista));
    }
    
    /**
     * Actualiza la asignación de un rol a un usuario existente.
     * Usa la acción 'ACTUALIZAR' del SP.
     */
    @PutMapping("/actualizar")
    public ResponseEntity<ApiResponse<UsuarioRolResponseDTO>> actualizar(@RequestBody UsuarioRolRequestDTO dto) {
        dto.setAccion("ACTUALIZAR"); UsuarioRolResponseDTO response = service.ejecutarAccion(dto);
        
        if(response.getMensaje() != null && response.getMensaje().startsWith("ERROR:")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, response.getMensaje(), null));
        } return ResponseEntity.ok(new ApiResponse<>(true, response.getMensaje(), response));
    }
    
    /**
     * Elimina una asignación usuario-rol (acción 'ELIMINAR').
     */
    @DeleteMapping("/eliminar")
    public ResponseEntity<ApiResponse<UsuarioRolResponseDTO>> eliminar(@RequestBody UsuarioRolRequestDTO dto) {
        try {
            UsuarioRolResponseDTO response = service.eliminar(dto.getIdUsuario(), dto.getIdRol());
            if(response.getMensaje() != null && response.getMensaje().startsWith("ERROR:")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, response.getMensaje(), null));
            } return ResponseEntity.ok(new ApiResponse<>(true, response.getMensaje(), null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, "Error en la operación: " + e.getMessage(), null));
        }
    }
    
    
}


package com.vet.manadawoof.controller;

import com.vet.manadawoof.dtos.response.JwtResponseDTO;
import com.vet.manadawoof.dtos.request.LoginRequestDTO;
import com.vet.manadawoof.service.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {
    
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    
    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequestDTO loginRequest) {
        
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtService.generateToken(authentication);
        
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority :: getAuthority)
                .collect(Collectors.toList());
        
        // Extraer ID del usuario (necesitarías modificar UserDetails para incluir el ID)
        // Por simplicidad, aquí se retorna 0, deberías implementar la lógica para obtener el ID real
        Long userId = 0L;
        
        return ResponseEntity.ok(new JwtResponseDTO(jwt, userId, userDetails.getUsername(), roles));
    }
    
    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser() {
        // En JWT stateless, el logout se maneja en el cliente eliminando el token
        return ResponseEntity.ok("Logout exitoso");
    }
    
    @GetMapping("/validate")
    public ResponseEntity<?> validateToken() {
        // Si el token es válido, el filtro ya habrá establecido la autenticación
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null && authentication.isAuthenticated()) {
            return ResponseEntity.ok("Token válido");
        }
        return ResponseEntity.status(401).body("Token inválido");
    }
}


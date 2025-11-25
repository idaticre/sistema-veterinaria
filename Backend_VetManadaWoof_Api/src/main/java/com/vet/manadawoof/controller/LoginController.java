package com.vet.manadawoof.controller;

import com.vet.manadawoof.service.LoginService;

import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/login")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class LoginController {
    
    private final LoginService loginService;
    
    @PostMapping
    public ResponseEntity<?> login(@RequestBody Map<String, String> credenciales) {
        String username = credenciales.get("usuario");
        String password = credenciales.get("password");
        
        var usuarioOpt = loginService.verificarUsuario(username, password);
        
        if(usuarioOpt.isPresent()) {
            return ResponseEntity.ok(Map.of(
                    "message", "Login exitoso",
                    "success", true
            ));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "message", "UsuarioEntity o contraseña incorrectos",
                    "success", false
            ));
        }
    }
    
}

//401 y HttpStatus.UNAUTHORIZED son lo mismo pero uno es mas tecnico

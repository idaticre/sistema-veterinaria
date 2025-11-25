
package com.vet.manadawoof.service;

import com.vet.manadawoof.entity.UsuarioEntity;
import com.vet.manadawoof.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
//import org.springdoc.security.cryto.bcrypt.BCryptPasswordEncoder;

@Service
public class LoginService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    //private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public Optional<UsuarioEntity> verificarUsuario(String username, String password) {
        Optional<UsuarioEntity> usuarioOpt = usuarioRepository.findByUsername(username);

        if(usuarioOpt.isPresent()) {
            UsuarioEntity usuario = usuarioOpt.get();
            if(usuario.getPasswordHash().equals(password)) {
                return Optional.of(usuario);
            }
        }
        return Optional.empty();
    }
}

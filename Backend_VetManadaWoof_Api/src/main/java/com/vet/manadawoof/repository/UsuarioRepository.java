package com.vet.manadawoof.repository;

import com.vet.manadawoof.entity.UsuarioEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Integer> {
    
    //añadido para el login
    Optional<UsuarioEntity> findByUsername(String username);
    
}

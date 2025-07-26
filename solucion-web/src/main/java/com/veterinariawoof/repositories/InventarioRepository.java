package com.veterinariawoof.repositories;

import com.veterinariawoof.models.Inventario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventarioRepository extends JpaRepository<Inventario, Integer> {}

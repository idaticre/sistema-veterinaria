package com.veterinariawoof.repositories;

import com.veterinariawoof.models.Especie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EspecieRepository extends JpaRepository<Especie, Long> {
}

package com.veterinariawoof.services;

import com.veterinariawoof.models.CajaGeneral;
import com.veterinariawoof.repositories.CajaGeneralRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CajaGeneralService {

    private final CajaGeneralRepository repository;

    public CajaGeneralService(CajaGeneralRepository repository) {
        this.repository = repository;
    }

    public List<CajaGeneral> findAll() {
        return repository.findAll();
    }

    public CajaGeneral save(CajaGeneral movimiento) {
        return repository.save(movimiento);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}

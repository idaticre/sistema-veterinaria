package com.veterinariawoof.services.impl;

import com.veterinariawoof.models.Inventario;
import com.veterinariawoof.repositories.InventarioRepository;
import com.veterinariawoof.services.InventarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventarioServiceImpl implements InventarioService {
    @Autowired
    private InventarioRepository inventarioRepo;

    @Override
    public List<Inventario> listar() {return inventarioRepo.findAll();}

    @Override
    public Inventario obtenerPorId(int id) {return inventarioRepo.findById(id).orElse(null);}

    @Override
    public Inventario guardar(Inventario inventario) {return inventarioRepo.save(inventario);}

    @Override
    public void eliminar(int id) {inventarioRepo.deleteById(id);}
}

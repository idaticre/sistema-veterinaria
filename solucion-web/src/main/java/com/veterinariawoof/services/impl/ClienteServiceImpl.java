package com.veterinariawoof.services.impl;

import com.veterinariawoof.models.Cliente;
import com.veterinariawoof.repositories.ClienteRepository;
import com.veterinariawoof.services.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteServiceImpl implements ClienteService {

    @Autowired
    private ClienteRepository clienteRepo;

    @Override
    public List<Cliente> obtenerTodos() {
        return clienteRepo.findAll();
    }

    @Override
    public Cliente obtenerPorId(Long id) {
        return clienteRepo.findById(id).orElse(null);
    }

    @Override
    public Cliente guardar(Cliente cliente) {
        return clienteRepo.save(cliente);
    }

    @Override
    public void eliminar(Long id) {
        clienteRepo.deleteById(id);
    }
}

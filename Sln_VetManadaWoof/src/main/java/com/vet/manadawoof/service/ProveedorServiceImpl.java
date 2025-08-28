package com.vet.manadawoof.service;

import com.vet.manadawoof.entity.ProveedorEntity;
import com.vet.manadawoof.repository.ProveedorRepository;
import com.vet.manadawoof.service.ProveedorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProveedorServiceImpl implements ProveedorService {

    private final ProveedorRepository repository;

    @Override
    public String registrarProveedor(ProveedorEntity proveedor) {
        return repository.registrarProveedor(
                proveedor.getEntidad().getIdEntidad(),
                proveedor.getActivo()
        );
    }

    @Override
    public String actualizarProveedor(ProveedorEntity proveedor) {
        return repository.actualizarProveedor(
                proveedor.getId(),
                proveedor.getEntidad().getIdEntidad(),
                proveedor.getActivo()
        );
    }

    @Override
    public ProveedorEntity findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public List<ProveedorEntity> findAll() {
        return repository.findAll();
    }
}

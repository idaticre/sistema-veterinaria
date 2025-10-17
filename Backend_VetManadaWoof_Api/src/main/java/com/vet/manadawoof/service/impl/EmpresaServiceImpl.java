package com.vet.manadawoof.service.impl;

import com.vet.manadawoof.entity.EmpresaEntity;
import com.vet.manadawoof.repository.EmpresaRepository;
import com.vet.manadawoof.service.EmpresaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmpresaServiceImpl implements EmpresaService {

    private final EmpresaRepository repository;

    @Override
    public List<EmpresaEntity> listarEmpresas() {
        return repository.findAll();
    }

    @Override
    public EmpresaEntity obtenerEmpresa(Integer id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public String actualizarEmpresa(EmpresaEntity empresa) {
        repository.save(empresa);
        return "Empresa actualizada correctamente";
    }
}

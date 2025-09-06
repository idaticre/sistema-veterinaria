package com.vet.manadawoof.service.impl;

import com.vet.manadawoof.entity.EmpresaEntity;
import com.vet.manadawoof.repository.EmpresaRepository;
import com.vet.manadawoof.service.EmpresaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmpresaServiceImpl implements EmpresaService {

    private final EmpresaRepository repository;

    @Override
    @Transactional
    public List<EmpresaEntity> listarEmpresas() {
        return repository.spEmpresa(
                "READ",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null);
    }

    @Override
    @Transactional
    public EmpresaEntity obtenerEmpresa(Integer id) {
        List<EmpresaEntity> result = repository.spEmpresa(
                "READ", id,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null);
        return result.isEmpty() ? null : result.get(0);
    }

    @Override
    @Transactional
    public String actualizarEmpresa(EmpresaEntity empresa) {
        List<EmpresaEntity> result = repository.spEmpresa(
                "UPDATE",
                empresa.getId(),
                empresa.getRazonSocial(),
                empresa.getRuc(),
                empresa.getDireccion(),
                empresa.getCiudad(),
                empresa.getDistrito(),
                empresa.getTelefono(),
                empresa.getCorreo(),
                empresa.getRepresentante(),
                empresa.getLogoEmpresa()
        );
        return result.isEmpty() ? "No se actualizó la empresa." : "Empresa actualizada correctamente.";
    }
}

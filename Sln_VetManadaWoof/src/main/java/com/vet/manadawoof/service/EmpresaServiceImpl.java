package com.vet.manadawoof.service;

import com.vet.manadawoof.entity.EmpresaEntity;
import com.vet.manadawoof.repository.EmpresaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmpresaServiceImpl implements EmpresaService {

    private final EmpresaRepository repository;

    @Override
    public List<EmpresaEntity> listarEmpresas() {
        return repository.spEmpresaList("READ", null, null, null, null, null, null, null, null, null, null);
    }

    @Override
    public EmpresaEntity obtenerEmpresa(Long id) {
        List<EmpresaEntity> result = repository.spEmpresaList("READ", id, null, null, null, null, null, null, null, null, null);
        return result.isEmpty() ? null : result.get(0);
    }

    @Override
    public String actualizarEmpresa(EmpresaEntity empresa) {
        return repository.spEmpresaUpdate(
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
    }
}

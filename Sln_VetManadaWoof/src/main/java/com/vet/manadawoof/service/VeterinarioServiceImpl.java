/*package com.vet.manadawoof.service;

import com.vet.manadawoof.entity.VeterinarioEntity;
import com.vet.manadawoof.repository.VeterinarioRepository;
import com.vet.manadawoof.service.VeterinarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VeterinarioServiceImpl implements VeterinarioService {

    private final VeterinarioRepository repository;

    @Override
    public String registrarVeterinario(VeterinarioEntity veterinario) {
        return repository.registrarVeterinario(
                veterinario.getColaborador().getId(),
                veterinario.getEspecialidad().getId(),
                veterinario.getCodigo(),
                veterinario.getCmp(),
                veterinario.getActivo()
        );
    }

    @Override
    public String actualizarVeterinario(VeterinarioEntity veterinario) {
        return repository.actualizarVeterinario(
                veterinario.getId(),
                veterinario.getColaborador().getId(),
                veterinario.getEspecialidad().getId(),
                veterinario.getCodigo(),
                veterinario.getCmp(),
                veterinario.getActivo()
        );
    }

    @Override
    public VeterinarioEntity findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public List<VeterinarioEntity> findAll() {
        return repository.findAll();
    }
}
*/
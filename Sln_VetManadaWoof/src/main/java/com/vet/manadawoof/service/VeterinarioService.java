package com.vet.manadawoof.service;

import com.vet.manadawoof.entity.VeterinarioEntity;

import java.util.List;

public interface VeterinarioService {
    String registrarVeterinario(VeterinarioEntity veterinario);

    String actualizarVeterinario(VeterinarioEntity veterinario);

    VeterinarioEntity findById(Long id);

    List<VeterinarioEntity> findAll();
}

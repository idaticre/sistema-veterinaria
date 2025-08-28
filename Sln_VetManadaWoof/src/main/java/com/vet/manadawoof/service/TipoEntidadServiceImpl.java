package com.vet.manadawoof.service;

import com.vet.manadawoof.entity.TipoEntidadEntity;
import com.vet.manadawoof.repository.TipoEntidadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TipoEntidadServiceImpl implements TipoEntidadService {

    private final TipoEntidadRepository repository;

    @Override
    public String crearTipoEntidad(String nombre, Boolean activo) {
        return repository.spTipoEntidad("CREATE", null, nombre, activo);
    }

    @Override
    public String actualizarTipoEntidad(Long id, String nombre, Boolean activo) {
        return repository.spTipoEntidad("UPDATE", id, nombre, activo);
    }

    @Override
    public String eliminarTipoEntidad(Long id) {
        return repository.spTipoEntidad("DELETE", id, null, null);
    }

    @Override
    public List<TipoEntidadEntity> listarTipoEntidad(Long id) {
        repository.spTipoEntidad("READ", id, null, null);
        // Retorna lista con findAll como respaldo, SP gestiona validaciones y mensajes
        return repository.findAll();
    }
}

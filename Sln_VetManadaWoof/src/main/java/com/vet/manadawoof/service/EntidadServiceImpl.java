package com.vet.manadawoof.service;

import com.vet.manadawoof.entity.EntidadEntity;
import com.vet.manadawoof.repository.EntidadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EntidadServiceImpl implements EntidadService {

    private final EntidadRepository repository;

    @Override
    public String registrarEntidad(EntidadEntity entidad) {
        return repository.registrarEntidad(
                entidad.getTipoEntidad().getId(),
                entidad.getTipoPersonaJuridica().getId(),
                entidad.getNombre(),
                entidad.getSexo(),
                entidad.getDocumento(),
                entidad.getTipoDocumento().getId(),
                entidad.getCorreo(),
                entidad.getTelefono(),
                entidad.getDireccion(),
                entidad.getCiudad(),
                entidad.getDistrito(),
                entidad.getRepresentante()
        );
    }

    @Override
    public String actualizarEntidad(EntidadEntity entidad) {
        return repository.actualizarEntidad(
                entidad.getId(),
                entidad.getTipoPersonaJuridica().getId(),
                entidad.getNombre(),
                entidad.getSexo(),
                entidad.getDocumento(),
                entidad.getTipoDocumento().getId(),
                entidad.getCorreo(),
                entidad.getTelefono(),
                entidad.getDireccion(),
                entidad.getCiudad(),
                entidad.getDistrito(),
                entidad.getRepresentante(),
                entidad.getActivo()
        );
    }

    @Override
    public EntidadEntity findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public List<EntidadEntity> findAll() {
        return repository.findAll();
    }
}

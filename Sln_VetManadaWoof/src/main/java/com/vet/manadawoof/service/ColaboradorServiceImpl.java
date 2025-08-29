package com.vet.manadawoof.service;

import com.vet.manadawoof.entity.ColaboradorEntity;
import com.vet.manadawoof.repository.ColaboradorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ColaboradorServiceImpl implements ColaboradorService {

    private final ColaboradorRepository repository;

    @Override
    public String registrarColaborador(ColaboradorEntity c) {
        return repository.spColaboradorRegistrar(
                c.getEntidad().getTipoPersonaJuridica().getId(),
                c.getEntidad().getNombre(),
                c.getEntidad().getSexo(),
                c.getEntidad().getDocumento(),
                c.getEntidad().getTipoDocumento().getId(),
                c.getEntidad().getCorreo(),
                c.getEntidad().getTelefono(),
                c.getEntidad().getDireccion(),
                c.getEntidad().getCiudad(),
                c.getEntidad().getDistrito(),
                c.getFechaIngreso(),
                c.getEntidad().getId(), // id_usuario
                c.getFoto()
        );
    }

    @Override
    public String actualizarColaborador(ColaboradorEntity c) {
        return repository.spColaboradorActualizar(
                c.getEntidad().getId(),
                c.getEntidad().getTipoPersonaJuridica().getId(),
                c.getEntidad().getNombre(),
                c.getEntidad().getSexo(),
                c.getEntidad().getDocumento(),
                c.getEntidad().getTipoDocumento().getId(),
                c.getEntidad().getCorreo(),
                c.getEntidad().getTelefono(),
                c.getEntidad().getDireccion(),
                c.getEntidad().getCiudad(),
                c.getEntidad().getDistrito(),
                c.getFechaIngreso(),
                c.getEntidad().getId(),
                c.getFoto(),
                c.getActivo()
        );
    }

    @Override
    public ColaboradorEntity findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public List<ColaboradorEntity> findAll() {
        return repository.findAll();
    }
}

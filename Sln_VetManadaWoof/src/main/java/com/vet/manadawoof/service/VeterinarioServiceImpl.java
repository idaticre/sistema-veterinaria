package com.vet.manadawoof.service;

import com.vet.manadawoof.entity.VeterinarioEntity;
import com.vet.manadawoof.repository.VeterinarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VeterinarioServiceImpl implements VeterinarioService {

    private final VeterinarioRepository repository;

    @Override
    public String registrarVeterinario(VeterinarioEntity v) {
        return repository.registrarVeterinario(
                v.getColaborador().getEntidad().getId(),
                v.getColaborador().getEntidad().getTipoPersonaJuridica().getId(),
                v.getColaborador().getEntidad().getNombre(),
                v.getColaborador().getEntidad().getSexo(),
                v.getColaborador().getEntidad().getDocumento(),
                v.getColaborador().getEntidad().getTipoDocumento().getId(),
                v.getColaborador().getEntidad().getCorreo(),
                v.getColaborador().getEntidad().getTelefono(),
                v.getColaborador().getEntidad().getDireccion(),
                v.getColaborador().getEntidad().getCiudad(),
                v.getColaborador().getEntidad().getDistrito(),
                v.getColaborador().getEntidad().getRepresentante(),
                v.getIdEspecificoEspecialidadId(), // ajustar según relación
                v.getCmp()
        );
    }

    @Override
    public String actualizarVeterinario(VeterinarioEntity v) {
        return repository.actualizarVeterinario(
                v.getColaborador().getEntidad().getId(),
                v.getColaborador().getEntidad().getTipoPersonaJuridica().getId(),
                v.getColaborador().getEntidad().getNombre(),
                v.getColaborador().getEntidad().getSexo(),
                v.getColaborador().getEntidad().getDocumento(),
                v.getColaborador().getEntidad().getTipoDocumento().getId(),
                v.getColaborador().getEntidad().getCorreo(),
                v.getColaborador().getEntidad().getTelefono(),
                v.getColaborador().getEntidad().getDireccion(),
                v.getColaborador().getEntidad().getCiudad(),
                v.getColaborador().getEntidad().getDistrito(),
                v.getColaborador().getEntidad().getRepresentante(),
                v.getColaborador().getId(),
                v.getColaborador().getFoto(),
                v.getEspecialidad().getId(),
                v.getCmp(),
                v.getActivo()
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

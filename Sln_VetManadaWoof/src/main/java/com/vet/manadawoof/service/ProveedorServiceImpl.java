package com.vet.manadawoof.service;

import com.vet.manadawoof.entity.ProveedorEntity;
import com.vet.manadawoof.repository.ProveedorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class ProveedorServiceImpl implements ProveedorService {

    private final ProveedorRepository repository;

    private Integer safeId(Long id) {
        return id != null ? id.intValue() : null;
    }

    @Override
    public String registrarProveedor(ProveedorEntity proveedor) {
        return repository.registrarProveedor(
                safeId(proveedor.getEntidad().getTipoPersonaJuridica() != null ? proveedor.getEntidad().getTipoPersonaJuridica().getId() : null),
                proveedor.getEntidad().getNombre(),
                proveedor.getEntidad().getSexo(),
                proveedor.getEntidad().getDocumento(),
                safeId(proveedor.getEntidad().getTipoDocumento() != null ? proveedor.getEntidad().getTipoDocumento().getId() : null),
                proveedor.getEntidad().getCorreo(),
                proveedor.getEntidad().getTelefono(),
                proveedor.getEntidad().getDireccion(),
                proveedor.getEntidad().getCiudad(),
                proveedor.getEntidad().getDistrito(),
                proveedor.getEntidad().getRepresentante()
        );
    }

    @Override
    public String actualizarProveedor(ProveedorEntity proveedor) {
        return repository.actualizarProveedor(
                proveedor.getEntidad().getId(),
                safeId(proveedor.getEntidad().getTipoPersonaJuridica() != null ? proveedor.getEntidad().getTipoPersonaJuridica().getId() : null),
                proveedor.getEntidad().getNombre(),
                proveedor.getEntidad().getSexo(),
                proveedor.getEntidad().getDocumento(),
                safeId(proveedor.getEntidad().getTipoDocumento() != null ? proveedor.getEntidad().getTipoDocumento().getId() : null),
                proveedor.getEntidad().getCorreo(),
                proveedor.getEntidad().getTelefono(),
                proveedor.getEntidad().getDireccion(),
                proveedor.getEntidad().getCiudad(),
                proveedor.getEntidad().getDistrito(),
                proveedor.getEntidad().getRepresentante(),
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

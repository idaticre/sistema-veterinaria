package com.vet.manadawoof.service;

import com.vet.manadawoof.entity.ClienteEntity;
import com.vet.manadawoof.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository repository;

    @Override
    public String registrarCliente(ClienteEntity cliente) {
        Integer tipoPersonaJuridicaId = cliente.getEntidad().getTipoPersonaJuridica() != null
                ? cliente.getEntidad().getTipoPersonaJuridica().getId().intValue()
                : null;
        Integer tipoDocumentoId = cliente.getEntidad().getTipoDocumento() != null
                ? cliente.getEntidad().getTipoDocumento().getId().intValue()
                : null;

        return repository.registrarCliente(
                tipoPersonaJuridicaId,
                cliente.getEntidad().getNombre(),
                cliente.getEntidad().getSexo(),
                cliente.getEntidad().getDocumento(),
                tipoDocumentoId,
                cliente.getEntidad().getCorreo(),
                cliente.getEntidad().getTelefono(),
                cliente.getEntidad().getDireccion(),
                cliente.getEntidad().getCiudad(),
                cliente.getEntidad().getDistrito(),
                null,
                null
        );
    }

    @Override
    public String actualizarCliente(ClienteEntity cliente) {
        Long idEntidad = cliente.getEntidad().getId();

        Integer tipoPersonaJuridicaId = cliente.getEntidad().getTipoPersonaJuridica() != null
                ? cliente.getEntidad().getTipoPersonaJuridica().getId().intValue()
                : null;
        Integer tipoDocumentoId = cliente.getEntidad().getTipoDocumento() != null
                ? cliente.getEntidad().getTipoDocumento().getId().intValue()
                : null;

        return repository.actualizarCliente(
                idEntidad,
                tipoPersonaJuridicaId,
                cliente.getEntidad().getNombre(),
                cliente.getEntidad().getSexo(),
                cliente.getEntidad().getDocumento(),
                tipoDocumentoId,
                cliente.getEntidad().getCorreo(),
                cliente.getEntidad().getTelefono(),
                cliente.getEntidad().getDireccion(),
                cliente.getEntidad().getCiudad(),
                cliente.getEntidad().getDistrito(),
                cliente.getActivo()
        );
    }

    @Override
    public ClienteEntity obtenerPorId(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public List<ClienteEntity> listarClientes() {
        return repository.findAll();
    }
}

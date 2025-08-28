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
                null, // OUT código entidad
                null  // OUT código cliente
        );
    }

    @Override
    public String actualizarCliente(ClienteEntity cliente) {
        // NO convertimos id_entidad a Integer, se mantiene como Long
        Long idEntidad = cliente.getEntidad().getId();

        Integer tipoPersonaJuridicaId = cliente.getEntidad().getTipoPersonaJuridica() != null
                ? cliente.getEntidad().getTipoPersonaJuridica().getId().intValue()
                : null;

        Integer tipoDocumentoId = cliente.getEntidad().getTipoDocumento() != null
                ? cliente.getEntidad().getTipoDocumento().getId().intValue()
                : null;

        return repository.actualizarCliente(
                idEntidad,                 // Long
                tipoPersonaJuridicaId,     // Integer
                cliente.getEntidad().getNombre(),
                cliente.getEntidad().getSexo(),
                cliente.getEntidad().getDocumento(),
                tipoDocumentoId,           // Integer
                cliente.getEntidad().getCorreo(),
                cliente.getEntidad().getTelefono(),
                cliente.getEntidad().getDireccion(),
                cliente.getEntidad().getCiudad(),
                cliente.getEntidad().getDistrito(),
                cliente.getActivo()        // Boolean
        );
    }

    @Override
    public ClienteEntity findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public List<ClienteEntity> findAll() {
        return repository.findAll();
    }
}

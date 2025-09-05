package com.vet.manadawoof.service.impl;

import com.vet.manadawoof.dtos.request.ClienteRequestDTO;
import com.vet.manadawoof.dtos.response.ClienteResponseDTO;
import com.vet.manadawoof.entity.ClienteEntity;
import com.vet.manadawoof.entity.EntidadEntity;
import com.vet.manadawoof.repository.ClienteRepository;
import com.vet.manadawoof.service.ClienteService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository repository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public ClienteResponseDTO registrarCliente(ClienteRequestDTO request) {
        ClienteEntity cliente = new ClienteEntity();
        cliente.setEntidad(entityManager.find(EntidadEntity.class, request.getIdTipoPersonaJuridica()));

        StoredProcedureQuery spQuery = entityManager
                .createNamedStoredProcedureQuery("ClienteEntity.registrarCliente");

        spQuery.setParameter("p_id_tipo_persona_juridica", request.getIdTipoPersonaJuridica().intValue());
        spQuery.setParameter("p_nombre", request.getNombre());
        spQuery.setParameter("p_sexo", request.getSexo());
        spQuery.setParameter("p_documento", request.getDocumento());
        spQuery.setParameter("p_id_tipo_documento", request.getIdTipoDocumento().intValue());
        spQuery.setParameter("p_correo", request.getCorreo());
        spQuery.setParameter("p_telefono", request.getTelefono());
        spQuery.setParameter("p_direccion", request.getDireccion());
        spQuery.setParameter("p_ciudad", request.getCiudad());
        spQuery.setParameter("p_distrito", request.getDistrito());

        spQuery.execute();

        String mensaje = (String) spQuery.getOutputParameterValue("p_mensaje");
        String codigoCliente = (String) spQuery.getOutputParameterValue("p_codigo_cliente");

        return ClienteResponseDTO.builder()
                .codigoCliente(codigoCliente)
                .mensaje(mensaje)
                .build();
    }

    @Override
    @Transactional
    public ClienteResponseDTO actualizarCliente(Integer idCliente, ClienteRequestDTO request) {
        ClienteEntity cliente = repository.findById(idCliente).orElseThrow(
                () -> new RuntimeException("Cliente no encontrado para actualizar")
        );

        StoredProcedureQuery spQuery = entityManager
                .createNamedStoredProcedureQuery("ClienteEntity.actualizarCliente");

        spQuery.setParameter("p_id_entidad", cliente.getEntidad().getId());
        spQuery.setParameter("p_id_tipo_persona_juridica", request.getIdTipoPersonaJuridica().intValue());
        spQuery.setParameter("p_nombre", request.getNombre());
        spQuery.setParameter("p_sexo", request.getSexo());
        spQuery.setParameter("p_documento", request.getDocumento());
        spQuery.setParameter("p_id_tipo_documento", request.getIdTipoDocumento().intValue());
        spQuery.setParameter("p_correo", request.getCorreo());
        spQuery.setParameter("p_telefono", request.getTelefono());
        spQuery.setParameter("p_direccion", request.getDireccion());
        spQuery.setParameter("p_ciudad", request.getCiudad());
        spQuery.setParameter("p_distrito", request.getDistrito());
        spQuery.setParameter("p_activo", request.getActivo());

        spQuery.execute();

        String mensaje = (String) spQuery.getOutputParameterValue("p_mensaje");

        return ClienteResponseDTO.builder()
                .codigoCliente(cliente.getCodigo())
                .mensaje(mensaje)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteEntity obtenerPorId(Integer id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClienteEntity> listarClientes() {
        return repository.findAll();
    }
}

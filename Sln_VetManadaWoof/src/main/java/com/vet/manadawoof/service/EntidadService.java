package com.vet.manadawoof.service;

import com.vet.manadawoof.entity.EntidadEntity;

import java.util.List;

public interface EntidadService {

    String registrarEntidad(EntidadEntity entidad);

    String actualizarEntidad(EntidadEntity entidad);

    EntidadEntity findById(Long id);

    List<EntidadEntity> findAll();
}

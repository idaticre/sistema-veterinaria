package com.vet.manadawoof.service;

import com.vet.manadawoof.entity.RegistroAsistenciaEntity;

import java.util.List;

public interface RegistroAsistenciaService {
    String ejecutarSP(RegistroAsistenciaEntity registro, String accion);

    RegistroAsistenciaEntity findById(Long id);

    List<RegistroAsistenciaEntity> findAll();
}

package com.vet.manadawoof.service;

import com.vet.manadawoof.entity.ColaboradorEntity;

import java.util.List;

public interface ColaboradorService {
    String registrarColaborador(ColaboradorEntity colaborador);

    String actualizarColaborador(ColaboradorEntity colaborador);

    ColaboradorEntity findById(Long id);

    List<ColaboradorEntity> findAll();
}

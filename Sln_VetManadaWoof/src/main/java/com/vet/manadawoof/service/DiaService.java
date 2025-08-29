package com.vet.manadawoof.service;

import com.vet.manadawoof.entity.DiaEntity;

import java.util.List;

public interface DiaService {

    String crearDia(DiaEntity dia);

    String actualizarDia(DiaEntity dia);

    String eliminarDia(Long id);

    List<DiaEntity> listarDias();
}

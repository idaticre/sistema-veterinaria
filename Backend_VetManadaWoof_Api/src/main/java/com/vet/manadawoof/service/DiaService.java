package com.vet.manadawoof.service;

import com.vet.manadawoof.entity.DiaEntity;

import java.util.List;

public interface DiaService {

    DiaEntity crearDia(DiaEntity entity);

    DiaEntity actualizarDia(DiaEntity entity);

    String eliminarDia(Integer id);

    List<DiaEntity> listarDias();

    DiaEntity obtenerPorId(Integer id);
}

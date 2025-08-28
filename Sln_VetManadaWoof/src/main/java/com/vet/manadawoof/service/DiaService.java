package com.vet.manadawoof.service;

import com.vet.manadawoof.entity.DiaEntity;

import java.util.List;

public interface DiaService {
    String spDiasSemana(String accion, Long id, String nombre, Boolean activo);

    List<DiaEntity> findAll();

    DiaEntity findById(Long id);
}

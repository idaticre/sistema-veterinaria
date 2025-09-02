package com.vet.manadawoof.service;

import com.vet.manadawoof.entity.HorarioTrabajoEntity;

import java.util.List;

public interface HorarioTrabajoService {

    HorarioTrabajoEntity findById(Integer id);

    List<HorarioTrabajoEntity> findAll();
}

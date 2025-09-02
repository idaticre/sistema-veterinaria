package com.vet.manadawoof.service.impl;

import com.vet.manadawoof.entity.HorarioTrabajoEntity;
import com.vet.manadawoof.repository.HorarioTrabajoRepository;
import com.vet.manadawoof.service.HorarioTrabajoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HorarioTrabajoServiceImpl implements HorarioTrabajoService {

    private final HorarioTrabajoRepository repository;


    @Override
    @Transactional(readOnly = true)
    public HorarioTrabajoEntity findById(Integer id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HorarioTrabajoEntity> findAll() {
        return repository.findAll();
    }
}

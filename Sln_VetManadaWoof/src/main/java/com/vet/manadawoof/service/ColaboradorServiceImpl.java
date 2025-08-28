package com.vet.manadawoof.service;

import com.vet.manadawoof.entity.ColaboradorEntity;
import com.vet.manadawoof.repository.ColaboradorRepository;
import com.vet.manadawoof.service.ColaboradorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ColaboradorServiceImpl implements ColaboradorService {

    private final ColaboradorRepository repository;

    @Override
    public String registrarColaborador(ColaboradorEntity colaborador) {
        java.sql.Date fechaIngresoSql = new java.sql.Date(colaborador.getFechaIngreso().getTime());
        // El SP genera automáticamente el código
        return repository.registrarColaborador(
                colaborador.getEntidad().getIdEntidad(),
                fechaIngresoSql,
                colaborador.getFoto(),
                colaborador.getActivo()
        );
    }

    @Override
    public String actualizarColaborador(ColaboradorEntity colaborador) {
        java.sql.Date fechaIngresoSql = new java.sql.Date(colaborador.getFechaIngreso().getTime());
        return repository.actualizarColaborador(
                colaborador.getId(),
                colaborador.getEntidad().getIdEntidad(),
                fechaIngresoSql,
                colaborador.getFoto(),
                colaborador.getActivo()
        );
    }

    @Override
    public ColaboradorEntity findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public List<ColaboradorEntity> findAll() {
        return repository.findAll();
    }
}

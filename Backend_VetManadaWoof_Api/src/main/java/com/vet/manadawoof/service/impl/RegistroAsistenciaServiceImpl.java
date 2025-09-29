package com.vet.manadawoof.service.impl;

import com.vet.manadawoof.entity.RegistroAsistenciaEntity;
import com.vet.manadawoof.repository.RegistroAsistenciaRepository;
import com.vet.manadawoof.service.RegistroAsistenciaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RegistroAsistenciaServiceImpl implements RegistroAsistenciaService {

    private final RegistroAsistenciaRepository repository;

    @Override
    @Transactional
    public RegistroAsistenciaEntity crearRegistro(RegistroAsistenciaEntity entity) {
        return repository.save(entity);
    }

    @Override
    @Transactional
    public RegistroAsistenciaEntity actualizarRegistro(RegistroAsistenciaEntity entity) {
        RegistroAsistenciaEntity existente = repository.findById(entity.getId())
                .orElseThrow(() -> new RuntimeException("Registro de asistencia no encontrado"));

        existente.setColaborador(entity.getColaborador());
        existente.setFecha(entity.getFecha());
        existente.setHoraEntrada(entity.getHoraEntrada());
        existente.setHoraSalida(entity.getHoraSalida());
        existente.setObservaciones(entity.getObservaciones());

        return repository.save(existente);
    }

    @Override
    @Transactional
    public String eliminarRegistro(Integer id) {
        RegistroAsistenciaEntity entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registro de asistencia no encontrado"));
        repository.delete(entity);
        return "Registro eliminado correctamente";
    }

    @Override
    @Transactional(readOnly = true)
    public List<RegistroAsistenciaEntity> listarRegistros() {
        return repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public RegistroAsistenciaEntity obtenerPorId(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registro de asistencia no encontrado"));
    }
}

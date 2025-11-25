package com.vet.manadawoof.service.impl;


import com.vet.manadawoof.dtos.request.HorarioBaseRolRequestDTO;
import com.vet.manadawoof.dtos.response.HorarioBaseRolResponseDTO;
import com.vet.manadawoof.entity.DiaEntity;
import com.vet.manadawoof.entity.HorarioBaseEntity;
import com.vet.manadawoof.entity.HorarioBaseRolEntity;
import com.vet.manadawoof.entity.RolEntity;
import com.vet.manadawoof.mapper.HorarioBaseRolMapper;
import com.vet.manadawoof.repository.DiaRepository;
import com.vet.manadawoof.repository.HorarioBaseRepository;
import com.vet.manadawoof.repository.HorarioBaseRolRepository;
import com.vet.manadawoof.repository.RolRepository;
import com.vet.manadawoof.service.HorarioBaseRolService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HorarioBaseRolServiceImpl implements HorarioBaseRolService {
    
    private final HorarioBaseRolRepository repository;
    private final RolRepository rolRepository;
    private final HorarioBaseRepository horarioBaseRepository;
    private final DiaRepository diaRepository;
    private final HorarioBaseRolMapper mapper;
    
    @Override
    @Transactional
    public HorarioBaseRolResponseDTO asignarHorarioARol(HorarioBaseRolRequestDTO request) {
        // Validar que el rol exista
        RolEntity rol = rolRepository.findById(request.getIdRol())
                .orElseThrow(() -> new RuntimeException("Rol no encontrado con ID: " + request.getIdRol()));
        
        // Validar que el horario base exista
        HorarioBaseEntity horarioBase = horarioBaseRepository.findById(request.getIdHorarioBase())
                .orElseThrow(() -> new RuntimeException("Horario base no encontrado con ID: " + request.getIdHorarioBase()));
        
        // Validar que el día exista
        DiaEntity dia = diaRepository.findById(request.getIdDiaSemana())
                .orElseThrow(() -> new RuntimeException("Día no encontrado con ID: " + request.getIdDiaSemana()));
        
        // Validar que no exista la misma asignación
        if(repository.existsByRolIdAndHorarioBaseIdAndDiaId(
                request.getIdRol(), request.getIdHorarioBase(), request.getIdDiaSemana())) {
            throw new RuntimeException("Ya existe una asignación para este rol, horario y día");
        }
        
        // Crear la nueva asignación
        HorarioBaseRolEntity entity = HorarioBaseRolEntity.builder()
                .rol(rol)
                .horarioBase(horarioBase)
                .dia(dia)
                .build();
        
        HorarioBaseRolEntity guardado = repository.save(entity);
        return mapper.toResponse(guardado);
    }
    
    @Override
    @Transactional
    public void eliminarAsignacion(Long id) {
        if(! repository.existsById(id)) {
            throw new RuntimeException("Asignación no encontrada con ID: " + id);
        }
        repository.deleteById(id);
    }
    
    @Override
    @Transactional
    public List<HorarioBaseRolResponseDTO> listarTodos() {
        return repository.findAllWithDetails().stream()
                .map(mapper :: toResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public List<HorarioBaseRolResponseDTO> listarPorRol(Integer idRol) {
        if(! rolRepository.existsById(idRol)) {
            throw new RuntimeException("Rol no encontrado con ID: " + idRol);
        }
        return repository.findByRolIdWithDetails(idRol).stream()
                .map(mapper :: toResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public List<HorarioBaseRolResponseDTO> listarPorHorarioBase(Integer idHorarioBase) {
        if(! horarioBaseRepository.existsById(idHorarioBase)) {
            throw new RuntimeException("Horario base no encontrado con ID: " + idHorarioBase);
        }
        return repository.findByHorarioBaseIdWithDetails(idHorarioBase).stream()
                .map(mapper :: toResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public List<HorarioBaseRolResponseDTO> listarPorDia(Integer idDiaSemana) {
        if(! diaRepository.existsById(idDiaSemana)) {
            throw new RuntimeException("Día no encontrado con ID: " + idDiaSemana);
        }
        return repository.findByDiaIdWithDetails(idDiaSemana).stream()
                .map(mapper :: toResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public HorarioBaseRolResponseDTO obtenerPorId(Long id) {
        HorarioBaseRolEntity entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Asignación no encontrada con ID: " + id));
        return mapper.toResponse(entity);
    }
}

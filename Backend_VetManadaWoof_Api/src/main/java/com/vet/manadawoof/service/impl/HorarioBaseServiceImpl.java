package com.vet.manadawoof.service.impl;

import com.vet.manadawoof.dtos.request.HorarioBaseRequestDTO;
import com.vet.manadawoof.dtos.response.HorarioBaseResponseDTO;
import com.vet.manadawoof.entity.HorarioBaseEntity;
import com.vet.manadawoof.mapper.HorarioBaseMapper;
import com.vet.manadawoof.repository.HorarioBaseRepository;
import com.vet.manadawoof.service.HorarioBaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HorarioBaseServiceImpl implements HorarioBaseService {
    
    private final HorarioBaseRepository repository;
    private final HorarioBaseMapper mapper;
    
    @Override
    @Transactional
    public HorarioBaseResponseDTO crearHorario(HorarioBaseRequestDTO request) {
        // Validar que no exista otro horario con el mismo nombre
        repository.findByNombreIgnoreCase(request.getNombre())
                .ifPresent(h -> {
                    throw new RuntimeException("Ya existe un horario con el nombre: " + request.getNombre());
                });
        
        // Validar que si no es descanso, debe tener horas
        if(request.getHoraInicio() != null || request.getHoraFin() != null) {
            if(request.getHoraInicio() == null || request.getHoraFin() == null) {
                throw new RuntimeException("Debe especificar tanto hora de inicio como hora de fin");
            }
            
            // Validar lógica overnight
            if(! request.getOvernight() && request.getHoraFin().isBefore(request.getHoraInicio())) {
                throw new RuntimeException("La hora de fin debe ser mayor a la hora de inicio para horarios no overnight");
            }
        }
        
        HorarioBaseEntity entity = mapper.toEntity(request);
        HorarioBaseEntity guardado = repository.save(entity);
        return mapper.toResponse(guardado);
    }
    
    @Override
    @Transactional
    public HorarioBaseResponseDTO actualizarHorario(Integer id, HorarioBaseRequestDTO request) {
        HorarioBaseEntity existente = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Horario no encontrado con ID: " + id));
        
        // Validar nombre único si se está actualizando
        if(request.getNombre() != null && ! request.getNombre().equals(existente.getNombre())) {
            if(repository.existsByNombreIgnoreCaseAndIdNot(request.getNombre(), id)) {
                throw new RuntimeException("Ya existe otro horario con el nombre: " + request.getNombre());
            }
        }
        
        // Actualizar campos usando el mapper
        mapper.updateEntityFromRequest(existente, request);
        
        HorarioBaseEntity actualizado = repository.save(existente);
        return mapper.toResponse(actualizado);
    }
    
    @Override
    @Transactional
    public void eliminarHorario(Integer id) {
        if(! repository.existsById(id)) {
            throw new RuntimeException("Horario no encontrado con ID: " + id);
        }
        repository.deleteById(id);
    }
    
    @Override
    @Transactional
    public List<HorarioBaseResponseDTO> listarHorarios() {
        return repository.findAll().stream()
                .map(mapper :: toResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public HorarioBaseResponseDTO obtenerPorId(Integer id) {
        HorarioBaseEntity entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Horario no encontrado con ID: " + id));
        return mapper.toResponse(entity);
    }
}

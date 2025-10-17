package com.vet.manadawoof.service.impl;

import com.vet.manadawoof.entity.HorarioTrabajoEntity;
import com.vet.manadawoof.repository.HorarioTrabajoRepository;
import com.vet.manadawoof.service.HorarioTrabajoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio que gestiona las operaciones CRUD sobre los horarios de trabajo.
 * Permite crear, actualizar, eliminar y listar horarios registrados.
 */
@Service
@RequiredArgsConstructor
public class HorarioTrabajoServiceImpl implements HorarioTrabajoService {
    
    private final HorarioTrabajoRepository repository;
    
    /**
     * Crea un nuevo horario de trabajo.
     * Valida que no exista otro con el mismo nombre antes de guardarlo.
     *
     * @param entity Objeto HorarioTrabajoEntity con los datos del nuevo horario.
     * @return El horario creado y guardado en base de datos.
     * @throws RuntimeException Si ya existe un horario con el mismo nombre.
     */
    @Override
    @Transactional
    public HorarioTrabajoEntity crearHorario(HorarioTrabajoEntity entity) {
        repository.findAll().stream().filter(e -> e.getNombre().equalsIgnoreCase(entity.getNombre())).findFirst().ifPresent(e -> {
            throw new RuntimeException("Horario ya existe");
        });
        return repository.save(entity);
    }
    
    /**
     * Actualiza un horario de trabajo existente.
     * Comprueba que el ID exista y que no haya otro horario con el mismo nombre.
     *
     * @param entity Horario con los nuevos datos a actualizar.
     * @return El horario actualizado.
     * @throws RuntimeException Si el horario no existe o el nombre está duplicado.
     */
    @Override
    @Transactional
    public HorarioTrabajoEntity actualizarHorario(HorarioTrabajoEntity entity) {
        HorarioTrabajoEntity existente = repository.findById(entity.getId()).orElseThrow(() -> new RuntimeException("Horario no encontrado"));
        repository.findAll().stream().filter(e -> e.getNombre().equalsIgnoreCase(entity.getNombre()) && ! e.getId().equals(entity.getId())).findFirst().ifPresent(e -> {
            throw new RuntimeException("Otro Horario con esa asignación ya existe");
        });
        existente.setNombre(entity.getNombre()); existente.setActivo(entity.getActivo());
        return repository.save(existente);
    }
    
    /**
     * Elimina un horario de trabajo por su ID.
     *
     * @param id Identificador del horario a eliminar.
     * @return Mensaje de confirmación.
     * @throws RuntimeException Si el horario no existe.
     */
    @Override
    @Transactional
    public String eliminarHorario(Integer id) {
        if(! repository.existsById(id)) {
            throw new RuntimeException("Horario no existe");
        }
        repository.deleteById(id);
        return "Horario eliminado correctamente";
    }
    
    /**
     * Lista todos los horarios de trabajo existentes.
     *
     * @return Lista completa de horarios registrados.
     */
    @Override
    @Transactional(readOnly = true)
    public List<HorarioTrabajoEntity> listarHorarios() {
        return repository.findAll();
    }
    
    /**
     * Obtiene un horario de trabajo por su ID.
     *
     * @param id Identificador del horario.
     * @return Horario encontrado.
     * @throws RuntimeException Si el horario no se encuentra.
     */
    @Override
    @Transactional(readOnly = true)
    public HorarioTrabajoEntity obtenerPorId(Integer id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Horario no encontrado"));
    }
}
